package AI;

import board.Board;
import board.Move;
import player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BoardStateTree {

    //clone of the players that are playing
    protected final Player AI;
    protected final Player OPPONENT;

    //the limits on how we will allow the tree to become
    protected final int MAX_NODES;
    protected final int MAX_DEPTH;
    protected final int OPPONENT_MAX_NODES;
    protected final int OPPONENT_MAX_DEPTH;

    //how deep down in the tree are we?
    protected final int CURRENT_DEPTH;

    //the evaluator used to evaluate the current board. Note that this is the value of the AI player, not the opponent
    //we donn't want to just store the value so that we can evaluate again after a move
    protected final BoardEval EVALUATOR;

    /**
     * The constructor for the BoardStateTree class.
     * It will make a tree of all the best moves, and find the best one.
     * The shape of the free is determined by the parameters.
     * @param AI the player that this tree is finding the move for
     * @param opponent the opponent that we are playing against
     * @param maxNodes how many children will we allow for each node? (how many moves will we analyze)
     * @param maxDepth how deep will we allow the tree to grow? (how far into the future we will look)
     * @param opponentMaxNodes how many children will we allow for each node in the opponents tree?
     * @param opponentMaxDepth how deep will we allow the opponents tree to grow?
     * @param currentDepth how deep are we currently in the tree? (amount of moves away from the initial board state)
     */
    public BoardStateTree(Player AI, Player opponent, int maxNodes, int maxDepth, int opponentMaxNodes, int opponentMaxDepth, int currentDepth) {

        //TODO: make these clones so that we don't move the actually game when calculating
        this.AI = AI;
        this.OPPONENT = opponent;

        this.MAX_NODES = maxNodes;
        this.MAX_DEPTH = maxDepth;
        this.OPPONENT_MAX_NODES = opponentMaxNodes;
        this.OPPONENT_MAX_DEPTH = opponentMaxDepth;

        this.CURRENT_DEPTH = currentDepth;

        this.EVALUATOR = new BoardEval(this.AI, this.OPPONENT);
    }

    /**
     * find the move that will give the branch with the best results
     * this should only be used from the root node
     * @return the best move we can find
     */
    public Move getBestMove(){
        //get the moves that we use to branch out
        List<Move> moves = (List<Move>) filterMoves(AI.getLegalMoves());

        //the first one is the best one for now
        Move bestMove = moves.get(0);

        //we haven't found a value yet
        int bestBranchValue = 0;

        //try out the moves we have and find the one with the best value by using getBestBranchValue()
        for (Move m : moves){
            //TODO: clone the players

            AI.makeMove(m);

            //create a node from here with this move (m)
            OpponentBoardStateTree BT = new OpponentBoardStateTree(OPPONENT, AI, OPPONENT_MAX_NODES, OPPONENT_MAX_DEPTH, MAX_NODES, MAX_DEPTH, CURRENT_DEPTH+1);

            //find the best value in the BT node
            int branchValue = BT.getBestBranchValue();

            //check if this new value is better than what we already have
            if (branchValue > bestBranchValue) {
                bestBranchValue = branchValue;
                bestMove = m;
            }
        }
        //return the move that gave the best value
        return bestMove;
    }

    /**
     * build the tree as far as allowed and return the best value we could find
     * @return the value of the highest value leaf
     */
    protected int getBestBranchValue(){

        //we reached the bottom
        if (CURRENT_DEPTH >= MAX_DEPTH) return EVALUATOR.getValue();

        //get the moves that we use to branch out
        List<Move> moves = (List<Move>) filterMoves(AI.getLegalMoves());

        //in the rare case where there is no more moves
        if (moves.isEmpty()) return EVALUATOR.getValue();

        //branch out and find the best value of all the leafs
        int bestBranch = 0;
        for (Move m : moves){
            //TODO: clone the players

            AI.makeMove(m);

            //create a node from here
            OpponentBoardStateTree BT = new OpponentBoardStateTree(OPPONENT, AI, OPPONENT_MAX_NODES, OPPONENT_MAX_DEPTH, MAX_NODES, MAX_DEPTH, CURRENT_DEPTH+1);

            int branchValue = BT.getBestBranchValue();
            if (branchValue > bestBranch) bestBranch = branchValue;
        }
        return bestBranch;
    }

    /**
     * filter out the moves we don't need
     * @param moveCollection collection of moves to filter from
     * @return a collection of the moves we will use to branch out with, size <= MAX_NODES
     */
    protected Collection<Move> filterMoves(Collection<Move> moveCollection){

        //the collection with the moves remaining after the filtering
        ArrayList<Move> filteredMoveCollection = new ArrayList<>();

        //scores of board after the moves. not necessary but makes code readable and reduce calculations
        ArrayList<Integer> scores = new ArrayList<>();

        //smallest score in the scores collection so that we don't need to calculate that every time.
        int minScore = scores.indexOf(Collections.min(scores));

        //filter the moves based on the evaluator
        for (Move m : moveCollection){

            //get the score before and after the move
            int eval1 = EVALUATOR.getValue();
            AI.makeMove(m);
            int eval2 = EVALUATOR.getValue();

            //is the new score better than before? and is it better than the smallest score we have found so far?
            if (eval2 > eval1 && eval2 > scores.get(minScore)) {
                if (!(filteredMoveCollection.size() > MAX_NODES)) {
                    filteredMoveCollection.add(m);
                    scores.add(eval2);
                }
                else {
                    filteredMoveCollection.set(minScore, m);
                    scores.set(minScore, eval2);
                    minScore = scores.indexOf(Collections.min(scores));
                }
            }
        }

        //this should never happen
        assert(filteredMoveCollection.size() <= MAX_NODES);

        return filteredMoveCollection;
    }
}