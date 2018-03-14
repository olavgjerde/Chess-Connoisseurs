package swingExampleGui;

import board.Board;
import board.BoardUtils;
import board.Coordinate;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: *THIS SHALL BE REPLACED BY AN UPDATED JAVAFX VARIANT*
 * GUI class to represent the chessboard.
 */
public class Table {

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final Board chessBoard;
    //todo fix this path
    private static String pieceImagesPath = "application/src/main/resources/images/";

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

    public Table() {
        // main frame
        this.gameFrame = new JFrame("Chess");
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.gameFrame.setLayout(new BorderLayout());
        // add menu
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        // construct data structure board representation
        this.chessBoard = Board.createStandardBoard();
        // add visual board representation
        this.boardPanel = new BoardPanel();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);

        // show frame
        this.gameFrame.setVisible(true);

    }

    /**
     * Creates the top menu bar for the window frame
     * @return JMenuBar with menu items
     */
    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        return tableMenuBar;
    }

    /**
     * Creates a menu item 'File' which contains several options
     * @return JMenu with several JMenuItems
     */
    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(e -> System.out.println("Open pgn file.."));
        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    /**
     * This class BoardPanel keeps track of all tiles on a board
     * Keeps a list of board-width * board-height tiles.
     */
    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        public BoardPanel() {
            super(new GridLayout(BoardUtils.getWidth(), BoardUtils.getHeight()));
            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < BoardUtils.getHeight(); i++) {
                for (int j = 0; j < BoardUtils.getWidth(); j++) {
                    final TilePanel tilePanel = new TilePanel(this, new Coordinate(j, i));
                    this.boardTiles.add(tilePanel);
                    add(tilePanel);
                }
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }
    }

    /**
     * Class represents each individual tile on the board
     */
    private class TilePanel extends JPanel {
        private final Coordinate coordinateId;

        TilePanel(BoardPanel boardPanel, Coordinate coordinateId) {
            super(new GridBagLayout());
            this.coordinateId = coordinateId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceImage(chessBoard);
            validate();
        }

        /**
         * Set image on tile given what piece the tile contains.
         * @param board which contain the tiles and pieces
         */
        private void assignTilePieceImage(Board board) {
            this.removeAll();
            if (!board.getTile(this.coordinateId).isTileEmpty()) {
                try {
                    // append path with alliance type and string representation of piece to get correct image from resources
                    final BufferedImage image = ImageIO.read(new File(pieceImagesPath +
                            board.getTile(this.coordinateId).getPiece().getPieceAlliance().toString().substring(0, 1) +
                            board.getTile(this.coordinateId).getPiece().toString() + ".png"));
                    // image scaling using java built in feature
                    ImageIcon pieceImage = new ImageIcon(image);
                    Image imageToScale = pieceImage.getImage();
                    Image scaledImage = imageToScale.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH);
                    this.add(new JLabel(new ImageIcon(scaledImage)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Assign a color to the tiles based on its coordinates
         */
        private void assignTileColor() {
           if ((coordinateId.getY() % 2) == (coordinateId.getX() % 2)) {
               setBackground(Color.decode("#ca9b5e"));
           } else {
               setBackground(Color.decode("#e9d7be"));
           }
        }
    }
}
