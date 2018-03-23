package pieces.standardLayoutTests;

import board.Board;
import board.BoardUtils;
import board.Move;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pieces.Alliance;
import pieces.Knight;

import java.util.List;

import static board.Board.*;
import static board.Move.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class KnightTest {

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
     * Check that the knight found all moves on an empty board
     */
    @Test
    void calculateLegalMovesOnOpenBoard() {
        Builder builder = new Builder();
        Knight knight = new Knight(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.BLACK);
        builder.setPiece(knight);
        builder.setMoveMaker(Alliance.BLACK);
        Board board = builder.build();
        List<Move> knightCalculatedMoves = (List<Move>) knight.calculateLegalMoves(board);

        // check that all 8 moves on the empty board were found
        assertEquals(8, knightCalculatedMoves.size());

        assertTrue(knightCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("d2"))));
        assertTrue(knightCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("c3"))));

        assertTrue(knightCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("c5"))));
        assertTrue(knightCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("d6"))));

        assertTrue(knightCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("f6"))));
        assertTrue(knightCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("g5"))));

        assertTrue(knightCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("g3"))));
        assertTrue(knightCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("f2"))));
    }

    /**
     * Check that the knight is not allowed to attack an ally
     */
    @Test
    void knightCanNotAttackAlly() {
        Builder builder = new Builder();
        Knight knightInQuestion = new Knight(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Knight knightEnemy = new Knight(BoardUtils.getCoordinateFromAlgebraicNotation("d2"), Alliance.WHITE);
        builder.setPiece(knightInQuestion);
        builder.setPiece(knightEnemy);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        Move illegalAttack = MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("d2"));

        assertFalse(knightInQuestion.calculateLegalMoves(board).contains(illegalAttack));
    }

    /**
     * Check that the knight is allowed to attack an enemy
     */
    @Test
    void knightGeneratesAttackOnEnemyEncounter() {
        Builder builder = new Builder();
        Knight knightInQuestion = new Knight(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Knight knightEnemy = new Knight(BoardUtils.getCoordinateFromAlgebraicNotation("d2"), Alliance.BLACK);
        builder.setPiece(knightInQuestion);
        builder.setPiece(knightEnemy);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        Move legalAttack = MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("d2"));

        assertTrue(knightInQuestion.calculateLegalMoves(board).contains(legalAttack));
    }

}