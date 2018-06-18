package player;

import board.Board;

import static board.Move.*;

/**
 * Class represents a transition from one board-state to another when executing a move.
 */
public class MoveTransition {
    private final Board transitionBoard;
    private final MoveStatus moveStatus;

    /**
     * Construct a MoveTransition object
     * @param transitionBoard board with initial positions
     * @param moveStatus status of that given move (can be done or not etc.)
     */
    MoveTransition(Board transitionBoard, MoveStatus moveStatus) {
        this.transitionBoard = transitionBoard;
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
