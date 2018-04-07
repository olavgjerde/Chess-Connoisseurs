package player.basicAI;

import board.Board;
import board.BoardUtils;
import board.Move;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import pieces.Alliance;
import player.MoveTransition;

import java.util.Collection;
import java.util.Comparator;

import static board.Move.*;

/**
 * A co-recursive implementation of the "MiniMax" algorithm
 *
 * @see <a href=https://en.wikipedia.org/wiki/Minimax>MiniMax</a>
 * @see <a href=https://en.wikipedia.org/wiki/Alpha-beta_pruning>Alpha-beta pruning</a>
 */
public class MiniMax implements MoveStrategy {
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;

    public MiniMax(int searchDepth) {
        this.boardEvaluator = new RegularBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    @Override
    public String toString() {
        return "MiniMax";
    }

    /**
     * Execute the mini-max algorithm for the current player, the method will check the alliance of the player and treat
     * the white player as the maximizing, and the black player as minimizing.
     *
     * @param board to generate move for
     * @return best move found
     */
    @Override
    public Move execute(Board board) {
        final long startTime = System.currentTimeMillis();
        Move bestMove = new NullMove();

        int highestEncounteredValue = Integer.MIN_VALUE;
        int lowestEncounteredValue = Integer.MAX_VALUE;
        int currentValue;

        System.out.println(board.currentPlayer().getAlliance() + " EVALUATING with depth: " + searchDepth);

        for (Move move : moveSort(board.currentPlayer().getLegalMoves())) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                if (board.currentPlayer().getAlliance() == Alliance.WHITE) {
                    currentValue = min(moveTransition.getTransitionBoard(), searchDepth - 1, highestEncounteredValue, lowestEncounteredValue);
                } else {
                    currentValue = max(moveTransition.getTransitionBoard(), searchDepth - 1, highestEncounteredValue, lowestEncounteredValue);
                }

                if (board.currentPlayer().getAlliance() == Alliance.WHITE && currentValue > highestEncounteredValue) {
                    // maximizing player
                    highestEncounteredValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getAlliance() == Alliance.BLACK && currentValue < lowestEncounteredValue) {
                    // minimizing player
                    lowestEncounteredValue = currentValue;
                    bestMove = move;
                }
            }
        }

        final long timeSpent = System.currentTimeMillis() - startTime;
        System.out.println("\tTIME TAKEN: " + timeSpent + "ms");

        return bestMove;
    }

    /**
     * Minimizing function
     *
     * @param board to make move on
     * @param searchDepth current depth of search
     * @param alpha
     * @param beta
     * @return lowest board value encountered
     */
    private int min(Board board, int searchDepth, int alpha, int beta) {
        if (searchDepth == 0 || isEndGame(board)) {
            return this.boardEvaluator.evaluate(board, searchDepth);
        }

        int currentLowestValue = beta;
        for (Move move : moveSort(board.currentPlayer().getLegalMoves())) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);

            if (moveTransition.getMoveStatus().isDone()) {
                currentLowestValue = Math.min(currentLowestValue,
                        max(moveTransition.getTransitionBoard(), searchDepth - 1, alpha, currentLowestValue));

                // alpha beta break off
                if (currentLowestValue <= alpha) break;
            }
        }
        return currentLowestValue;
    }

    /**
     * Maximizing function
     *
     * @param board to make move on
     * @param searchDepth current depth of search
     * @param alpha
     * @param beta
     * @return highest board value encountered
     */
    private int max(Board board, int searchDepth, int alpha, int beta) {
        if (searchDepth == 0 || isEndGame(board)) {
            return this.boardEvaluator.evaluate(board, searchDepth);
        }

        int currentHighestValue = alpha;
        for (Move move : moveSort(board.currentPlayer().getLegalMoves())) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);

            if (moveTransition.getMoveStatus().isDone()) {
                currentHighestValue = Math.max(currentHighestValue,
                        min(moveTransition.getTransitionBoard(), searchDepth - 1, currentHighestValue, beta));

                // alpha beta break off
                if (beta <= currentHighestValue) break;
            }
        }
        return currentHighestValue;
    }

    /**
     * Check if the current player is in checkmate or in a stalemate
     *
     * @param board to evaluate
     * @return true if current player is in checkmate or stalemate, false otherwise
     */
    private boolean isEndGame(Board board) {
        return board.currentPlayer().isInCheckmate() || board.currentPlayer().isInStalemate();
    }

    /**
     * Sorts moves contained in a collection
     * General comparison outline:
     * Check if move puts a player in check
     * If it is an attack move
     * If it is a castling move
     * The value of the piece moving
     *
     * @param moves to sort
     * @return a immutable collection which contains the moves in sorted order
     */
    private static Collection<Move> moveSort(final Collection<Move> moves) {
        return Ordering.from((Comparator<Move>) (moveA, moveB) -> ComparisonChain.start()
                .compareTrueFirst(BoardUtils.hasCheckStatus(moveA.getBoard()), BoardUtils.hasCheckStatus(moveB.getBoard()))
                .compareTrueFirst(moveA.isAttack(), moveB.isAttack())
                .compareTrueFirst(moveA.isCastlingMove(), moveB.isCastlingMove())
                .compare(moveB.getMovedPiece().getPieceType().getPieceValue(), moveA.getMovedPiece().getPieceType().getPieceValue())
                .result()).immutableSortedCopy(moves);
    }
}
