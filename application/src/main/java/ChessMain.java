import GUI.GameGUI;

import java.util.ArrayList;

public class ChessMain {
    Game game = new Game();
    boolean isAI;
    Player player1;
    Player player2;
    AI AI;
    GameGUI GUI = new GameGUI();

    public static void main(String[] args) {
        //set up all the instance variables
        initialize();

        //not yet implemented
        assert(AI);

        while (true){
            //GUI.getMove(the player, was the previous move valid? (used for displaying error messages))
            validMove = game.move(GUI.getMove(player1, true));
            //if the fist move wasnt valid we keep asking for it
            while (!validMove)
                validMove = game.move(GUI.getMove(player1, false));

            //0 = game is over, 1 = chess (handled in game.validMove())...
            if (game.checkBoard() == 0)
                end(player1); //game is over

            //now we do the same for player2

            //GUI.getMove(the player, was the previous move valid? (used for displaying error messages))
            validMove = game.move(GUI.getMove(player2, true));
            //if the fist move wasnt valid we keep asking for it
            while (!validMove)
                validMove = game.move(GUI.getMove(player2, false));

            //0 = game is over, 1 = chess (handled in game.validMove())...
            if (game.checkBoard() == 0)
                end(player2); //game is over

        }


    }
    private static voind initialize(){
        //make a menuGUI object that we use to get the necessary information
        MenuGUI menuGUI = new MenuGUI();

        //get the game info from the menuGUI
        GameInfo gameInfo = menuGUI.getGameInfo();

        //make the game
        game = new Game();

        //is there an AI?
        isAI = gameInfo.isAI();

        //make player1
        player1 = new Player(gameInfo.getPlayer1Info());

        //make player2 or the AI
        if (isAI)
            AI = new AI(gameInfo.getDifficulty());
        else
            player2 = new Player(gameInfo.getPlayer2Info());

    }
}
