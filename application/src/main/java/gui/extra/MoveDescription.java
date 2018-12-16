package gui.extra;

import board.Coordinate;
import board.Tile;
import pieces.Piece;

/**
 * A mutable class that the chess game can use to represent the current move of a user
 */
public class MoveDescription {
    private Tile startTile, destinationTile;
    private Coordinate hintStartCoordinate, hintDestinationCoordinate;
    private Piece userMovedPiece;

    public Tile getStartTile() {
        return startTile;
    }

    public void setStartTile(Tile startTile) {
        this.startTile = startTile;
    }

    public Tile getDestinationTile() {
        return destinationTile;
    }

    public void setDestinationTile(Tile destinationTile) {
        this.destinationTile = destinationTile;
    }

    public Coordinate getHintStartCoordinate() {
        return hintStartCoordinate;
    }

    public void setHintStartCoordinate(Coordinate hintStartCoordinate) {
        this.hintStartCoordinate = hintStartCoordinate;
    }

    public Coordinate getHintDestinationCoordinate() {
        return hintDestinationCoordinate;
    }

    public void setHintDestinationCoordinate(Coordinate hintDestinationCoordinate) {
        this.hintDestinationCoordinate = hintDestinationCoordinate;
    }

    public void setUserMovedPiece(Piece userMovedPiece) {
        this.userMovedPiece = userMovedPiece;
    }

    public boolean swapBetweenPieces() {
        if (destinationTile.getPiece() != null && userMovedPiece != null) {
            if (destinationTile.getPiece().getPieceAlliance() == userMovedPiece.getPieceAlliance()) {
                startTile = destinationTile;
                destinationTile = null;
                return true;
            }
        }
        return false;
    }

    public void resetDescription() {
        this.startTile = null;
        this.destinationTile = null;
        this.userMovedPiece = null;
    }

    public void resetHints() {
        this.hintStartCoordinate = null;
        this.hintDestinationCoordinate = null;
    }
}
