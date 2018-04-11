package player.basicAI;

import board.Board;
import board.Move;
import pieces.*;
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
    // todo: bonus for keep king "castle-able" -> private final static int CASTLE_CAPABLE_BONUS = 25;
    private final static int MOBILITY_MULTIPLIER = 2;
    private final static int ATTACK_MULTIPLIER = 2;
    private final static int TWO_BISHOPS_BONUS = 50;

    private final boolean usePieceSquareBoards;
    private boolean isPieceSquareEndGame = false;

    public RegularBoardEvaluator(boolean usePieceSquareBoards) {
        this.usePieceSquareBoards = usePieceSquareBoards;
    }

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
        if (usePieceSquareBoards) calculatePieceSquareEndGame(board);
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
                checkmateValue(player, depth) +
                castledValue(player) +
                attackValue(player);
    }

    /**
     * Calculate the total value for the player's set of pieces
     *
     * @param player to evaluate
     * @return total value of all pieces (+ a bonus for having both bishops)
     */
    private int pieceValues(Player player) {
        int pieceValueScore = 0, numberOfBishops = 0;
        for (Piece piece : player.getActivePieces()) {
            pieceValueScore += piece.getPieceType().getPieceValue();
            if (usePieceSquareBoards) pieceValueScore += piece.locationValue(isPieceSquareEndGame);
            if (piece.getPieceType().toString().equals("B")) numberOfBishops++;
        }
        return pieceValueScore + (numberOfBishops == 2 ? TWO_BISHOPS_BONUS : 0);
    }

    /**
     * Check if the player's opponent is in checkmate, and account a bonus if so.
     * If not, check if opponent is in check.
     *
     * @param player to evaluate
     * @param depth  of evaluation
     * @return a bonus for having the other player in checkmate, or in check.
     */
    private static int checkmateValue(Player player, int depth) {
        return player.getOpponent().isInCheckmate() ? CHECKMATE_BONUS * depthBonus(depth) : checkValue(player);
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
     * Calculate a bonus score based on the depth of an evaluation
     *
     * @param depth of evaluation
     * @return a higher value if depth is deeper
     */
    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    /**
     * Calculate a score for how "mobile" a given player is on the current board
     *
     * @param player to evaluate
     * @return the score for mobility
     */
    private static int mobilityValue(Player player) {
        return MOBILITY_MULTIPLIER * mobilityRatio(player);
    }

    /**
     * Calculate a ratio of "mobility" for a given player depending on his/her legal move size
     * in relation to the opponents possible moves
     *
     * @param player to evaluate
     * @return ration of a given players move size vs opponents move size
     */
    private static int mobilityRatio(final Player player) {
        return (int)((player.getLegalMoves().size() * 100.0) / player.getOpponent().getLegalMoves().size());
    }

    /**
     * Check if the player is able to attack a more or equally valuable piece with a piece of lower value,
     * and account a bonus if so.
     *
     * @param player to evaluate
     * @return bonus for attacking
     */
    private static int attackValue(final Player player) {
        int attackScore = 0;
        for (Move move: player.getLegalMoves()) {
            if (move.isAttack()) {
                final Piece movingPiece = move.getMovedPiece();
                final Piece attackedPiece = move.getAttackedPiece();
                // if the moving piece attacks a more or equally valuable piece, increase score
                if (movingPiece.getPieceType().getPieceValue() <= attackedPiece.getPieceType().getPieceValue()) {
                    attackScore++;
                }
            }
        }
        return attackScore * ATTACK_MULTIPLIER;
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

    /**
     * Check if the end game has been reached
     * This corresponds to:
     * "1.Both sides have no queens or
     *  2.Every side which has a queen has additionally no other pieces or one minorpiece maximum."
     *  See link for more information -> King section.
     * @see <a href="https://chessprogramming.wikispaces.com/Simplified%20evaluation%20function">Simplified evaluation function</a>
     * @param board
     */
    private void calculatePieceSquareEndGame(Board board) {
        int whiteQueenCount = 0, blackQueenCount = 0,
                whiteBishopCount = 0, blackBishopCount = 0,
                whiteKnightCount = 0, blackKnightCount = 0,
                whiteOtherCount = 0, blackOtherCount = 0;

        for (Piece piece : board.getWhitePieces()) {
            if (piece instanceof Queen) whiteQueenCount++;
            else if (piece instanceof Bishop) whiteBishopCount++;
            else if (piece instanceof Knight) whiteKnightCount++;
            else whiteOtherCount++;
        }
        for (Piece piece : board.getBlackPieces()) {
            if (piece instanceof Queen) blackQueenCount++;
            else if (piece instanceof Bishop) blackBishopCount++;
            else if (piece instanceof Knight) blackKnightCount++;
            else blackOtherCount++;
        }

        if (whiteQueenCount == 0 && blackQueenCount == 0) {
            isPieceSquareEndGame = true;
        } else if ((whiteQueenCount == 1 && blackQueenCount == 0) &&
                ((whiteBishopCount == 1 && whiteKnightCount == 0) || (whiteKnightCount == 1 && whiteBishopCount == 0)) &&
                whiteOtherCount == 0) {
            isPieceSquareEndGame = true;
        } else if ((blackQueenCount == 1 && whiteQueenCount == 0) &&
                ((blackBishopCount == 1 && blackKnightCount == 0) || (blackKnightCount == 1 && blackBishopCount == 0)) &&
                blackOtherCount == 0) {
            isPieceSquareEndGame = true;
        }
    }
}

