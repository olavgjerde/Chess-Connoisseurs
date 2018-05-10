package pieces;

import board.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static board.Move.*;

/**
 * Represents the chess-piece "Knight"
 * See more documentation in Piece-class.
 */
public class Knight extends Piece {

    /**
     * List holds possible coordinates in relation to the piece's position where it may move.
     * NB: 2 and 2 integers represent x and y. [0] = x [1] = y etc.
     */
    private final static int[] POSSIBLE_MOVE_COORDINATES = {-2, 1, -1, 2, 1, 2, 2, 1, -2, -1, -1, -2, 1, -2, 2, -1};

    /**
     * Constructor which defaults the Pieces isFirstMove variable to true
     */
    public Knight(Coordinate pieceCoordinate, Alliance pieceAlliance) {
        super(pieceCoordinate, pieceAlliance, true, PieceType.KNIGHT);
    }

    /**
     * Constructor which allows the setting of isFirstMove variable
     */
    public Knight(Coordinate pieceCoordinate, Alliance pieceAlliance, boolean isFirstMove) {
        super(pieceCoordinate, pieceAlliance, isFirstMove, PieceType.KNIGHT);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (int i = 0; i < POSSIBLE_MOVE_COORDINATES.length; i += 2) {
            int x = POSSIBLE_MOVE_COORDINATES[i];
            int y = POSSIBLE_MOVE_COORDINATES[i + 1];

            Coordinate possibleDestCoord = new Coordinate(this.pieceCoordinate.getX() + x, this.pieceCoordinate.getY() + y);
            if (BoardUtils.isValidCoordinate(possibleDestCoord)) {
                final Tile possibleDestinationTile = board.getTile(possibleDestCoord);
                if (possibleDestinationTile.isEmpty()) {
                    legalMoves.add(new MajorMove(board, this, possibleDestCoord));
                } else {
                    final Piece pieceAtDestination = possibleDestinationTile.getPiece();
                    if (this.pieceAlliance != pieceAtDestination.getPieceAlliance()) {
                        // enemy tile detected
                        legalMoves.add(new MajorAttackMove(board, this, possibleDestCoord, pieceAtDestination));
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Knight movePiece(Move move) {
        return new Knight(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false);
    }

    @Override
    public String toString() {
        return PieceType.KNIGHT.toString();
    }

    @Override
    public int locationValue(boolean isEndGame) {
        return this.pieceAlliance.knightSquareValue(this.pieceCoordinate);
    }
}
