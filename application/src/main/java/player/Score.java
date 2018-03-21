package player;
import java.io.*;
import java.util.HashMap;

public class Score {
    private HashMap<String, Integer> userRating = new HashMap<>();

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

        double S1 = gameResult1;
        double S2 = gameResult2;

        double newPlayer1Rating = player1Rating + K*(S1 - E1);
        double newPlayer2Rating = player2Rating + K*(S2 - E2);

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
     * @param username
     */
    public int getScore(String username){
        return userRating.get(username);
    }

    /**
     * Adds a username with 1500 rating to highscore.txt if the username does not already exist
     * @param username
     */
    public void addUsername(String username){
        if(!userRating.containsKey(username)){
            userRating.put(username, 1500);
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
                f.createNewFile();
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
                userRating.put(temp[0],rating);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses the hashMap userRating to write out the highscore.txt
     */
    private void writeHighscore(){
        try {
            PrintWriter out = new PrintWriter(new File("highscore.txt"));
            for (String s : userRating.keySet()) {
                String line = s + " " + userRating.get(s) + "\n";
                out.write(line);
            }
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFound");
        }
    }
}
