package board;

import java.util.Objects;

/**
 * Class represent a coordinate with x and y values
 */
public class Coordinate {
    private final int x, y;

    /**
     * Construct a new coordinate
     * @param x value of coordinate
     * @param y value of coordinate
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return x-value of coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * @return y-value of coordinate
     */
    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
