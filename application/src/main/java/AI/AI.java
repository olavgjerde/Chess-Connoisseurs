package AI;

import board.Board;
import board.Move;

public class AI {

    private final Board BOARD;
    private final boolean IS_HARD;

    /**
     * constructor for the AI
     * @param board the board we are moving on
     * @param isHard decides the shape of the tree
     */
    public AI(Board board, boolean isHard) {
        this.BOARD = board;
        this.IS_HARD = isHard;
    }

    /**
     * get the best move that the AI can find, for the current player
     * @return the best move
     */
    public Move getMove(){
        BoardStateTree BT;

        //make trees with different settings based on the difficulty
        if (IS_HARD) BT = new BoardStateTree(BOARD,5, 10, 0);
        else BT = new BoardStateTree(BOARD, 2, 2, 0);

        return BT.getBestMove();
    }
}
