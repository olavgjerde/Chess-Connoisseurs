package swingExampleGui;

import board.*;
import com.google.common.collect.Lists;
import pieces.Piece;
import player.MoveTransition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

/**
 * TODO: *THIS SHALL BE REPLACED BY AN UPDATED JAVAFX VARIANT*
 * GUI class to represent the chessboard.
 */
public class Table {
    // base structures
    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private Board chessBoard;

    // movement
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;

    // options
    private BoardDirection boardDirection;
    public boolean highLightLegalMoves;

    // visual dimensions
    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

    //todo correct this path
    private static String pieceImagesPath = "application/src/main/resources/images/";

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
        // set orientation
        this.boardDirection = BoardDirection.NORMAL;
        // add visual board representation
        this.boardPanel = new BoardPanel();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        // show main frame
        this.gameFrame.setVisible(true);
    }

    /**
     * Creates the top menu bar for the window frame
     * @return JMenuBar with menu items
     */
    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferenceMenu());
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
     * Creates a menu item 'Preference' which contains several options
     * @return JMenu with several JMenuItems
     */
    private JMenu createPreferenceMenu() {
        final JMenu preferenceMenu = new JMenu("Preferences");

        final JMenuItem flipBoardItem = new JMenuItem("Flip Board");
        flipBoardItem.addActionListener(e -> {
            boardDirection = boardDirection.opposite();
            boardPanel.drawBoard(chessBoard);
        });
        preferenceMenu.add(flipBoardItem);

        preferenceMenu.addSeparator();

        final JCheckBoxMenuItem highLighting = new JCheckBoxMenuItem("Highlight Legal Moves", false);
        highLighting.addActionListener(e -> this.highLightLegalMoves = !this.highLightLegalMoves);
        preferenceMenu.add(highLighting);

        return preferenceMenu;
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

        /**
         * Redraw/repaint a given BoardPanel
         * @param board which contains the board to redraw
         */
        public void drawBoard(Board board) {
            removeAll();
            // draw board in a orientation given by enum boardDirection
            for (TilePanel tile : boardDirection.traverse(boardTiles)) {
                tile.drawTile(board);
                add(tile);
            }
            validate();
            repaint();
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
            if (highLightLegalMoves) highlightPossibleMoves(chessBoard);
            validate();

            // every tile has a event listener
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isLeftMouseButton(e)) {
                        if (sourceTile == null) {
                            // first click (pick piece to move)
                            sourceTile = chessBoard.getTile(coordinateId);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) sourceTile = null;
                        } else {
                            // second click (pick destination)
                            destinationTile = chessBoard.getTile(coordinateId);
                            final Move move = Move.MoveFactory.createMove(chessBoard,
                                                                          sourceTile.getTileCoord(),
                                                                          destinationTile.getTileCoord());
                            // this step updates the board
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getTransitionBoard();
                                System.out.println("\n" + chessBoard);
                                //todo: add move to the move log
                            }
                            //todo: refactor clear tiles
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(() -> boardPanel.drawBoard(chessBoard));
                    } else if (isRightMouseButton(e)) {
                        // cancel tile selection
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    }
                }

                // not used by gui
                @Override
                public void mousePressed(MouseEvent e) { }
                @Override
                public void mouseReleased(MouseEvent e) { }
                @Override
                public void mouseEntered(MouseEvent e) { }
                @Override
                public void mouseExited(MouseEvent e) { }
            });

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
                    Image scaledImage = imageToScale.getScaledInstance(25, 25,  java.awt.Image.SCALE_SMOOTH);
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

        /**
         * Highlights legal moves on the board
         * @param board which contains the pieces the player might move
         */
        private void highlightPossibleMoves(Board board) {
            for (Move move: pieceLegalMoves(board)) {
                if (move.getDestinationCoordinate().equals(this.coordinateId)) {
                    setBackground(Color.GREEN);
                }
            }
        }

        /**
         * Find the legal moves for a given piece
         * @param board on which the piece resides
         * @return a collection of legal moves, empty if none
         */
        private Collection<Move> pieceLegalMoves(Board board) {
            if (humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }

        /**
         * Set image and background color for a piece
         * @param board where the tiles are located
         */
        public void drawTile(Board board) {
            assignTileColor();
            assignTilePieceImage(board);
            if (highLightLegalMoves) highlightPossibleMoves(board);
            validate();
            repaint();
        }
    }

    /**
     * BoardDirection enums represent in which orientation the board shall be viewed.
     * This helps the GUI flip the board.
     */
    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }
}
