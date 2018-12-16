package gui.windows;

import board.BoardUtils;
import board.GameMode;
import gui.GameStateManager;
import gui.extra.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pieces.Alliance;
import pieces.Piece;
import java.util.Arrays;

public class PromotionWindow {
    private Stage promotionStage;
    private GameStateManager gameStateManager;
    private Piece.PieceType pieceSelected;

    public PromotionWindow(double pStageX, double pStageY, double parentWidth, double parentHeight, final GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
        this.promotionStage = new Stage();

        FlowPane menuRoot = createPromotionContent(parentWidth, parentHeight);

        //Positioning
        promotionStage.setX(pStageX + parentWidth / 2 - promotionStage.getWidth() / 2);
        promotionStage.setY(pStageY + parentHeight / 2 - promotionStage.getHeight() / 2);

        //Window settings
        promotionStage.initStyle(StageStyle.UNDECORATED);
        promotionStage.initModality(Modality.APPLICATION_MODAL);
        promotionStage.setResizable(false);
        promotionStage.setScene(new Scene(menuRoot));
    }

    private FlowPane createPromotionContent(double parentWidth, double parentHeight) {
        FlowPane menuRoot = new FlowPane();
        menuRoot.setAlignment(Pos.CENTER);
        menuRoot.setPadding(new Insets(0));

        //Give buttons a size in relation to screen dimensions
        double buttonSize = ((parentWidth + parentHeight) * 4 / (BoardUtils.getInstance().getWidth() * BoardUtils.getInstance().getHeight()));

        //Fetch images for promotion and scale them to fit within buttons
        ResourceLoader resources = ResourceLoader.getInstance();
        Alliance playerAlliance = gameStateManager.currentPlayerAlliance();
        ImageView q = playerAlliance == Alliance.WHITE ? new ImageView(resources.WQ) : new ImageView(resources.BQ),
                  k = playerAlliance == Alliance.WHITE ? new ImageView(resources.WN) : new ImageView(resources.BN),
                  b = playerAlliance == Alliance.WHITE ? new ImageView(resources.WB) : new ImageView(resources.BB),
                  r = playerAlliance == Alliance.WHITE ? new ImageView(resources.WR) : new ImageView(resources.BR);
        for (ImageView image : Arrays.asList(q, k, b, r)) {
            image.setPreserveRatio(true);
            image.setFitWidth(buttonSize / 4);
        }

        Button queen = new Button("QUEEN", q), knight = new Button("KNIGHT", k),
               bishop = new Button("BISHOP", b), rook = new Button("ROOK", r);

        //Promotion conditions for light brigade
        if (gameStateManager.getGameMode().equals(GameMode.LIGHTBRIGADE)) {
            if (gameStateManager.currentPlayerAlliance() == Alliance.WHITE) knight.setDisable(true);
            else queen.setDisable(true);
            bishop.setDisable(true);
            rook.setDisable(true);
        }

        menuRoot.getChildren().addAll(queen, knight, bishop, rook);

        //Style buttons
        menuRoot.setPrefWrapLength(buttonSize);
        for (Button button : Arrays.asList(queen, knight, bishop, rook)) {
            button.setPrefWidth(buttonSize);
            button.setPrefHeight(buttonSize / 2);
            button.setFocusTraversable(false);
        }

        //Set values when selecting button
        queen.setOnAction(event -> {
            pieceSelected = Piece.PieceType.QUEEN;
            promotionStage.close();
        });
        knight.setOnAction(event -> {
            pieceSelected = Piece.PieceType.KNIGHT;
            promotionStage.close();
        });
        bishop.setOnAction(event -> {
            pieceSelected = Piece.PieceType.BISHOP;
            promotionStage.close();
        });
        rook.setOnAction(event -> {
            pieceSelected = Piece.PieceType.ROOK;
            promotionStage.close();
        });

        //Scaling of window
        promotionStage.setWidth(bishop.getPrefWidth() * 2 + 10);
        promotionStage.setHeight(bishop.getPrefHeight() * 2 + 10);

        return menuRoot;
    }

    public void showPromotionWindow() {
        promotionStage.showAndWait();
    }

    public Piece.PieceType getSelectedPiece() {
        if (pieceSelected == null) throw new IllegalStateException("No piece selected");
        return pieceSelected;
    }
}
