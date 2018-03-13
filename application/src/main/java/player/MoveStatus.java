package player;

/**
 * Enums represents the different statuses that a move
 * can have.
 */
public enum MoveStatus {
    DONE {
        @Override
        boolean isDone() {
            return true;
        }
    },
    ILLEGAL_MOVE {
        @Override
        boolean isDone() {
            return false;
        }
    }, LEAVES_PLAYER_IN_CHECK {
        @Override
        boolean isDone() {
            return false;
        }
    };

    /**
     * @return true if the move can be done, false otherwise
     */
    abstract boolean isDone();
}
