package swingExampleGui;

import board.Board;
import board.Move;
import pieces.Alliance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static swingExampleGui.Table.MoveLog;

public class GameHistoryPanel extends JPanel {
    private final DataModel model;
    private final JScrollPane scrollPane;
    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100, 400);

    /**
     * Constructor for the game history panel
     */
    GameHistoryPanel() {
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        // this.setVisible(true);
    }

    /**
     * Redo the GameHistoryPanel according to what has been registered in a MoveLog
     *
     * @param board where the players are making their moves
     * @param moveLog to evaluate what pieces that have moved
     */
    void redoPanel(Board board, MoveLog moveLog) {
        int currentRow = 0;
        this.model.clear();
        for (Move move : moveLog.getMoves()) {
            final String moveText = move.toString();
            if (move.getMovedPiece().getPieceAlliance() == Alliance.WHITE) {
                this.model.setValueAt(move, currentRow, 0);
            } else if (move.getMovedPiece().getPieceAlliance() == Alliance.BLACK) {
                this.model.setValueAt(move, currentRow, 1);
                currentRow++;
            }
        }

        if (moveLog.getMoves().size() > 0) {
            final Move lastMove = moveLog.getMoves().get(moveLog.size() - 1);
            final String moveText = lastMove.toString();
            if (lastMove.getMovedPiece().getPieceAlliance() == Alliance.WHITE) {
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow, 0);
            } else if (lastMove.getMovedPiece().getPieceAlliance() == Alliance.BLACK) {
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow - 1, 1);
            }

            final JScrollBar vertical = scrollPane.getVerticalScrollBar();
            // scroll down to last move
            vertical.setValue(vertical.getMaximum());
        }
    }

    /**
     * Checks if the player is in checkmate or check and returns the string
     * representation of this situation
     * @param board where the players are making moves
     * @return String representation of checkmate or check, blank if none of the situation are present
     */
    private String calculateCheckAndCheckMateHash(Board board) {
        if (board.currentPlayer().isInCheckMate()) {
            return "#";
        } else if (board.currentPlayer().isInCheck()) {
            return "+";
        }
        return "";
    }

    private static class DataModel extends DefaultTableModel {
        private final List<Row> values;
        private static final String[] NAMES = {"WHITE", "BLACK"};

        DataModel() {
            this.values = new ArrayList<>();
        }

        public void clear() {
            this.values.clear();
            this.setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if (this.values == null) return 0;
            return this.values.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(int row, int column) {
            final Row currentRow = this.values.get(row);
            if (column == 0) return currentRow.getWhiteMove();
            else if (column == 1) return currentRow.getBlackMove();
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            final Row currentRow;
            if (this.values.size() <= row) {
                currentRow = new Row();
                this.values.add(currentRow);
            } else {
                currentRow = this.values.get(row);
            }

            if (column == 0) {
                currentRow.setWhiteMove(aValue.toString());
                fireTableRowsInserted(row, row);
            } else if (column == 1) {
                currentRow.setBlackMove(aValue.toString());
                fireTableCellUpdated(row, column);
            }
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return Move.class;
        }

        @Override
        public String getColumnName(int column) {
            return NAMES[column];
        }

    }

    private static class Row {
        private String whiteMove;
        private String blackMove;

        Row() {
        }

        public String getWhiteMove() {
            return whiteMove;
        }

        public void setWhiteMove(String whiteMove) {
            this.whiteMove = whiteMove;
        }

        public String getBlackMove() {
            return blackMove;
        }

        public void setBlackMove(String blackMove) {
            this.blackMove = blackMove;
        }
    }

}
