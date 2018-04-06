package board;

import java.util.*;

/**
 * Helper class for the chess engine, contains various methods that define dimensions for the board and layout,
 * but also methods that help with calculations of movements.
 */
public class BoardUtils {
    private static final Map<Coordinate, String> COORDINATE_TO_ALGEBRAIC = initializeAlgebraicNotation();
    private static final Map<String, Coordinate> ALGEBRAIC_TO_COORDINATE = initializeAlgebraicToCoordinateMap();

    private BoardUtils() {
    }

    /**
     * Checks if a coordinate is out of bounds
     *
     * @param coordinate of given piece that will be evaluated
     * @return true if valid coordinate, else false
     */
    public static boolean isValidCoordinate(Coordinate coordinate) {
        int x = coordinate.getX();
        int y = coordinate.getY();
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    public static int getHeight() {
        return 8;
    }

    public static int getWidth() {
        return 8;
    }

    /**
     * Get the algebraic notation given a coordinate
     *
     * @param coordinate to get algebraic notation for
     * @return String (algebraic notation) corresponding to coordinate
     */
    public static String getAlgebraicNotationFromCoordinate(Coordinate coordinate) {
        return COORDINATE_TO_ALGEBRAIC.get(coordinate);
    }

    /**
     * Get the coordinate given a algebraic notation of that coordinate
     *
     * @param algebraicNotation of a coordinate
     * @return Coordinate corresponding to algebraic notation
     */
    public static Coordinate getCoordinateFromAlgebraicNotation(String algebraicNotation) {
        return ALGEBRAIC_TO_COORDINATE.get(algebraicNotation);
    }

    /**
     * Maps possible coordinates to their algebraic notation format
     *
     * @return Map with key Coordinate and value String
     */
    private static Map<Coordinate, String> initializeAlgebraicNotation() {
        Map<Coordinate, String> coordToString = new HashMap<>();
        char letter = 'a';

        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                String algebraic = letter++ + "" + (getHeight() - i);
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
    private static Map<String, Coordinate> initializeAlgebraicToCoordinateMap() {
        Map<String, Coordinate> stringToCoord = new HashMap<>();
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                stringToCoord.put(COORDINATE_TO_ALGEBRAIC.get(new Coordinate(j, i)), new Coordinate(j, i));
            }
        }
        return stringToCoord;
    }

    /**
     * Returns the euclidean distance between two positive coordinates
     * @param a first coordinate
     * @param b second coordinate
     * @return distance between coordinates
     */
    public static double euclideanDistance(Coordinate a, Coordinate b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

}
