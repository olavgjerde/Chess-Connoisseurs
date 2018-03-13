package board;

import pieces.Piece;

public abstract class Move {
    private final Board board;
    private final Piece movePiece;
    private final Coordinate destination;

    private Move(Board board, Piece movePiece, Coordinate destination) {
        this.board = board;
        this.movePiece = movePiece;
        this.destination = destination;
    }

    public static final class MajorMove extends Move {
        public MajorMove(Board board, Piece movePiece, Coordinate destination) {
            super(board, movePiece, destination);
        }
    }

    public static final class AttackMove extends Move {
        final Piece attackedPiece;
        public AttackMove(Board board, Piece movePiece, Coordinate destination, Piece attackedPiece) {
            super(board, movePiece, destination);
            this.attackedPiece = attackedPiece;
        }
    }

}
