package player;

import board.Board;
import board.BoardUtils;
import board.Move;
import org.junit.jupiter.api.Test;
import pieces.Alliance;
import pieces.King;
import pieces.Pawn;
import pieces.Rook;

import static board.Board.*;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    /**
     * Check that a player can be set in checkmate, this accounts for both BlackPlayer and WhitePlayer since they
     * extend the Player-class
     */
    @Test
    void playerIsInCheckmate() {
        Board board = createStandardBoard();

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

    /**
     * Check that the player is not allowed to move into check (aka attacking-zone of enemy piece)
     */
    @Test
    void doesNotMoveIntoCheck() {
        Builder builder = new Builder();
        King whiteKing = new King(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Pawn blackPawn = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("e6"), Alliance.BLACK);
        builder.setPiece(whiteKing);
        builder.setPiece(blackPawn);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        Move illegalMove = new Move.MajorMove(board, whiteKing, BoardUtils.getCoordinateFromAlgebraicNotation("d5"));
        assertFalse(board.currentPlayer().makeMove(illegalMove).getMoveStatus().isDone());
    }

    /**
     * Check that when there are kings on the board, the player class can return them correctly
     */
    @Test
    void kingsOnBoardAreFound() {
        Builder builder = new Builder();
        King whiteKing = new King(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        King blackKing = new King(BoardUtils.getCoordinateFromAlgebraicNotation("e6"), Alliance.BLACK);
        builder.setPiece(whiteKing);
        builder.setPiece(blackKing);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        assertEquals(whiteKing, board.getWhitePlayer().getPlayerKing());
        assertEquals(blackKing, board.getBlackPlayer().getPlayerKing());
    }

    /**
     * Check that the calculateAttacksOnCoordinate method in player is able to find the possible attacks
     */
    @Test
    void attackMovesAreFound() {
        Builder builder = new Builder();
        King whiteKing = new King(BoardUtils.getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Pawn blackPawn = new Pawn(BoardUtils.getCoordinateFromAlgebraicNotation("d5"), Alliance.BLACK);
        builder.setPiece(whiteKing);
        builder.setPiece(blackPawn);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        // there should be one available attack on white king from black pawn
        assertEquals(1, Player.calculateAttacksOnCoordinate(whiteKing.getPieceCoordinate(), board.getAllLegalMoves()).size());
    }

    /**
     * Check that the calculation of castling moves in BlackPlayer returns all possibilities
     */
    @Test
    void blackCastleMovesAreFound() {
        Builder builder = new Builder();
        King blackKing = new King(BoardUtils.getCoordinateFromAlgebraicNotation("e8"), Alliance.BLACK);
        Rook blackRookOne = new Rook(BoardUtils.getCoordinateFromAlgebraicNotation("a8"), Alliance.BLACK);
        Rook blackRookTwo = new Rook(BoardUtils.getCoordinateFromAlgebraicNotation("h8"), Alliance.BLACK);
        builder.setPiece(blackKing);
        builder.setPiece(blackRookOne);
        builder.setPiece(blackRookTwo);
        builder.setMoveMaker(Alliance.BLACK);
        Board board = builder.build();

        assertEquals(2, board.getBlackPlayer().calculateKingCastles(board.getBlackPlayer().getLegalMoves(),
                                                                             board.getWhitePlayer().getLegalMoves()).size());

    }

    /**
     * Check that the calculation of castling moves in WhitePlayer returns all possibilities
     */
    @Test
    void whiteCastleMovesAreFound() {
        Builder builder = new Builder();
        King whiteKing = new King(BoardUtils.getCoordinateFromAlgebraicNotation("e1"), Alliance.WHITE);
        Rook whiteRookOne = new Rook(BoardUtils.getCoordinateFromAlgebraicNotation("a1"), Alliance.WHITE);
        Rook whiteRookTwo = new Rook(BoardUtils.getCoordinateFromAlgebraicNotation("h1"), Alliance.WHITE);
        builder.setPiece(whiteKing);
        builder.setPiece(whiteRookOne);
        builder.setPiece(whiteRookTwo);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        assertEquals(2, board.getWhitePlayer().calculateKingCastles(board.getWhitePlayer().getLegalMoves(),
                board.getBlackPlayer().getLegalMoves()).size());

    }

}