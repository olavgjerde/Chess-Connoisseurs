package AI;

import board.Move;
import player.Player;

public class AI {

    //players in the game we are playing
    private final Player AI;
    private final Player OPPONENT;

    //difficulty of this AI
    private final boolean IS_HARD;

    /**
     * constructor for the AI class
     * @param AI the player that is the AI in the game (the one we are finding a move for)
     * @param opponent the opponent of the AI
     * @param isHard difficulty of the AI
     */
    public AI(Player AI, Player opponent, boolean isHard) {
        this.AI = AI;
        this.OPPONENT = opponent;
        this.IS_HARD = isHard;
    }

    /**
     * get the best move that the AI can find
     * @return the best move
     */
    public Move getMove(){
        BoardStateTree BT;

        //make trees with different settings based if we want it to be hard or not
        if (IS_HARD) BT = new BoardStateTree(AI, OPPONENT, 5, 10, 2, 5, 0);
        else BT = new BoardStateTree(AI, OPPONENT, 2, 5, 1, 1, 0);
        return BT.getBestMove();
    }
}
