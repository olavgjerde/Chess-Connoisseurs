package player.basicAI;

import board.Board;

/**
 * The general interface for a board evaluator
 */
public interface BoardEvaluator {

    /**
     * This method shall generate the score of a board, a measure of "goodness"
     * @param board to evaluate
     * @param depth of the evaluation
     * @return score of the current board (positive value may acknowledge that the white player has the upper hand
     * and negative value may acknowledge that the black player has the upper hand)
     */
    int evaluate(Board board, int depth);

}
