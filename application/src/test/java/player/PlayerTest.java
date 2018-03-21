package player;

import board.Board;
import board.BoardUtils;
import board.Move;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    /**
     * Check that a player can be set in checkmate, this accounts for both BlackPlayer and WhitePlayer since they
     * extend the Player-class
     */
    @Test
    void playerIsInCheckmate() {
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

        final Move blackQueenToE5 = Move.MoveFactory.createMove(board, BoardUtils.getCoordinateFromAlgebraicNotation("d8"),
                BoardUtils.getCoordinateFromAlgebraicNotation("h4"));
        final MoveTransition trans4 = board.currentPlayer().makeMove(blackQueenToE5);
        assertTrue(trans4.getMoveStatus().isDone());
        board = trans4.getTransitionBoard();

        assertTrue(board.getWhitePlayer().isInCheckmate());
    }

}