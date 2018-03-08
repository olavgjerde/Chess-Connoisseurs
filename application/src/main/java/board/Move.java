package board;

public class Move {
    private Piece piece;
    private Position newPosition;

    public Move(Piece piece, Position newPosition) {
        this.piece = piece;
        this.newPosition = newPosition;
    }

    public Piece getPiece() {
        return piece;
    }

    public Position getNewPosition() {
        return newPosition;
    }
}
