package player;

public class Score {
    String username;
    int EloRating = findEloRating(username);



    /**
     * Method to check if a username exists in usernames.txt
     * @param username
     * @return the elo-rating for the user as int, set the rating to 1500(placeholder) if name is not found.
     */
    public int findEloRating(String username){
        //TODO
        String[2] userElo; //stores username/elo rating in [0]/[1]
        try (BufferedReader br = new BufferedReader(new FileReader("username.txt"))) {
            String line = br.readLine();

            while(line != null){
                userElo = line.split("/");
                int  = Integer.parseInt(userElo[1]);

            }
        }
        ELO = 1500;
        return ELO;
    }


}
