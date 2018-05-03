import board.Board;
import board.Move;

import java.util.ArrayList;

/**
 * Keep track of all the previous moves and board states to allow for undoing a move and to check for draw
 */
public class BoardStateManager {

    //Keep count of board history (board states)
    private ArrayList<Board> boardHistory = new ArrayList<>();
    //Move history, even = white moves, odd = black moves
    private ArrayList<Move> moveHistory = new ArrayList<>();

    /**
     * Constructor for the BoardStateManager class
     * @param initialBoard the first initial board state
     */
    public BoardStateManager(Board initialBoard) {
        boardHistory = new ArrayList<>();
        moveHistory = new ArrayList<>();
        boardHistory.add(initialBoard);
    }

    /**
     * Update the gameStateManager with a new board and move
     * @param board the new current board state
     * @param move the new last move
     */
    public void update(Board board, Move move) {
        boardHistory.add(board);
        moveHistory.add(move);
    }

    /**
     * Undo a move, lastBoardState and lastMove will now be the previous one before that
     * NB! this is only one move back in time, usually you would want to call this twice!
     */
    public void undo() {
        if (boardHistory.size() >= 1) boardHistory.remove(boardHistory.size()-1);
        if (!moveHistory.isEmpty()) moveHistory.remove(moveHistory.size()-1);
    }

    /**
     * Get the last board state.
     * @return the last board in the board history
     */
    public Board getLastBoardState() {
        return boardHistory.get(boardHistory.size()-1);
    }

    /**
     * Get the last move.
     * @return the last move in the move history
     */
    public Move getLastMove() {
        return moveHistory.isEmpty() ? null : moveHistory.get(moveHistory.size()-1);
    }

    /**
     * clear all the saved board states and moves
     */
    public void clear() {
        boardHistory.clear();
        moveHistory.clear();
    }

    /**
     * Size of the board history
     * @return board history size
     */
    public int boardHistorySize() {
        return boardHistory.size();
    }

    /**
     * Check if the last board state has already appeared 3 times before to see id its a draw
     * @return true if its a draw, false otherwise
     */
    public boolean isDraw(){
        if (boardHistory.stream().filter(b -> b.toString().equals(getLastBoardState().toString())).toArray().length >= 3) return true;
        return false;
    }
}
