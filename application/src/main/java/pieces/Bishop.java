package pieces;

import board.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static board.Move.AttackMove;
import static board.Move.MajorMove;

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
        final List<Move> legalMoves = new ArrayList<>();

        for (int i = 0; i < POSSIBLE_MOVE_COORDINATES.length; i += 2) {
            int x = POSSIBLE_MOVE_COORDINATES[i];
            int y = POSSIBLE_MOVE_COORDINATES[i + 1];

            // calculate for the 4 diagonal directions on the board
            Coordinate possibleDestCoord = new Coordinate(this.pieceCoordinate.getX() + x, this.pieceCoordinate.getY() + y);
            while (BoardUtils.isValidCoordinate(possibleDestCoord)) {
                final Tile possibleDestinationTile = board.getTile(possibleDestCoord);

                if (possibleDestinationTile.isTileEmpty()) {
                    legalMoves.add(new MajorMove(board, this, possibleDestCoord));
                } else {
                    final Piece pieceAtDestination = possibleDestinationTile.getPiece();
                    if (this.pieceAlliance != pieceAtDestination.getPieceAlliance()) {
                        // enemy tile detected
                        legalMoves.add(new AttackMove(board, this, possibleDestCoord, pieceAtDestination));
                    }
                    // path obstructed -> can't move beyond
                    break;
                }
                possibleDestCoord = new Coordinate(possibleDestCoord.getX() + x, possibleDestCoord.getY() + y);
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Bishop movePiece(Move move) {
        return new Bishop(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false);
    }

    @Override
    public String toString() {
        return PieceType.BISHOP.toString();
    }
}
