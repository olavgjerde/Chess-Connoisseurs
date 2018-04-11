package pieces;

import board.Board;
import board.BoardUtils;
import board.Coordinate;
import board.Move;

import java.util.Collection;
import java.util.Objects;

/**
 * Abstract class representing the fundamental behavior and structure of a chess piece
 */
public abstract class Piece {

    final Coordinate pieceCoordinate;
    final Alliance pieceAlliance;
    private final boolean isFirstMove;
    private final PieceType pieceType;

    /**
     * Sets a piece's position and alliance
     *
     * @param pieceCoordinate position defined by a Coordinate object
     * @param pieceAlliance   alliance of the piece
     * @param isFirstMove     if it is the piece's first move or not
     * @param pieceType       enum Type of the piece
     */
    public Piece(final Coordinate pieceCoordinate, final Alliance pieceAlliance, final boolean isFirstMove, final PieceType pieceType) {
        if (BoardUtils.isValidCoordinate(pieceCoordinate)) this.pieceCoordinate = pieceCoordinate;
        else throw new RuntimeException("Piece constructed with coordinate out of bounds");
        this.pieceAlliance = pieceAlliance;
        this.isFirstMove = isFirstMove;
        this.pieceType = pieceType;
    }

    /**
     * Every class which implements this method shall calculate according to the rules defined by itself which moves
     * that are legal to do.
     *
     * @param board on which the piece belongs
     * @return A list of possible moves that a piece can make
     */
    public abstract Collection<Move> calculateLegalMoves(final Board board);

    /**
     * Pieces shall construct a copy of itself but with the new coordinates
     * contained in the Move object.
     *
     * @param move Move object which the piece shall evaluate
     * @return a Piece with the new position
     */
    public abstract Piece movePiece(final Move move);

    /**
     * Get the score for the placement of the piece
     * Used for board evaluation (see RegularBoardEvaluator)
     * @param  isEndGame to fetch the location values for end game or not
     * @return score for placement of piece
     */
    public abstract int locationValue(boolean isEndGame);

    /**
     * Get the alliance of a piece object
     *
     * @return enum Alliance
     */
    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }

    /**
     * Get the enum PieceType of the piece object
     *
     * @return enum PieceType
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * @return a Coordinate object belonging to the piece
     */
    public Coordinate getPieceCoordinate() {
        return this.pieceCoordinate;
    }

    /**
     * Check if it is the piece's first move
     *
     * @return true or false depending on if it is the first move of the piece
     */
    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return isFirstMove == piece.isFirstMove &&
                Objects.equals(pieceCoordinate, piece.pieceCoordinate) &&
                pieceAlliance == piece.pieceAlliance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceCoordinate, pieceAlliance, isFirstMove);
    }


    /**
     * Enums for the different types of pieces; to help
     * represent the board in String based manner and assign
     * values to each piece.
     */
    public enum PieceType {
        PAWN("P", 100),
        KNIGHT("N", 320),
        BISHOP("B", 330),
        ROOK("R", 500),
        QUEEN("Q", 900),
        KING("K", 20000);

        private String pieceName;
        private int pieceValue;

        PieceType(String pieceName, int pieceValue) {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        /**
         * @return value of a given piece type
         */
        public int getPieceValue() {
            return pieceValue;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }
    }
}
