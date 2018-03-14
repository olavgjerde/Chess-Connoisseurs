package player;

import board.*;
import pieces.Alliance;
import pieces.Piece;
import pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static board.Move.*;

/**
 * Class represents a chess-player which control the black pieces on a given board
 * For more documentation see Player.java
 */
public class WhitePlayer extends Player {

    public WhitePlayer(Board board, Collection<Move> whiteStandardLegalMoves, Collection<Move> blackStandardLegalMoves) {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.getBlackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(Collection<Move> playerMoves, Collection<Move> opponentMoves) {
        final List<Move> kingCastles = new ArrayList<>();

        if (this.playerKing.isFirstMove() && !this.isInCheck()) {
            // White king side castle
            Coordinate oneStepRight = new Coordinate(this.playerKing.getPieceCoordinate().getX() + 1, this.playerKing.getPieceCoordinate().getY());
            Coordinate twoStepsRight = new Coordinate(oneStepRight.getX() + 1, oneStepRight.getY());
            if (this.board.getTile(oneStepRight).isTileEmpty() && this.board.getTile(twoStepsRight).isTileEmpty()) {

                // Check that that the rook is in position, and that it is making it's first move.
                // Check that there are no attacks on tiles in between the king and the rook
                final Piece rookPiece = this.board.getTile(new Coordinate(BoardUtils.getWidth()-1, BoardUtils.getHeight()-1)).getPiece();
                if (rookPiece instanceof Rook && rookPiece.isFirstMove() &&
                    calculateAttacksOnTile(oneStepRight, opponentMoves).isEmpty() &&
                    calculateAttacksOnTile(twoStepsRight, opponentMoves).isEmpty()) {
                    // add this move to list of possible castling moves
                    kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, twoStepsRight,
                                   (Rook) rookPiece, rookPiece.getPieceCoordinate(), oneStepRight));
                }
            }
            // White queen side castle
            Coordinate oneStepLeft = new Coordinate(this.playerKing.getPieceCoordinate().getX() - 1, this.playerKing.getPieceCoordinate().getY());
            Coordinate twoStepsLeft = new Coordinate(oneStepLeft.getX() - 1, oneStepLeft.getY());
            Coordinate threeStepsLeft = new Coordinate(twoStepsLeft.getX()  - 1, twoStepsLeft.getY());
            if (this.board.getTile(oneStepLeft).isTileEmpty() && this.board.getTile(twoStepsLeft).isTileEmpty() &&
                this.board.getTile(threeStepsLeft).isTileEmpty()) {

                // Check that that the rook is in position, and that it is making it's first move.
                // Check that there are no attacks on tiles in between the king and the rook
                final Piece rookPiece = this.board.getTile(new Coordinate(0, BoardUtils.getHeight() - 1)).getPiece();
                if (rookPiece instanceof Rook && rookPiece.isFirstMove() &&
                    calculateAttacksOnTile(oneStepLeft, opponentMoves).isEmpty() &&
                    calculateAttacksOnTile(twoStepsLeft, opponentMoves).isEmpty() &&
                    calculateAttacksOnTile(threeStepsLeft, opponentMoves).isEmpty()) {
                    // add this move to list of possible castling moves
                    kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing, twoStepsLeft,
                                   (Rook) rookPiece, rookPiece.getPieceCoordinate(), oneStepLeft));
                }
            }
        }
        return Collections.unmodifiableList(kingCastles);
    }
}
