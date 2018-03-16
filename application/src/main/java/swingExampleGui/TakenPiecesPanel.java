package swingExampleGui;

import board.BoardUtils;
import board.Move;
import com.google.common.primitives.Ints;
import pieces.Alliance;
import pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static swingExampleGui.Table.MoveLog;

/**
 * This class represents the panel of the gui where the taken pieces are displayed
 */
public class TakenPiecesPanel extends JPanel {
    private static final Color PANEL_COLOR = Color.decode("#832921");
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40, 80);
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private final JPanel northPanel;
    private final JPanel southPanel;

    /**
     * Constructor for the taken pieces panel
     */
    public TakenPiecesPanel() {
        super(new BorderLayout());
        this.setBackground(PANEL_COLOR);
        this.setBorder(PANEL_BORDER);

        this.northPanel = new JPanel(new GridLayout(BoardUtils.getHeight(), 2));
        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel = new JPanel(new GridLayout(BoardUtils.getHeight(), 2));
        this.southPanel.setBackground(PANEL_COLOR);
        this.add(this.northPanel, BorderLayout.NORTH);
        this.add(this.southPanel, BorderLayout.SOUTH);

        this.setPreferredSize(TAKEN_PIECES_DIMENSION);
    }

    /**
     * Redo the TakenPiecesPanel according to what has been registered in a MoveLog
     *
     * @param moveLog to evaluate what pieces that have been taken
     */
    public void redoPanel(MoveLog moveLog) {
        this.southPanel.removeAll();
        this.northPanel.removeAll();
        List<Piece> whiteTakenPieces = new ArrayList<>();
        List<Piece> blackTakenPieces = new ArrayList<>();

        for (Move move : moveLog.getMoves()) {
            if (move.isAttack()) {
                final Piece takenPiece = move.getAttackedPiece();
                if (takenPiece.getPieceAlliance() == Alliance.WHITE) whiteTakenPieces.add(takenPiece);
                else if (takenPiece.getPieceAlliance() == Alliance.BLACK) blackTakenPieces.add(takenPiece);
                else
                    throw new RuntimeException("Something went wrong when adding taken pieces to the TakenPiecesPanel");
            }
        }
        // sort pieces according to their set value
        whiteTakenPieces.sort((o1, o2) -> Ints.compare(o1.getPieceType().getPieceValue(), o2.getPieceType().getPieceValue()));
        blackTakenPieces.sort((o1, o2) -> Ints.compare(o1.getPieceType().getPieceValue(), o2.getPieceType().getPieceValue()));
        // get image for each piece
        for (Piece takenPiece : whiteTakenPieces) this.add(getPieceImage(takenPiece));
        for (Piece takenPiece : blackTakenPieces) this.add(getPieceImage(takenPiece));
        validate();
    }

    /**
     * Get image belonging to a given piece
     *
     * @param piece to get image for
     * @return JLabel graphical representation of piece
     */
    private JLabel getPieceImage(Piece piece) {
        try {
            // append path with alliance type and string representation of piece to get correct image from resources
            final BufferedImage image = ImageIO.read(new File("application/src/main/resources/images/" +
                    piece.getPieceAlliance().toString().substring(0, 1) +
                    piece.toString() + ".png"));
            return new JLabel(new ImageIcon(image));
        } catch (IOException e) {
            System.out.println("Something went wrong when assigning images to pieces [TakenPiecesPanel.java]");
            e.printStackTrace();
        }
        return null;
    }

}
