package gui.panes;

import board.GameMode;
import gui.ChessGame;
import gui.GameStateManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import pieces.Alliance;

/**
 * This class creates a game over pane that displays the statistics of the game, and gives the user
 * the ability to continue with another round or exit to the main menu
 */
public class GameOverPane extends FlowPane {
    private final ChessGame parentGui;
    private final GameStateManager gameStateManager;

    public GameOverPane(double windowWidth, final GameStateManager gameStateManager, BorderPane gamePlayPane, final ChessGame parentGui) {
        this.parentGui = parentGui;
        this.gameStateManager = gameStateManager;
        this.setPadding(new Insets(3, 0, 2, 0));
        this.setAlignment(Pos.CENTER);

        addText(windowWidth);
        addButtons(windowWidth, gamePlayPane);
    }

    private void addButtons(double windowWidth, BorderPane gamePlayPane) {
        String whitePlayerName = gameStateManager.getWhiteUsername();
        String blackPlayerName = gameStateManager.getWhiteUsername();

        //Buttons
        Button newRound = new Button("NEXT ROUND"), quit = new Button("QUIT");
        newRound.setOnAction(e -> {
            //Construct new game state manager with settings from last rounds game state manager
            GameStateManager newManager = new GameStateManager(whitePlayerName, blackPlayerName,
                    gameStateManager.isWhiteAI(), gameStateManager.isBlackAI(),
                    gameStateManager.getAiDepth(), gameStateManager.getGameMode());
            parentGui.setGameManager(newManager);

            //Removes game over pane
            gamePlayPane.setBottom(new InfoPane(windowWidth, gameStateManager));
            //Redraw
            parentGui.drawChessPane();
            //Makes the first move in new round if AI is white
            if (gameStateManager.currentPlayerAlliance() == Alliance.WHITE && gameStateManager.isWhiteAI()) {
                parentGui.doAiMove();
            } else if (gameStateManager.currentPlayerAlliance() == Alliance.BLACK && gameStateManager.isBlackAI()) {
                parentGui.doAiMove();
            }
        });
        quit.setOnAction(e -> System.exit(0));

        //Button container
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setSpacing(10);
        buttonContainer.getChildren().addAll(newRound, quit);

        this.getChildren().addAll(buttonContainer);
    }

    private void addText(double windowWidth) {
        String whitePlayerName = gameStateManager.getWhiteUsername();
        String blackPlayerName = gameStateManager.getWhiteUsername();
        int whitePlayerScore = gameStateManager.getWhitePlayerScore();
        int blackPlayerScore = gameStateManager.getBlackPlayerScore();

        //Text
        Text title = new Text("GAME OVER - ");
        if (gameStateManager.getGameMode().equals(GameMode.HORDE)) title = new Text("GAME OVER - ");
        else if (gameStateManager.currentPlayerInCheckMate()) title = new Text("CHECKMATE - ");
        else if (gameStateManager.currentPlayerInStaleMate()) title = new Text("STALEMATE - ");
        else if (gameStateManager.isDraw()) title = new Text("DRAW - ");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, windowWidth / 85));
        Text t1 = new Text("UPDATED SCORES: ");
        t1.setFont(Font.font("Verdana", FontWeight.BOLD, windowWidth / 85));
        Text t2 = new Text(whitePlayerName + ": " + whitePlayerScore + " / ");
        Text t3 = new Text(blackPlayerName + ": " + blackPlayerScore + " ");
        double length = t2.getText().length() + t3.getText().length();
        t2.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, windowWidth / 85 - length / 50));
        t3.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, windowWidth / 85 - length / 50));
        this.getChildren().addAll(title, t1, t2, t3);
    }
}
