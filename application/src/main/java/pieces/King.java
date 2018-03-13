package pieces;

import board.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static board.Move.AttackMove;
import static board.Move.MajorMove;

public class King extends Piece {

    /**
     * List holds possible coordinates in relation to the piece's position where it may move.
     * NB: 2 and 2 integers represent x and y. [0] = x [1] = y etc.
     */
    private final static int[] POSSIBLE_MOVE_COORDINATES = {-1, 0, -1, -1, 0, -1, 1, -1, 1, 0, 1, 1, 0, 1, -1, 1};

    /**
     * Sets a piece's position and alliance
     *
     * @param pieceCoordinate position defined by an int
     * @param pieceAlliance   alliance of the piece
     */
    public King(Coordinate pieceCoordinate, Alliance pieceAlliance) {
        super(pieceCoordinate, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (int i = 0; i < POSSIBLE_MOVE_COORDINATES.length; i += 2) {
            int x = POSSIBLE_MOVE_COORDINATES[i];
            int y = POSSIBLE_MOVE_COORDINATES[i + 1];

            Coordinate possibleDestCoord = new Coordinate(this.pieceCoordinate.getX() + x, this.pieceCoordinate.getY() + y);

            final Tile possibleDestinationTile = board.getTile(possibleDestCoord);
            if (BoardUtils.isValidCoordinate(possibleDestCoord)) {
                if (possibleDestinationTile.isTileEmpty()) {
                    legalMoves.add(new MajorMove(board, this, possibleDestCoord));
                } else {
                    final Piece pieceAtDestination = possibleDestinationTile.getPiece();
                    if (this.pieceAlliance != pieceAtDestination.getPieceAlliance()) {
                        // enemy tile detected
                        legalMoves.add(new AttackMove(board, this, possibleDestCoord, pieceAtDestination));
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public String toString() {
        return PieceType.KING.toString();
    }
}
