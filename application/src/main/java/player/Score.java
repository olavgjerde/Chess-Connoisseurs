package player;
import java.io.*;
import java.util.HashMap;

public class Score {
    private HashMap<String, Integer> userRating = new HashMap<>();

    /**
     *  takes the old rating of both players, and the result of the game for both players,
     1 for win, 0.5 for draw and 0 for loss
     * @param playerRating rating for player1
     * @param player2Rating rating for player2
     * @param Gameresult gameresult for player 1
     * @param Gameresult2 gameresult for player 2
     * @return Array containing updated ratings for both players
     */
    public int [] matchRating(int playerRating,int player2Rating,double Gameresult, double Gameresult2){
        int score [] = new int[2];
        int K = 32;
        int e = playerRating/(playerRating-player2Rating);
        int e2 = player2Rating/(player2Rating-playerRating);

        double newplayerRating = playerRating+(K*(Gameresult-e));
        score[0] = (int)newplayerRating;

        double newplayer2Rating = player2Rating+(K*(Gameresult2-e2));
        score[1] = (int)newplayer2Rating;

        return score;
    }

    //TODO get gameresult
    public void updateHighscore(String username1, String username2){
        int score1 = userRating.get(username1);
        int score2 = userRating.get(username2);
    }

    public int getScore(String username){return userRating.get(username);}

    /**
     * Adds a username with 1500 rating to highscore.txt if the username does not already exist
     * @param username
     */
    public void addUsername(String username){
        try {
            PrintWriter out = new PrintWriter("highscore.txt");
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFound");; }

        if(!userRating.containsKey(username)){
            try (BufferedReader br = new BufferedReader(new FileReader("highscore.txt"))) {
                String line = br.readLine();
                line = line + username + " 1500\n";
                out.println(line);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * reads highscore.txt to a hashmap containing usernames as key and rating as value
     * @param highscore
     */
    public void readUsername(String highscore){
        try (BufferedReader br = new BufferedReader(new FileReader("highscore.txt"))) {
            String line = br.readLine();
            String[] temp;
            while(line != null){
                temp = line.split(" ");
                int rating = Integer.parseInt((temp[1]));
                userRating.put(temp[0],rating);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
