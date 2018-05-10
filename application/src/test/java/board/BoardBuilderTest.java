package board;

import org.junit.jupiter.api.Test;
import pieces.Alliance;
import pieces.Pawn;

import static board.Board.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that the builder in the Board class behaves as expected
 */
class BoardBuilderTest {

    /**
     * Test that a new Builder object is empty
     */
    @Test
    void newBuilderIsEmpty() {
        Builder builder = new Builder();
        assertEquals(0, builder.boardConfig.size());
        assertNull(builder.nextMoveMaker);
        assertNull(builder.enPassantPawn);
    }

    /**
     * Check that the builder places a piece correctly in its config
     */
    @Test
    void builderSetsPiece() {
        Builder builder = new Builder();
        Pawn testPawn = new Pawn(new Coordinate(0,0), Alliance.WHITE, false);
        builder.setPiece(testPawn);
        assertEquals(testPawn, builder.boardConfig.get(new Coordinate(0,0)));
    }

    /**
     * Check that the builder can set the pawn open for an 'en passant' attack
     */
    @Test
    void builderSetsEnPassantPawn() {
        Builder builder = new Builder();
        Pawn testPawn = new Pawn(new Coordinate(0,0), Alliance.WHITE, false);
        builder.setEnPassantPawn(testPawn);
        assertEquals(testPawn, builder.enPassantPawn);
    }

    /**
     * Check that the builder can set the Alliance of who makes the next move
     */
    @Test
    void builderSetsNextMoveMaker() {
        Builder builder = new Builder();
        builder.setMoveMaker(Alliance.BLACK);
        assertEquals(Alliance.BLACK, builder.nextMoveMaker);
    }

}