package board;

import pieces.Pawn;
import pieces.Piece;
import pieces.Rook;

import java.util.Objects;
import static board.Board.*;

/**
 * Abstract class Move represents the base logic of a move
 * on a chessboard, where we have the board the movement takes place on,
 * the piece that is moving, and the destination coordinate of the moving piece.
 */
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
     * @return the destination coordinate of the given move
     */
    public Coordinate getDestinationCoordinate() {
        return this.destination;
    }

    /**
     * @return the start coordinate of a move
     */
    public Coordinate getCurrentCoordinate() {
        return movedPiece.getPieceCoordinate();
    }

    /**
     * @return the piece which is to be moved in a given Move object
     */
    public Piece getMovedPiece() {
        return movedPiece;
    }

    public boolean isAttack() {
        return false;
    }

    public boolean isCastlingMove() {
        return false;
    }

    public Piece getAttackedPiece() {
        return null;
    }

    /**
     * Enums represents the different statuses that a move
     * can have.
     */
    public enum MoveStatus {
        DONE {
            @Override
            public boolean isDone() {
                return true;
            }
        },
        ILLEGAL_MOVE {
            @Override
            public boolean isDone() {
                return false;
            }
        }, LEAVES_PLAYER_IN_CHECK {
            @Override
            public boolean isDone() {
                return false;
            }
        };

        /**
         * @return true if the move can be done, false otherwise
         */
        public abstract boolean isDone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Objects.equals(movedPiece, move.movedPiece) &&
                Objects.equals(destination, move.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movedPiece, destination);
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

    public static final class NullMove extends Move {
        NullMove() {
            super(null, null, null);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Cannot execute a null move");
        }
    }

    public static final class PawnMove extends Move {
        public PawnMove(Board board, Piece movePiece, Coordinate destination) {
            super(board, movePiece, destination);
        }

        //todo:
        @Override
        public Board execute() {
            return null;
        }
    }

    public static final class PawnJump extends Move {
        public PawnJump(Board board, Piece movePiece, Coordinate destination) {
            super(board, movePiece, destination);
        }

        //todo:
        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            // record pawn that executed a 'en passant' move
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    /**
     * Captures the logic of a castling move
     */
    static abstract class CastleMove extends Move {
        final Rook castleRook;
        final Coordinate castleRookStart;
        final Coordinate castleRookDestination;

        CastleMove(Board board, Piece movePiece, Coordinate destination, Rook castleRook,
                   Coordinate castleRookStart, Coordinate castleRookDestination) {
            super(board, movePiece, destination);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook() {
            return castleRook;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            //todo: check first move boolean on pieces
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceAlliance()));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    public static final class KingSideCastleMove extends CastleMove {

        public KingSideCastleMove(Board board, Piece movePiece, Coordinate destination, Rook castleRook,
                                  Coordinate castleRookStart, Coordinate castleRookDestination) {
            super(board, movePiece, destination, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString() {
            return "0-0";
        }
    }

    public static final class QueenSideCastleMove extends CastleMove {
        public QueenSideCastleMove(Board board, Piece movePiece, Coordinate destination, Rook castleRook, Coordinate castleRookStart, Coordinate castleRookDestination) {
            super(board, movePiece, destination, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString() {
            return "0-0-0";
        }
    }

    public static class AttackMove extends Move {
        final Piece attackedPiece;
        public AttackMove(Board board, Piece movePiece, Coordinate destination, Piece attackedPiece) {
            super(board, movePiece, destination);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }

        @Override
        public Board execute() {
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            AttackMove that = (AttackMove) o;
            return Objects.equals(attackedPiece, that.attackedPiece);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), attackedPiece);
        }
    }

    public static class PawnAttackMove extends AttackMove {
        final Piece attackedPiece;
        public PawnAttackMove(Board board, Piece movePiece, Coordinate destination, Piece attackedPiece) {
            super(board, movePiece, destination, attackedPiece);
            this.attackedPiece = attackedPiece;
        }

        //todo:
        @Override
        public Board execute() {
            return null;
        }
    }

    public static final class PawnEnPassantAttackMove extends PawnAttackMove {
        final Piece attackedPiece;
        public PawnEnPassantAttackMove(Board board, Piece movePiece, Coordinate destination, Piece attackedPiece) {
            super(board, movePiece, destination, attackedPiece);
            this.attackedPiece = attackedPiece;
        }

        //todo:
        @Override
        public Board execute() {
            return null;
        }
    }

    public static class MoveFactory {
        private static final Move NULL_MOVE = new NullMove();

        private MoveFactory() {
            throw new RuntimeException("Do not initialise");
        }

        public static Move createMove(Board board, Coordinate currentCoordinate, Coordinate destinationCoordinate) {
            for (Move move : board.getAllLegalMoves()) {
                if (move.getCurrentCoordinate().equals(currentCoordinate) &&
                    move.getDestinationCoordinate().equals(destinationCoordinate)) {
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }
}
