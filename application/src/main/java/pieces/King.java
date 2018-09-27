package pieces;

import board.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static board.Move.*;

public class King extends Piece {

    /**
     * List holds possible coordinates in relation to the piece's position where it may move.
     * NB: 2 and 2 integers represent x and y. [0] = x [1] = y etc.
     */
    private final static int[] POSSIBLE_MOVE_COORDINATES = {-1, 0, -1, -1, 0, -1, 1, -1, 1, 0, 1, 1, 0, 1, -1, 1};
    private final boolean isCastled;

    /**
     * Constructor which defaults the Pieces isFirstMove variable to true
     */
    public King(Coordinate pieceCoordinate, Alliance pieceAlliance) {
        super(pieceCoordinate, pieceAlliance, true, PieceType.KING);
        this.isCastled = false;
    }

    /**
     * Constructor which allows the setting of isFirstMove and isCastled variable
     */
    public King(Coordinate pieceCoordinate, Alliance pieceAlliance, boolean isFirstMove, boolean isCastled) {
        super(pieceCoordinate, pieceAlliance, isFirstMove, PieceType.KING);
        this.isCastled = isCastled;
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        List<Move> allMoves = new ArrayList<>();
        for (int i = 0; i < POSSIBLE_MOVE_COORDINATES.length; i += 2) {
            allMoves.addAll(travelInDirection(POSSIBLE_MOVE_COORDINATES[i], POSSIBLE_MOVE_COORDINATES[i+1], board, 1));
        }

        return Collections.unmodifiableList(allMoves);
    }

    @Override
    public King movePiece(Move move) {
        return new King(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false, move.isCastlingMove());
    }

    @Override
    public String toString() {
        return PieceType.KING.toString();
    }

    @Override
    public int locationValue(boolean isEndGame) {
        return this.pieceAlliance.kingSquareValue(this.pieceCoordinate, isEndGame);
    }

    public boolean isCastled() {
        return this.isCastled;
    }
}
