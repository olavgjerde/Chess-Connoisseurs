package pieces;

import board.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static board.Move.AttackMove;
import static board.Move.MajorMove;

/**
 * Represents the chess-piece "Queen"
 * See more documentation in Piece-class.
 */
public class Queen extends Piece {

    /**
     * List holds possible coordinates in relation to the piece's position where it may move.
     * NB: 2 and 2 integers represent x and y. [0] = x [1] = y etc.
     */
    private final static int[] POSSIBLE_MOVE_COORDINATES = {-1, 1, -1, -1, 1, -1, 1, 1, -1, 0, 0, -1, 0, 1, 1, 0};

    /**
     * Sets a piece's position and alliance
     *
     * @param pieceCoordinate position defined by an int
     * @param pieceAlliance   alliance of the piece
     */
    public Queen(Coordinate pieceCoordinate, Alliance pieceAlliance) {
        super(pieceCoordinate, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (int i = 0; i < POSSIBLE_MOVE_COORDINATES.length; i += 2) {
            int x = POSSIBLE_MOVE_COORDINATES[i];
            int y = POSSIBLE_MOVE_COORDINATES[i + 1];

            // calculate for the 2 horizontal, 2 vertical and 4 diagonal directions on the board
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
                        // path obstructed by enemy -> can't move beyond
                        break;
                    }
                }
                possibleDestCoord = new Coordinate(possibleDestCoord.getX() + x, possibleDestCoord.getY() + y);
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public String toString() {
        return PieceType.QUEEN.toString();
    }
}
