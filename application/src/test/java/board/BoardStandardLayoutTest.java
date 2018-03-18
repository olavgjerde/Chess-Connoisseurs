package board;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that the state of the board corresponds to what is allowed when starting on
 * a normal chess board layout
 */
class BoardStandardLayoutTest {
    private static Board board;

    /**
     * Construct the standard chess layout
     */
    @BeforeAll
    static void setStandardBoard() {
        board = Board.createStandardBoard();
    }

    /**
     * Check the size of the current player's move list
     */
    @Test
    void currentPlayerMoveSize() {
        assertEquals(20, board.currentPlayer().getLegalMoves().size());
    }

    /**
     * Check the size of the current player's opponent move list
     */
    @Test
    void opponentPlayerMoveSize() {
        assertEquals(20, board.currentPlayer().getOpponent().getLegalMoves().size());
    }

    /**
     * Check the size of list of white pieces
     */
    @Test
    void allWhitePiecesSize() {
        assertEquals(16, board.getWhitePieces().size());
    }

    /**
     * Check the size of list of black pieces
     */
    @Test
    void allBlackPiecesSize() {
        assertEquals(16, board.getBlackPieces().size());
    }

    /**
     * Check the size of all moves available to the player at start
     */
    @Test
    void allPossibleMovesAtStartSize() {
        assertEquals(40, board.getAllLegalMoves().size());
    }

    /**
     * Check that the starting players are not in check
     */
    @Test
    void playersNotInCheck() {
        assertFalse(board.currentPlayer().isInCheck());
        assertFalse(board.currentPlayer().getOpponent().isInStalemate());
    }

    /**
     * Check that the starting players are not in checkmate
     */
    @Test
    void playersNotInCheckmate() {
        assertFalse(board.currentPlayer().isInCheckmate());
        assertFalse(board.currentPlayer().getOpponent().isInCheckmate());
    }

    /**
     * Check that the starting players are not castled
     */
    @Test
    void playersNotCastled() {
        assertFalse(board.currentPlayer().isCastled());
        assertFalse(board.currentPlayer().getOpponent().isCastled());
    }

    /**
     * Check that the starting players are not in a stalemate
     */
    @Test
    void playersNotInStalemate() {
        assertFalse(board.currentPlayer().isInStalemate());
        assertFalse(board.currentPlayer().getOpponent().isInStalemate());
    }

    /**
     * Check there have not been set a pawn open for an 'en passant' attack
     */
    @Test
    void noEnPassantPawnPresent() {
        assertNull(board.getEnPassantPawn());
    }
}