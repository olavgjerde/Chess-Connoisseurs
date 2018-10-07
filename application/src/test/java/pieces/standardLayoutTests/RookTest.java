package pieces.standardLayoutTests;

import board.Board;
import board.BoardUtils;
import board.Move;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pieces.Alliance;
import pieces.Rook;

import java.util.Collection;

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
        assumeTrue(BoardUtils.getInstance().getWidth() == 8 && BoardUtils.getInstance().getHeight() == 8,
                "Board size not in bounds for the standard piece type logic");
    }

    /**
     * Check that the rook found all moves on an empty board
     */
    @Test
    void calculateLegalMovesOnOpenBoard() {
        Builder builder = new Builder();
        Rook rook = new Rook(BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), Alliance.BLACK);
        builder.setPiece(rook);
        builder.setMoveMaker(Alliance.BLACK);
        Board board = builder.build();
        Collection<Move> rookCalculatedMoves = board.getBlackPlayer().getLegalMoves();

        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e1"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e2"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e3"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e5"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e6"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e7"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e8"))));

        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("a4"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("b4"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("c4"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("d4"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("g4"))));
        assertTrue(rookCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("h4"))));
    }

    /**
     * Check that the rook is not allowed to move past an ally
     */
    @Test
    void rookStopsOnAllyEncounter() {
        Builder builder = new Builder();
        Rook rookInQuestion = new Rook(BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Rook rookAlly = new Rook(BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e5"), Alliance.WHITE);
        builder.setPiece(rookInQuestion);
        builder.setPiece(rookAlly);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        Move illegalMove = MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e6"));

        assertFalse(rookInQuestion.calculateLegalMoves(board).contains(illegalMove));
    }

    /**
     * Check that the rook is not allowed to move past an enemy
     */
    @Test
    void rookStopsOnEnemyEncounter() {
        Builder builder = new Builder();
        Rook rookInQuestion = new Rook(BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Rook rookEnemy = new Rook(BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e5"), Alliance.BLACK);
        builder.setPiece(rookInQuestion);
        builder.setPiece(rookEnemy);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        Move illegalMove = MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e6"));

        assertFalse(rookInQuestion.calculateLegalMoves(board).contains(illegalMove));
    }

    /**
     * Check that the rook generates an attack move when enemy is within reach
     */
    @Test
    void rookGeneratesAttackOnEnemyEncounter() {
        Builder builder = new Builder();
        Rook rookInQuestion = new Rook(BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Rook rookEnemy = new Rook(BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e5"), Alliance.BLACK);
        builder.setPiece(rookInQuestion);
        builder.setPiece(rookEnemy);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        Move attackMove = MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e5"));

        assertTrue(rookInQuestion.calculateLegalMoves(board).contains(attackMove));
    }
}