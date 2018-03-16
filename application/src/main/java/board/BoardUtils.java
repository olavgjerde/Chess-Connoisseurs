package board;

import java.util.*;

public class BoardUtils {
    //todo: getting this values will not work if class is not initialized - fix this
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
        Iterator<String> algebraic = Arrays.asList(
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1").iterator();
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                coordToString.put(new Coordinate(j, i), algebraic.next());
            }
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

}
