package board;

public class BoardUtils {
    /**
     * Throws an exception if someone tries to instantiate this helper class
     */
    private BoardUtils() {
        throw new RuntimeException("Do not instantiate this class");
    }

    /**
     * Checks if a coordinate is out of bounds
     * @param coordinate of given piece that will be evaluated
     * @return true if valid coordinate, else false
     */
    public static boolean isValidCoordinate(Coordinate coordinate) {
        int x = coordinate.getX();
        int y = coordinate.getY();
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public static int getHeight() {
        return 8;
    }

    public static int getWidth() {
        return 8;
    }

    //todo removable
    public static double distanceTo(Coordinate one, Coordinate two) {
        return Math.sqrt(Math.pow(one.getX() - two.getX(), 2) + Math.pow(one.getY() - two.getY(), 2));
    }
}
