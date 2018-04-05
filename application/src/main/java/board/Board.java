package board;

import pieces.*;
import player.BlackPlayer;
import player.Player;
import player.WhitePlayer;

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

    public Board(Builder builder) {
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(builder, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(builder, Alliance.BLACK);
        this.enPassantPawn = builder.enPassantPawn;

        final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);
        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, blackStandardLegalMoves, whiteStandardLegalMoves);

        this.currentPlayer = builder.nextMoveMaker.choosePlayerByAlliance(this.whitePlayer, this.blackPlayer);
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
        final List<Piece> activePieces = new ArrayList<>();
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
        return Collections.unmodifiableList(legalMoves);
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
        builder.setPiece(new Pawn(new Coordinate(0,1), Alliance.BLACK));
        builder.setPiece(new Pawn(new Coordinate(1,1), Alliance.BLACK));
        builder.setPiece(new Pawn(new Coordinate(2,1), Alliance.BLACK));
        builder.setPiece(new Pawn(new Coordinate(3,1), Alliance.BLACK));
        builder.setPiece(new Pawn(new Coordinate(4,1), Alliance.BLACK));
        builder.setPiece(new Pawn(new Coordinate(5,1), Alliance.BLACK));
        builder.setPiece(new Pawn(new Coordinate(6,1), Alliance.BLACK));
        builder.setPiece(new Pawn(new Coordinate(7,1), Alliance.BLACK));

        // White pieces
        builder.setPiece(new Rook(new Coordinate(0,7), Alliance.WHITE));
        builder.setPiece(new Knight(new Coordinate(1,7), Alliance.WHITE));
        builder.setPiece(new Bishop(new Coordinate(2,7), Alliance.WHITE));
        builder.setPiece(new Queen(new Coordinate(3,7), Alliance.WHITE));
        builder.setPiece(new King(new Coordinate(4,7), Alliance.WHITE));
        builder.setPiece(new Bishop(new Coordinate(5,7), Alliance.WHITE));
        builder.setPiece(new Knight(new Coordinate(6,7), Alliance.WHITE));
        builder.setPiece(new Rook(new Coordinate(7,7), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(0,6), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(1,6), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(2,6), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(3,6), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(4,6), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(5,6), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(6,6), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(7,6), Alliance.WHITE));
        builder.setMoveMaker(Alliance.WHITE);
        return builder.build();
    }

    /**
     * Creates a board with the pieces for a regular chess game spread out randomly
     * @return a Board with a random layout
     */
    public static Board createRandomBoard() {
        final Builder builder = new Builder();
        ThreadLocalRandom randGen = ThreadLocalRandom.current();
        List<Coordinate> coordinateList = new ArrayList<>();

        // generate king coordinates first
        Coordinate whiteKingCoordinate = new Coordinate(randGen.nextInt(BoardUtils.getWidth()), randGen.nextInt(BoardUtils.getHeight()));
        Coordinate blackKingCoordinate = new Coordinate(randGen.nextInt(BoardUtils.getWidth()), randGen.nextInt(BoardUtils.getHeight()));
        // check that the kings coordinates are separated by more than one tile
        while (BoardUtils.euclideanDistance(whiteKingCoordinate, blackKingCoordinate) <= 2.0) {
            whiteKingCoordinate = new Coordinate(randGen.nextInt(BoardUtils.getWidth()), randGen.nextInt(BoardUtils.getHeight()));
        }
        coordinateList.add(whiteKingCoordinate);
        coordinateList.add(blackKingCoordinate);

        // generate coordinates for all other pieces than kings
        for (int i = 0; i < ((BoardUtils.getHeight() * BoardUtils.getWidth()) / 2) - 2; i++) {
            Coordinate generatedCoordinate = new Coordinate(randGen.nextInt(BoardUtils.getWidth()), randGen.nextInt(BoardUtils.getHeight()));
            // "re-roll" if same coordinate is generated
            while (coordinateList.contains(generatedCoordinate)) {
                generatedCoordinate = new Coordinate(randGen.nextInt(BoardUtils.getWidth()), randGen.nextInt(BoardUtils.getHeight()));
            }
            coordinateList.add(generatedCoordinate);
        }
        Iterator<Coordinate> coordinateIterator = coordinateList.iterator();

        // place kings
        builder.setPiece(new King(coordinateIterator.next(), Alliance.WHITE, false, false));
        builder.setPiece(new King(coordinateIterator.next(), Alliance.BLACK, false, false));

        // place pawns
        for (int i = 0; i < 8; i++) {
            Coordinate whiteCoordinate = coordinateIterator.next();
            if (Alliance.WHITE.isPawnPromotionCoordinate(whiteCoordinate)) {
                builder.setPiece(new Queen(whiteCoordinate, Alliance.WHITE, false));
            } else {
                builder.setPiece(new Pawn(whiteCoordinate, Alliance.WHITE, false));
            }
            Coordinate blackCoordinate = coordinateIterator.next();
            if (Alliance.BLACK.isPawnPromotionCoordinate(blackCoordinate)) {
                builder.setPiece(new Queen(blackCoordinate, Alliance.BLACK, false));
            } else {
                builder.setPiece(new Pawn(blackCoordinate, Alliance.BLACK, false));
            }
        }

        // place all other major pieces
        builder.setPiece(new Rook(coordinateIterator.next(), Alliance.BLACK, false));
        builder.setPiece(new Rook(coordinateIterator.next(), Alliance.BLACK, false));
        builder.setPiece(new Knight(coordinateIterator.next(), Alliance.BLACK, false));
        builder.setPiece(new Knight(coordinateIterator.next(), Alliance.BLACK, false));
        builder.setPiece(new Bishop(coordinateIterator.next(), Alliance.BLACK, false));
        builder.setPiece(new Bishop(coordinateIterator.next(), Alliance.BLACK, false));
        builder.setPiece(new Queen(coordinateIterator.next(), Alliance.BLACK, false));

        builder.setPiece(new Rook(coordinateIterator.next(), Alliance.WHITE, false));
        builder.setPiece(new Rook(coordinateIterator.next(), Alliance.WHITE, false));
        builder.setPiece(new Knight(coordinateIterator.next(), Alliance.WHITE, false));
        builder.setPiece(new Knight(coordinateIterator.next(), Alliance.WHITE, false));
        builder.setPiece(new Bishop(coordinateIterator.next(), Alliance.WHITE, false));
        builder.setPiece(new Bishop(coordinateIterator.next(), Alliance.WHITE, false));
        builder.setPiece(new Queen(coordinateIterator.next(), Alliance.WHITE, false));

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
         * Construct a new board object with the "settings" for this Builder object
         * @return a Board object
         */
        public Board build() {
            return new Board(this);
        }

    }
}
