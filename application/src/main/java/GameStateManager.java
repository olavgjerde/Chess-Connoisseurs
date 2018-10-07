import board.*;
import board.Move.MoveFactory;
import board.Move.PawnPromotion;
import pieces.Alliance;
import pieces.Piece;
import player.MoveTransition;
import player.basicAI.BoardEvaluator;
import player.basicAI.MiniMax;
import player.basicAI.MoveStrategy;
import player.basicAI.RegularBoardEvaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The game state manager wraps the Board class and supplies the GUI
 * with methods that retrieve information about the current game state.
 */
class GameStateManager {
    private Board chessDataBoard;
    private boolean isWhiteAI, isBlackAI, tutorMode;
    private final int aiDepth;
    private final GameMode boardType;
    //Keep count of board history (board states)
    private final List<Board> boardHistory = new ArrayList<>();
    //Move history, even = white moves, odd = black moves
    private final List<Move> moveHistory = new ArrayList<>();
    //List of all the dead pieces
    private final List<Piece> takenPieces = new ArrayList<>();

    /**
     * Constructor for GameStateManager class
     *
     * @param isWhiteAI     if white ai is playing
     * @param isBlackAI     if black ai is playing
     * @param aiDepth       depth that ai should use for its search
     * @param boardType     enum ex. GameMode.RANDOM, GameMode.HORDE etc
     */
    GameStateManager(boolean isWhiteAI, boolean isBlackAI, int aiDepth, GameMode boardType) {
        this.isWhiteAI = isWhiteAI;
        this.isBlackAI = isBlackAI;
        this.aiDepth = aiDepth;
        this.boardType = boardType;

        if (boardType.equals(GameMode.RANDOM)) this.chessDataBoard = Board.createRandomBoard();
        else if (boardType.equals(GameMode.HORDE)) this.chessDataBoard = Board.createHordeBoard();
        else if (boardType.equals(GameMode.LIGHTBRIGADE)) this.chessDataBoard = Board.createLightBrigadeBoard();
        else if (boardType.equals(GameMode.TUTOR)) {
            //Pick random tutor-board
            switch (ThreadLocalRandom.current().nextInt(4)) {
                case 0: this.chessDataBoard = Board.createTutorBoardOne(); break;
                case 1: this.chessDataBoard = Board.createTutorBoardTwo(); break;
                case 2: this.chessDataBoard = Board.createTutorBoardThree(); break;
                case 3: this.chessDataBoard = Board.createTutorBoardFour(); break;
                default: this.chessDataBoard = Board.createTutorBoardOne();
            }
            this.tutorMode = true;
        }
        else this.chessDataBoard = Board.createStandardBoard();

        //Add first board to boardHistory
        this.boardHistory.add(this.chessDataBoard);
    }

    /**
     * Tries to make a move on the chess board given two coordinates
     *
     * @param start       coordinate of the move
     * @param destination coordinate of the move
     * @return returns true if move was made, false otherwise
     */
    boolean makeMove(Coordinate start, Coordinate destination) {
        Move moveAttempt = MoveFactory.createMove(chessDataBoard, start, destination);
        MoveTransition moveTransition = chessDataBoard.currentPlayer().makeMove(moveAttempt);

        if (moveTransition.getMoveStatus().isDone()) {
            if (moveAttempt instanceof PawnPromotion) {
                Piece.PieceType userSelectedType = ChessGUI.showPromotionWindow();
                List<PawnPromotion> availablePromotions = MoveFactory.getPromotionMoves(chessDataBoard);
                for (PawnPromotion promotion : availablePromotions) {
                    if (promotion.getUpgradeType() == userSelectedType &&
                            promotion.getDestinationCoordinate().equals(destination)) {
                        //Changes the move that altered the board
                        moveTransition = chessDataBoard.currentPlayer().makeMove(promotion);
                        moveAttempt = promotion;
                        break;
                    }
                }
            }

            chessDataBoard = moveTransition.getTransitionBoard();
            moveHistory.add(moveAttempt);
            boardHistory.add(chessDataBoard);
            if (moveAttempt.isAttack()) takenPieces.add(moveAttempt.getAttackedPiece());
            return true;
        }
        return false;
    }

    /**
     * Lets the AI make a move on the board
     *
     * @return returns true if move was made, false otherwise
     */
    boolean makeAIMove() {
        if ((currentPlayerAlliance() == Alliance.WHITE && isWhiteAI) || (currentPlayerAlliance() == Alliance.BLACK && isBlackAI)) {
            MoveStrategy moveStrategy = new MiniMax(aiDepth, 1000, true, true);
            final Move AIMove = moveStrategy.execute(chessDataBoard);
            final MoveTransition moveTransition = chessDataBoard.currentPlayer().makeMove(AIMove);

            if (moveTransition.getMoveStatus().isDone()) {
                //clear out undone boards and moves
                chessDataBoard = moveTransition.getTransitionBoard();
                moveHistory.add(AIMove);
                boardHistory.add(chessDataBoard);
                if (AIMove.isAttack()) takenPieces.add(AIMove.getAttackedPiece());
                return true;
            }
        }
        return false;
    }

    /**
     * Undo the current players last move if more than
     * 3 moves have been made on the current board
     */
    void undoMove() {
        if (boardHistory.size() < 3 || moveHistory.isEmpty()) return;
        for (int i = 0; i < 2; i++) {
            boardHistory.remove(boardHistory.size() - 1);
            Move lastMove = moveHistory.get(moveHistory.size() - 1);
            if (lastMove.isAttack()) takenPieces.remove(lastMove.getAttackedPiece());
            moveHistory.remove(lastMove);
        }
        this.chessDataBoard = boardHistory.get(boardHistory.size() - 1);
    }

    /**
     * Checks if undo is allowed on the board
     *
     * @return true if undo is allowed, false otherwise
     */
    boolean undoIsIllegal() {
        return boardHistory.size() < 3 || (!isBlackAI && !isWhiteAI);
    }

    /**
     * Sets the toggles for white and black ai to false
     */
    void killAI() {
        this.isBlackAI = false;
        this.isWhiteAI = false;
    }

    /**
     * @return true if the current player is in check, false otherwise
     */
    boolean currentPlayerInCheck() {
        return this.chessDataBoard.currentPlayer().isInCheck();
    }

    /**
     * @return true if the current player is in checkmate
     */
    boolean currentPlayerInCheckMate() {
        return this.chessDataBoard.currentPlayer().isInCheckmate();
    }

    /**
     * @return true if the current player is in a stalemate
     */
    boolean currentPlayerInStaleMate() {
        return this.chessDataBoard.currentPlayer().isInStalemate();
    }

    /**
     * @return the Alliance of the player currently in turn
     */
    Alliance currentPlayerAlliance() {
        return chessDataBoard.currentPlayer().getAlliance();
    }

    /**
     * @return true if black ai is enabled, false otherwise
     */
    boolean isBlackAI() {
        return this.isBlackAI;
    }

    /**
     * @return true if white ai is enabled, false otherwise.
     */
    boolean isWhiteAI() {
        return this.isWhiteAI;
    }

    /**
     * @return true if tutor mode is enabled, false otherwise
     */
    boolean isTutorMode() {
        return this.tutorMode;
    }

    /**
     * Get the board type enum of the current board
     * @see GameMode
     * @return the board type in the form of an enum
     */
    GameMode getBoardType() {
        return boardType;
    }

    /**
     * Check if the game is over
     *
     * @return true if there are no further moves for the player (or all black pieces are gone - horde mode)
     */
    boolean isGameOver() {
        boolean checkmate = chessDataBoard.currentPlayer().isInCheckmate();
        boolean stalemate = chessDataBoard.currentPlayer().isInStalemate();
        boolean repetition = isDraw();

        //For horde mode game over condition
        boolean allBlackPiecesTaken = chessDataBoard.getBlackPlayer().getActivePieces().isEmpty();

        return checkmate || stalemate || repetition || allBlackPiecesTaken;
    }

    /**
     * Check if a single board state is repeated within the last 5 turns to check if its a draw
     *
     * @return true if its a draw, false otherwise
     */
    boolean isDraw() {
        int counter = 0;
        for (Board b : boardHistory) {
            if (chessDataBoard.toString().equals(b.toString())) counter++;
            if (counter >= 4) return true;
        }
        return false;
    }

    /**
     * @return the depth the AI is using for its searches
     */
    int getAiDepth() {
        return this.aiDepth;
    }

    /**
     * Runs the AI board evaluation function on the current board
     *
     * @return the score the board was given
     */
    int getBoardEvaluation() {
        BoardEvaluator boardEvaluator = new RegularBoardEvaluator(true);
        int score = boardEvaluator.evaluate(chessDataBoard, 4);
        return chessDataBoard.currentPlayer().getAlliance() == Alliance.WHITE ? score : score * -1;
    }

    /**
     * @return the PNG notation of the last move made on the board
     */
    String getLastMoveText() {
        String lastMoveText = "";
        if (!moveHistory.isEmpty()) {
            lastMoveText = moveHistory.get(moveHistory.size() - 1).toString();
            if (boardHistory.get(boardHistory.size() - 1).currentPlayer().isInCheckmate()) lastMoveText += "#";
            else if (boardHistory.get(boardHistory.size() - 1).currentPlayer().isInCheck()) lastMoveText += "+";
        }
        return lastMoveText;
    }

    /**
     * Get the last move
     *
     * @return the last move that happened on the board
     */
    Move getLastMove() {
        return moveHistory.isEmpty() ? null : moveHistory.get(moveHistory.size() - 1);
    }

    /**
     * Lets the AI suggest a move
     *
     * @param searchDepth for the ai looking for "best move"
     * @param quiescence  how many deeper searches is allowed
     * @return move that ai found to be best
     */
    Move getHint(int searchDepth, int quiescence) {
        MoveStrategy strategy = new MiniMax(searchDepth, quiescence, true, true);
        return strategy.execute(this.chessDataBoard);
    }

    /**
     * @return an immutable copy of the list of taken pieces
     */
    List<Piece> getTakenPieces() {
        return takenPieces;
    }

    /**
     * Find the tile on the chess Board given a coordinate
     *
     * @param coordinate of tile
     * @return tile on the given coordinate
     */
    Tile getTile(Coordinate coordinate) {
        return chessDataBoard.getTile(coordinate);
    }

    /**
     * Makes a list of all legal moves available to the current player from the given board tile
     *
     * @param tile tile on the board
     * @return a list of legal moves available from a given tile
     */
    Collection<Coordinate> getLegalMovesFromTile(Tile tile) {
        List<Move> temp = new ArrayList<>(this.chessDataBoard.currentPlayer().getLegalMovesForPiece(tile.getPiece()));
        List<Coordinate> coordinatesToHighlight = new ArrayList<>();
        for (Move move : temp) {
            if (this.chessDataBoard.currentPlayer().makeMove(move).getMoveStatus().isDone()) {
                coordinatesToHighlight.add(move.getDestinationCoordinate());
            }
        }
        return coordinatesToHighlight;
    }
}
