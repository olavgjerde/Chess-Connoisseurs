package gui.panes;

import gui.GameStateManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import pieces.Alliance;

/**
 * This class creates an info-pane that displays who's turn it is and if a player is in check
 */
public class InfoPane extends FlowPane {
    public InfoPane(double windowWidth, final GameStateManager gameStateManager) {
        //Do not draw if game has ended (would overwrite game over pane)
        if (gameStateManager.isGameOver()) return;

        this.setPadding(new Insets(3, 0, 2, 0));
        this.setAlignment(Pos.CENTER);

        if (gameStateManager.currentPlayerInCheck() || gameStateManager.currentPlayerInStaleMate()) {
            this.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 120%, derive(darkred, -20%), black)");
        } else {
            this.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 120%, green, black)");
        }

        String player = gameStateManager.currentPlayerAlliance() == Alliance.WHITE ?
                gameStateManager.getWhiteUsername() : gameStateManager.getBlackUsername();
        String turnText = "IT'S " + player.toUpperCase() + "'S TURN";
        Text infoText = new Text(turnText);
        infoText.setFont(Font.font("Verdana", FontWeight.BOLD, windowWidth / 85));
        infoText.setFill(Color.WHITE);

        this.getChildren().add(infoText);
    }
}
