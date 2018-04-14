import javafx.scene.image.Image;
import javafx.scene.image.Image;
import pieces.Alliance;
import pieces.Piece;

/**
 * Load all the resources into memory
 */
public class ResourceLoader {

    //Pieces
    final Image BB;
    final Image BK;
    final Image BN;
    final Image BP;
    final Image BQ;
    final Image BR;
    final Image WB;
    final Image WK;
    final Image WN;
    final Image WP;
    final Image WQ;
    final Image WR;

    //GUI images
    final Image hint;
    final Image redo;
    final Image undo;

    //sounds
    //TODO

    public ResourceLoader() {

        BB = new Image("/images/" + "BB" + ".png");
        BK = new Image("/images/" + "BK" + ".png");
        BN = new Image("/images/" + "BN" + ".png");
        BP = new Image("/images/" + "BP" + ".png");
        BQ = new Image("/images/" + "BQ" + ".png");
        BR = new Image("/images/" + "BR" + ".png");

        WB = new Image("/images/" + "WB" + ".png");
        WK = new Image("/images/" + "WK" + ".png");
        WN = new Image("/images/" + "WN" + ".png");
        WP = new Image("/images/" + "WP" + ".png");
        WQ = new Image("/images/" + "WQ" + ".png");
        WR = new Image("/images/" + "WR" + ".png");

        hint = new Image("/images/GUI/hint.png");
        redo = new Image("/images/GUI/redo.png");
        undo = new Image("/images/GUI/undo.png");

    }

    public Image getPieceImage(Piece p) {
        if (p.getPieceAlliance() == Alliance.WHITE) {
            switch (p.getPieceType()) {
                case BISHOP:
                    return WB;
                case KING:
                    return WK;
                case KNIGHT:
                    return WN;
                case PAWN:
                    return WP;
                case QUEEN:
                    return WQ;
                case ROOK:
                    return WR;
                default:
                    return null;
            }
        }
        else {
            switch (p.getPieceType()) {
                case BISHOP:
                    return BB;
                case KING:
                    return BK;
                case KNIGHT:
                    return BN;
                case PAWN:
                    return BP;
                case QUEEN:
                    return BQ;
                case ROOK:
                    return BR;
                default:
                    return null;
            }
        }
    }
}
