package board;

import org.junit.jupiter.api.Test;
import pieces.Alliance;
import pieces.Pawn;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that the major functions of the Tile class is in order
 */
class TileTest {

    /**
     * Check that an empty tile really is empty
     */
    @Test
    void tileIsEmpty() {
        Tile tile = new Tile.EmptyTile(new Coordinate(0,0));
        assertTrue(tile.isEmpty());
    }

    /**
     * Check that a tile constructed with a piece as a parameter is not empty
     */
    @Test
    void tileIsNotEmpty() {
        Tile tile = new Tile.OccupiedTile(new Coordinate(0,0), new Pawn(new Coordinate(0,0), Alliance.WHITE, false));
        assertFalse(tile.isEmpty());
    }

    /**
     * Check that the createTile method generates an EmptyTile object when given a Coordinate and
     * 'null' as piece parameters
     */
    @Test
    void createTileGeneratesEmptyTile() {
        Tile tile = Tile.createTile(new Coordinate(0,0), null);
        assertTrue(tile instanceof Tile.EmptyTile);
    }

    /**
     * Check that the createTile method generates an EmptyTile object when given a Coordinate and
     * a piece as parameters
     */
    @Test
    void createTileGeneratesOccupiedTile() {
        Tile tile = Tile.createTile(new Coordinate(0,0), new Pawn(new Coordinate(0,0), Alliance.BLACK, false));
        assertTrue(tile instanceof Tile.OccupiedTile);
    }

    /**
     * Check that the piece that a tile is created with is correctly returned from
     * the given tile.
     */
    @Test
    void occupiedTileContainsRightPiece() {
        Pawn myPawn = new Pawn(new Coordinate(0,0), Alliance.BLACK, false);
        Tile tile = new Tile.OccupiedTile(new Coordinate(0,0), myPawn);
        assertEquals(myPawn, tile.getPiece());
    }

}