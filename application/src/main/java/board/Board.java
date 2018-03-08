package board;

import java.util.ArrayList;

public class Board {
    private ArrayList<Piece> allPieces;
    private int width, height;

    public Board(ArrayList<Piece> allPieces, int width, int height) {
        this.allPieces = allPieces;
        this.width = width;
        this.height = height;
    }

    public void update(Move m) {
        //TODO: updates the board according to the Move object
    }
}
