package gui.panes;

import gui.extra.ResourceLoader;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import pieces.Alliance;
import pieces.Piece;
import java.util.Comparator;
import java.util.List;

/**
 * This class creates a pane where the chess pieces that haven been taken
 * are displayed to the user
 */
public class TakenPiecesPane extends VBox {

    public TakenPiecesPane(double parentWidth, List<Piece> takenPieces) {
        this.setStyle("-fx-border-color: black; -fx-background-color: radial-gradient(center 50% 50%, radius 120%, derive(darkslategray, -20%), black)");
        this.setMaxWidth(parentWidth / 16);
        this.setAlignment(Pos.CENTER);

        final double IMAGE_WIDTH = this.getMaxWidth() / 3.5;
        FlowPane whitePiecesBox = new FlowPane();
        whitePiecesBox.setPrefWrapLength(IMAGE_WIDTH * 2.1);
        whitePiecesBox.setAlignment(Pos.CENTER);
        FlowPane blackPieceBox = new FlowPane();
        blackPieceBox.setPrefWrapLength(IMAGE_WIDTH * 2.1);
        blackPieceBox.setAlignment(Pos.CENTER);

        //Sorts pieces by value
        Comparator<Piece> chessCompare = Comparator.comparingInt(o -> o.getPieceType().getPieceValue());
        takenPieces.sort(chessCompare);

        for (Piece taken : takenPieces) {
            ImageView takenImage = new ImageView(ResourceLoader.getInstance().getPieceImage(taken));
            takenImage.setFitWidth(IMAGE_WIDTH);
            takenImage.setPreserveRatio(true);
            if (taken.getPieceAlliance() == Alliance.WHITE) whitePiecesBox.getChildren().add(takenImage);
            else blackPieceBox.getChildren().add(takenImage);
        }

        this.getChildren().addAll(whitePiecesBox, blackPieceBox);
    }
}
