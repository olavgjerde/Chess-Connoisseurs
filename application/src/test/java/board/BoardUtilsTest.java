package board;

import org.junit.jupiter.api.Test;
import java.util.concurrent.ThreadLocalRandom;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The that the BoardUtils class' major function are working as expected
 */
class BoardUtilsTest {

    /**
     * Check to see that illegal coordinates does not pass
     */
    @Test
    void isIllegalCoordinate() {
        int randomXOutOfBounds = ThreadLocalRandom.current().nextInt();
        int randomYOutOfBounds = ThreadLocalRandom.current().nextInt();
        // if number was inside the bounds given by BoardUtils, roll random again
        while ((randomXOutOfBounds >= 0 && randomXOutOfBounds < BoardUtils.getWidth()) &&
               (randomYOutOfBounds >= 0 && randomYOutOfBounds < BoardUtils.getHeight())) {
            randomXOutOfBounds = ThreadLocalRandom.current().nextInt();
            randomYOutOfBounds = ThreadLocalRandom.current().nextInt();
        }
        assertFalse(BoardUtils.isValidCoordinate(new Coordinate(randomXOutOfBounds, randomYOutOfBounds)));
    }

    /**
     * Check to see that legal coordinates pass
     */
    @Test
    void isLegalCoordinate() {
        int randomXInBounds = ThreadLocalRandom.current().nextInt(BoardUtils.getWidth());
        int randomYInBounds = ThreadLocalRandom.current().nextInt(BoardUtils.getHeight());
        assertTrue(BoardUtils.isValidCoordinate(new Coordinate(randomXInBounds, randomYInBounds)));
    }
}