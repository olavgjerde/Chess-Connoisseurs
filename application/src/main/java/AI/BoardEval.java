package AI;

import board.Board;
import pieces.*;
import player.Player;

import java.util.Collection;

public class BoardEval {

    private final Board BOARD;

    //the players playing, PLAYER is the current player
    private final Player PLAYER;
    private final Player OPPONENT_PLAYER;

    //the values we will use to evaluate the board with
    private final int PAWN_VALUE = 1;
    private final int KNIGHT_VALUE = 3;
    private final int BISHOP_VALUE = 3;
    private final int ROOK_VALUE = 5;
    private final int QUEEN_VALUE = 9;

    /**
     * Constructor for the BoardEval class. This is used to analyze the board
     * @param board the board we are analyzing
     */
    public BoardEval(Board board) {
        this.BOARD = board;

        //TODO: this gives a nullPointerException????
        BOARD.currentPlayer();

        if (BOARD.currentPlayer().getAlliance() == Alliance.WHITE){
            this.PLAYER = BOARD.getWhitePlayer();
            this.OPPONENT_PLAYER = BOARD.getBlackPlayer();
        }
        else {
            this.PLAYER = BOARD.getBlackPlayer();
            this.OPPONENT_PLAYER = BOARD.getWhitePlayer();
        }
    }

    /**
     * get the board value of the board relative to the current player
     *
     * TODO: make pieces in danger have an effect on the score
     *
     * @return value of the board relative to the current player
     */
    public int getValueOfCurrentPlayer() {
        return getTotalScore(PLAYER.getActivePieces()) - getTotalScore(OPPONENT_PLAYER.getActivePieces());
    }

    /**
     * get the board value of the board relative to the opponent of the current player
     *
     * TODO: make pieces in danger have an effect on the score
     *
     * @return value of the board relative to the opponent if the current player
     */
    public int getValueOfOpponentPlayer() {
        return getTotalScore(OPPONENT_PLAYER.getActivePieces()) - getTotalScore(PLAYER.getActivePieces());

    }

    /**
     * Count up the total score of all the pieces in the given collection
     * The score is based on the value instance variables
     * @param pieceCollection the collection of pieces to count
     * @return the total value
     */
    private int getTotalScore (Collection<Piece> pieceCollection){
        int totalScore = 0;
        for (Piece p : pieceCollection){
            if (p instanceof Pawn)
                totalScore += PAWN_VALUE;
            else if (p instanceof Knight)
                totalScore += KNIGHT_VALUE;
            else if (p instanceof Bishop)
                totalScore += BISHOP_VALUE;
            else if (p instanceof Rook)
                totalScore += ROOK_VALUE;
            else if (p instanceof Queen)
                totalScore += QUEEN_VALUE;
        }
        return totalScore;
    }
}
