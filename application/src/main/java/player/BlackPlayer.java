package player;

import board.Board;
import board.Move;
import pieces.Alliance;
import pieces.Piece;

import java.util.Collection;

/**
 * Class represents a chess-player which control the black pieces on a given board
 * For more documentation see Player.java
 */
public class BlackPlayer extends Player {

    public BlackPlayer(Board board, Collection<Move> blackStandardLegalMoves, Collection<Move> whiteStandardLegalMoves) {
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.getWhitePlayer();
    }
}
