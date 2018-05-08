import javafx.scene.image.Image;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import pieces.Alliance;
import pieces.Piece;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Load all the resources into memory
 */
class ResourceLoader {

    //Pieces
    final Image BB, BK, BN, BP, BQ, BR, WB, WK, WN, WP, WQ, WR;

    //GUI images
    final Image ConnoisseurChess, hintButton, undoButton, gameStats;

    //Rule text
  //  final String horde, lightBrigade;

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

        hintButton = new Image("/images/GUI/HintButton.png");
        undoButton = new Image("/images/GUI/UndoButton.png");
        gameStats = new Image("/images/GUI/GameStats.png");
        ConnoisseurChess = new Image("/images/GUI/" + "ConnoisseurChess" + ".png");

      //  horde = readFile("/rules/horde.txt");
       // lightBrigade = readFile("/rules/lightbrigade.txt");
    }

    Image getPieceImage(Piece p) {
        Alliance pieceAlliance = p.getPieceAlliance();
        boolean isWhite = pieceAlliance == Alliance.WHITE;
        switch (p.getPieceType()) {
            case BISHOP:
                return isWhite ? WB : BB;
            case KING:
                return isWhite ? WK : BK;
            case KNIGHT:
                return isWhite ? WN : BN;
            case PAWN:
                return isWhite ? WP : BP;
            case QUEEN:
                return isWhite ? WQ : BQ;
            case ROOK:
                return isWhite ? WR : BR;
            default:
                return null;
        }
    }

    private String readFile(String path) {
        try {
                String data = "";
                data = new String(Files.readAllBytes(Paths.get(path)));
                return data;
            } catch (Exception e) {
                System.err.format("Exception occurred trying to read '%s'.", path);
                e.printStackTrace();
                return null;
            }

    }
}