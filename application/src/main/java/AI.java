import board.Board;
import board.Move;
import pieces.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AI {

    private final boolean IS_HARD;
    private final boolean IS_WHITE;
    //private final Player THIS_PLAYER;
    //private final Player OPPONENT;


    private final int PAWN_VALUE = 1;
    private final int KNIGHT_VALUE = 3;
    private final int BISHOP_VALUE = 3;
    private final int ROOK_VALUE = 5;
    private final int QUEEN_VALUE = 9;

    public AI(boolean isHard, boolean isWhite) {
        this.IS_HARD = isHard;
        this.IS_WHITE = isWhite;
    }

    public Move getMove(Board board){
        BoardStateTree BT = new BoardStateTree(board, 2, 5, 0, false);
        if (IS_HARD) BT = new BoardStateTree(board, 3, 10, 0, false);
        return BT.getBestMove();
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
    private int evalBoard(Board board, boolean opponent) {
        int myTotalScore = 0;
        int opponentTotalScore = 0;
        if (IS_WHITE) {
            myTotalScore = getTotalScore(board.getWhitePieces());
            opponentTotalScore = getTotalScore(board.getBlackPieces());
        }
        else {
            myTotalScore = getTotalScore(board.getBlackPieces());
            opponentTotalScore = getTotalScore(board.getWhitePieces());
        }
        if (opponent) return opponentTotalScore - myTotalScore;
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



    private class BoardStateTree {

        private final int MAX_NODES;
        private final int MAX_DEPTH;
        private final int OPPONENT_MAX_NODES;
        private final int OPPONENT_MAX_DEPTH;

        private final boolean IS_MY_TURN;

        private final int CURRENT_DEPTH;

        private final Board BOARD;
        private final int BOARD_VALUE;

        Collection<BoardStateTree> nodes;

        public BoardStateTree(Board board, int maxNodes, int maxDepth, int currentDepth, boolean isOpponentTurn) {
            this.MAX_NODES = maxNodes;
            this.MAX_DEPTH = maxDepth;

            //defaults to 1
            this.OPPONENT_MAX_NODES = 1;
            this.OPPONENT_MAX_DEPTH = 1;

            this.IS_MY_TURN = !isOpponentTurn;

            this.BOARD = board;
            this.BOARD_VALUE = evalBoard(board, !IS_MY_TURN);

            this.CURRENT_DEPTH = currentDepth;
        }

        /**
         * find the move that will give the branch with the best results
         * @return the best move we can find
         */
        private Move getBestMove(){
            //get the moves that we use to branch out
            List<Move> moves;
            if (IS_WHITE) moves = (List<Move>)filterMoves(BOARD.getWhiteStandardLegalMoves());
            else moves = (List<Move>) filterMoves(BOARD.getBlackStandardLegalMoves());

            Move bestMove = moves.get(0);
            int bestBranchValue = 0;
            for (Move m : moves){
                Board b = BOARD.clone();
                //TODO: b.move(m);
                BoardStateTree BT = new BoardStateTree(b, MAX_NODES, MAX_DEPTH, CURRENT_DEPTH+1, true);
                int branchValue = BT.getBestBranchValue();
                if (branchValue > bestBranchValue) {
                    bestBranchValue = branchValue;
                    bestMove = m;
                }
            }
            return bestMove;
        }

        /**
         * build the tree as far as allowed and return the best value we could find
         * @return the value of the highest value leaf
         */
        private int getBestBranchValue(){

            //we reached the bottom
            if (CURRENT_DEPTH >= MAX_DEPTH) return BOARD_VALUE;

            //get the moves that we use to branch out
            Collection<Move> moves;
            if (IS_WHITE) moves = filterMoves(BOARD.getWhiteStandardLegalMoves());
            else moves = filterMoves(BOARD.getBlackStandardLegalMoves());

            //in the rare case where there is no more moves
            if (moves.isEmpty()) return BOARD_VALUE;

            //branch out and find the best value of all the leafs
            int bestBranch = 0;
            for (Move m : moves){
                Board b = BOARD.clone();
                //TODO: b.move(m);
                BoardStateTree BT = new BoardStateTree(b, MAX_NODES, MAX_DEPTH, CURRENT_DEPTH+1, true);
                int branchValue = BT.OpponentBestValue();
                if (branchValue > bestBranch) bestBranch = branchValue;
            }
            return bestBranch;
        }

        /**
         * let the opponent make a move
         * TODO: make it so that we can let the opponent analyse the game further then one move
         */
        private int OpponentBestValue(){

            //get the moves that we use to branch out
            Collection<Move> moves;
            if (!IS_WHITE) moves = filterMoves(BOARD.getWhiteStandardLegalMoves());
            else moves = filterMoves(BOARD.getBlackStandardLegalMoves());

            //in the rare case where there is no more moves, we don't move
            if (moves.isEmpty()) return getBestBranchValue();

            //branch out and find the best value of all the leafs
            int bestBranch = 0;
            for (Move m : moves){
                Board b = BOARD.clone();
                //TODO: b.move(m);
                BoardStateTree BT = new BoardStateTree(b, MAX_NODES, MAX_DEPTH, CURRENT_DEPTH+1, false);
                int branchValue = BT.getBestBranchValue();
                if (branchValue > bestBranch) bestBranch = branchValue;
            }
            return bestBranch;
        }

        /**
         * filter out the moves we don't need
         * @param moveCollection collection of moves to filter from
         * @return a collection of the moves we will use to branch out with, size = MAX_NODES
         */
        private Collection<Move> filterMoves(Collection<Move> moveCollection){

            //the collection with the moves remaining after the filtering
            ArrayList<Move> filteredMoveCollection = new ArrayList<Move>();

            //scores of board after the moves. not necessary but makes code readable and reduce calculations
            ArrayList<Integer> scores = new ArrayList<Integer>();

            //smallest score in the scores collection so that we don't need to calculate that every time.
            int minScore = scores.indexOf(Collections.min(scores));

            //filter the moves based on eval
            for (Move m : moveCollection){
                Board b = BOARD.clone();
                //TODO: b.move(m);
                int eval = evalBoard(b, !IS_MY_TURN);
                if (eval > BOARD_VALUE && eval > scores.get(minScore)) {
                    if (!(filteredMoveCollection.size() > MAX_NODES)) {
                        filteredMoveCollection.add(m);
                        scores.add(eval);
                    }
                    else {
                        filteredMoveCollection.set(minScore, m);
                        scores.set(minScore, eval);
                        minScore = scores.indexOf(Collections.min(scores));
                    }
                }
            }

            //this should never happen
            assert(filteredMoveCollection.size() <= MAX_NODES);

            return filteredMoveCollection;
        }

        public int getBOARD_VALUE() {
            return BOARD_VALUE;
        }
    }
}
