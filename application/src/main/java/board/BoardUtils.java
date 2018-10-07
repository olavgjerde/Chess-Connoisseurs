package board;

import java.util.*;

/**
 * Helper class for the chess engine, contains various methods that define dimensions for the board and layout,
 * but also methods that help with calculations of movements.
 */
public class BoardUtils {
    private static BoardUtils boardUtils = null;
    private final Map<Coordinate, String> COORDINATE_TO_ALGEBRAIC;
    private final Map<String, Coordinate> ALGEBRAIC_TO_COORDINATE;
    private final Map<Coordinate, Integer> COORDINATE_TO_INTEGER;
    private final int height;
    private final int width;

    private BoardUtils() {
        this.COORDINATE_TO_ALGEBRAIC = initializeAlgebraicNotation();
        this.ALGEBRAIC_TO_COORDINATE = initializeAlgebraicToCoordinateMap();
        this.COORDINATE_TO_INTEGER = initializeIntegerToCoordinateMap();
        this.height = 8;
        this.width = 8;
    }

    public static BoardUtils getInstance() {
        if (boardUtils == null) {
            boardUtils = new BoardUtils();
        }
        return boardUtils;
    }

    /**
     * Checks if a coordinate is out of bounds
     *
     * @param coordinate of given piece that will be evaluated
     * @return true if valid coordinate, else false
     */
    public boolean isValidCoordinate(Coordinate coordinate) {
        int x = coordinate.getX();
        int y = coordinate.getY();
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    /**
     * The height of the chessboard
     * @return height of chessboard
     */
    public int getHeight() {
        return height;
    }

    /**
     * The width of the chessboard
     * @return width of chessboard
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the algebraic notation given a coordinate
     *
     * @param coordinate to get algebraic notation for
     * @return String (algebraic notation) corresponding to coordinate
     */
    String getAlgebraicNotationFromCoordinate(Coordinate coordinate) {
        return COORDINATE_TO_ALGEBRAIC.get(coordinate);
    }

    /**
     * Get the coordinate given a algebraic notation of that coordinate
     *
     * @param algebraicNotation of a coordinate
     * @return Coordinate corresponding to algebraic notation
     */
    public Coordinate getCoordinateFromAlgebraicNotation(String algebraicNotation) {
        return ALGEBRAIC_TO_COORDINATE.get(algebraicNotation);
    }

    /**
     * Get the integer representation of a coordinate
     *
     * @param coordinate to fetch integer representation for
     * @return integer representation
     */
    public int getIntegerRepresentationFromCoordinate(Coordinate coordinate) {
        return COORDINATE_TO_INTEGER.get(coordinate);
    }

    /**
     * Maps possible coordinates to their algebraic notation format
     *
     * @return Map with key Coordinate and value String
     */
    private Map<Coordinate, String> initializeAlgebraicNotation() {
        Map<Coordinate, String> coordToString = new HashMap<>();
        char letter = 'a';

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String algebraic = letter++ + "" + (8 - i);
                coordToString.put(new Coordinate(j, i), algebraic);
            }
            letter = 'a';
        }
        return coordToString;
    }

    /**
     * Maps all algebraic notations of the positions to their coordinates
     *
     * @return Map with key String and value Coordinate
     */
    private Map<String, Coordinate> initializeAlgebraicToCoordinateMap() {
        Map<String, Coordinate> stringToCoord = new HashMap<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                stringToCoord.put(COORDINATE_TO_ALGEBRAIC.get(new Coordinate(j, i)), new Coordinate(j, i));
            }
        }
        return stringToCoord;
    }

    /**
     * Maps all coordinates on the board to a integer representation (8x8 = 0-64 ints)
     *
     * @return Map with key Coordinate and value Integer
     */
    private Map<Coordinate, Integer> initializeIntegerToCoordinateMap() {
        Map<Coordinate, Integer> coordToInteger = new HashMap<>();
        int integerRepresentation = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                coordToInteger.put(new Coordinate(j,i), integerRepresentation++);
            }
        }
        return coordToInteger;
    }

    /**
     * Retrieve the last moves (n) that has happened on a board
     * @param board to get moves from
     * @param n how many moves to retrieve
     * @return a list of the moves retrieved
     */
    public List<Move> retrieveLastNMoves(Board board, int n) {
        final List<Move> moveHistory = new ArrayList<>();
        Move currentMove = board.getTransitionMove();
        int i = 0;
        while(currentMove != null && i < n) {
            moveHistory.add(currentMove);
            currentMove = currentMove.getBoard().getTransitionMove();
            i++;
        }
        return Collections.unmodifiableList(moveHistory);
    }
}
