package pieces;

import board.BoardUtils;
import board.Coordinate;
import player.BlackPlayer;
import player.Player;
import player.WhitePlayer;

/**
 * Holds the two types of alliances that a chess piece can have
 */
public enum Alliance {
    WHITE {
        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public int getOppositeDirection() {
            return 1;
        }

        @Override
        public boolean isPawnPromotionCoordinate(Coordinate coordinate) {
            return coordinate.getY() == 0;
        }

        @Override
        public Player choosePlayerByAlliance(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return whitePlayer;
        }

        @Override
        public String toString() {
            return "White";
        }
    },
    BLACK {
        @Override
        public int getDirection() {
            return 1;
        }

        @Override
        public int getOppositeDirection() {
            return -1;
        }

        @Override
        public boolean isPawnPromotionCoordinate(Coordinate coordinate) {
            return coordinate.getY() == BoardUtils.getHeight() - 1;
        }

        @Override
        public Player choosePlayerByAlliance(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return blackPlayer;
        }

        @Override
        public String toString() {
            return "Black";
        }
    };

    /**
     * Determine the which way the pieces are moving on the board
     * @return int -1 if Alliance is white, 1 if black.
     */
    public abstract int getDirection();

    /**
     * Give the opposite way of which the pieces are moving on the board
     * @return int 1 if Alliance is white, -1 if black.
     */
    public abstract int getOppositeDirection();

    /**
     * Checks if the coordinate belongs to a tile where pawn promotion can happen
     * @param coordinate to check
     * @return true if pawn promotion is available, false otherwise
     */
    public abstract boolean isPawnPromotionCoordinate(Coordinate coordinate);

    /**
     * This method shall choose the current player given the players of a board
     * See constructor of Board.java for example usage.
     * @param whitePlayer the white player on the board
     * @param blackPlayer the black player on the board
     * @return the Player object which controls the next move
     */
    public abstract Player choosePlayerByAlliance(WhitePlayer whitePlayer, BlackPlayer blackPlayer);


}
