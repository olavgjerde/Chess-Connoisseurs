package pieces;

import board.BoardUtils;
import board.Coordinate;
import player.Player;

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
        public Player choosePlayerByAlliance(Player whitePlayer, Player blackPlayer) {
            return whitePlayer;
        }

        @Override
        public int pawnSquareValue(Coordinate coordinate) {
            return WHITE_PAWN_PREFERRED_COORDINATES[BoardUtils.getInstance().getIntegerRepresentationFromCoordinate(coordinate)];
        }

        @Override
        public int knightSquareValue(Coordinate coordinate) {
            return WHITE_KNIGHT_PREFERRED_COORDINATES[BoardUtils.getInstance().getIntegerRepresentationFromCoordinate(coordinate)];
        }

        @Override
        public int bishopSquareValue(Coordinate coordinate) {
            return WHITE_BISHOP_PREFERRED_COORDINATES[BoardUtils.getInstance().getIntegerRepresentationFromCoordinate(coordinate)];
        }

        @Override
        public int rookSquareValue(Coordinate coordinate) {
            return WHITE_ROOK_PREFERRED_COORDINATES[BoardUtils.getInstance().getIntegerRepresentationFromCoordinate(coordinate)];
        }

        @Override
        public int queenSquareValue(Coordinate coordinate) {
            return WHITE_QUEEN_PREFERRED_COORDINATES[BoardUtils.getInstance().getIntegerRepresentationFromCoordinate(coordinate)];
        }

        @Override
        public int kingSquareValue(Coordinate coordinate, boolean isEndGame) {
            int locationValue;
            if (isEndGame) locationValue = WHITE_KING_PREFERRED_ENDGAME_COORDINATES[BoardUtils.getInstance().getIntegerRepresentationFromCoordinate(coordinate)];
            else locationValue = WHITE_KING_PREFERRED_COORDINATES[BoardUtils.getInstance().getIntegerRepresentationFromCoordinate(coordinate)];
            return locationValue;
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
            return coordinate.getY() == BoardUtils.getInstance().getHeight() - 1;
        }

        @Override
        public Player choosePlayerByAlliance(Player whitePlayer, Player blackPlayer) {
            return blackPlayer;
        }

        @Override
        public int pawnSquareValue(Coordinate coordinate) {
            return BLACK_PAWN_PREFERRED_COORDINATES[BoardUtils.getInstance().getIntegerRepresentationFromCoordinate(coordinate)];
        }

        @Override
        public int knightSquareValue(Coordinate coordinate) {
            return BLACK_KNIGHT_PREFERRED_COORDINATES[BoardUtils.getInstance().getIntegerRepresentationFromCoordinate(coordinate)];
        }

        @Override
        public int bishopSquareValue(Coordinate coordinate) {
            return BLACK_BISHOP_PREFERRED_COORDINATES[BoardUtils.getInstance().getIntegerRepresentationFromCoordinate(coordinate)];
        }

        @Override
        public int rookSquareValue(Coordinate coordinate) {
            return BLACK_ROOK_PREFERRED_COORDINATES[BoardUtils.getInstance().getIntegerRepresentationFromCoordinate(coordinate)];
        }

        @Override
        public int queenSquareValue(Coordinate coordinate) {
            return BLACK_QUEEN_PREFERRED_COORDINATES[BoardUtils.getInstance().getIntegerRepresentationFromCoordinate(coordinate)];
        }

        @Override
        public int kingSquareValue(Coordinate coordinate, boolean isEndGame) {
            int locationValue;
            if (isEndGame) locationValue = BLACK_KING_PREFERRED_ENDGAME_COORDINATES[BoardUtils.getInstance().getIntegerRepresentationFromCoordinate(coordinate)];
            else locationValue = BLACK_KING_PREFERRED_COORDINATES[BoardUtils.getInstance().getIntegerRepresentationFromCoordinate(coordinate)];
            return locationValue;
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
    public abstract Player choosePlayerByAlliance(Player whitePlayer, Player blackPlayer);

    /**
     * Return the piece-square value for the given coordinate
     * @param coordinate where the piece is placed
     * @return value of piece placement
     * @see <a href="https://chessprogramming.wikispaces.com/Simplified%20evaluation%20function">Piece-square values</a>
     */
    public abstract int pawnSquareValue(Coordinate coordinate);
    public abstract int knightSquareValue(Coordinate coordinate);
    public abstract int bishopSquareValue(Coordinate coordinate);
    public abstract int rookSquareValue(Coordinate coordinate);
    public abstract int queenSquareValue(Coordinate coordinate);

    /**
     * Return the piece-square value for the given coordinate
     * @param coordinate where the piece is placed
     * @param isEndGame to fetch the position values for endgame or not
     * @return value of piece placement
     * @see <a href="https://chessprogramming.wikispaces.com/Simplified%20evaluation%20function">Piece-square values</a>
     */
    public abstract int kingSquareValue(Coordinate coordinate, boolean isEndGame);

    private final static int[] WHITE_PAWN_PREFERRED_COORDINATES = {
            0,  0,  0,  0,  0,  0,  0,  0,
            50, 50, 50, 50, 50, 50, 50, 50,
            10, 10, 20, 30, 30, 20, 10, 10,
            5,  5, 10, 25, 25, 10,  5,  5,
            0,  0,  0, 20, 20,  0,  0,  0,
            5, -5,-10,  0,  0,-10, -5,  5,
            5, 10, 10,-20,-20, 10, 10,  5,
            0,  0,  0,  0,  0,  0,  0,  0
    };

    private final static int[] BLACK_PAWN_PREFERRED_COORDINATES = {
            0,  0,  0,  0,  0,  0,  0,  0,
            5, 10, 10,-20,-20, 10, 10,  5,
            5, -5,-10,  0,  0,-10, -5,  5,
            0,  0,  0, 20, 20,  0,  0,  0,
            5,  5, 10, 25, 25, 10,  5,  5,
            10, 10, 20, 30, 30, 20, 10, 10,
            50, 50, 50, 50, 50, 50, 50, 50,
            0,  0,  0,  0,  0,  0,  0,  0
    };

    private final static int[] WHITE_KNIGHT_PREFERRED_COORDINATES = {
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50
    };

    private final static int[] BLACK_KNIGHT_PREFERRED_COORDINATES = {
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50
    };

    private final static int[] WHITE_BISHOP_PREFERRED_COORDINATES = {
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -20,-10,-10,-10,-10,-10,-10,-20
    };

    private final static int[] BLACK_BISHOP_PREFERRED_COORDINATES = {
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -20,-10,-10,-10,-10,-10,-10,-20
    };

    private final static int[] WHITE_ROOK_PREFERRED_COORDINATES = {
            0,  0,  0,  0,  0,  0,  0,  0,
            5, 20, 20, 20, 20, 20, 20,  5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            0,  0,  0,  5,  5,  0,  0,  0
    };

    private final static int[] BLACK_ROOK_PREFERRED_COORDINATES = {
            0,  0,  0,  5,  5,  0,  0,  0,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            5, 20, 20, 20, 20, 20, 20,  5,
            0,  0,  0,  0,  0,  0,  0,  0
    };

    private final static int[] WHITE_QUEEN_PREFERRED_COORDINATES = {
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -5,  0,  5,  5,  5,  5,  0, -5,
            0,  0,  5,  5,  5,  5,  0, -5,
            -10,  5,  5,  5,  5,  5,  0,-10,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20
    };

    private final static int[] BLACK_QUEEN_PREFERRED_COORDINATES = {
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -10,  5,  5,  5,  5,  5,  0,-10,
            0,  0,  5,  5,  5,  5,  0, -5,
            0,  0,  5,  5,  5,  5,  0, -5,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20
    };

    private final static int[] WHITE_KING_PREFERRED_COORDINATES = {
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -20,-30,-30,-40,-40,-30,-30,-20,
            -10,-20,-20,-20,-20,-20,-20,-10,
            20, 20,  0,  0,  0,  0, 20, 20,
            20, 30, 10,  0,  0, 10, 30, 20
    };

    private final static int[] WHITE_KING_PREFERRED_ENDGAME_COORDINATES = {
            -50,-40,-30,-20,-20,-30,-40,-50,
            -30,-20,-10,  0,  0,-10,-20,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-30,  0,  0,  0,  0,-30,-30,
            -50,-30,-30,-30,-30,-30,-30,-50
    };

    private final static int[] BLACK_KING_PREFERRED_COORDINATES = {
            20, 30, 10,  0,  0, 10, 30, 20,
            20, 20,  0,  0,  0,  0, 20, 20,
            -10,-20,-20,-20,-20,-20,-20,-10,
            -20,-30,-30,-40,-40,-30,-30,-20,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30
    };

    private final static int[] BLACK_KING_PREFERRED_ENDGAME_COORDINATES = {
            -50,-30,-30,-30,-30,-30,-30,-50,
            -30,-30,  0,  0,  0,  0,-30,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-20,-10,  0,  0,-10,-20,-30,
            -50,-40,-30,-20,-20,-30,-40,-50
    };

}
