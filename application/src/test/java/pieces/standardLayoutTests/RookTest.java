package pieces.standardLayoutTests;

import board.Board;
import board.BoardUtils;
import board.Move;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pieces.Alliance;
import pieces.Rook;

import java.util.Collection;
import java.util.List;

import static board.Board.*;
import static board.Move.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class RookTest {

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
     * Check that the rook found all moves on an empty board
     */
    @Test
    void calculateLegalMovesOnOpenBoard() {
        Builder builder = new Builder();
        Rook rook = new Rook(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.BLACK);
        builder.setPiece(rook);
        builder.setMoveMaker(Alliance.BLACK);
        Board board = builder.build();
        Collection<Move> rookCalculatedMoves = board.getBlackPlayer().getLegalMoves();

        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("e1"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("e2"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("e3"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("e5"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("e6"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("e7"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("e8"))));

        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("a4"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("b4"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("c4"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("d4"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("g4"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("h4"))));
    }

    /**
     * Check that the rook is not allowed to move past an ally
     */
    @Test
    void rookStopsOnAllyEncounter() {
        Builder builder = new Builder();
        Rook rookInQuestion = new Rook(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Rook rookAlly = new Rook(BoardUtils.getCoordinateFromAlgebraicNotation("e5"), Alliance.WHITE);
        builder.setPiece(rookInQuestion);
        builder.setPiece(rookAlly);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        Move illegalMove = MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("e6"));

        assertFalse(rookInQuestion.calculateLegalMoves(board).contains(illegalMove));
    }

    /**
     * Check that the rook is not allowed to move past an enemy
     */
    @Test
    void rookStopsOnEnemyEncounter() {
        Builder builder = new Builder();
        Rook rookInQuestion = new Rook(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Rook rookEnemy = new Rook(BoardUtils.getCoordinateFromAlgebraicNotation("e5"), Alliance.BLACK);
        builder.setPiece(rookInQuestion);
        builder.setPiece(rookEnemy);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        Move illegalMove = MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("e6"));

        assertFalse(rookInQuestion.calculateLegalMoves(board).contains(illegalMove));
    }

    /**
     * Check that the rook generates an attack move when enemy is within reach
     */
    @Test
    void rookGeneratesAttackOnEnemyEncounter() {
        Builder builder = new Builder();
        Rook rookInQuestion = new Rook(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Rook rookEnemy = new Rook(BoardUtils.getCoordinateFromAlgebraicNotation("e5"), Alliance.BLACK);
        builder.setPiece(rookInQuestion);
        builder.setPiece(rookEnemy);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        Move attackMove = MoveFactory.createMove(board,
                BoardUtils.getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getCoordinateFromAlgebraicNotation("e5"));

        assertTrue(rookInQuestion.calculateLegalMoves(board).contains(attackMove));
    }
}