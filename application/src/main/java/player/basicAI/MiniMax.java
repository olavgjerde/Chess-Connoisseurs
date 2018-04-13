package player.basicAI;

import board.Board;
import board.BoardUtils;
import board.Move;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import pieces.Alliance;
import pieces.Piece;
import player.MoveTransition;

import java.util.*;

import static board.Move.*;

/**
 * An implementation of the "MiniMax" algorithm with alpha-beta pruning and quiescence search
 *
 * @see <a href=https://en.wikipedia.org/wiki/Minimax>MiniMax</a>
 * @see <a href=https://en.wikipedia.org/wiki/Alpha-beta_pruning>Alpha-beta pruning</a>
 */
public class MiniMax implements MoveStrategy {
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    private static boolean shuffleMode = false;
    private boolean printMoveInformation;

    private int quiescenceCount;
    private int totalQuiescence;
    private int maxQuiescence;

    /**
     * The constructor for the MiniMax Alpha-beta algorithm
     * @param searchDepth depth of the search (plys)
     * @param maxQuiescence how many times the ai is allowed to search deeper per top move node
     * @param usePieceSquareBoards to use piece-square board or not
     * @param printMoveInformation to print information about
     * @param shuffleMode to shuffle moves, this removes move ordering and makes it random (for use with random board generator)
     */
    public MiniMax(int searchDepth, int maxQuiescence, boolean usePieceSquareBoards, boolean printMoveInformation, boolean shuffleMode) {
        this.boardEvaluator = new RegularBoardEvaluator(usePieceSquareBoards);
        this.searchDepth = searchDepth;
        this.maxQuiescence = maxQuiescence;
        this.printMoveInformation = printMoveInformation;
        this.shuffleMode = shuffleMode;
    }

    @Override
    public String toString() {
        return "Mini-Max/AB+MO+QUIESCENCE";
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
        int currentValue = 0;

        if (printMoveInformation) System.out.println(board.currentPlayer().getAlliance() + " EVALUATING with depth: " + searchDepth);
        Collection<Move> sorted = moveSortExpensive(board.currentPlayer().getLegalMoves());
        int moveCount = 1;
        for (Move move : sorted) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            //Reset quiescence for every start node
            this.quiescenceCount = 0;
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
                    if (moveTransition.getTransitionBoard().getBlackPlayer().isInCheckmate()) break;
                } else if (board.currentPlayer().getAlliance() == Alliance.BLACK && currentValue < lowestEncounteredValue) {
                    // minimizing player
                    lowestEncounteredValue = currentValue;
                    bestMove = move;
                    if (moveTransition.getTransitionBoard().getWhitePlayer().isInCheckmate()) break;
                }
            }

            if (printMoveInformation) {
                System.out.println(this.toString() + "(" + searchDepth + ") (" + moveCount++ + "/" + sorted.size() + ") "
                        + "MOVE ANALYZED: " + move + " "
                        + "QUIESCENCE COUNT: " + quiescenceCount + " "
                        + "BEST MOVE: " + bestMove + " [Score: " + currentValue + "]");
            }

        }

        if (printMoveInformation) {
            final long timeSpent = System.currentTimeMillis() - startTime;
            System.out.println("\tTIME TAKEN: " + timeSpent + "ms");
            System.out.println("\tTOTAL QUIESCENCE COUNT: " + totalQuiescence);
        }

        return bestMove;
    }

    /**
     * Minimizing function
     *
     * @param board       to make move on
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
        for (Move move : moveSortStandard(board.currentPlayer().getLegalMoves())) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);

            if (moveTransition.getMoveStatus().isDone()) {
                currentLowestValue = Math.min(currentLowestValue,
                        max(moveTransition.getTransitionBoard(), calculateQuiescenceDepth(moveTransition, searchDepth), alpha, currentLowestValue));

                // alpha beta break off
                if (currentLowestValue <= alpha) break;
            }
        }
        return currentLowestValue;
    }

    /**
     * Maximizing function
     *
     * @param board       to make move on
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
        for (Move move : moveSortStandard(board.currentPlayer().getLegalMoves())) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);

            if (moveTransition.getMoveStatus().isDone()) {
                currentHighestValue = Math.max(currentHighestValue,
                        min(moveTransition.getTransitionBoard(), calculateQuiescenceDepth(moveTransition, searchDepth), currentHighestValue, beta));

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
     * Calculates the quiescence depth
     *
     * @param moveTransition transition that represents the last move
     * @param searchDepth    current depth of the mini-max search
     * @return the depth the mini-max should continue its search with
     * @see <a href="https://chessprogramming.wikispaces.com/Quiescence+Search">Quiescence</a>
     */
    private int calculateQuiescenceDepth(final MoveTransition moveTransition, final int searchDepth) {
        if (searchDepth == 1 && this.quiescenceCount < maxQuiescence) {
            int activityScore = 0;
            if (moveTransition.getTransitionBoard().currentPlayer().isInCheck()) {
                activityScore += 2;
            }
            for (final Move move : BoardUtils.retrieveLastNMoves(moveTransition.getTransitionBoard(), 4)) {
                if (move.isAttack()) activityScore += 1;
            }
            if (activityScore > 3) {
                this.quiescenceCount++;
                this.totalQuiescence++;
                return 2;
            }
        }
        return searchDepth - 1;
    }

    /**
     * Sorts moves contained in a collection
     * General comparison outline:
     * Check if move is on a board where one of the player are in check
     * If it is an attack move
     * If it is a castling move
     * The value of the piece moving
     *
     * @param moves to sort
     * @return a immutable collection which contains the moves in sorted order
     */
    private static Collection<Move> moveSortSmart(final Collection<Move> moves) {
        if (shuffleMode) {
            Collections.shuffle((List)moves);
            return moves;
        }
        return Ordering.from((Comparator<Move>) (moveA, moveB) -> ComparisonChain.start()
                .compareTrueFirst(hasCheckStatus(moveA.getBoard()), hasCheckStatus(moveB.getBoard()))
                .compareTrueFirst(moveA.isAttack(), moveB.isAttack())
                .compareTrueFirst(moveA.isCastlingMove(), moveB.isCastlingMove())
                .compare(moveB.getMovedPiece().getPieceType().getPieceValue(), moveA.getMovedPiece().getPieceType().getPieceValue())
                .result()).immutableSortedCopy(moves);
    }

    /**
     * Sorts moves contained in a collection
     * General comparison outline:
     * Check if move puts opponent in check
     * Check if move is a castling move
     * Use MVV-LVA heuristic
     *
     * @param moves to sort
     * @return sorted collection of moves
     */
    private static Collection<Move> moveSortExpensive(Collection<Move> moves) {
        if (shuffleMode) {
            Collections.shuffle((List)moves);
            return moves;
        }
        return Ordering.from((Comparator<Move>) (moveA, moveB) -> ComparisonChain.start()
                .compareTrueFirst(moveCreatesCheck(moveA), moveCreatesCheck(moveB))
                .compareTrueFirst(moveA.isCastlingMove(), moveB.isCastlingMove())
                .compare(mvvlva(moveB), mvvlva(moveA))
                .result()).immutableSortedCopy(moves);
    }

    /**
     * Sorts moves contained in a collection
     * General comparison outline:
     * Check if move is a castling move
     * Use MVV-LVA heuristic
     *
     * @param moves to sort
     * @return sorted collection of moves
     */
    private static Collection<Move> moveSortStandard(Collection<Move> moves) {
        if (shuffleMode) {
            Collections.shuffle((List)moves);
            return moves;
        }
        return Ordering.from((Comparator<Move>) (moveA, moveB) -> ComparisonChain.start()
                .compareTrueFirst(moveA.isCastlingMove(), moveB.isCastlingMove())
                .compare(mvvlva(moveB), mvvlva(moveA))
                .result()).immutableSortedCopy(moves);
    }

    /**
     * Checks if the move puts the opponent player in check
     *
     * @param move to evaluate
     * @return true if opponent is in check
     */
    private static boolean moveCreatesCheck(final Move move) {
        final Board board = move.getBoard();
        MoveTransition transition = board.currentPlayer().makeMove(move);
        return transition.getTransitionBoard().currentPlayer().isInCheck();
    }

    /**
     * Gives a score for a move according to the "Most Valueable Victim - Least Valuable Aggressor" heuristic
     *
     * @param move to evaluate
     * @return score for the move
     * @see <a href="https://chessprogramming.wikispaces.com/MVV-LVA">MVV-LVA</a>
     */
    private static int mvvlva(final Move move) {
        final Piece movingPiece = move.getMovedPiece();
        if (move.isAttack()) {
            final Piece attackedPiece = move.getAttackedPiece();
            return (attackedPiece.getPieceType().getPieceValue() - movingPiece.getPieceType().getPieceValue() + Piece.PieceType.KING.getPieceValue()) * 100;
        }
        return Piece.PieceType.KING.getPieceValue() - movingPiece.getPieceType().getPieceValue();
    }

    /**
     * Checks if either of the players on a board is in check
     * (for use with sorting)
     *
     * @param board to evaluate
     * @return true if either of the players is in check
     */
    private static boolean hasCheckStatus(Board board) {
        return board.getWhitePlayer().isInCheck() || board.getBlackPlayer().isInCheck();
    }
}
