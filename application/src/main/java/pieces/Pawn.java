package pieces;

import board.Board;
import board.BoardUtils;
import board.Coordinate;
import board.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static board.Move.*;
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
     * Constructor which defaults the Pieces isFirstMove variable to true
     */
    public Pawn(Coordinate pieceCoordinate, Alliance pieceAlliance) {
        super(pieceCoordinate, pieceAlliance, true, PieceType.PAWN);
    }

    /**
     * Constructor which allows the setting of isFirstMove variable
     */
    public Pawn(Coordinate pieceCoordinate, Alliance pieceAlliance, boolean isFirstMove) {
        super(pieceCoordinate, pieceAlliance, isFirstMove, PieceType.PAWN);
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
                if ((x == 0 && y == 1) && board.getTile(possibleDestCoord).isTileEmpty()) {
                    // if pawn is moving 1 step

                    // TODO: MORE LOGIC HERE | pawn promotion etc
                    legalMoves.add(new MajorMove(board, this, possibleDestCoord));

                } else if ((x == 0 && y == 2) && this.isFirstMove() &&
                          ((this.pieceCoordinate.getY() == 1 && this.getPieceAlliance() == Alliance.BLACK) ||
                          (this.pieceCoordinate.getY() == BoardUtils.getHeight()-2 && this.getPieceAlliance() == Alliance.WHITE))) {
                    // logic for a "pawn jump" for both directions

                    // check if tile at the jump destination is empty and if the tile in-between is empty
                    final int oneBehindY = this.pieceCoordinate.getY() + this.getPieceAlliance().getDirection();
                    final Coordinate behindPossibleDestCoord = new Coordinate(this.pieceCoordinate.getX() , oneBehindY);
                    if (board.getTile(behindPossibleDestCoord).isTileEmpty() && board.getTile(possibleDestCoord).isTileEmpty()) {
                        legalMoves.add(new PawnJump(board, this, possibleDestCoord));
                    }
                } else {
                    // logic for diagonal attack moves
                    if (!board.getTile(possibleDestCoord).isTileEmpty()) {
                        final Piece pieceAtDestination = board.getTile(possibleDestCoord).getPiece();
                        if (this.getPieceAlliance() != pieceAtDestination.getPieceAlliance()) {
                            // todo: fix attack into pawn promotion
                            legalMoves.add(new PawnAttackMove(board, this, possibleDestCoord, pieceAtDestination));
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false);
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }
}
