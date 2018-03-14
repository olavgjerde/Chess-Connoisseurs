package AI;

import board.Move;
import player.Player;

import java.util.Collection;
import java.util.List;

public class OpponentBoardStateTree extends BoardStateTree {

    /**
     * The constructor for the OpponentBoardStateTree class.
     * this is used only from the BoardStateTree class to predict the opponents move
     *
     * @param AI               the player that this tree is finding the move for
     * @param opponent         the opponent that we are playing against
     * @param maxNodes         how many children will we allow for each node? (how many moves will we analyze)
     * @param maxDepth         how deep will we allow the tree to grow? (how far into the future we will look)
     * @param opponentMaxNodes how many children will we allow for each node in the opponents tree?
     * @param opponentMaxDepth how deep will we allow the opponents tree to grow?
     * @param currentDepth     how deep are we currently in the tree? (amount of moves away from the initial board state)
     */
    public OpponentBoardStateTree(Player AI, Player opponent, int maxNodes, int maxDepth, int opponentMaxNodes, int opponentMaxDepth, int currentDepth) {
        super(AI, opponent, maxNodes, maxDepth, opponentMaxNodes, opponentMaxDepth, currentDepth);
    }

    /**
     * This should not be used from a OpponentBoardStateTree!
     * @return nothing
     */
    @Override
    public Move getBestMove() {
        //we want the opponent to move but we aren't interested in what move
        assert(true);
        return null;
    }

    /**
     * similar to the getBestBrachValue from the BoardStateTree class
     * but it will only spawn one node with a move found by making another tree
     * @return the value of the best branch (for the opponent in this case)
     */
    @Override
    protected int getBestBranchValue() {

        //we are in a tree where we don't want to spawn any more trees. So we just pick the first element in the moves list (it should only have one move)
        if (MAX_DEPTH <= 1 && MAX_NODES <= 1){
            List<Move> moves = (List<Move>) filterMoves(AI.getLegalMoves());
            Move m = moves.get(0);
            AI.makeMove(m);
            return EVALUATOR.getValue();
        }

        //This will find the best move for the opponent (the current AI), note that we don't allow this tree to spawn any more trees
        BoardStateTree BT = new BoardStateTree(AI, OPPONENT, MAX_NODES, MAX_DEPTH, 1, 1, 0);
        AI.makeMove(BT.getBestMove());

        //create a single node from here, and return the value
        BT = new BoardStateTree(OPPONENT, AI, OPPONENT_MAX_NODES, OPPONENT_MAX_DEPTH, MAX_NODES, MAX_DEPTH, CURRENT_DEPTH+1);
        return BT.getBestBranchValue();
    }

    /**
     * no different from the parent method. I just like to have it here for readability
     * @param moveCollection collection of moves to filter from
     * @return
     */
    @Override
    protected Collection<Move> filterMoves(Collection<Move> moveCollection) {
        return super.filterMoves(moveCollection);
    }
}
