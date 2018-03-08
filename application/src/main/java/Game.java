import board.Board;
import board.Move;
import board.Piece;
import board.Position;

import java.util.ArrayList;

public class Game {
    Board chessboard = new Board();

    private boolean move(Move x){
        if (checkMove(x)) {
            // update piece position
            return true;
        }

        return false;
    }

    private boolean checkMove(Move move) {
        return getLegalMoves(move.getPiece()).contains(move.getNewPosition());
    }

    /**
     * @return 1 if a winner is present, 0 if not
     */
    private int checkBoard(){
        // check state after each 'update'
        return 0;
    }

    private Board getChessboard() {
        return chessboard;
    }

    private ArrayList<Position> getLegalMoves(Piece piece) {
        //check which type the piece is, return legal moves in relation to piece.

        return new ArrayList<Position>();
    }
}
