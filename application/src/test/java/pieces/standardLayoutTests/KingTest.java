package pieces.standardLayoutTests;

import board.Board;
import board.BoardUtils;
import board.Move;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pieces.Alliance;
import pieces.King;
import pieces.Pawn;

import java.util.List;

import static board.Board.*;
import static board.Move.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class KingTest {

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
     * Check that the king found all moves on an empty board
     */
    @Test
    void calculateLegalMovesOnOpenBoard() {
        Builder builder = new Builder();
        King king = new King(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.BLACK);
        builder.setPiece(king);
        builder.setMoveMaker(Alliance.BLACK);
        Board board = builder.build();
        List<Move> kingCalculatedMoves = (List<Move>) king.calculateLegalMoves(board);

        assertTrue(kingCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("d3"))));
        assertTrue(kingCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("d4"))));
        assertTrue(kingCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("d5"))));

        assertTrue(kingCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("e3"))));
        assertTrue(kingCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("e5"))));

        assertTrue(kingCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("f3"))));
        assertTrue(kingCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("f4"))));
        assertTrue(kingCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("f5"))));
    }

    /**
     * Check that the king is not allowed to attack an ally
     */
    @Test
    void kingCanNotAttackAlly() {
        Builder builder = new Builder();
        King kingInQuestion = new King(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Pawn pawnAlly = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("e5"), Alliance.WHITE);
        builder.setPiece(kingInQuestion);
        builder.setPiece(pawnAlly);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        Move illegalMove = MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("e5"));

        assertFalse(kingInQuestion.calculateLegalMoves(board).contains(illegalMove));
    }

    /**
     * Check that the king is not allowed to attack an enemy
     */
    @Test
    void kingCanAttackEnemy() {
        Builder builder = new Builder();
        King kingInQuestion = new King(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Pawn pawnEnemy = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("e5"), Alliance.BLACK);
        builder.setPiece(kingInQuestion);
        builder.setPiece(pawnEnemy);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        Move attackMove = MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("e5"));

        assertTrue(kingInQuestion.calculateLegalMoves(board).contains(attackMove));
    }
}