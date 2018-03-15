package player;

public class Score {
    String username;
    int ELOrating;

    public Score(String username) { //Constructor is also a placeholder, not sure how the logic will work yet.
        this.username = username;
        int elo = findUsername(username);
        if (elo == -1) {
            ELOrating = 1500; //1500 is a placeholder for the default starting rating.
        } else {
            ELOrating = elo;
        } //Else set ELOrating to the rating that already exists for the user.
    }


    /**
     * Method to check if a username exists in usernames.txt
     * @param username
     * @return the elo-rating for the user as int, returns -1 if username is not found.
     */
    public int findUsername(String username){
        //TODO

        return -1;
    }


}
