package swingExampleGui;

import swingExampleGui.Table;

/**
 * Used for testing the legacy swing GUI
 */
public class ExampleGuiTester {
    public static void main(String[] args) {
        //Board board = Board.createStandardBoard();
        //System.out.println(board);

        Table.get().show();
    }
}