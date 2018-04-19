package player.basicAI;

import board.Board;
import board.BoardUtils;
import board.Move;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import player.MoveTransition;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests regarding the AI's behaviour
 */
class StandardLayoutAITest {

    /**
     * Check that the BoardUtils board dimension are set to 8x8 ->
     * because these tests are written with the standard chess board layout in mind
     */
    @BeforeAll
    static void checkEightTimesEightSize() {
        assumeTrue(BoardUtils.getWidth() == 8 && BoardUtils.getHeight() == 8,
                "Board size not in bounds for the standard layout AI logic");
    }

    /**
     * Check that the AI is able to recognize the move for checkmate given a fool's mate scenario
     * @see <a href="https://en.wikipedia.org/wiki/Fool's_mate">https://en.wikipedia.org</a>
     */
    @Test
    void foolsMateTest() {
        Board board = Board.createStandardBoard();

        final Move whitePawnToF3 = Move.MoveFactory.createMove(board, BoardUtils.getCoordinateFromAlgebraicNotation("f2"),
                                                                      BoardUtils.getCoordinateFromAlgebraicNotation("f3"));
        final MoveTransition trans1 = board.currentPlayer().makeMove(whitePawnToF3);
        assertTrue(trans1.getMoveStatus().isDone());
        board = trans1.getTransitionBoard();

        final Move blackPawnToE5 = Move.MoveFactory.createMove(board, BoardUtils.getCoordinateFromAlgebraicNotation("e7"),
                                                                      BoardUtils.getCoordinateFromAlgebraicNotation("e5"));
        final MoveTransition trans2 = board.currentPlayer().makeMove(blackPawnToE5);
        assertTrue(trans2.getMoveStatus().isDone());
        board = trans2.getTransitionBoard();

        final Move whitePawnToG4 = Move.MoveFactory.createMove(board, BoardUtils.getCoordinateFromAlgebraicNotation("g2"),
                                                                      BoardUtils.getCoordinateFromAlgebraicNotation("g4"));
        final MoveTransition trans3 = board.currentPlayer().makeMove(whitePawnToG4);
        assertTrue(trans3.getMoveStatus().isDone());
        board = trans3.getTransitionBoard();

        final MoveStrategy moveStrategy = new MiniMax(3, 5000, false, false);
        final Move AIMove = moveStrategy.execute(board);
        final Move bestMove = Move.MoveFactory.createMove(board, BoardUtils.getCoordinateFromAlgebraicNotation("d8"),
                                                                 BoardUtils.getCoordinateFromAlgebraicNotation("h4"));

        assertEquals(bestMove, AIMove);
    }

}