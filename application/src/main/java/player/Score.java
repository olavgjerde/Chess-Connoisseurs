package player;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Score {
    String username;
    int EloRating = findEloRating(username);



    /**
     * Method to check if a username exists in usernames.txt
     * @param username
     * @return the elo-rating for the user as int, set the rating to 1500(placeholder) if name is not found.
     */
    //TODO: Handle exceptions, calculate new rating, fetch username
    public int findEloRating(String username) {
        String[] userElo = new String[2]; //stores username/elo rating in [0]/[1]
        int elo = 1500; //Placeholder number

        try (BufferedReader br = new BufferedReader(new FileReader("username.txt"))) {
            String line = br.readLine();

            while (line != null) {
                userElo = line.split("/");
                int tempElo = Integer.parseInt(userElo[1]);
                if (userElo[0].equals(username)) {
                    elo = tempElo;
                    break;
                }
            }
            return elo;
        }
    }

}
