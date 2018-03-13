package pieces;

import board.Board;
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
    final boolean isFirstMove;

    /**
     * Sets a piece's position and alliance
     * @param pieceCoordinate position defined by an int
     * @param pieceAlliance alliance of the piece
     */
    public Piece(final Coordinate pieceCoordinate, final Alliance pieceAlliance) {
        this.pieceCoordinate = pieceCoordinate;
        this.pieceAlliance = pieceAlliance;

        //todo more stuff
        this.isFirstMove = false;
    }

    /**
     * Get the alliance of a piece object
     * @return enum Alliance
     */
    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }

    public Coordinate getPieceCoordinate() {
        return this.pieceCoordinate;
    }

    /**
     * Check if it is the piece's first move
     * @return true or false depending on if it is the first move of the piece
     */
    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    /**
     * Every class which implements this method shall calculate according to the rules defined by itself which moves
     * that are legal to do.
     * @param board on which the piece belongs
     * @return A list of possible moves that a piece can make
     */
    public abstract Collection<Move> calculateLegalMoves(final Board board);

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

    public enum PieceType {
        PAWN("P"),
        KNIGHT("N"),
        BISHOP("B"),
        ROOK("R"),
        QUEEN("Q"),
        KING("K");

        private String pieceName;

        PieceType(String pieceName) {
            this.pieceName = pieceName;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }
    }
}
