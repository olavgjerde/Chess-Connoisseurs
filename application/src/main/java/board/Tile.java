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
    public Coordinate getTileCoord() {
        return tileCoord;
    }

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
        for (int i = 0; i < BoardUtils.getInstance().getHeight(); i++) {
            for (int j = 0; j < BoardUtils.getInstance().getWidth(); j++) {
                emptyTileMap.put(new Coordinate(j,i), new EmptyTile(new Coordinate(j,i)));
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
    static Tile createTile(final Coordinate tileCoord, final Piece piece) {
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
    public abstract boolean isEmpty();

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
    static final class EmptyTile extends Tile {
        EmptyTile(final Coordinate coord) {
            super(coord);
        }

        @Override
        public boolean isEmpty() {
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
    static final class OccupiedTile extends Tile {
        // piece contained in the tile
        private final Piece pieceAtTile;

        OccupiedTile(final Coordinate tileCoord, Piece pieceAtTile) {
            super(tileCoord);
            this.pieceAtTile = pieceAtTile;
        }

        @Override
        public boolean isEmpty() {
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
