package player.basicAI;

import board.Board;
import pieces.Piece;
import player.Player;

/**
 * This class contains methods for assigning a score to a chess board
 * based on it's different states.
 */
public final class RegularBoardEvaluator implements BoardEvaluator {
    private static final int CHECK_BONUS = 50;
    private static final int CHECKMATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 60;

    /**
     * Evaluate the current state of the board.
     * If white has an advantage the value will be positive,
     * if black has an advantage the number will be negative.
     *
     * @param board to evaluate
     * @param depth of the evaluation
     * @return score of the board-state
     */
    @Override
    public int evaluate(Board board, int depth) {
        return scorePlayer(board.getWhitePlayer(), depth) - scorePlayer(board.getBlackPlayer(), depth);
    }

    /**
     * Get the score for a player given a player to evaluate
     *
     * @param player to evaluate
     * @param depth  of evaluation
     * @return the total score for a player
     */
    private int scorePlayer(Player player, int depth) {
        return pieceValues(player) +
                mobilityValue(player) +
                checkValue(player) +
                checkmateValue(player, depth) +
                castledValue(player);
    }

    /**
     * Calculate the total value for the player's set of pieces
     *
     * @param player to evaluate
     * @return total value of all pieces
     */
    private int pieceValues(Player player) {
        int pieceValueScore = 0;
        for (Piece piece : player.getActivePieces()) {
            pieceValueScore += piece.getPieceType().getPieceValue();
        }
        return pieceValueScore;
    }

    /**
     * Check if the player's opponent is in checkmate, and account a bonus if so.
     *
     * @param player to evaluate
     * @param depth  of evaluation
     * @return a bonus for having the other player in checkmate
     */
    private static int checkmateValue(Player player, int depth) {
        return player.getOpponent().isInCheckmate() ? CHECKMATE_BONUS * depthBonus(depth) : 0;
    }

    /**
     * Calculate a bonus score based on the depth of an evaluation
     *
     * @param depth of evaluation
     * @return a higher value if depth is deeper
     */
    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    /**
     * Check if the player's opponent is in check, and account a bonus if so.
     *
     * @param player to evaluate
     * @return a bonus for having the opposite player in check
     */
    private static int checkValue(Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    /**
     * Check how many moves are available to a player
     *
     * @param player to evaluate
     * @return how many possible moves the player has
     */
    private static int mobilityValue(Player player) {
        return player.getLegalMoves().size();
    }

    /**
     * Check if the player is castled, and account a bonus if so
     *
     * @param player to evaluate
     * @return bonus for being castled
     */
    private int castledValue(Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }
}

