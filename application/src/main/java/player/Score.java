package player;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.List;


import java.net.UnknownHostException;


public class Score {
    private HashMap<String, Integer> userRating = new HashMap<>();
    private HashMap<String, Stats> userStats = new HashMap<>();
    private List<Document> highscoreDB = new ArrayList<Document>();


    /**
     *  takes the old rating of both players, and the result of the game for both players,
     1 for win, 0.5 for draw and 0 for loss
     * @param username1 player1
     * @param username2 player2
     * @param gameResult1 gameresult for player 1
     * @param gameResult2 gameresult for player 2
     * @return Array containing updated ratings for both players
     */
    public int [] matchRating(String username1, String username2, double gameResult1, double gameResult2){

        int score [] = new int[2];
        double player1Rating = userRating.get(username1);
        double player2Rating = userRating.get(username2);

        int K = 32;

        double R1 = Math.pow(10, (player1Rating/400));
        double R2 = Math.pow(10, (player2Rating/400));

        double E1 = R1/(R1+R2);
        double E2 = R2/(R1+R2);

        double newPlayer1Rating = player1Rating + K*(gameResult1 - E1);
        double newPlayer2Rating = player2Rating + K*(gameResult2 - E2);

        score[0] = (int) newPlayer1Rating;
        score[1] = (int) newPlayer2Rating;

        return score;
    }

    /**
     * Updates a users highscore
     * @param username name of the user
     * @param newScore what to set the new score to
     */
    public void updateHighscore(String username, int newScore){
        userRating.put(username, newScore);
        writeHighscore();
    }

    /**
     * Returns a users rating
     * @param username the username of the user
     * @return the score of the given user
     */
    public int getScore(String username){
        return userRating.get(username);
    }

    /**
     * Returns a users game statistics
     * @param username the username of the user
     * @return a string with wins/draws/losses
     */
    public String getStats (String username) {return userStats.get(username).getStats();}

    /**
     * Returns a users game statistics
     * @param username the username of the user
     * @return a string with Wins: w, Draws: d, Losses: l
     */
    public String getStatsVerbose (String username){return userStats.get(username).getStatsVerbose();}

    /**
     * Adds a username with 1500 rating and enmpty stats to highscore.txt if the username does not already exist
     * @param username name of the user to add
     */
    public void addUsername(String username){
        if(!userRating.containsKey(username)){
            userRating.put(username, 1500);
            userStats.put(username, new Stats(0,0,0));
            writeHighscore();
        }

    }

    /**
     * Reads highscore.txt to a hashmap containing usernames as key and rating as value
     */
    public void readHighscore(){

        //In case the highscore.txt does not exist
        File f = new File("highscore.txt");
        if(!f.exists()) {
            try {
                boolean created = f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader("highscore.txt"))) {
            String line;
            String[] temp;
            while((line = br.readLine()) != null){
                temp = line.split(" ");
                int rating = Integer.parseInt((temp[1]));
                Stats stats = readStats(temp[2]);
                userRating.put(temp[0], rating);
                userStats.put(temp[0], stats);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        /*
        MongoClientURI uri  = new MongoClientURI("mongodb://ccuser:ccpass@ds129706.mlab.com:29706/ccdb");
        MongoClient client = new MongoClient(uri);
        MongoDatabase db = client.getDatabase(uri.getDatabase());
        MongoCollection<Document> highscore = db.getCollection("highscore");


        Document findQuery = new Document("", new Document("$gte",10));
        Document orderBy = new Document("score", 1);
        MongoCursor<Document> cursor = highscore.find(findQuery).sort(orderBy).iterator();

        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                System.out.println(doc.get("playername") + ", " + doc.get("score") +" " +doc.get("win") + doc.get("draw") + doc.get("draw"));
            }
        } finally {
            cursor.close();
        }

        client.close();
*/
    }

    /**
     * Uses the hashMap userRating to write out the highscore.txt
     */
    private void writeHighscore(){
        try {
            PrintWriter out = new PrintWriter(new File("highscore.txt"));
            for (String s : userRating.keySet()) {
                String line = s + " " + userRating.get(s) + " " + userStats.get(s).getStats() + "\n";
                out.write(line);
            }
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFound");
        }

        /*
        MongoClientURI uri  = new MongoClientURI("mongodb://ccuser:ccpass@ds129706.mlab.com:29706/ccdb");
        MongoClient client = new MongoClient(uri);
        MongoDatabase db = client.getDatabase(uri.getDatabase());
        MongoCollection<Document> highscore = db.getCollection("highscore");
        highscore.insertMany(highscoreDB);


            for (String s : userRating.keySet()) {
               // String line = s + " " + userRating.get(s) + " " + userStats.get(s).getStats() + "\n";
                highscoreDB.add(new Document("playername", s)
                        .append("score", userRating.get(s))
                    .append("win", userStats.get(s).getStats())
                        .append("draw", 0)
                        .append("loss", 0)

                );
            }

            client.close();*/

    }

    private Stats readStats(String stats){
        String[] temp = stats.split("/");
        int w = Integer.parseInt(temp[0]);
        int d = Integer.parseInt(temp[1]);
        int l = Integer.parseInt(temp[2]);
        return new Stats(w,d,l);
    }

    public void addWin(String username){
        userStats.get(username).addWin();
    }

    public void addDraw(String username){
        userStats.get(username).addDraw();
    }

    public void addLoss(String username){
        userStats.get(username).addLoss();
    }

    public ArrayList<String> getScoreboard (){
        ArrayList<String> scoreboard = new ArrayList<>();
        HashMap<String, Integer> temp = new HashMap<>(userRating);
        while(!temp.isEmpty()){
            int hi = 0;
            String u = "";
            for(String s : temp.keySet()){
                if(temp.get(s) >= hi) {
                    hi = temp.get(s);
                    u = s;
                }
            }
            scoreboard.add(u);
            temp.remove(u,hi);
        }
        return scoreboard;
    }

    public int size(){
        return userRating.size();
    }

    private class Stats {
        int wins, draws, losses;

        private Stats(int wins, int draws, int losses) {
            this.wins = wins;
            this.draws = draws;
            this.losses = losses;
        }

        private void addWin() {
            wins++;
        }

        private void addDraw() {
            draws++;
        }

        private void addLoss() {
            losses++;
        }

        private String getStats(){
            return wins + "/"+ draws + "/" + losses;
        }

        private String getStatsVerbose(){
            return "Wins: " + wins + ", Draws: " + draws + ", Losses: " + losses;
        }
    }
}
