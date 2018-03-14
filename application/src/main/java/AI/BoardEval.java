package AI;

import board.Board;
import pieces.*;
import player.Player;

import java.util.Collection;

public class BoardEval {

    //which players are we evaluating relative to?
    private final Player PLAYER;
    private final Player OPPONENT_PLAYER;

    //the values we will use to evaluate the board with
    private final int PAWN_VALUE = 1;
    private final int KNIGHT_VALUE = 3;
    private final int BISHOP_VALUE = 3;
    private final int ROOK_VALUE = 5;
    private final int QUEEN_VALUE = 9;

    /**
     * construction for the BoardEval class
     * It will give the score of the board state relative to the player parameter
     * @param player the player that we are evaluating for
     * @param opponent the opponent of the player
     */
    public BoardEval(Player player, Player opponent) {
        this.PLAYER = player;
        this.OPPONENT_PLAYER = opponent;
    }

    /**
     * A very simple way of analysing the board.
     * Its just the score of the AI and subtract the score of the opponent
     * So if you take one of the opponent pieces the score will go up, if you loose one the score will go down.
     *
     * TODO: make it such that pieces in danger have an effect on the score, also implement some way of evaluating trades
     *
     * @return the score of the board for the AI player
     */
    public int getValue() {
        return getTotalScore(PLAYER.getActivePieces()) - getTotalScore(OPPONENT_PLAYER.getActivePieces());
    }

    /**
     * Count up the total score of all the pieces in the given collection
     * The score is based on the value instance variables
     * @param pieceCollection : the collection of pieces to count
     * @return totalScore : the total value
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
