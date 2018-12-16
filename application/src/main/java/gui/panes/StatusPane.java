package gui.panes;

import gui.ChessGame;
import gui.GameStateManager;
import gui.extra.ResourceLoader;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import pieces.Alliance;

/**
 * This class creates a status pane displaying statistics about the chess game to the user
 */
public class StatusPane extends VBox {
    private ChessGame parentGui;
    private ResourceLoader resources;
    private GameStateManager gameStateManager;

    public StatusPane(double parentWidth, final GameStateManager gameStateManager, boolean boardStatusEnabled, ChessGame parentGui) {
        this.parentGui = parentGui;
        this.resources = ResourceLoader.getInstance();
        this.gameStateManager = gameStateManager;

        String whitePlayerName = gameStateManager.getWhiteUsername();
        String blackPlayerName = gameStateManager.getWhiteUsername();
        int whitePlayerScore = gameStateManager.getWhitePlayerScore();
        int blackPlayerScore = gameStateManager.getBlackPlayerScore();
        String whitePlayerStats = gameStateManager.getWhitePlayerStats();
        String blackPlayerStats = gameStateManager.getBlackPlayerStats();

        this.setStyle("-fx-border-color: black; -fx-background-color: radial-gradient(center 50% 50%, radius 140%, derive(darkslategray, -20%), black)");
        this.setPadding(new Insets(30, 30, 0, 30));
        this.setMaxWidth(parentWidth / 8);
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(20);
        // Title for "Game Stats"
        ImageView gameStatImage = new ImageView(resources.gameStats);
        gameStatImage.setPreserveRatio(true);
        gameStatImage.setFitWidth(this.getMaxWidth() + 100);
        // WK and BK image in front of player name and score wrapped in a HBox
        HBox whiteScore = new HBox();
        HBox blackScore = new HBox();
        ImageView blackKingImg = new ImageView(resources.BK);
        ImageView whiteKingImg = new ImageView(resources.WK);
        whiteKingImg.setPreserveRatio(true);
        blackKingImg.setPreserveRatio(true);
        whiteKingImg.setFitHeight(this.getMaxWidth() / 5);
        blackKingImg.setFitHeight(this.getMaxWidth() / 5);
        //Player names and scores
        String whiteDisplayName = whitePlayerName.length() >= 15 ? whitePlayerName.substring(0, 12) + "..." : whitePlayerName;
        String blackDisplayName = blackPlayerName.length() >= 15 ? blackPlayerName.substring(0, 12) + "..." : blackPlayerName;
        Text whitePlayerText = new Text(whiteDisplayName + ": " + whitePlayerScore + " | " + whitePlayerStats);
        Text blackPlayerText = new Text(blackDisplayName + ": " + blackPlayerScore + " | " + blackPlayerStats);
        //Player names and scores styling
        whitePlayerText.setFont(Font.font("Verdana", FontWeight.NORMAL, (this.getMaxWidth() / 9) - whitePlayerText.getText().length() / 50.0));
        blackPlayerText.setFont(Font.font("Verdana", FontWeight.NORMAL, (this.getMaxWidth() / 9) - blackPlayerText.getText().length() / 50.0));
        whiteScore.setAlignment(Pos.CENTER);
        blackScore.setAlignment(Pos.CENTER);

        whitePlayerText.setFill(Color.WHITE);
        blackPlayerText.setFill(Color.WHITE);
        whiteScore.setSpacing(5);
        blackScore.setSpacing(5);
        whiteScore.getChildren().addAll(whiteKingImg,whitePlayerText);
        blackScore.getChildren().addAll(blackKingImg,blackPlayerText);

        this.getChildren().addAll(gameStatImage, whiteScore, blackScore);

        //Show the evaluation of the current board relative to the current player, can help you know how well you are doing
        if (boardStatusEnabled) {
            Color circleColor = Color.FORESTGREEN;
            if (gameStateManager.getBoardEvaluation() < 0) circleColor = Color.DARKRED;
            Circle circle = new Circle(this.getMaxWidth() / 12, circleColor);
            //Add fade to circle
            FadeTransition fade = new FadeTransition(Duration.millis(1300), circle);
            fade.setFromValue(1.0);
            fade.setToValue(0.8);
            fade.setCycleCount(Timeline.INDEFINITE);
            fade.setAutoReverse(true);
            fade.play();

            Text boardStatusText = new Text("ESTIMATED ODDS ");
            boardStatusText.setFont(Font.font("Verdana", FontWeight.NORMAL, this.getMaxWidth() / 10));
            boardStatusText.setFill(Color.WHITE);

            HBox boardStatusBox = new HBox();
            boardStatusBox.setAlignment(Pos.CENTER);
            boardStatusBox.getChildren().addAll(boardStatusText, circle);
            this.getChildren().addAll(boardStatusBox);
        }

        //Show the previous moves made
        Text moveHistoryText = new Text("PREVIOUS MOVE: " + gameStateManager.getLastMoveText());
        moveHistoryText.setFont(Font.font("Verdana", FontWeight.NORMAL, this.getMaxWidth() / 11));

        this.getChildren().addAll(moveHistoryText, createStatusPaneButtonBox(this.getMaxWidth() / 4));

        //Color all texts in the root node of status pane to the color white
        for (Node x : this.getChildren()) {
            if (x instanceof Text) ((Text) x).setFill(Color.WHITE);
        }
    }

    /**
     * Creates the HBox with buttons to display in the status pane
     *
     * @return populated HBox
     */
    private HBox createStatusPaneButtonBox(double buttonSize) {
        //Hint button for player help
        ImageView image = new ImageView(resources.hintButton);
        image.setFitHeight(buttonSize);
        image.setPreserveRatio(true);
        Button hintButton = new Button("",image);
        hintButton.setOnMouseEntered(event -> {
            Tooltip tp = new Tooltip("Let the AI suggest a move");
            Tooltip.install(hintButton, tp);
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(0.25);
            hintButton.setEffect(colorAdjust);
        });
        hintButton.setOnMouseExited(event -> {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(0);
            hintButton.setEffect(colorAdjust);
        });
        hintButton.setOnAction(event -> parentGui.showMoveHint());

        //Button for undoing a move
        image = new ImageView(resources.undoButton);
        image.setFitHeight(buttonSize);
        image.setPreserveRatio(true);
        Button backButton = new Button("", image);
        backButton.setOnMouseEntered(event -> {
            Tooltip tp = new Tooltip("Undo a move");
            Tooltip.install(backButton, tp);
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(0.25);
            backButton.setEffect(colorAdjust);
        });
        backButton.setOnAction(event -> {
            gameStateManager.undoMove();
            parentGui.drawChessPane();
        });
        backButton.setOnMouseExited(event -> {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(0);
            backButton.setEffect(colorAdjust);
        });
        if (gameStateManager.undoIsIllegal()) backButton.setDisable(true);

        //extra button styling
        HBox buttonContainer = new HBox(backButton, hintButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets((buttonSize / 500) * 165, 0, 0, 0));
        buttonContainer.setSpacing(5);
        for (Node x : buttonContainer.getChildren()) {
            x.setStyle("-fx-focus-color: darkslategrey; -fx-faint-focus-color: transparent; -fx-background-color: transparent;");
            x.setFocusTraversable(false);
            //Disable buttons
            if ((gameStateManager.isBlackAI() && gameStateManager.isWhiteAI()) ||
                (gameStateManager.currentPlayerAlliance() == Alliance.WHITE && gameStateManager.isWhiteAI() ||
                gameStateManager.currentPlayerAlliance() == Alliance.BLACK && gameStateManager.isBlackAI()) ||
                gameStateManager.currentPlayerInCheckMate() || gameStateManager.currentPlayerInStaleMate()) {

                x.setDisable(true);
            }
        }

        return buttonContainer;
    }
}
