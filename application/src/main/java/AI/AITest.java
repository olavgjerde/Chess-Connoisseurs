package AI;

import board.Board;
import player.Player;

import java.util.concurrent.TimeUnit;

public class AITest {



    public static void main(String[] args)  throws InterruptedException {
        Board b = Board.createStandardBoard();

        while (true) {

            //since we make a new board every move we also need to retrieve the players from the most recent board when making a move.
            //also have to make a new AI every time with the new and updated board

            Player white = b.getWhitePlayer();
            AI AI1 = new AI(b, false);
            b = white.makeMove(AI1.getMove()).getTransitionBoard();
            System.out.println("########################\n" + b.toString() + "########################\n");
            TimeUnit.SECONDS.sleep(1);

            Player black = b.getBlackPlayer();
            AI AI2 = new AI(b, false);
            b = black.makeMove(AI2.getMove()).getTransitionBoard();
            System.out.println("########################\n" + b.toString() + "########################\n");
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
