package board;

public abstract class Piece {
    boolean isWhite;
    boolean hasMoved;
    Position pos;

    public Piece(Position pos, boolean isWhite) {
        this.isWhite = isWhite;
        this.pos = pos;
    }

    public Position getPosition(){
        return pos;
    }

    public void updatePosition(Position pos) {
        this.pos = pos;
    }
}
