package pieces;

import board.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static board.Move.*;

/**
 * Represents the chess-piece "Rook"
 * See more documentation in Piece-class.
 */
public class Rook extends Piece {

    /**
     * List holds possible coordinates in relation to the piece's position where it may move.
     * NB: 2 and 2 integers represent x and y. [0] = x [1] = y etc.
     */
    private final static int[] POSSIBLE_MOVE_COORDINATES = {-1, 0, 0, -1, 0, 1, 1, 0};

    /**
     * Constructor which defaults the Pieces isFirstMove variable to true
     */
    public Rook(Coordinate pieceCoordinate, Alliance pieceAlliance) {
        super(pieceCoordinate, pieceAlliance, true, PieceType.ROOK);
    }

    /**
     * Constructor which allows the setting of isFirstMove variable
     */
    public Rook(Coordinate pieceCoordinate, Alliance pieceAlliance, boolean isFirstMove) {
        super(pieceCoordinate, pieceAlliance, isFirstMove, PieceType.ROOK);
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
    public Rook movePiece(Move move) {
        return new Rook(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false);
    }

    @Override
    public int locationValue(boolean isEndGame) {
        return this.pieceAlliance.rookSquareValue(this.pieceCoordinate);
    }

    @Override
    public String toString() {
        return PieceType.ROOK.toString();
    }
}
