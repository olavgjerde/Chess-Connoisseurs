package pieces.standardLayoutTests;

import board.Board;
import board.BoardUtils;
import board.Move;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pieces.Alliance;
import pieces.Pawn;
import pieces.Queen;

import java.util.List;

import static board.Board.*;
import static board.Move.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class QueenTest {

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
     * Check that the queen found all moves on an empty board
     */
    @Test
    void calculateLegalMovesOnOpenBoard() {
        Builder builder = new Builder();
        Queen queen = new Queen(BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), Alliance.BLACK);
        builder.setPiece(queen);
        builder.setMoveMaker(Alliance.BLACK);
        Board board = builder.build();
        List<Move> queenCalculatedMoves = (List<Move>) queen.calculateLegalMoves(board);

        // check that the available move for every column has been calculated
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("a4"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("a8"))));

        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("b1"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("b4"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("b7"))));

        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("c2"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("c4"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("c6"))));

        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("d3"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("d4"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("d5"))));

        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e1"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e2"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e3"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e5"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e6"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e7"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e8"))));

        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("f3"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("f4"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("f5"))));

        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("g2"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("g4"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("g6"))));

        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("h1"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("h4"))));
        assertTrue(queenCalculatedMoves.contains(MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("h7"))));
    }

    /**
     * Check that the queen is not allowed to move past an ally
     */
    @Test
    void queenStopsOnAllyEncounter() {
        Builder builder = new Builder();
        Queen queenInQuestion = new Queen(BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Pawn pawnAlly = new Pawn(BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("f5"), Alliance.WHITE, false);
        builder.setPiece(queenInQuestion);
        builder.setPiece(pawnAlly);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        Move illegalMove = MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("g6"));

        assertFalse(queenInQuestion.calculateLegalMoves(board).contains(illegalMove));
    }

    /**
     * Check that the queen is not allowed to move past an enemy piece
     */
    @Test
    void queenStopsOnEnemyEncounter() {
        Builder builder = new Builder();
        Queen queenInQuestion = new Queen(BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Queen pawnEnemy = new Queen(BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("f5"), Alliance.BLACK);
        builder.setPiece(queenInQuestion);
        builder.setPiece(pawnEnemy);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        Move illegalMove = MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("g6"));

        assertFalse(queenInQuestion.calculateLegalMoves(board).contains(illegalMove));
    }

    /**
     * Check that the queen generates an attack move when enemy is within reach
     */
    @Test
    void queenGeneratesAttackOnEnemyEncounter() {
        Builder builder = new Builder();
        Queen queenInQuestion = new Queen(BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), Alliance.WHITE);
        Queen queenEnemy = new Queen(BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("f5"), Alliance.BLACK);
        builder.setPiece(queenInQuestion);
        builder.setPiece(queenEnemy);
        builder.setMoveMaker(Alliance.WHITE);
        Board board = builder.build();

        Move attackMove = MoveFactory.createMove(board,
                BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("e4"), BoardUtils.getInstance().getCoordinateFromAlgebraicNotation("f5"));

        assertTrue(queenInQuestion.calculateLegalMoves(board).contains(attackMove));
    }
}