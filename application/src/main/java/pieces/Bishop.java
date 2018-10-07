package pieces;

import board.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents the chess-piece "Bishop"
 * See more documentation in Piece-class.
 */
public class Bishop extends Piece {

    /**
     * List holds possible coordinates in relation to the piece's position where it may move.
     * NB: 2 and 2 integers represent x and y. [0] = x [1] = y etc.
     */
    private final static int[] POSSIBLE_MOVE_COORDINATES = {-1, 1, -1, -1, 1, -1, 1, 1};

    /**
     * Constructor which defaults the Pieces isFirstMove variable to true
     */
    public Bishop(Coordinate pieceCoordinate, Alliance pieceAlliance) {
        super(pieceCoordinate, pieceAlliance, true, PieceType.BISHOP);
    }

    /**
     * Constructor which allows the setting of isFirstMove variable
     */
    public Bishop(Coordinate pieceCoordinate, Alliance pieceAlliance, boolean isFirstMove) {
        super(pieceCoordinate, pieceAlliance, isFirstMove, PieceType.BISHOP);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        List<Move> allMoves = new ArrayList<>();
        for (int i = 0; i < POSSIBLE_MOVE_COORDINATES.length; i += 2) {
            allMoves.addAll(travelInDirection(POSSIBLE_MOVE_COORDINATES[i], POSSIBLE_MOVE_COORDINATES[i+1], board, -1));
        }

        return Collections.unmodifiableList(allMoves);
    }

    @Override
    public Bishop movePiece(Move move) {
        return new Bishop(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false);
    }

    @Override
    public int locationValue(boolean isEndGame) {
        return this.pieceAlliance.bishopSquareValue(this.pieceCoordinate);
    }

    @Override
    public String toString() {
        return PieceType.BISHOP.toString();
    }
}
