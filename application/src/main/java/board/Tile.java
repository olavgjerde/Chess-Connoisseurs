package board;

import pieces.Alliance;
import pieces.Piece;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Abstract class representing the concept of a chessboard tile.
 */
public abstract class Tile {
    /**
     * the coordinate of a tile
     */
    private final Coordinate tileCoord;

    /**
     * a map that contains empty tiles for every available position
     */
    private static final Map<Coordinate, EmptyTile> EMPTY_TILES_CACHE = createAllEmptyTiles();

    /**
     * Creates a hashmap which holds an empty tile at every position possible
     * @return an immutable map with empty tiles for every available position
     */
    private static Map<Coordinate, EmptyTile> createAllEmptyTiles() {
        final Map<Coordinate, EmptyTile> emptyTileMap = new HashMap<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                emptyTileMap.put(new Coordinate(i,j), new EmptyTile(new Coordinate(i,j)));
            }
        }
        return Collections.unmodifiableMap(emptyTileMap);
    }

    /**
     * Wrapper for constructing an EmptyTile or an OccupiedTile
     * @param tileCoord coordinate of the tile
     * @param piece Piece which the tile will hold
     * @return Either a EmptyTile given no piece, or a OccupiedTile given a piece
     */
    public static Tile createTile(final Coordinate tileCoord, final Piece piece) {
        return piece != null ? new OccupiedTile(tileCoord, piece) : EMPTY_TILES_CACHE.get(tileCoord);
    }

    /**
     * Sets the position of a tile
     * @param tileCoord int which defines the position of a tile
     */
    private Tile(Coordinate tileCoord) {
        this.tileCoord = tileCoord;
    }

    /**
     * Method shall check if the tile contains a piece or not
     * @return true or false depending on the contents of a given tile
     */
    public abstract boolean isTileEmpty();

    /**
     * Method shall return the contents of a given tile
     * @return the piece or null if no piece is present
     */
    public abstract Piece getPiece();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return Objects.equals(tileCoord, tile.tileCoord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tileCoord);
    }

    /**
     * Class represents a tile which has no content
     */
    private static final class EmptyTile extends Tile {
        private EmptyTile(final Coordinate coord) {
            super(coord);
        }

        @Override
        public boolean isTileEmpty() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public String toString() {
            return "-";
        }
    }

    /**
     * Class represents a tile which has some content; a given type of piece
     */
    private static final class OccupiedTile extends Tile {
        // piece contained in the tile
        private final Piece pieceAtTile;

        private OccupiedTile(final Coordinate tileCoord, Piece pieceAtTile) {
            super(tileCoord);
            this.pieceAtTile = pieceAtTile;
        }

        @Override
        public boolean isTileEmpty() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return pieceAtTile;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            OccupiedTile that = (OccupiedTile) o;
            return Objects.equals(pieceAtTile, that.pieceAtTile);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), pieceAtTile);
        }

        /**
         * Black pieces shows at lower case, white as uppercase
         * @return string representation of a given piece
         */
        @Override
        public String toString() {
            return this.getPiece().getPieceAlliance() == Alliance.BLACK ? this.getPiece().toString().toLowerCase() : this.getPiece().toString();
        }
    }
}
