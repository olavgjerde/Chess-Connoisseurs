package player;

import board.Board;
import board.Move;
import static board.Move.*;

/**
 * Class represents a transition from one board-state to another when executing a move.
 */
public class MoveTransition {
    private final Board transitionBoard;
    private final Move move;
    private final MoveStatus moveStatus;

    /**
     * Construct a MoveTransition object
     * @param transitionBoard board with initial positions
     * @param move to be made
     * @param moveStatus status of that given move (can be done or not etc.)
     */
    public MoveTransition(Board transitionBoard, Move move, MoveStatus moveStatus) {
        this.transitionBoard = transitionBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    /**
     * Return the status of a move
     * @return a MoveStatus object; see MoveStatus.java for examples.
     */
    public MoveStatus getMoveStatus() {
        return this.moveStatus;
    }

    public Board getTransitionBoard() {
        return this.transitionBoard;
    }
}
