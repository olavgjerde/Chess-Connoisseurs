import board.Board;
import board.Move;
import pieces.*;

import java.util.Collection;

public class AI {

    private final boolean IS_HARD;
    //private final Player THIS_PLAYER;
    //private final Player OPPONENT;


    private final int PAWN_VALUE = 1;
    private final int KNIGHT_VALUE = 3;
    private final int BISHOP_VALUE = 3;
    private final int ROOK_VALUE = 5;
    private final int QUEEN_VALUE = 9;

    public AI(boolean isHard) {
        this.IS_HARD = isHard;
    }

    public Move getMove(Board board){

        //TODO: give the next move
        return null;
    }

    /**
     * A very simple way of analysing the board.
     * Its just the score of the AI - the score of the opponent
     * So if you take one of the opponent pieces the score will go up, if you loose one ths score will go down.
     *
     * TODO: make it such that pieces in danger have an effect on the score, also implement some way of evaluating trades
     *
     * @return the score of the board for the AI player
     */
    private int evalBoard(Board board, boolean isWhite) {
        int myTotalScore = 0;
        int opponentTotalScore = 0;
        if (isWhite) {
            myTotalScore = getTotalScore(board.getWhitePieces());
            opponentTotalScore = getTotalScore(board.getBlackPieces());
        }
        else {
            myTotalScore = getTotalScore(board.getBlackPieces());
            opponentTotalScore = getTotalScore(board.getWhitePieces());
        }
        return myTotalScore - opponentTotalScore;
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
