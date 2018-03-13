package pieces;

import board.Board;
import board.BoardUtils;
import board.Coordinate;
import board.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static board.Move.AttackMove;
import static board.Move.MajorMove;

/**
 * Represents the chess-piece "Pawn"
 * See more documentation in Piece-class.
 */
public class Pawn extends Piece {

    /**
     * List holds possible coordinates in relation to the piece's position where it may move.
     * NB: 2 and 2 integers represent x and y. [0] = x [1] = y etc.
     */
    private final static int[] POSSIBLE_MOVE_COORDINATES = {0, 1, 0, 2, -1, 1, 1, 1};

    /**
     * Sets a piece's position and alliance
     *
     * @param pieceCoordinate position defined by an int
     * @param pieceAlliance   alliance of the piece
     */
    public Pawn(Coordinate pieceCoordinate, Alliance pieceAlliance) {
        super(pieceCoordinate, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (int i = 0; i < POSSIBLE_MOVE_COORDINATES.length; i += 2) {
            // piece alliance is used to determine if the piece is moving up or down the board
            int x = POSSIBLE_MOVE_COORDINATES[i];
            int y = POSSIBLE_MOVE_COORDINATES[i + 1];

            Coordinate possibleDestCoord = new Coordinate(this.pieceCoordinate.getX() + x * this.getPieceAlliance().getDirection(),
                                                          this.pieceCoordinate.getY() + y * this.getPieceAlliance().getDirection());

            if (BoardUtils.isValidCoordinate(possibleDestCoord)) {
                // if pawn is moving 1 step
                if ((x == 0 && y == 1) && board.getTile(possibleDestCoord).isTileEmpty()) {
                    // TODO: MORE LOGIC HERE | pawn promotion etc
                    legalMoves.add(new MajorMove(board, this, possibleDestCoord));
                // logic for a "pawn jump" for both directions
                } else if ((x == 0 && y == 2) && this.isFirstMove() &&
                          (this.pieceCoordinate.getY() == 1 && this.getPieceAlliance() == Alliance.BLACK) ||
                          (this.pieceCoordinate.getY() == BoardUtils.getHeight()-2 && this.getPieceAlliance() == Alliance.WHITE)) {

                    // check if tile at the jump destination is empty and if the tile in-between is empty
                    final int oneBehindY = this.pieceCoordinate.getY() + this.getPieceAlliance().getDirection();
                    final Coordinate behindPossibleDestCoord = new Coordinate(this.pieceCoordinate.getX() , oneBehindY);
                    if (board.getTile(behindPossibleDestCoord).isTileEmpty() && board.getTile(possibleDestCoord).isTileEmpty()) {
                        legalMoves.add(new MajorMove(board, this, possibleDestCoord));
                    }
                // logic for diagonal attack moves
                } else {
                    if (!board.getTile(possibleDestCoord).isTileEmpty()) {
                        final Piece pieceAtDestination = board.getTile(possibleDestCoord).getPiece();
                        if (this.getPieceAlliance() != pieceAtDestination.getPieceAlliance()) {
                            // todo: fix attack into pawn promotion
                            legalMoves.add(new AttackMove(board, this, possibleDestCoord, pieceAtDestination));
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }
}
