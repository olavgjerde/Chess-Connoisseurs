package pieces;

import board.Board;
import board.BoardUtils;
import board.Coordinate;
import org.junit.jupiter.api.Test;
import player.Player;
import java.util.concurrent.ThreadLocalRandom;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Check that the different enums major functions behave correctly
 */
class AllianceTest {

    /**
     * Check that the white alliance's direction is negative, calculating their
     * moves with this logic moves them towards the top of the chess board
     */
    @Test
    void getDirectionWhiteAlliance() {
        assertEquals(-1, Alliance.WHITE.getDirection());
    }

    /**
     * Check that the black alliance's direction is positive, calculating their
     * moves with this logic moves them towards the bottom of the chess board
     */
    @Test
    void getDirectionBlackAlliance() {
        assertEquals(1, Alliance.BLACK.getDirection());
    }

    /**
     * Check that the opposite direction of the black alliance is equal to the direction of the
     * white alliance
     */
    @Test
    void getOppositeDirectionBlackAlliance() {
        assertEquals(Alliance.WHITE.getDirection(), Alliance.BLACK.getOppositeDirection());
    }

    /**
     * Check that the opposite direction of the white alliance is equal to the direction the
     * black alliance
     */
    @Test
    void getOppositeDirectionWhiteAlliance() {
        assertEquals(Alliance.BLACK.getDirection(), Alliance.WHITE.getOppositeDirection());
    }

    /**
     * Check that the black alliance can detect that a coordinate is a coordinate
     * where pawn promotion can happen
     */
    @Test
    void isPawnPromotionCoordinateBlackPiece() {
        int promotionY = BoardUtils.getHeight() - 1;
        int randomXInBounds = ThreadLocalRandom.current().nextInt(BoardUtils.getWidth());
        Coordinate promotionCoordinate = new Coordinate(randomXInBounds, promotionY);
        assertTrue(Alliance.BLACK.isPawnPromotionCoordinate(promotionCoordinate));
    }

    /**
     * Check that the black alliance does not wrongly detect that a coordinate is a coordinate
     * pawn promotion coordinate
     */
    @Test
    void isNotPawnPromotionCoordinateBlackPiece() {
        int promotionY = ThreadLocalRandom.current().nextInt();
        while (promotionY == BoardUtils.getHeight() - 1) promotionY = ThreadLocalRandom.current().nextInt();
        int randomXInBounds = ThreadLocalRandom.current().nextInt(BoardUtils.getWidth());
        Coordinate promotionCoordinate = new Coordinate(randomXInBounds, promotionY);
        assertFalse(Alliance.BLACK.isPawnPromotionCoordinate(promotionCoordinate));
    }

    /**
     * Check that the white alliance can detect that a coordinate is a coordinate
     * where pawn promotion can happen
     */
    @Test
    void isPawnPromotionCoordinateWhitePiece() {
        int promotionY = 0;
        int randomXInBounds = ThreadLocalRandom.current().nextInt(BoardUtils.getWidth());
        Coordinate promotionCoordinate = new Coordinate(randomXInBounds, promotionY);
        assertTrue(Alliance.WHITE.isPawnPromotionCoordinate(promotionCoordinate));
    }

    /**
     * Check that the white alliance does not wrongly detect that a coordinate is a coordinate
     * pawn promotion coordinate
     */
    @Test
    void isNotPawnPromotionCoordinateWhitePiece() {
        int promotionY = ThreadLocalRandom.current().nextInt();
        while (promotionY == 0) promotionY = ThreadLocalRandom.current().nextInt();
        int randomXInBounds = ThreadLocalRandom.current().nextInt(BoardUtils.getWidth());
        Coordinate promotionCoordinate = new Coordinate(randomXInBounds, promotionY);
        assertFalse(Alliance.WHITE.isPawnPromotionCoordinate(promotionCoordinate));
    }

    /**
     * Check that the white alliance will pick the white player, when given the option between the
     * white player and the black player
     */
    @Test
    void choosePlayerByAllianceWhiteAlliance() {
        Board board = Board.createStandardBoard();
        Player whitePlayer = board.getWhitePlayer();
        assertEquals(whitePlayer, Alliance.WHITE.choosePlayerByAlliance(board.getWhitePlayer(), board.getBlackPlayer()));
    }

    /**
     * Check that the black alliance will pick the black player, when given the option between the
     * white player and the black player
     */
    @Test
    void choosePlayerByAllianceBlackAlliance() {
        Board board = Board.createStandardBoard();
        Player blackPlayer = board.getBlackPlayer();
        assertEquals(blackPlayer, Alliance.BLACK.choosePlayerByAlliance(board.getWhitePlayer(), board.getBlackPlayer()));
    }
}