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
    private final boolean lightBrigadeMode;

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
        List<Move> allMoves = new ArrayList<>();

        Coordinate firstStep = this.goForward(1);
        if (BoardUtils.getInstance().isValidCoordinate(firstStep) && board.getTile(firstStep) != null && board.getTile(firstStep).isEmpty()) {
            allMoves = addNormalMoves(board, firstStep);
            // here we know that position inbetween jump is empty
            Coordinate secondStep = this.goForward(2);
            if (this.isFirstMove() && board.getTile(secondStep) != null && board.getTile(secondStep).isEmpty()) {
                allMoves.addAll(addNormalMoves(board, secondStep));
            }
        }

        Coordinate attack1 = new Coordinate(getPieceCoordinate().getX() - 1, goForward(1).getY());
        Coordinate attack2 = new Coordinate(getPieceCoordinate().getX() + 1, goForward(1).getY());
        if (BoardUtils.getInstance().isValidCoordinate(attack1)) allMoves.addAll(addAttackMoves(board, attack1));
        if (BoardUtils.getInstance().isValidCoordinate(attack2)) allMoves.addAll(addAttackMoves(board, attack2));

        return Collections.unmodifiableList(allMoves);
    }

    /**
     * Add all normal moves to a list
     * @param board to evaluate
     * @param destination of pawn
     * @return list with all possible normal moves added
     */
    private List<Move> addNormalMoves(Board board, Coordinate destination) {
        List<Move> moves = new ArrayList<>();

        if (this.pieceAlliance.isPawnPromotionCoordinate(destination)) {
            if (this.lightBrigadeMode && this.pieceAlliance == Alliance.WHITE) {
                moves.add(new PawnPromotion(new PawnMove(board, this, destination), PieceType.QUEEN));
            } else if (this.lightBrigadeMode && this.pieceAlliance == Alliance.BLACK) {
                moves.add(new PawnPromotion(new PawnMove(board, this, destination), PieceType.KNIGHT));
            } else {
                moves.add(new PawnPromotion(new PawnMove(board, this, destination), PieceType.QUEEN));
                moves.add(new PawnPromotion(new PawnMove(board, this, destination), PieceType.ROOK));
                moves.add(new PawnPromotion(new PawnMove(board, this, destination), PieceType.BISHOP));
                moves.add(new PawnPromotion(new PawnMove(board, this, destination), PieceType.KNIGHT));
            }
        } else if (Math.abs(this.getPieceCoordinate().getY() - destination.getY()) == 2) {
            moves.add(new PawnJump(board, this, destination));
        } else {
            moves.add(new PawnMove(board, this, destination));
        }

        return moves;
    }

    /**
     * Adds all available attack moves to a list
     * @param board to evaluate
     * @param destination of pawn
     * @return a list with all possible attack moves added
     */
    private List<Move> addAttackMoves(Board board, Coordinate destination) {
        List<Move> moves = new ArrayList<>();

        if (board.isEnemy(this, destination)) {
            Piece pieceAtDestination = board.getTile(destination).getPiece();
            if (this.pieceAlliance.isPawnPromotionCoordinate(destination)) {
                if (this.lightBrigadeMode && this.pieceAlliance == Alliance.WHITE) {
                    moves.add(new PawnPromotion(new PawnMove(board, this, destination), PieceType.QUEEN));
                } else if (this.lightBrigadeMode && this.pieceAlliance == Alliance.BLACK) {
                    moves.add(new PawnPromotion(new PawnMove(board, this, destination), PieceType.KNIGHT));
                } else {
                    moves.add(new PawnPromotion(new PawnAttackMove(board, this, destination, pieceAtDestination), PieceType.QUEEN));
                    moves.add(new PawnPromotion(new PawnAttackMove(board, this, destination, pieceAtDestination), PieceType.ROOK));
                    moves.add(new PawnPromotion(new PawnAttackMove(board, this, destination, pieceAtDestination), PieceType.BISHOP));
                    moves.add(new PawnPromotion(new PawnAttackMove(board, this, destination, pieceAtDestination), PieceType.KNIGHT));
                }
            }
            moves.add(new PawnAttackMove(board, this, destination, pieceAtDestination));
        } else if (board.getTile(destination).isEmpty() && board.getEnPassantPawn() != null) {
            Move ep = getEnPassantMove(board, destination);
            if (ep != null) moves.add(ep);
        }

        return moves;
    }

    /**
     * Adds avaiable EnPassant move to a list
     * @param board to evaluate
     * @param destination of pawn
     * @return en passant move, null if not present
     */
    private Move getEnPassantMove(Board board, Coordinate destination) {
        final Pawn enPassantPawn = board.getEnPassantPawn();
        final Coordinate passantCoordinate = board.getEnPassantPawn().pieceCoordinate;

        if (board.isEnemy(this, passantCoordinate) && destination.equals(enPassantPawn.goBackward(1))) {
            return new PawnEnPassantAttackMove(board, this, destination, board.getEnPassantPawn());
        }

        return null;
    }

    /**
     * Takes n steps forwards based on alliance of pawn (white goes north, black goes south)
     * @param nIncrements how many steps to move forward
     * @return new coordinate based on initial piece coordinate
     */
    private Coordinate goForward(int nIncrements) {
        if (this.pieceAlliance == Alliance.WHITE) return new Coordinate(getPieceCoordinate().getX(), getPieceCoordinate().getY() - nIncrements);
        return new Coordinate(getPieceCoordinate().getX(), getPieceCoordinate().getY() + nIncrements);
    }

    /**
     * Takes n steps backwards based on alliance of pawn (white goes south, black goes north)
     * @param nIncrements how many steps to move forward
     * @return new coordinate based on initial piece coordinate
     */
    private Coordinate goBackward(int nIncrements) {
        if (this.pieceAlliance == Alliance.WHITE) return new Coordinate(getPieceCoordinate().getX(), getPieceCoordinate().getY() + nIncrements);
        return new Coordinate(getPieceCoordinate().getX(), getPieceCoordinate().getY() - nIncrements);
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false, this.lightBrigadeMode);
    }

    @Override
    public int locationValue(boolean isEndGame) {
        return this.pieceAlliance.pawnSquareValue(this.pieceCoordinate);
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }
}