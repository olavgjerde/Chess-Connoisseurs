package AI;

import board.Board;
import board.Move;

import java.util.concurrent.TimeUnit;

public class AITest {



    public static void main(String[] args)  throws InterruptedException {
        Board b = Board.createStandardBoard();

        while (true) {

            //since we make a new board every move we also have to make a new AI every time with the new and updated board

            AI AI1 = new AI(b, false);
            Move m = AI1.getMove();
            b = m.execute();
            System.out.printf("WHITE MAKES MOVE: " + moveToString(m) + "\n");
            System.out.println("########################\n" + b.toString() + "########################\n");
            TimeUnit.SECONDS.sleep(1);

            AI AI2 = new AI(b, false);
            m = AI2.getMove();
            b = m.execute();
            System.out.printf("BLACK MAKES MOVE: " + moveToString(m) + "\n");
            System.out.println("########################\n" + b.toString() + "########################\n");
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * dont wanna mess around in the move class too much so i just add a simple toString method here
     * @return
     */
    private static String moveToString(Move m) {
        int x1 = m.getCurrentCoordinate().getX();
        int y1 = m.getCurrentCoordinate().getY();
        int x2 = m.getDestinationCoordinate().getX();
        int y2 = m.getDestinationCoordinate().getY();

        return ("(" + x1 + ", " + y1 + ") -> (" + x2 + ", " + y2 + ")");
    }
}
