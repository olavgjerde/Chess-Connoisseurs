package swingExampleGui;

import board.*;
import com.google.common.collect.Lists;
import pieces.Piece;
import player.MoveTransition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javax.swing.SwingUtilities.*;

/**
 * TODO: *THIS SHALL BE REPLACED BY AN UPDATED JAVAFX VARIANT*
 * GUI class to represent the chessboard.
 */
public class Table {
    // base structures
    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private Board chessBoard;
    private final MoveLog moveLog;

    // movement
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;

    // options
    private BoardDirection boardDirection;
    public boolean highLightLegalMoves;

    // visual dimensions
    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(900,900);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(500, 450);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(20, 20);

    // todo: correct this path
    private static String pieceImagesPath = "application/src/main/resources/images/";

    public Table() {
        // main frame
        this.gameFrame = new JFrame("Chess");
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.gameFrame.setLayout(new BorderLayout());
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        // add a log of movements
        this.moveLog = new MoveLog();
        // add menu
        final JMenuBar tableMenuBar = populateTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        // construct data structure board representation
        this.chessBoard = Board.createStandardBoard();
        // set orientation
        this.boardDirection = BoardDirection.NORMAL;
        // add visual board representation
        this.boardPanel = new BoardPanel();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        // add game history panel and taken pieces panel
        this.gameFrame.add(gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.add(takenPiecesPanel, BorderLayout.WEST);
        // show main frame
        this.gameFrame.setVisible(true);
    }

    /**
     * Creates the top menu bar for the window frame
     * @return JMenuBar with menu items
     */
    private JMenuBar populateTableMenuBar() {
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
            int x = 0;
            for (final TilePanel boardTile : boardDirection.traverse(boardTiles)) {
                boardTile.drawTile(board);
                add(boardTile);
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

        TilePanel(BoardPanel boardPanel, Coordinate tilePosition) {
            super(new GridBagLayout());
            this.coordinateId = tilePosition;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceImage(chessBoard);
            if (highLightLegalMoves) highlightPossibleMoves(chessBoard);
            // every tile has a event listener
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isRightMouseButton(e)) {
                        // reset choice
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    } else if (isLeftMouseButton(e)) {
                        if (sourceTile == null) {
                            // select piece to move
                            sourceTile = chessBoard.getTile(tilePosition);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) {
                                sourceTile = null;
                            }
                        } else {
                            // select where to move
                            destinationTile = chessBoard.getTile(tilePosition);
                            // does this move (source to destination) exist? -> check with the MoveFactory
                            final Move moveToTile = Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoord(), destinationTile.getTileCoord());
                            // try to execute the move on the board
                            final MoveTransition boardChange = chessBoard.currentPlayer().makeMove(moveToTile);
                            // did the move follow through? if so -> replace current board -> redraw
                            if (boardChange.getMoveStatus().isDone()) {
                                chessBoard = boardChange.getTransitionBoard();
                                moveLog.addMove(moveToTile);
                                gameHistoryPanel.redoPanel(chessBoard, moveLog);
                                takenPiecesPanel.redoPanel(moveLog);
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                    }
                    invokeLater(() -> {
                        boardPanel.drawBoard(chessBoard);
                    });
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
            validate();
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

        /**
         * TODO: WARNING THIS MAY BE SLOW -> FETCH IMAGE FROM DISK FOR EVERY REDRAW (= for every board change)
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
                    this.add(new JLabel(new ImageIcon(image)));
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
               setBackground(Color.decode("#411410"));
           } else {
               setBackground(Color.decode("#832821"));
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
                return board.currentPlayer().getLegalMovesForPiece(humanMovedPiece);
            }
            return Collections.emptyList();
        }
    }

    /**
     * This class wraps methods for list so that we can
     * represent the moves that happen on a board.
     */
    static class MoveLog {
        private final List<Move> moves;

        private MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(Move move) {
            this.moves.add(move);
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public boolean removeMove(Move move) {
           return this.moves.remove(move);
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
