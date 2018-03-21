package pieces.standardLayoutTests;

import board.Board;
import board.BoardUtils;
import board.Move;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pieces.Alliance;
import pieces.Pawn;
import pieces.Rook;
import player.MoveTransition;

import java.util.List;

import static board.Board.*;
import static board.Move.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class PawnTest {

    /**
     * Check that the BoardUtils board dimension are set to 8x8 ->
     * because the standard pieces calculate their moves-set with this dimension in mind
     */
    @BeforeAll
    static void checkEightTimesEightSize() {
        assumeTrue(BoardUtils.getWidth() == 8 && BoardUtils.getHeight() == 8,
                "Board size not in bounds for the standard piece type logic");
    }

    /**
     * Check if the black alliance pawn is able to calculate 1 step 'forward'
     */
    @Test
    void blackPawnCalculateLegalMovesOnOpenBoard() {
        Builder builder = new Builder();
        Pawn blackPawn = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.BLACK, false);
        builder.setPiece(blackPawn);
        builder.setMoveMaker(Alliance.BLACK);
        Board board = builder.build();
        List<Move> pawnCalculatedMoves = (List<Move>) blackPawn.calculateLegalMoves(board);

        assertTrue(pawnCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("e3"))));
    }

    /**
     * Check if the white alliance pawn is able to calculate 1 step 'forward'
     */
    @Test
    void whitePawnCalculateLegalMovesOnOpenBoard() {
        Builder builder = new Builder();
        Pawn whitePawn = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE, false);
        builder.setPiece(whitePawn);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();
        List<Move> pawnCalculatedMoves = (List<Move>) whitePawn.calculateLegalMoves(board);

        assertTrue(pawnCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("e5"))));
    }

    /**
     * Check if the black alliance pawn is able to calculate a jump when is has not moved earlier
     */
    @Test
    void blackPawnJump() {
        Builder builder = new Builder();
        Pawn blackPawn = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("e7"), Alliance.BLACK);
        builder.setPiece(blackPawn);
        builder.setMoveMaker(Alliance.BLACK);
        Board board = builder.build();
        List<Move> pawnCalculatedMoves = (List<Move>) blackPawn.calculateLegalMoves(board);

        assertTrue(pawnCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e7"), BoardUtils.getCoordinateFromAlgebraicNotation("e5"))));
    }

    /**
     * Check if the white alliance pawn is able to calculate a jump when is has not moved earlier
     */
    @Test
    void whitePawnJump() {
        Builder builder = new Builder();
        Pawn whitePawn = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("e2"), Alliance.WHITE);
        builder.setPiece(whitePawn);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();
        List<Move> pawnCalculatedMoves = (List<Move>) whitePawn.calculateLegalMoves(board);

        assertTrue(pawnCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e2"), BoardUtils.getCoordinateFromAlgebraicNotation("e4"))));
    }

    /**
     * Check if the black alliance pawn is able to calculate attacks into both it's diagonals
     */
    @Test
    void blackPawnAttack() {
        Builder builder = new Builder();
        Pawn blackPawn = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.BLACK, false);
        Pawn whitePawnOne = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("d3"), Alliance.WHITE);
        Pawn whitePawnTwo = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("f3"), Alliance.WHITE);
        builder.setPiece(blackPawn);
        builder.setPiece(whitePawnOne);
        builder.setPiece(whitePawnTwo);
        builder.setMoveMaker(Alliance.BLACK);
        Board board = builder.build();
        List<Move> pawnCalculatedMoves = (List<Move>) blackPawn.calculateLegalMoves(board);

        assertTrue(pawnCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("d3"))));
        assertTrue(pawnCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("f3"))));
    }

    /**
     * Check if the white alliance pawn is able to calculate attacks into both it's diagonals
     */
    @Test
    void whitePawnAttack() {
        Builder builder = new Builder();
        Pawn whitePawn = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE, false);
        Pawn blackPawnOne = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("d5"), Alliance.BLACK);
        Pawn blackPawnTwo = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("f5"), Alliance.BLACK);
        builder.setPiece(whitePawn);
        builder.setPiece(blackPawnOne);
        builder.setPiece(blackPawnTwo);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();
        List<Move> pawnCalculatedMoves = (List<Move>) whitePawn.calculateLegalMoves(board);

        assertTrue(pawnCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("d5"))));
        assertTrue(pawnCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("f5"))));
    }

    /**
     * Check if the black alliance pawn is able to calculate an attack 'behind' an enemy pawn that has just made a jump
     */
    @Test
    void blackPawnCanAttackEnPassant() {
        Builder builder = new Builder();
        Pawn blackPawn = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("f4"), Alliance.BLACK, false);
        Pawn whitePawnOne = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("e2"), Alliance.WHITE);
        builder.setPiece(blackPawn);
        builder.setPiece(whitePawnOne);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        // white pawn opens for 'en passant' attack
        Move pawnJump = MoveFactory.createMove(board, BoardUtils.getCoordinateFromAlgebraicNotation("e2"), BoardUtils.getCoordinateFromAlgebraicNotation("e4"));
        MoveTransition pawnMadeJump = board.currentPlayer().makeMove(pawnJump);
        assertTrue(pawnMadeJump.getMoveStatus().isDone());
        board = pawnMadeJump.getTransitionBoard();

        List<Move> pawnCalculatedMoves = (List<Move>) blackPawn.calculateLegalMoves(board);
        // black pawn attacks 'behind' white pawn who made the jump
        assertTrue(pawnCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("f4"), BoardUtils.getCoordinateFromAlgebraicNotation("e3"))));
    }

    /**
     * Check if the white alliance pawn is able to calculate an attack 'behind' an enemy pawn that has just made a jump
     */
    @Test
    void whitePawnCanAttackEnPassant() {
        Builder builder = new Builder();
        Pawn whitePawn = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("f5"), Alliance.WHITE, false);
        Pawn blackPawnOne = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("e7"), Alliance.BLACK);
        builder.setPiece(whitePawn);
        builder.setPiece(blackPawnOne);
        builder.setMoveMaker(Alliance.BLACK);
        Board board = builder.build();

        // black pawn makes opens for 'en passant' attack
        Move pawnJump = MoveFactory.createMove(board, BoardUtils.getCoordinateFromAlgebraicNotation("e7"), BoardUtils.getCoordinateFromAlgebraicNotation("e5"));
        MoveTransition pawnMadeJump = board.currentPlayer().makeMove(pawnJump);
        assertTrue(pawnMadeJump.getMoveStatus().isDone());
        board = pawnMadeJump.getTransitionBoard();

        List<Move> pawnCalculatedMoves = (List<Move>) whitePawn.calculateLegalMoves(board);
        // white pawn attacks 'behind' black pawn who made the jump
        assertTrue(pawnCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("f5"), BoardUtils.getCoordinateFromAlgebraicNotation("e6"))));
    }

    /**
     * Check if the black alliance pawn is able to generate a regular pawn promotion move
     */
    @Test
    void blackPawnPromotion() {
        Builder builder = new Builder();
        Pawn blackPawn = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("e2"), Alliance.BLACK);
        builder.setPiece(blackPawn);
        builder.setMoveMaker(Alliance.BLACK);
        Board board = builder.build();

        PawnPromotion estimatedPawnPromotion = new PawnPromotion(new PawnMove(board, blackPawn, BoardUtils.getCoordinateFromAlgebraicNotation("e1")));

        assertTrue(blackPawn.calculateLegalMoves(board).contains(estimatedPawnPromotion));
    }

    /**
     * Check if the white alliance pawn is able to generate a regular pawn promotion move
     */
    @Test
    void whitePawnPromotion() {
        Builder builder = new Builder();
        Pawn whitePawn = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("e7"), Alliance.WHITE);
        builder.setPiece(whitePawn);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        PawnPromotion estimatedPawnPromotion = new PawnPromotion(new PawnMove(board, whitePawn, BoardUtils.getCoordinateFromAlgebraicNotation("e8")));

        assertTrue(whitePawn.calculateLegalMoves(board).contains(estimatedPawnPromotion));
    }

    /**
     * Check if the black alliance pawn is able to generate a pawn promotion move when attacking into a tile
     * where promotion is available
     */
    @Test
    void blackPawnAttackIntoPromotion() {
        Builder builder = new Builder();
        Pawn blackPawn = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("e2"), Alliance.BLACK, false);
        Rook whiteRook = new Rook(BoardUtils.getCoordinateFromAlgebraicNotation("f1"), Alliance.WHITE, false);
        builder.setPiece(blackPawn);
        builder.setPiece(whiteRook);
        builder.setMoveMaker(Alliance.BLACK);
        Board board = builder.build();

        PawnPromotion estimatedPawnPromotion = new PawnPromotion(new PawnAttackMove(board, blackPawn, BoardUtils.getCoordinateFromAlgebraicNotation("f1"), whiteRook));

        assertTrue(blackPawn.calculateLegalMoves(board).contains(estimatedPawnPromotion));
    }

    /**
     * Check if the white alliance pawn is able to generate a pawn promotion move when attacking into a tile
     * where promotion is available
     */
    @Test
    void whitePawnAttackIntoPromotion() {
        Builder builder = new Builder();
        Pawn whitePawn = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("e7"), Alliance.WHITE, false);
        Rook blackRook = new Rook(BoardUtils.getCoordinateFromAlgebraicNotation("f8"), Alliance.BLACK, false);
        builder.setPiece(whitePawn);
        builder.setPiece(blackRook);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        PawnPromotion estimatedPawnPromotion = new PawnPromotion(new PawnAttackMove(board, whitePawn, BoardUtils.getCoordinateFromAlgebraicNotation("f8"), blackRook));

        assertTrue(whitePawn.calculateLegalMoves(board).contains(estimatedPawnPromotion));
    }

}