package pieces.standardLayoutTests;

import board.Board;
import board.BoardUtils;
import board.Move;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static board.Move.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import pieces.Alliance;
import pieces.Bishop;

import java.util.List;

import static board.Board.*;
import static org.junit.jupiter.api.Assertions.*;

class BishopTest {

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
     * Check that the bishop found all moves on an empty board
     */
    @Test
    void calculateLegalMovesOnOpenBoard() {
        Builder builder = new Builder();
        Bishop bishop = new Bishop(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.BLACK);
        builder.setPiece(bishop);
        builder.setMoveMaker(Alliance.BLACK);
        Board board = builder.build();
        List<Move> bishopCalculatedMoves = (List<Move>) bishop.calculateLegalMoves(board);

        // checking both diagonals
        assertTrue(bishopCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("d3"))));
        assertTrue(bishopCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("c2"))));
        assertTrue(bishopCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("b1"))));
        assertTrue(bishopCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("f5"))));
        assertTrue(bishopCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("g6"))));
        assertTrue(bishopCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("h7"))));

        assertTrue(bishopCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("f3"))));
        assertTrue(bishopCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("g2"))));
        assertTrue(bishopCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("h1"))));
        assertTrue(bishopCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("d5"))));
        assertTrue(bishopCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("c6"))));
        assertTrue(bishopCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("b7"))));
        assertTrue(bishopCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("a8"))));

    }

    /**
     * Check that the bishop is not allowed to move past an ally
     */
    @Test
    void bishopStopsOnAllyEncounter() {
        Builder builder = new Builder();
        Bishop bishopInQuestion = new Bishop(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Bishop bishopAlly = new Bishop(BoardUtils.getCoordinateFromAlgebraicNotation("f5"), Alliance.WHITE);
        builder.setPiece(bishopInQuestion);
        builder.setPiece(bishopAlly);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        Move illegalMove = MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("g6"));

        assertFalse(bishopInQuestion.calculateLegalMoves(board).contains(illegalMove));
    }

    /**
     * Check that the bishop is not allowed to move past an enemy piece
     */
    @Test
    void bishopStopsOnEnemyEncounter() {
        Builder builder = new Builder();
        Bishop bishopInQuestion = new Bishop(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Bishop bishopEnemy = new Bishop(BoardUtils.getCoordinateFromAlgebraicNotation("f5"), Alliance.BLACK);
        builder.setPiece(bishopInQuestion);
        builder.setPiece(bishopEnemy);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        Move illegalMove = MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("g6"));

        assertFalse(bishopInQuestion.calculateLegalMoves(board).contains(illegalMove));
    }

    /**
     * Check that the bishop generates an attack move when enemy is within reach
     */
    @Test
    void bishopGeneratesAttackOnEnemyEncounter() {
        Builder builder = new Builder();
        Bishop bishopInQuestion = new Bishop(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Bishop bishopEnemy = new Bishop(BoardUtils.getCoordinateFromAlgebraicNotation("f5"), Alliance.BLACK);
        builder.setPiece(bishopInQuestion);
        builder.setPiece(bishopEnemy);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        Move attackMove = MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("f5"));

        assertTrue(bishopInQuestion.calculateLegalMoves(board).contains(attackMove));
    }
}