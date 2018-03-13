package pieces;

/**
 * Holds the two types of alliances that a chess piece can have
 */
public enum Alliance {
    WHITE {
        @Override
        public int getDirection() {
            return -1;
        }
    },
    BLACK {
        @Override
        public int getDirection() {
            return 1;
        }
    };

    public abstract int getDirection();
}
