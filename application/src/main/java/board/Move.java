package board;

import pieces.Piece;

import static board.Board.*;

public abstract class Move {

    final Board board;
    final Piece movedPiece;
    private final Coordinate destination;

    private Move(Board board, Piece movedPiece, Coordinate destination) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destination = destination;
    }

    /**
     * When a move has been executed the method shall generate a new board,
     * this is needed because of the immutable structure of the board.
     * @return Board object
     */
    public abstract Board execute();

    /**
     * @return the coordinate of which the given move is headed
     */
    public Coordinate getDestinationCoordinate() {
        return this.destination;
    }

    /**
     * @return the piece which is to be moved in a given Move object
     */
    public Piece getMovedPiece() {
        return movedPiece;
    }

    public static final class MajorMove extends Move {
        public MajorMove(Board board, Piece movePiece, Coordinate destination) {
            super(board, movePiece, destination);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            // place all of the current player's pieces that has not been moved
            for (Piece piece : this.board.currentPlayer().getActivePieces()) {
                if(!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            // place all of the opponent player's pieces that has not been moved
            for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            // move the 'moving' piece
            builder.setPiece(this.movedPiece.movePiece(this));
            // the next move shall be made by the opponent
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    public static final class AttackMove extends Move {
        final Piece attackedPiece;
        public AttackMove(Board board, Piece movePiece, Coordinate destination, Piece attackedPiece) {
            super(board, movePiece, destination);
            this.attackedPiece = attackedPiece;
        }

        //todo:
        @Override
        public Board execute() {
            return null;
        }
    }

}
