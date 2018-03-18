package board;

import org.junit.jupiter.api.Test;
import java.util.concurrent.ThreadLocalRandom;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that the Coordinate class' major functions are in order
 */
class CoordinateTest {
    /**
     * Check that the getX-method returns the x-value set on creation
     */
    @Test
    void getX() {
        int myX = ThreadLocalRandom.current().nextInt();
        Coordinate coordinate = new Coordinate(myX,0);
        assertEquals(myX, coordinate.getX());
    }

    /**
     * Check that the getY-method returns the y-value set on creation
     */
    @Test
    void getY() {
        int myY = ThreadLocalRandom.current().nextInt();
        Coordinate coordinate = new Coordinate(0,myY);
        assertEquals(myY, coordinate.getY());
    }

    /**
     * Check that the equal method of the Coordinate class is correct
     * if other.x = x && other.y == y -> then they are equal
     */
    @Test
    void equals() {
        int myX = ThreadLocalRandom.current().nextInt(), myY = ThreadLocalRandom.current().nextInt();
        Coordinate coordinate = new Coordinate(myX, myY);
        assertTrue(coordinate.equals(new Coordinate(myX, myY)));
    }
}