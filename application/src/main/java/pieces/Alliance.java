package pieces;

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
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return whitePlayer;
        }
    },
    BLACK {
        @Override
        public int getDirection() {
            return 1;
        }

        @Override
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return blackPlayer;
        }
    };

    /**
     * Determine the which way the pieces are moving on the board
     * @return int -1 if Alliance is white, 1 if black.
     */
    public abstract int getDirection();

    /**
     * This method shall choose the current player given the players of a board
     * See constructor of Board.java for example usage.
     * @param whitePlayer the white player on the board
     * @param blackPlayer the black player on the board
     * @return the Player object which controls the next move
     */
    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}
