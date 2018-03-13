package board;

import pieces.*;
import player.BlackPlayer;
import player.Player;
import player.WhitePlayer;

import java.util.*;

/**
 * This class represents a chessboard.
 * A Map<Coordinate, Tile> is the base representation of the board layout,
 * where a Coordinate object as key gives a Tile object as value with some content.
 */
public class Board {
    private final Map<Coordinate, Tile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;
    private final Collection<Move> whiteStandardLegalMoves;
    private final Collection<Move> blackStandardLegalMoves;
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;

    public Board(Builder builder) {
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, Alliance.BLACK);

        this.whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        this.blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, blackStandardLegalMoves, whiteStandardLegalMoves);

        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    /**
     * Creates a chessboard with a certain layout
     * @param builder defines the layout/settings of the board
     * @return a Map with coordinates as keys and tiles as values
     */
    private Map<Coordinate, Tile> createGameBoard(Builder builder) {
        Map<Coordinate, Tile> coordTile = new HashMap<>();
        for (int i = 0; i < BoardUtils.getHeight(); i++) {
            for (int j = 0; j < BoardUtils.getWidth(); j++) {
                coordTile.put(new Coordinate(j,i), Tile.createTile(new Coordinate(j,i), builder.boardConfig.get(new Coordinate(j,i))));
            }
        }
        return coordTile;
    }

    /**
     * Calculates how many active pieces there is of a given alliance on a board
     * @param gameBoard board representation which holds pieces
     * @param alliance to calculate for (black/white)
     * @return a list of active pieces of a given alliance
     */
    private static Collection<Piece> calculateActivePieces(Map<Coordinate, Tile> gameBoard, Alliance alliance) {
        final List<Piece> activePieces = new ArrayList<>();
        for (Tile tile : gameBoard.values()) {
            if (!tile.isTileEmpty()) {
                final Piece piece = tile.getPiece();
                if (piece.getPieceAlliance() == alliance) {
                    activePieces.add(piece);
                }
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
    public Tile getTile(final Coordinate tileCoordinate) {
        return this.gameBoard.get(tileCoordinate);
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
     * @return the Player currently playing ('in charge')
     */
    public Player currentPlayer() {
        //todo:
        return null;
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
     * Returns an identical board to this one.
     * Mainly used by AI to try out moves without effecting this board
     * @return clone of this board
     */
    public Board clone(){

        //TODO: implement method

        return null; //placeholder
    }

    public Collection<Move> getBlackStandardLegalMoves() {
        return blackStandardLegalMoves;
    }

    public Collection<Move> getWhiteStandardLegalMoves() {
        return whiteStandardLegalMoves;
    }

    public Map<Coordinate, Tile> getGameBoard() {
        return gameBoard;
    }

    public Collection<Piece> getWhitePieces() {
        return whitePieces;
    }

    public Collection<Piece> getBlackPieces() {
        return blackPieces;
    }

    /**
     * Helper class for constructing chessboards given a defined layout
     */
    public static class Builder {
        Map<Coordinate, Piece> boardConfig;
        Alliance nextMoveMaker;

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
         * Construct a new board object with the "settings" for this Builder object
         * @return a Board object
         */
        public Board build() {
            return new Board(this);
        }
    }
}
