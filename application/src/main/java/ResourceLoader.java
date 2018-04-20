import javafx.scene.image.Image;
import javafx.scene.image.Image;
import pieces.Alliance;
import pieces.Piece;

/**
 * Load all the resources into memory
 */
class ResourceLoader {

    //Pieces
    final Image BB, BK ,BN, BP, BQ, BR, WB, WK, WN, WP, WQ, WR, ConnoisseurChess;

    //GUI images
    final Image hint, undo;

    //sounds
    //TODO

    ResourceLoader() {
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
        undo = new Image("/images/GUI/undo.png");

        ConnoisseurChess = new Image("/images/GUI/" + "ConnoisseurChess" + ".png");
    }

    Image getPieceImage(Piece p) {
        Alliance pieceAlliance = p.getPieceAlliance();
        boolean isWhite = pieceAlliance == Alliance.WHITE;
        switch (p.getPieceType()) {
            case BISHOP: return isWhite ? WB : BB;
            case KING: return isWhite ? WK : BK;
            case KNIGHT: return isWhite ? WN : BN;
            case PAWN: return isWhite ? WP : BP;
            case QUEEN: return isWhite ? WQ : BQ;
            case ROOK: return isWhite ? WR : BR;
            default: return null;
        }
    }
}
