package player;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
public class Score {
    String username; //TODO Must fetch username from GUI input
    HashMap<String, Integer> userRating = new HashMap<>(); //Hashmap needs to retrieve usernames and  ratings
    int EloRating = findEloRating(username, userRating);

    /**
     *  takes the old rating of both players, and the result of the game for both players,
     1 for win, 0.5 for draw and 0 for loss
     * @param playerRating rating for player1
     * @param player2Rating rating for player2
     * @param Gameresult gameresult for player 1
     * @param Gameresult2 gameresult for player 2
     * @return Array containing updated ratings for both players
     */
    public int [] MatchRating(int playerRating,int player2Rating,double Gameresult, double Gameresult2){
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
    /**
     * Method to check if a username exists in usernames.txt
     * @param username
     * @param userRating Hashmap containing usernames and rating.
     * @return the elo-rating for the user as int, set the rating to 1500(placeholder) if name is not found.
     */
    //TODO: When to read username.txt

    public int findEloRating(String username, HashMap<String, Integer> userRating) {
        try (BufferedReader br = new BufferedReader(new FileReader("username.txt"))) {
            String line = br.readLine();
            String[] temp;
            while(line != null){
                temp = line.split("/");
                if(userRating.containsKey(temp[0])){
                    return userRating.get(username);
                }else{
                    userRating.put(username, 1500);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1500;
    }
}
