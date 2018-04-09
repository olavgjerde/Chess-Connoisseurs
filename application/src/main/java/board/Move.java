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
 *
 * Note: many of the classes that extend this one, do so only to represent ceratain moves with
 * other forms of "algebraic" notation. Others handle special types of moves.
 */
public abstract class Move {
    final Board board;
    final Piece movedPiece;
    final Coordinate destinationCoordinate;
    final boolean isFirstMove;

    private Move(Board board, Piece movedPiece, Coordinate destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    private Move(Board board, Coordinate destinationCoordinate) {
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.movedPiece = null;
        this.isFirstMove = false;
    }
    
    /**
     * @return the piece which is to be moved in a given Move object
     */
    public Piece getMovedPiece() {
        return movedPiece;
    }

    /**
     * @return the destination coordinate of the given move
     */
    public Coordinate getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    /**
     * @return the start coordinate of a move
     */
    public Coordinate getCurrentCoordinate() {
        return movedPiece.getPieceCoordinate();
    }

    /**
     * @return true if a given move is an attack move, false otherwise
     */
    public boolean isAttack() {
        return false;
    }

    /**
     * @return true if a given move is a castling move, false otherwise
     */
    public boolean isCastlingMove() {
        return false;
    }

    /**
     * @return piece which is attacked, null if no piece is attacked
     */
    public Piece getAttackedPiece() {
        return null;
    }

    /**
     * When a move has been executed this method shall generate a new board
     * with the position of the moving pieces altered,
     * this is needed because of the immutable structure of the board.
     *
     * Note: this method is overridden by many other types of moves to handle
     * special cases that may occur in a game of chess
     *
     * @return Board object
     */
    public Board execute() {
        final Builder builder = new Builder();
        // place all of the current player's pieces that has not been moved
        for (Piece piece : this.board.currentPlayer().getActivePieces()) {
            if (!this.movedPiece.equals(piece)) {
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

    /**
     * @return Board object which the move is operating on
     */
    public Board getBoard() {
        return this.board;
    }

    @Override
    public String toString() {
        return BoardUtils.getAlgebraicNotationFromCoordinate(destinationCoordinate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return isFirstMove == move.isFirstMove &&
                Objects.equals(getCurrentCoordinate(), move.getCurrentCoordinate()) &&
                Objects.equals(board, move.board) &&
                Objects.equals(movedPiece, move.movedPiece) &&
                Objects.equals(destinationCoordinate, move.destinationCoordinate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, movedPiece, destinationCoordinate, isFirstMove);
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

    /**
     * Represents an illeagal move, with coordinates that does not exist in the bound of a regular board
     */
    public static final class NullMove extends Move {
        public NullMove() {
            super(null, new Coordinate(-1,-1));
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Cannot execute a null move");
        }
    }

    /**
     * Captures the logic of a regular major piece's move; when you move to an empty space with no
     * special rules interfering.
     */
    public static final class MajorMove extends Move {
        public MajorMove(Board board, Piece movePiece, Coordinate destination) {
            super(board, movePiece, destination);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece)) builder.setPiece(piece);
            }
            for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof MajorMove && super.equals(o);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType() + BoardUtils.getAlgebraicNotationFromCoordinate(destinationCoordinate);
        }
    }

    /**
     * Captures the logic of a regular pawn move
     */
    public static class PawnMove extends Move {
        public PawnMove(Board board, Piece movePiece, Coordinate destination) {
            super(board, movePiece, destination);
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof PawnMove && super.equals(o);
        }

        @Override
        public String toString() {
            return BoardUtils.getAlgebraicNotationFromCoordinate(destinationCoordinate);
        }
    }

    /**
     * Captures the logic of a pawn promotion move
     * This class wraps a regular pawn move and handles the upgrading of the pawn;
     * fetching the piece that it shall be promoted to and setting this at the destination
     * instead of the regular pawn.
     */
    public static final class PawnPromotion extends PawnMove {
        final Move decoratedMove;
        final Pawn promotedPawn;

        public PawnPromotion(Move decoratedMove) {
            super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
        }

        @Override
        public Board execute() {
            final Board pawnMovedBoard = this.decoratedMove.execute();
            final Builder builder = new Builder();
            for (Piece piece :  pawnMovedBoard.currentPlayer().getActivePieces()) {
                if (!this.promotedPawn.equals(piece)) builder.setPiece(piece);
            }
            for (Piece piece : pawnMovedBoard.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isAttack() {
            return this.decoratedMove.isAttack();
        }

        @Override
        public Piece getAttackedPiece() {
            return this.decoratedMove.getAttackedPiece();
        }

        @Override
        public String toString() {
            return BoardUtils.getAlgebraicNotationFromCoordinate(this.movedPiece.getPieceCoordinate()) + "-" +
                    BoardUtils.getAlgebraicNotationFromCoordinate(this.destinationCoordinate) + "=" +
                    this.promotedPawn.getPromotionPiece();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            PawnPromotion that = (PawnPromotion) o;
            return Objects.equals(decoratedMove, that.decoratedMove) &&
                    Objects.equals(promotedPawn, that.promotedPawn);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), decoratedMove, promotedPawn);
        }
    }

    /**
     * Captures the logic of a regular pawn jump
     */
    public static final class PawnJump extends Move {
        public PawnJump(Board board, Piece movePiece, Coordinate destination) {
            super(board, movePiece, destination);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece)) builder.setPiece(piece);
            }
            for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            // record pawn that executed a jump move -> this piece can be taken by an "en passant" move
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
            // set a new rook that represents the one involved in the castling
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceAlliance(), false));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            CastleMove that = (CastleMove) o;
            return Objects.equals(castleRook, that.castleRook) &&
                    Objects.equals(castleRookStart, that.castleRookStart) &&
                    Objects.equals(castleRookDestination, that.castleRookDestination);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), castleRook, castleRookStart, castleRookDestination);
        }
    }

    /**
     * Extends the CastleMove with a change toString method
     */
    public static final class KingSideCastleMove extends CastleMove {

        public KingSideCastleMove(Board board, Piece movePiece, Coordinate destination, Rook castleRook,
                                  Coordinate castleRookStart, Coordinate castleRookDestination) {
            super(board, movePiece, destination, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof KingSideCastleMove && super.equals(o);
        }

        @Override
        public String toString() {
            return "0-0";
        }
    }

    /**
     * Extends the CastleMove with a change toString method
     */
    public static final class QueenSideCastleMove extends CastleMove {
        public QueenSideCastleMove(Board board, Piece movePiece, Coordinate destination, Rook castleRook,
                                   Coordinate castleRookStart, Coordinate castleRookDestination) {
            super(board, movePiece, destination, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof QueenSideCastleMove && super.equals(o);
        }

        @Override
        public String toString() {
            return "0-0-0";
        }
    }

    /**
     * Captures the logic for a regular attack move, and keeps track of the attacked piece
     */
    public abstract static class AttackMove extends Move {
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
            final Builder builder = new Builder();
            for (Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece)) builder.setPiece(piece);
            }
            for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                if (!piece.equals(this.getAttackedPiece())) builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
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

    /**
     * Captures the logic of a regular major piece's attackmove; when you move into an attack with no
     * special rules interfering.
     */
    public static final class MajorAttackMove extends AttackMove {
        public MajorAttackMove(Board board, Piece movePiece, Coordinate destination, Piece attackedPiece) {
            super(board, movePiece, destination, attackedPiece);
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof MajorAttackMove && super.equals(o);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType() + "x" + BoardUtils.getAlgebraicNotationFromCoordinate(this.destinationCoordinate);
        }
    }

    /**
     * Captures the logic of a pawn attack
     */
    public static class PawnAttackMove extends AttackMove {
        final Piece attackedPiece;

        public PawnAttackMove(Board board, Piece movePiece, Coordinate destination, Piece attackedPiece) {
            super(board, movePiece, destination, attackedPiece);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof PawnAttackMove && super.equals(o);
        }

        @Override
        public String toString() {
            return BoardUtils.getAlgebraicNotationFromCoordinate(this.movedPiece.getPieceCoordinate()).substring(0,1) + "x" +
                   BoardUtils.getAlgebraicNotationFromCoordinate(this.destinationCoordinate);
        }
    }

    /**
     * Captures the logic of a 'en passant' attack move
     */
    public static final class PawnEnPassantAttackMove extends PawnAttackMove {
        final Piece attackedPiece;

        public PawnEnPassantAttackMove(Board board, Piece movePiece, Coordinate destination, Piece attackedPiece) {
            super(board, movePiece, destination, attackedPiece);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            PawnEnPassantAttackMove that = (PawnEnPassantAttackMove) o;
            return Objects.equals(attackedPiece, that.attackedPiece);
        }
    }

    /**
     * This class helps us fetch a move from the legal moves on a board
     * given the coordinates of a move that we which to create.
     * If this does not exist a NullMove will be returned with coordinates that
     * fall out of bounds.
     */
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
