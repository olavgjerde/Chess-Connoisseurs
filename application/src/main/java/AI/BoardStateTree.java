package AI;

import board.Board;
import board.Move;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BoardStateTree {

    //the board we are working on
    protected Board BOARD;

    //the limits on how we will allow the tree to become
    protected final int MAX_NODES;
    protected final int MAX_DEPTH;

    //how deep down in the tree are we?
    protected final int CURRENT_DEPTH;

    //value of the current board relative to the current player
    protected final int BOARD_VALUE;

    /**
     * Constructor of the BoardStateTree class.
     * Used for making a tree of different moves and finding the best one
     *
     * @param board the board we are finding a move for (for the current player)
     * @param maxNodes the maximum amount of the nodes we will spawn from here
     * @param maxDepth the maximum depth of the tree we are currently in
     * @param currentDepth the depth of the node we are currently in
     */
    public BoardStateTree(Board board, int maxNodes, int maxDepth, int currentDepth) {
        this.BOARD = board;
        this.MAX_NODES = maxNodes;
        this.MAX_DEPTH = maxDepth;
        this.CURRENT_DEPTH = currentDepth;
        BoardEval evaluator = new BoardEval(BOARD);
        this.BOARD_VALUE = evaluator.getValueOfCurrentPlayer();
    }

    /**
     * find the move that will give the branch with the best results
     * this should only be used from the root node
     *
     * @return the best move we can find
     */
    public Move getBestMove(){

        //get the MAX_NODES best moves
        List<Move> moves = filterMoves(BOARD.currentPlayer().getLegalMoves());

        //value of the best move
        int bestMoveValue = -500;

        //the best move
        Move bestMove = moves.get(0);

        //iterate through the moves and find the best one
        for (Move m : moves){
            //double check if the move is legal
            if (BOARD.currentPlayer().makeMove(m).getMoveStatus().isDone()){

                //get the new board after the move
                Board transitionBoard = BOARD.currentPlayer().makeMove(m).getTransitionBoard();

                //make a node with the new board. The current player is switched so the opponent will go in this next node
                BoardStateTree BT = new BoardStateTree(transitionBoard, MAX_NODES, MAX_DEPTH, CURRENT_DEPTH+1);

                //make a tree and retrieve the best value with this node
                int moveValue = BT.getBestMoveValue();

                //if the new move gave better results we save that
                if (moveValue > bestMoveValue) {bestMove = m; bestMoveValue = moveValue;}
            }
        }
        return bestMove;
    }

    /**
     * build the tree as far as allowed and return the best value we could find
     *
     * @return the value of the highest value leaf
     */
    protected int getBestMoveValue(){

        // we have made it as far as we are allowed to so we return the value of the current board
        if (CURRENT_DEPTH == MAX_DEPTH) return BOARD_VALUE;

        //get the MAX_NODES best moves
        List<Move> moves = filterMoves(BOARD.currentPlayer().getLegalMoves());

        //value of the best move
        int bestMoveValue = -500;

        //iterate through the moves and find the best value
        for (Move m : moves){

            //double check if the move is legal
            if (BOARD.currentPlayer().makeMove(m).getMoveStatus().isDone()){

                //get the new board after the move
                Board transitionBoard = BOARD.currentPlayer().makeMove(m).getTransitionBoard();

                //make a node with the new board. The current player is switched so the opponent will go in this next node
                BoardStateTree BT = new BoardStateTree(transitionBoard, MAX_NODES, MAX_DEPTH, CURRENT_DEPTH+1);

                //make a tree and retrieve the best value with this node
                int moveValue = BT.getBestMoveValue();

                //if the new move gave a better value we save that value
                if (moveValue > bestMoveValue) bestMoveValue = moveValue;
            }
        }
        return bestMoveValue;
    }

    /**
     * filter out the moves that will decrease the value, will also only pick the MAX_NODES best moves
     * TODO: we are now just taking the first MAX_NODES that wont decrease the value. We want the best ones!
     *
     * @param moves collection of moves we are filtering
     * @return a collection of the moves we will use to branch out with, size <= MAX_NODES
     */
    protected List<Move> filterMoves(Collection<Move> moves){

        //the list we will store the filtered moves in
        List<Move> filteredMoves = new ArrayList<Move>();

        //iterate through all the moves
        for (Move m : moves){

            //make sure the move is legal
            if (BOARD.currentPlayer().makeMove(m).getMoveStatus().isDone()){

                //get the new board after the move
                Board transitionBoard = BOARD.currentPlayer().makeMove(m).getTransitionBoard();

                //make a evaluator for the new boar. note that the current player of that board has changed
                BoardEval transitionBoardEval = new BoardEval(transitionBoard);

                //get the value of the board relative to the current player before the move (the current player of BOARD)
                int transitionBoardValue = transitionBoardEval.getValueOfOpponentPlayer();

                //is the new value more than or the same as the current board value?
                if (transitionBoardValue >= BOARD_VALUE) {

                    //do we already have the maximum amount of moves?
                    if (filteredMoves.size() < MAX_NODES) {
                        filteredMoves.add(m);
                    }
                    else {
                        //TODO: replace the worst move in filteredMoves with m
                    }
                }
            }
        }
        return filteredMoves;
    }
}