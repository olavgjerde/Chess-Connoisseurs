package board;

import pieces.*;
import player.BlackPlayer;
import player.MoveTransition;
import player.Player;
import player.WhitePlayer;
import player.basicAI.MiniMax;
import player.basicAI.MoveStrategy;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class represents a chessboard.
 * Note: a Map<Coordinate, Tile> is the base representation of the board layout,
 * where a Coordinate object as key gives a Tile object as value with some content.
 */
public class Board {
    private final Map<Coordinate, Tile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassantPawn;
    private final Move transitionMove;

    public Board(Builder builder) {
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(builder, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(builder, Alliance.BLACK);
        this.enPassantPawn = builder.enPassantPawn;

        Collection<Move> whiteLegalMoves = calculateLegalMoves(this.whitePieces);
        Collection<Move> blackLegalMoves = calculateLegalMoves(this.blackPieces);
        this.whitePlayer = new WhitePlayer(this, whiteLegalMoves, blackLegalMoves);
        this.blackPlayer = new BlackPlayer(this, blackLegalMoves, whiteLegalMoves);

        this.currentPlayer = builder.nextMoveMaker.choosePlayerByAlliance(this.whitePlayer, this.blackPlayer);
        this.transitionMove = builder.transitionMove;
    }

    /**
     * Creates a chessboard with a the layout given by the builder object
     * @param builder defines the layout/settings of the board
     * @return a Map with coordinates as keys and tiles as values
     */
    private Map<Coordinate, Tile> createGameBoard(Builder builder) {
        Map<Coordinate, Tile> coordToTile = new HashMap<>();
        for (int i = 0; i < BoardUtils.getHeight(); i++) {
            for (int j = 0; j < BoardUtils.getWidth(); j++) {
                coordToTile.put(new Coordinate(j,i), Tile.createTile(new Coordinate(j,i), builder.boardConfig.get(new Coordinate(j,i))));
            }
        }
        return coordToTile;
    }

    /**
     * Calculates how many active pieces there is of a given alliance on a board
     * @param builder board-builder which holds piece layout
     * @param alliance to calculate for (black/white)
     * @return a list of active pieces of a given alliance
     */
    private static Collection<Piece> calculateActivePieces(Builder builder, Alliance alliance) {
        final List<Piece> activePieces = new ArrayList<>(BoardUtils.getWidth()*2);
        for (Piece piece : builder.boardConfig.values()) {
            if (piece.getPieceAlliance() == alliance) {
                activePieces.add(piece);
            }
        }
        return Collections.unmodifiableList(activePieces);
    }

    /**
     * Generates a list of all legal moves given a collection of pieces
     * @param pieces to evaluate legal moves for
     * @return a list with all possible legal moves
     */
    private Collection<Move> calculateLegalMoves(Collection<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>();
        for (Piece piece : pieces) {
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }
        return legalMoves;
    }

    /**
     * Returns a tile object given a coordinate
     * @param tileCoordinate coordinate of tile to get
     * @return the Tile-object at a given coordinate
     */
    public Tile getTile(Coordinate tileCoordinate) {
        return this.gameBoard.get(tileCoordinate);
    }

    /**
     * @return the pawn that is open for an 'en passant' attack
     */
    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    /**
     * @return all the black pieces on the board
     */
    public Collection<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    /**
     * @return all the white pieces on the board
     */
    public Collection<Piece> getWhitePieces() {
        return this.whitePieces;
    }

    /**
     * @return the Player object which controls the white pieces
     */
    public WhitePlayer getWhitePlayer() {
        return whitePlayer;
    }

    /**
     * @return the Player object which controls the black pieces
     */
    public BlackPlayer getBlackPlayer() {
        return blackPlayer;
    }

    /**
     * Adds all of the black and white player's moves to one list
     * @return an Iterable list of all the moves
     */
    public Collection<Move> getAllLegalMoves() {
        final List<Move> allMoves = new ArrayList<>();
        allMoves.addAll(this.getWhitePlayer().getLegalMoves());
        allMoves.addAll(this.getBlackPlayer().getLegalMoves());
        return Collections.unmodifiableList(allMoves);
    }

    /**
     * @return the Player currently playing ('in charge')
     */
    public Player currentPlayer() {
        return this.currentPlayer;
    }

    /**
     * @return the move that changed this board into its current state
     */
    public Move getTransitionMove() {
        return transitionMove;
    }

    /**
     * Constructs a text visualisation of the board
     * @return a string representation of the board-object
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BoardUtils.getHeight(); i++) {
            for (int j = 0; j < BoardUtils.getWidth(); j++) {
                final String tileText = this.gameBoard.get(new Coordinate(j,i)).toString();
                builder.append(String.format("%3s", tileText));
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * Constructs a board with a standard chess layout
     * @return a Board with the layout defined in the method below
     */
    public static Board createStandardBoard() {
        final Builder builder = new Builder();
        // Black pieces
        builder.setPiece(new Rook(new Coordinate(0,0), Alliance.BLACK));
        builder.setPiece(new Knight(new Coordinate(1,0), Alliance.BLACK));
        builder.setPiece(new Bishop(new Coordinate(2,0), Alliance.BLACK));
        builder.setPiece(new Queen(new Coordinate(3,0), Alliance.BLACK));
        builder.setPiece(new King(new Coordinate(4,0), Alliance.BLACK));
        builder.setPiece(new Bishop(new Coordinate(5,0), Alliance.BLACK));
        builder.setPiece(new Knight(new Coordinate(6,0), Alliance.BLACK));
        builder.setPiece(new Rook(new Coordinate(7,0), Alliance.BLACK));
        builder.setPiece(new Pawn(new Coordinate(0,1), Alliance.BLACK, false));
        builder.setPiece(new Pawn(new Coordinate(1,1), Alliance.BLACK, false));
        builder.setPiece(new Pawn(new Coordinate(2,1), Alliance.BLACK, false));
        builder.setPiece(new Pawn(new Coordinate(3,1), Alliance.BLACK, false));
        builder.setPiece(new Pawn(new Coordinate(4,1), Alliance.BLACK, false));
        builder.setPiece(new Pawn(new Coordinate(5,1), Alliance.BLACK, false));
        builder.setPiece(new Pawn(new Coordinate(6,1), Alliance.BLACK, false));
        builder.setPiece(new Pawn(new Coordinate(7,1), Alliance.BLACK, false));

        // White pieces
        builder.setPiece(new Rook(new Coordinate(0,7), Alliance.WHITE));
        builder.setPiece(new Knight(new Coordinate(1,7), Alliance.WHITE));
        builder.setPiece(new Bishop(new Coordinate(2,7), Alliance.WHITE));
        builder.setPiece(new Queen(new Coordinate(3,7), Alliance.WHITE));
        builder.setPiece(new King(new Coordinate(4,7), Alliance.WHITE));
        builder.setPiece(new Bishop(new Coordinate(5,7), Alliance.WHITE));
        builder.setPiece(new Knight(new Coordinate(6,7), Alliance.WHITE));
        builder.setPiece(new Rook(new Coordinate(7,7), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(0,6), Alliance.WHITE, false));
        builder.setPiece(new Pawn(new Coordinate(1,6), Alliance.WHITE, false));
        builder.setPiece(new Pawn(new Coordinate(2,6), Alliance.WHITE, false));
        builder.setPiece(new Pawn(new Coordinate(3,6), Alliance.WHITE, false));
        builder.setPiece(new Pawn(new Coordinate(4,6), Alliance.WHITE, false));
        builder.setPiece(new Pawn(new Coordinate(5,6), Alliance.WHITE, false));
        builder.setPiece(new Pawn(new Coordinate(6,6), Alliance.WHITE, false));
        builder.setPiece(new Pawn(new Coordinate(7,6), Alliance.WHITE, false));
        builder.setMoveMaker(Alliance.WHITE);
        return builder.build();
    }

    /**
     * Uses the AI to create a random starting point for the game
     * @return board with X number of pre-made moves
     */
    public static Board createRandomBoard() {
        Board board = Board.createStandardBoard();
        final MoveStrategy AI = new MiniMax(1, 0, true, false);

        // create empty board to hold last board before change
        Board boardBeforeChange = new Board(new Builder().setMoveMaker(Alliance.WHITE));
        int i;
        int numberOfMoves = ThreadLocalRandom.current().nextInt(5, 35);
        for (i = 0; i < numberOfMoves; i++) {
           Move aiMove = AI.execute(board);
           MoveTransition moveChange = board.currentPlayer().makeMove(aiMove);
           if (moveChange.getMoveStatus().isDone()) {
               boardBeforeChange = board;
               board = moveChange.getTransitionBoard();
           }
        }

        // revert to last board if last move made Alliance black
        if (board.currentPlayer().getAlliance() != Alliance.WHITE) {
            i--;
            board = boardBeforeChange;
        }

        // check that player is not put in checkmate when the ai makes its next move
        MoveStrategy smarterAI = new MiniMax(4, 100, true, false);
        Move aiNextMove = smarterAI.execute(board);
        Board nextIterationBoard = null;
        if (board.currentPlayer().makeMove(aiNextMove).getMoveStatus().isDone()) {
            nextIterationBoard = aiNextMove.execute();
        } else {
            // ai could not move, re-roll board
            System.out.println("Illegal board state: reshuffling board");
            createRandomBoard();
        }

        if (nextIterationBoard != null &&
            (board.currentPlayer().isInCheckmate() || board.currentPlayer().isInStalemate() ||
             board.currentPlayer().getOpponent().isInCheck() || board.currentPlayer().getOpponent().isInStalemate() ||
             nextIterationBoard.currentPlayer().isInCheck())) {

            System.out.println("Illegal board state: reshuffling board");
            createRandomBoard();
        }

        System.out.println("Board shuffled with " + i + " AI moves");
        return board;
    }

    /**
     * Constructs a board with a "horde" layout
     * @see <a href="https://en.wikipedia.org/wiki/Dunsany%27s_Chess">Horde chess</a>
     * @return board with hoard layout
     */
    public static Board createHordeBoard() {
        final Builder builder = new Builder();
        // Black pieces
        builder.setPiece(new Pawn(new Coordinate(0,0), Alliance.BLACK, false));
        builder.setPiece(new Pawn(new Coordinate(1,0), Alliance.BLACK, false));
        builder.setPiece(new Pawn(new Coordinate(2,0), Alliance.BLACK, false));
        builder.setPiece(new Pawn(new Coordinate(5,0), Alliance.BLACK, false));
        builder.setPiece(new Pawn(new Coordinate(6,0), Alliance.BLACK, false));
        builder.setPiece(new Pawn(new Coordinate(7,0), Alliance.BLACK, false));
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                builder.setPiece(new Pawn(new Coordinate(j, i), Alliance.BLACK, false));
            }
        }
        builder.setPiece(new Pawn(new Coordinate(3, 4), Alliance.BLACK, false));
        builder.setPiece(new Pawn(new Coordinate(4, 4), Alliance.BLACK, false));

        // White pieces
        builder.setPiece(new Rook(new Coordinate(0,7), Alliance.WHITE));
        builder.setPiece(new Knight(new Coordinate(1,7), Alliance.WHITE));
        builder.setPiece(new Bishop(new Coordinate(2,7), Alliance.WHITE));
        builder.setPiece(new Queen(new Coordinate(3,7), Alliance.WHITE));
        builder.setPiece(new King(new Coordinate(4,7), Alliance.WHITE));
        builder.setPiece(new Bishop(new Coordinate(5,7), Alliance.WHITE));
        builder.setPiece(new Knight(new Coordinate(6,7), Alliance.WHITE));
        builder.setPiece(new Rook(new Coordinate(7,7), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(0,6), Alliance.WHITE, false));
        builder.setPiece(new Pawn(new Coordinate(1,6), Alliance.WHITE, false));
        builder.setPiece(new Pawn(new Coordinate(2,6), Alliance.WHITE, false));
        builder.setPiece(new Pawn(new Coordinate(3,6), Alliance.WHITE, false));
        builder.setPiece(new Pawn(new Coordinate(4,6), Alliance.WHITE, false));
        builder.setPiece(new Pawn(new Coordinate(5,6), Alliance.WHITE, false));
        builder.setPiece(new Pawn(new Coordinate(6,6), Alliance.WHITE, false));
        builder.setPiece(new Pawn(new Coordinate(7,6), Alliance.WHITE, false));
        builder.setMoveMaker(Alliance.WHITE);

        return builder.build();
    }

    /**
     * Constructs a board with a "light brigade" layout
     * @return board with light brigade layout
     */
    public static Board createLightBrigadeBoard() {
        final Builder builder = new Builder();
        //Black pieces
        builder.setPiece(new Knight(new Coordinate(0,0), Alliance.BLACK));
        builder.setPiece(new Knight(new Coordinate(1,0), Alliance.BLACK));
        builder.setPiece(new Knight(new Coordinate(2,0), Alliance.BLACK));
        builder.setPiece(new Knight(new Coordinate(3,0), Alliance.BLACK));
        builder.setPiece(new King(new Coordinate(4,0), Alliance.BLACK));
        builder.setPiece(new Knight(new Coordinate(5,0), Alliance.BLACK));
        builder.setPiece(new Knight(new Coordinate(6,0), Alliance.BLACK));
        builder.setPiece(new Knight(new Coordinate(7,0), Alliance.BLACK));
        for (int i = 0; i < 8; i++) {
            builder.setPiece(new Pawn(new Coordinate(i, 1), Alliance.BLACK, true));
        }

        //White pieces
        for (int i = 0; i < 8; i++) {
            builder.setPiece(new Pawn(new Coordinate(i, 6), Alliance.WHITE, true));
        }
        builder.setPiece(new Queen(new Coordinate(1, 7), Alliance.WHITE));
        builder.setPiece(new Queen(new Coordinate(3, 7), Alliance.WHITE));
        builder.setPiece(new King(new Coordinate(4, 7), Alliance.WHITE));
        builder.setPiece(new Queen(new Coordinate(6, 7), Alliance.WHITE));
        builder.setMoveMaker(Alliance.WHITE);
        return builder.build();
    }

    /**
     * Helper class for constructing chessboards given a defined layout
     */
    public static class Builder {
        Map<Coordinate, Piece> boardConfig;
        Alliance nextMoveMaker;
        Pawn enPassantPawn;
        Move transitionMove = null;

        /**
         * Construct a Builder object with an empty map.
         */
        public Builder() {
            this.boardConfig = new HashMap<>();
        }

        /**
         * Adds a piece to the boardConfig-map
         * @param piece to be placed
         * @return Builder with this setting
         */
        public Builder setPiece(Piece piece) {
            this.boardConfig.put(piece.getPieceCoordinate(), piece);
            return this;
        }

        /**
         * Set who makes the next move
         * @param alliance who makes the next move
         * @return Builder with this setting
         */
        public Builder setMoveMaker(Alliance alliance) {
            this.nextMoveMaker = alliance;
            return this;
        }

        /**
         * Sets the pawn that is now open for an 'en passant' attack
         * @param enPassantPawn the pawn that made a pawn jump
         */
        public void setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }

        /**
         * Set the move that made a change to the board
         * @param transitionMove the move that changes the board
         */
        public Builder setMoveTransition(final Move transitionMove) {
            this.transitionMove = transitionMove;
            return this;
        }

        /**
         * Construct a new board object with the "settings" for this Builder object
         * @return a Board object
         */
        public Board build() {
            return new Board(this);
        }

    }
}
