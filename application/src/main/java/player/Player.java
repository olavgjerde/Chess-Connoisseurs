package player;

import board.Board;
import board.Coordinate;
import board.Move;
import pieces.Alliance;
import pieces.King;
import pieces.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static board.Move.*;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    /**
     * Constructor for abstract player object
     * @param board which the player plays on
     * @param legalMoves moves belonging to the player
     * @param opponentMoves move belonging to the opponent
     */
    Player(Board board, Collection<Move> legalMoves, Collection<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();

        // add castling moves to legal moves
        final Collection<Move> allMoves = new ArrayList<>();
        allMoves.addAll(legalMoves);
        allMoves.addAll(calculateKingCastles(legalMoves, opponentMoves));
        this.legalMoves = allMoves;
        this.isInCheck = !calculateAttacksOnTile(this.playerKing.getPieceCoordinate(), opponentMoves).isEmpty();
    }

    /**
     * @return a collection of the player's pieces
     */
    public abstract Collection<Piece> getActivePieces();

    /**
     * @return Alliance enum of the player
     */
    public abstract Alliance getAlliance();

    /**
     * @return a Player object which is the opponent of 'this'
     */
    public abstract Player getOpponent();

    /**
     * This method shall calculate if there are any castling moves that is available to the player
     * @param playerMoves the moves available to the player
     * @param opponentMoves the moves available to the opponent
     * @return a Collection of possible castling moves
     */
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerMoves, Collection<Move> opponentMoves);

    /**
     * Finds all possible attacks on a given coordinate
     * @param pieceCoordinate coordinate to calculate attacks for
     * @param moves available for the opponent player
     * @return a list for moves that can attack the given coordinate
     */
    protected static Collection<Move> calculateAttacksOnTile(Coordinate pieceCoordinate, Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();
        for (Move move :  moves) {
            if (pieceCoordinate.equals(move.getDestinationCoordinate())) {
                attackMoves.add(move);
            }
        }
        return Collections.unmodifiableList(attackMoves);
    }

    /**
     * Of all the player's pieces find the King piece
     * @return King(Piece) - object
     */
    private King establishKing() {
        for(Piece piece : getActivePieces()) {
            if (piece instanceof King) {
                return (King) piece;
            }
        }
        throw new RuntimeException("This is not a valid board");
    }

    /**
     * @return the King object of the player
     */
    public King getPlayerKing() {
        return playerKing;
    }

    /**
     * @return Collection of the player's legal moves
     */
    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }

    /**
     * Check if a given move is legal
     * @param move to evaluate
     * @return true is move is legal, false otherwise
     */
    private boolean isMoveLegal(Move move) {
        return this.legalMoves.contains(move);
    }

    /**
     * Check if the player is in check
     * @return true if player is in check, false otherwise
     */
    public boolean isInCheck() {
        return isInCheck;
    }

    /**
     * Check if the player is in checkmate
     * @return true if in checkmate, false otherwise
     */
    public boolean isInCheckMate() {
        return isInCheck && !hasEscapesMoves();
    }

    /**
     * Check if the player is in a stalemate
     * @return true if the player is not in check, but does not have any escape moves,
     * false otherwise
     */
    public boolean isInStaleMate() {
        return !isInCheck && !hasEscapesMoves();
    }

    //todo: implement method
    public boolean isCastled() {
        return false;
    }

    /**
     * Calculate if the player as any moves that enables them to escape 'check'
     * @return true if player has moves that escapes check-status, false otherwise
     */
    private boolean hasEscapesMoves() {
        for (Move move : this.legalMoves) {
            // make all moves possible and see if player "escapes"
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Requests a move on the board where the player is playing
     * @param move to execute
     * @return MoveTransition object which contains the Board, the Move that was requested, and the
     * status of that move.
     */
    public MoveTransition makeMove(Move move) {
        if (!isMoveLegal(move)) {
            // return same board
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }

        // Board where the move has taken place. NB!: if the player who made the move has the color white,
        // then currentPlayer() will return black after the transition.
        final Board transitionBoard = move.execute();
        // check if move leaves player's king in check
        final King kingOfPlayerThatMoves = transitionBoard.currentPlayer().getOpponent().getPlayerKing();
        final Collection<Move> currentPlayerMoves = transitionBoard.currentPlayer().getLegalMoves();
        final Collection<Move> attacksOnPlayerKing = Player.calculateAttacksOnTile(kingOfPlayerThatMoves.getPieceCoordinate(), currentPlayerMoves);
        if (!attacksOnPlayerKing.isEmpty()) {
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }
}
