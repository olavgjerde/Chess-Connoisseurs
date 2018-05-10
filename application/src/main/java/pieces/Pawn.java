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
    private boolean lightBrigadeMode;

    /**
     * Constructor which defaults the Pieces isFirstMove variable to true
     * @param lightBrigadeMode restricts promotion availability if true
     */
    public Pawn(Coordinate pieceCoordinate, Alliance pieceAlliance, boolean lightBrigadeMode) {
        super(pieceCoordinate, pieceAlliance, true, PieceType.PAWN);
        this.lightBrigadeMode = lightBrigadeMode;
    }

    /**
     * Constructor which allows the setting of isFirstMove variable
     * @param lightBrigadeMode restricts promotion availability if true
     */
    public Pawn(Coordinate pieceCoordinate, Alliance pieceAlliance, boolean isFirstMove, boolean lightBrigadeMode) {
        super(pieceCoordinate, pieceAlliance, isFirstMove, PieceType.PAWN);
        this.lightBrigadeMode = lightBrigadeMode;
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        final Alliance thisAlliance = this.getPieceAlliance();
        final int thisX = this.pieceCoordinate.getX(), thisY = this.pieceCoordinate.getY();

        for (int i = 0; i < POSSIBLE_MOVE_COORDINATES.length; i += 2) {
            // piece alliance is used to determine if the piece is moving up or down the board
            final int relativeX = POSSIBLE_MOVE_COORDINATES[i];
            final int relativeY = POSSIBLE_MOVE_COORDINATES[i + 1];
            Coordinate destination = new Coordinate(thisX + (relativeX * thisAlliance.getDirection()),
                                                    thisY + (relativeY * thisAlliance.getDirection()));

            if (BoardUtils.isValidCoordinate(destination)) {
                final boolean promotionIsPossible = this.pieceAlliance.isPawnPromotionCoordinate(destination);
                final boolean destinationIsEmpty = board.getTile(destination).isEmpty();
                final Pawn enPassantPawn = board.getEnPassantPawn();

                if (relativeX == 0 && relativeY == 1) {
                    // 1 step
                    if (promotionIsPossible && destinationIsEmpty) {
                        //Light brigade mode restricts promotion availability
                        if (this.lightBrigadeMode && thisAlliance == Alliance.WHITE) {
                            legalMoves.add(new PawnPromotion(new PawnMove(board, this, destination), PieceType.QUEEN));
                        } else if (this.lightBrigadeMode && thisAlliance == Alliance.BLACK) {
                            legalMoves.add(new PawnPromotion(new PawnMove(board, this, destination), PieceType.KNIGHT));
                        } else {
                            legalMoves.add(new PawnPromotion(new PawnMove(board, this, destination), PieceType.QUEEN));
                            legalMoves.add(new PawnPromotion(new PawnMove(board, this, destination), PieceType.ROOK));
                            legalMoves.add(new PawnPromotion(new PawnMove(board, this, destination), PieceType.BISHOP));
                            legalMoves.add(new PawnPromotion(new PawnMove(board, this, destination), PieceType.KNIGHT));
                        }
                    } else if (destinationIsEmpty) {
                        legalMoves.add(new PawnMove(board, this, destination));
                    }
                } else if (relativeX == 0 && relativeY == 2) {
                    // 2 steps (jump)
                    if (((thisY == 1 && thisAlliance == Alliance.BLACK) || (thisY == BoardUtils.getHeight() - 2 && thisAlliance == Alliance.WHITE)) && this.isFirstMove()) {
                        final Coordinate inTheMiddle = new Coordinate(thisX, thisY + thisAlliance.getDirection());
                        if (board.getTile(inTheMiddle).isEmpty() && destinationIsEmpty) {
                            legalMoves.add(new PawnJump(board, this, destination));
                        }
                    }
                } else if ((relativeX == -1 && relativeY == 1) || ((relativeX == 1) && (relativeY == 1))) {
                    // 1 step diagonal attacks
                    if (!destinationIsEmpty) {
                        final Piece pieceAtDestination = board.getTile(destination).getPiece();
                        if (pieceAtDestination.getPieceAlliance() != thisAlliance) {
                            if (promotionIsPossible) {
                                //Light brigade mode restricts promotion availability
                                if (this.lightBrigadeMode && thisAlliance == Alliance.WHITE) {
                                    legalMoves.add(new PawnPromotion(new PawnMove(board, this, destination), PieceType.QUEEN));
                                } else if (this.lightBrigadeMode && thisAlliance == Alliance.BLACK) {
                                    legalMoves.add(new PawnPromotion(new PawnMove(board, this, destination), PieceType.KNIGHT));
                                } else {
                                    legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, destination, pieceAtDestination), PieceType.QUEEN));
                                    legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, destination, pieceAtDestination), PieceType.ROOK));
                                    legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, destination, pieceAtDestination), PieceType.BISHOP));
                                    legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, destination, pieceAtDestination), PieceType.KNIGHT));
                                }
                            } else {
                                legalMoves.add(new PawnAttackMove(board, this, destination, pieceAtDestination));
                            }
                        }
                    } else if (enPassantPawn != null) {
                        // logic for 'en passant' attack
                        final Coordinate passantCoordinate = enPassantPawn.pieceCoordinate;
                        final Alliance passantAlliance = enPassantPawn.pieceAlliance;
                        // NB: this section checks for a possible 'en passant' attacks in both directions for
                        // both alliances, hence the 'toOneSide' / 'toTheOtherSide' notation
                        if (relativeX == -1 && relativeY == 1) {
                            final Coordinate toOneSide = new Coordinate(thisX + thisAlliance.getOppositeDirection(), thisY);
                            if (thisAlliance != passantAlliance && passantCoordinate.equals(toOneSide)) {
                                legalMoves.add(new PawnEnPassantAttackMove(board, this, destination, enPassantPawn));
                            }
                        } else if (relativeX == 1 && relativeY == 1) {
                            final Coordinate toTheOtherSide = new Coordinate(thisX - thisAlliance.getOppositeDirection(), thisY);
                            if (thisAlliance != passantAlliance && passantCoordinate.equals(toTheOtherSide)) {
                                legalMoves.add(new PawnEnPassantAttackMove(board, this, destination, enPassantPawn));
                            }
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false, this.lightBrigadeMode);
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

    /**
     * Set to Queen by default for simplicity
     * @return the piece that this pawn will be promoted to
     */
    public Piece getPromotionPiece() {
        return new Queen(this.pieceCoordinate, this.pieceAlliance, false);
    }

    @Override
    public int locationValue(boolean isEndGame) {
        return this.pieceAlliance.pawnSquareValue(this.pieceCoordinate);
    }
}
