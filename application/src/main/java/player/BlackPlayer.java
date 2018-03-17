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

    @Override
    protected Collection<Move> calculateKingCastles(Collection<Move> playerMoves, Collection<Move> opponentMoves) {
        final List<Move> kingCastles = new ArrayList<>();

        if (this.playerKing.isFirstMove() && !this.isInCheck()) {
            // Black king side castle
            Coordinate oneStepRight = new Coordinate(this.playerKing.getPieceCoordinate().getX() + 1, this.playerKing.getPieceCoordinate().getY());
            Coordinate twoStepsRight = new Coordinate(oneStepRight.getX() + 1, oneStepRight.getY());
            if (this.board.getTile(oneStepRight).isEmpty() && this.board.getTile(twoStepsRight).isEmpty()) {

                // Check that that the rook is in position, and that it is making it's first move.
                // Check that there are no attacks on tiles in between the king and the rook
                final Piece rookPiece = this.board.getTile(new Coordinate(BoardUtils.getWidth() - 1, 0)).getPiece();
                if (rookPiece instanceof Rook && rookPiece.isFirstMove() &&
                    calculateAttacksOnTile(oneStepRight, opponentMoves).isEmpty() &&
                    calculateAttacksOnTile(twoStepsRight, opponentMoves).isEmpty()) {
                    // add this move to list of possible castling moves
                    kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, twoStepsRight,
                                   (Rook) rookPiece, rookPiece.getPieceCoordinate(), oneStepRight));
                }
            }
            // Black queen side castle
            Coordinate oneStepLeft = new Coordinate(this.playerKing.getPieceCoordinate().getX() - 1, this.playerKing.getPieceCoordinate().getY());
            Coordinate twoStepsLeft = new Coordinate(oneStepLeft.getX() - 1, oneStepLeft.getY());
            Coordinate threeStepsLeft = new Coordinate(twoStepsLeft.getX()  - 1, twoStepsLeft.getY());
            if (this.board.getTile(oneStepLeft).isEmpty() && this.board.getTile(twoStepsLeft).isEmpty() &&
                this.board.getTile(threeStepsLeft).isEmpty()) {

                // Check that that the rook is in position, and that it is making it's first move.
                // Check that there are no attacks on tiles in between the king and the rook
                final Piece rookPiece = this.board.getTile(new Coordinate(0, 0)).getPiece();
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
