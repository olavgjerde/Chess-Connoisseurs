import board.*;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import pieces.Alliance;
import pieces.Piece;
import pieces.Piece.PieceType;
import player.Score;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ChessGUI extends Application {
    private static double screenWidth = Screen.getPrimary().getBounds().getWidth(), screenHeight = Screen.getPrimary().getBounds().getHeight();
    private static Stage primaryStage;
    private BorderPane gamePlayPane;
    private StackPane gameBackgroundContainer;
    //Game state manager is set after confirming on start menu
    private static GameStateManager gameStateManager;
    //Player movement
    private Tile startTile, destinationTile;
    private Piece userMovedPiece;
    //For piece animation (toggles one animation per finished move)
    private static boolean playMoveAnimation;
    //Hint coordinates
    private Coordinate hintStartCoordinate, hintDestinationCoordinate;
    //Information toggles
    private boolean availableMoveHighlightEnabled = true, lastMoveHighlightEnabled = true, boardStatusEnabled = true, playSound = true;
    //User score related variables
    private Score scoreSystem;
    private String whitePlayerName, whitePlayerStats, blackPlayerName, blackPlayerStats;
    private int whitePlayerScore, blackPlayerScore;
    //Sound and resource manager
    private static ResourceLoader resources = new ResourceLoader();
    private static SoundClipManager soundClipManager;
    private final double SOUNDTRACK_VOLUME = 0.14;

    @Override
    public void init() {
        this.scoreSystem = new Score();
        this.scoreSystem.readHighscore();
    }

    @Override
    public void start(Stage primaryStage) {
        ChessGUI.primaryStage = primaryStage;
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            screenWidth = newValue.doubleValue();
            if (gameStateManager != null) Platform.runLater(this::drawChessPane);
        });
        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
            screenHeight = newValue.doubleValue();
            if (gameStateManager != null) Platform.runLater(this::drawChessPane);
        });

        primaryStage.setTitle("Connoisseur Chess");
        primaryStage.setOnCloseRequest(event -> Platform.exit());

        showStartMenu(screenWidth / 1.6, screenHeight / 1.4);

        primaryStage.show();
    }

    /**
     * Play sounds like buttonClicks and PieceDrop without interrupting main music.
     *
     * @param name   of sound file (path to)
     * @param volume volume to be played at
     */
    private void playSound(String name, double volume) {
        new Thread(new Task<Object>() {
            @Override
            protected Object call() {
                new SoundClipManager(name, false, volume, playSound);
                return null;
            }
        }).start();
    }

    /**
     * Shows the start menu for the application
     */
    private void showStartMenu(double sceneWidth, double sceneHeight) {
        VBox menuBox = new VBox();
        menuBox.setMaxWidth(sceneWidth / 1.6);
        menuBox.setMaxHeight(sceneHeight / 1.5);
        menuBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-background-radius: 10;");
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setSpacing(7);

        //Play menu music
        if (playSound && soundClipManager != null) soundClipManager.clear();
        soundClipManager = new SoundClipManager("MenuMusic.wav", true, SOUNDTRACK_VOLUME, playSound);

        // Add logo
        ImageView logo = new ImageView(resources.ConnoisseurChess);
        logo.setStyle("-fx-alignment: centre;");
        menuBox.getChildren().add(logo);

        // Add white player fields
        HBox whiteOptionBox = new HBox();
        whiteOptionBox.setAlignment(Pos.CENTER);
        whiteOptionBox.setSpacing(5);
        ImageView whiteImage = new ImageView(resources.WK);
        whiteImage.setFitHeight(50);
        whiteImage.setPreserveRatio(true);
        Text whiteOptionText = new Text("WHITE PLAYER");
        whiteOptionText.setFont(new Font("Arial", sceneWidth/60));
        TextField whiteNameField = new TextField("Player1");
        whiteNameField.setMaxWidth(menuBox.getMaxWidth() / 4);
        // Buttons for white
        final ToggleGroup whiteOptions = new ToggleGroup();
        RadioButton whiteHumanOption = new RadioButton("Human");
        whiteHumanOption.setToggleGroup(whiteOptions);
        whiteHumanOption.setUserData(false);
        whiteHumanOption.setSelected(true);
        RadioButton whiteAiOption = new RadioButton("AI");
        whiteAiOption.setToggleGroup(whiteOptions);
        whiteAiOption.setUserData(true);
        // Add to whiteOptionBox then to root pane for the scene
        whiteOptionBox.getChildren().addAll(whiteImage, whiteOptionText, whiteNameField, whiteHumanOption, whiteAiOption);
        menuBox.getChildren().add(whiteOptionBox);

        // Add black player fields
        HBox blackOptionBox = new HBox();
        blackOptionBox.setAlignment(Pos.CENTER);
        blackOptionBox.setSpacing(5);
        ImageView blackImage = new ImageView(resources.BK);
        blackImage.setFitHeight(50);
        blackImage.setPreserveRatio(true);
        Text blackOptionText = new Text("BLACK PLAYER");
        blackOptionText.setFont(new Font("Arial", sceneWidth/60));
        TextField blackNameField = new TextField("Player2");
        blackNameField.setMaxWidth(menuBox.getMaxWidth() / 4);
        // Buttons for black
        final ToggleGroup blackOptions = new ToggleGroup();
        RadioButton blackHumanOption = new RadioButton("Human");
        blackHumanOption.setToggleGroup(blackOptions);
        blackHumanOption.setUserData(false);
        blackHumanOption.setSelected(true);
        RadioButton blackAiOption = new RadioButton("AI");
        blackAiOption.setToggleGroup(blackOptions);
        blackAiOption.setUserData(true);
        // Add to blackOptionBox then to root pane for the scene
        blackOptionBox.getChildren().addAll(blackImage, blackOptionText, blackNameField, blackHumanOption, blackAiOption);
        menuBox.getChildren().add(blackOptionBox);

        // Add buttons for the starting board state
        HBox boardStateBox = new HBox();
        boardStateBox.setAlignment(Pos.CENTER);
        boardStateBox.setSpacing(5);
        final ToggleGroup boardStateOptions = new ToggleGroup();
        RadioButton boardStateOption1 = new RadioButton("Standard board");
        boardStateOption1.setToggleGroup(boardStateOptions);
        boardStateOption1.setUserData(1);
        boardStateOption1.setSelected(true);
        RadioButton boardStateOption2 = new RadioButton("Random board");
        boardStateOption2.setToggleGroup(boardStateOptions);
        boardStateOption2.setUserData(2);
        boardStateOption2.setSelected(false);
        RadioButton boardStateOption3 = new RadioButton("Horde board");
        boardStateOption3.setToggleGroup(boardStateOptions);
        boardStateOption3.setUserData(3);
        boardStateOption3.setSelected(false);
        // Add to boardStateBox then to root pane for scene
        boardStateBox.getChildren().addAll(boardStateOption1, boardStateOption2, boardStateOption3);
        menuBox.getChildren().add(boardStateBox);

        // Create and add difficulty buttons
        HBox aiOptionBox = new HBox();
        aiOptionBox.setAlignment(Pos.CENTER);
        aiOptionBox.setSpacing(5);
        final ToggleGroup aiOptions = new ToggleGroup();
        String[] levelPrefix = {"Easy", "Intermediate", "Expert", "Experimental"};
        List<RadioButton> difficultyButtons = new ArrayList<>();
        for (int i = 0; i < levelPrefix.length; i++) {
            difficultyButtons.add(new RadioButton(levelPrefix[i]));
            if (levelPrefix[i].equals("Intermediate")) difficultyButtons.get(i).setSelected(true);
            difficultyButtons.get(i).setUserData(i + 2);
            difficultyButtons.get(i).setDisable(true);
            difficultyButtons.get(i).setToggleGroup(aiOptions);
            difficultyButtons.get(i).setOnAction(event -> playSound("ButtonClick.wav", 1));
        }
        Text aiDifficulty = new Text("AI DIFFICULTY");
        aiDifficulty.setFont(new Font(18));
        // Add to aiOptionBox then to root pane for scene
        aiOptionBox.getChildren().addAll(difficultyButtons);
        menuBox.getChildren().addAll(aiDifficulty, aiOptionBox);

        // Button events
        whiteHumanOption.setOnAction(e -> {
            playSound("ButtonClick.wav", 1);
            whiteNameField.setDisable(false);
            whiteNameField.setText("Player1");
            for (RadioButton x : difficultyButtons)
                if (!blackAiOption.isSelected()) x.setDisable(true);
        });
        blackHumanOption.setOnAction(e -> {
            playSound("ButtonClick.wav", 1);
            blackNameField.setDisable(false);
            blackNameField.setText("Player2");
            for (RadioButton x : difficultyButtons)
                if (!whiteAiOption.isSelected()) x.setDisable(true);
        });
        whiteAiOption.setOnAction(e -> {
            playSound("ButtonClick.wav", 1);
            whiteNameField.setDisable(true);
            whiteNameField.setText("CPU");
            for (RadioButton x : difficultyButtons) x.setDisable(false);
        });
        blackAiOption.setOnAction(e -> {
            playSound("ButtonClick.wav", 1);
            blackNameField.setDisable(true);
            blackNameField.setText("CPU");
            for (RadioButton x : difficultyButtons) x.setDisable(false);
        });
        boardStateOption1.setOnAction(event -> playSound("ButtonClick.wav", 1));
        boardStateOption3.setOnAction(event -> playSound("ButtonClick.wav", 1));

        menuBox.getChildren().add(createStartMenuConfirmButton(whiteOptions, blackOptions, aiOptions, boardStateOptions, whiteNameField, blackNameField));

        StackPane root = new StackPane(createStartMenuBackground(), menuBox);
        Scene startMenu = new Scene(root, sceneWidth, sceneHeight);
        primaryStage.setScene(startMenu);
    }

    /**
     * Creates a pane with floating circles
     * (Example use: background for a start menu)
     *
     * @return constructed pane
     */
    private Pane createStartMenuBackground() {
        final int CIRCLE_COUNT = 800;
        Pane backgroundContainer = new Pane();
        backgroundContainer.setStyle("-fx-background-color: radial-gradient(center 50% 50% , radius 80% , darkslategray, black);");
        for (int i = 0; i < CIRCLE_COUNT; i++) {
            spawnBackgroundCircle(backgroundContainer, 1.0);
        }
        return backgroundContainer;
    }

    /**
     * Spawns a circle on the pane given as parameter, with different animations and colors
     * Respawn a new circle if animation has ended
     * @param circleContainer pane which should contain the circles
     */
    private void spawnBackgroundCircle(Pane circleContainer, double startOpacity) {
        Color[] colors = {
                new Color(0.1,0.6,0.5, startOpacity).saturate().brighter().brighter(),
                new Color(0.2,0.3,0.3, startOpacity).saturate().brighter().brighter(),
                new Color(0.4,0.4,0.4, startOpacity).saturate().brighter().brighter(),
                new Color(0.2,0.4,0.4, startOpacity).saturate().brighter().brighter(),
                new Color(0.1,0.6,0.3, startOpacity).saturate().brighter().brighter()};
        Circle circle = new Circle(0);
        circle.setManaged(false);
        //Pick randomly from color array
        circle.setFill(colors[ThreadLocalRandom.current().nextInt(colors.length)]);
        //Take a random position within window size
        circle.setCenterX(ThreadLocalRandom.current().nextDouble(screenWidth));
        circle.setCenterY(ThreadLocalRandom.current().nextDouble(screenHeight));
        //Add to pane
        circleContainer.getChildren().add(circle);
        //Add animation
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(circle.radiusProperty(), 0),
                        new KeyValue(circle.centerXProperty(), circle.getCenterX()),
                        new KeyValue(circle.centerYProperty(), circle.getCenterY()),
                        new KeyValue(circle.opacityProperty(), 0)),
                new KeyFrame(
                        Duration.seconds(5 + ThreadLocalRandom.current().nextDouble() * 5),
                        new KeyValue(circle.opacityProperty(), ThreadLocalRandom.current().nextDouble()),
                        new KeyValue(circle.radiusProperty(), ThreadLocalRandom.current().nextDouble() * 20)),
                new KeyFrame(
                        Duration.seconds(10 + ThreadLocalRandom.current().nextDouble() * 20),
                        new KeyValue(circle.radiusProperty(), 0),
                        new KeyValue(circle.centerXProperty(), ThreadLocalRandom.current().nextDouble() * screenWidth),
                        new KeyValue(circle.centerYProperty(), ThreadLocalRandom.current().nextDouble() * screenHeight),
                        new KeyValue(circle.opacityProperty(), 0))
        );
        timeline.setCycleCount(1);
        timeline.setOnFinished(event -> {
            circleContainer.getChildren().remove(circle);
            spawnBackgroundCircle(circleContainer, 1.0);
        });
        //Start animation
        timeline.play();
    }

    /**
     * Create a confirm button to use in the StartMenu Scene
     *
     * @param whiteOptions      ToggleGroup which contains the options for white player
     * @param blackOptions      ToggleGroup which contains the options for black player
     * @param aiOptions         ToggleGroup which contains the options for ai
     * @param boardStateOptions ToggleGroup which contains the options for board state
     * @param whiteNameField    TextField where the white player enters name
     * @param blackNameField    TextField where the black player enters name
     * @return Button that confirms the settings and applies the to the games variables
     */
    private Button createStartMenuConfirmButton(ToggleGroup whiteOptions, ToggleGroup blackOptions, ToggleGroup aiOptions,
                                                ToggleGroup boardStateOptions, TextField whiteNameField, TextField blackNameField) {
        //Confirm settings button
        Button confirmSettings = new Button("Confirm");
        confirmSettings.setMaxWidth(100);
        //Confirm button action
        confirmSettings.setOnAction(e -> {
            boolean isWhiteAI = (boolean) whiteOptions.getSelectedToggle().getUserData();
            boolean isBlackAI = (boolean) blackOptions.getSelectedToggle().getUserData();
            int aiDepth = (int) aiOptions.getSelectedToggle().getUserData();

            String suffix;
            int rating;
            switch (aiDepth) {
                case 2: { suffix = "Easy"; rating = 1200; break; }
                case 3: { suffix = "Intermediate"; rating = 1500; break; }
                case 4: { suffix = "Expert"; rating = 1800; break; }
                case 5: { suffix = "Experimental"; rating = 2000; break; }
                default: { suffix = "Error"; rating = 9999; break; }
            }

            if (isWhiteAI) {
                whitePlayerName = "CPU(" + suffix + ")";
                scoreSystem.addUsername(whitePlayerName);
                scoreSystem.updateHighscore(whitePlayerName, rating);
            } else {
                whitePlayerName = whiteNameField.getText();
                scoreSystem.addUsername(whitePlayerName);
            }
            if (isBlackAI) {
                blackPlayerName = "CPU(" + suffix + ")";
                scoreSystem.addUsername(blackPlayerName);
                scoreSystem.updateHighscore(blackPlayerName, rating);
            } else {
                blackPlayerName = blackNameField.getText().trim();
                scoreSystem.addUsername(blackPlayerName);
            }

            whitePlayerScore = scoreSystem.getScore(whitePlayerName);
            blackPlayerScore = scoreSystem.getScore(blackPlayerName);
            whitePlayerStats = scoreSystem.getStats(whitePlayerName);
            blackPlayerStats = scoreSystem.getStats(blackPlayerName);

            int boardType = (int) boardStateOptions.getSelectedToggle().getUserData();

            //Construct new game state manager with settings from start menu
            gameStateManager = new GameStateManager(isWhiteAI, isBlackAI, aiDepth, boardType);

            showGameScene();
        });

        return confirmSettings;
    }

    /**
     * Bootstrapper for game scene, creates initial layout and draws every pane once
     */
    private void showGameScene() {
        this.gamePlayPane = new BorderPane();
        gamePlayPane.setTop(populateMenuBar());
        gamePlayPane.setRight(drawStatusPane());
        gamePlayPane.setLeft(drawTakenPiecesPane());

        //Add background pane for chess board and draw the chessboard itself
        this.gameBackgroundContainer = new StackPane();
        gameBackgroundContainer.setStyle("-fx-background-color: radial-gradient(center 50% 50% , radius 80% , darkslategray, black);");
        Pane circleContainer = new Pane();
        for (int i = 0; i < 100; i++) {
            spawnBackgroundCircle(circleContainer, 0.5);
        }
        gameBackgroundContainer.getChildren().addAll(circleContainer);
        gamePlayPane.setCenter(gameBackgroundContainer);
        drawChessPane();


        //Play game music
        if (playSound) {
            soundClipManager.clear();
            soundClipManager = new SoundClipManager("GameMusic.wav", true, SOUNDTRACK_VOLUME, playSound);
        }
        Scene gameScene = new Scene(gamePlayPane, screenWidth, screenHeight);
        gameScene.setCamera(new PerspectiveCamera());
        primaryStage.setScene(gameScene);
        //Set off white AI (in case of human vs white ai / ai vs ai)
        if (gameStateManager.isWhiteAI()) doAiMove();
    }

    /**
     * Populate the menu-bar with different segments and options
     *
     * @return populated MenuBar
     */
    private MenuBar populateMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(createFileMenu(), createOptionMenu());
        return menuBar;
    }

    /**
     * Create an options menu
     *
     * @return return populated options menu
     */
    private Menu createOptionMenu() {
        Menu optionsMenu = new Menu("Options");

        CheckMenuItem toggleHighlight = new CheckMenuItem("Highlight available moves");
        toggleHighlight.setOnAction(e -> availableMoveHighlightEnabled = !availableMoveHighlightEnabled);
        toggleHighlight.setSelected(true);

        CheckMenuItem toggleMoveHighlight = new CheckMenuItem("Highlight previous move");
        toggleMoveHighlight.setOnAction(event -> {
            lastMoveHighlightEnabled = !lastMoveHighlightEnabled;
            drawChessPane();
        });
        toggleMoveHighlight.setSelected(true);

        CheckMenuItem toggleBoardStatus = new CheckMenuItem("Show board status");
        toggleBoardStatus.setOnAction(event -> {
            boardStatusEnabled = !boardStatusEnabled;
            drawStatusPane();
        });
        toggleBoardStatus.setSelected(true);

        CheckMenuItem toggleMute = new CheckMenuItem("Toggle sound");
        toggleMute.setOnAction(e -> {
            if (playSound) {
                soundClipManager.clear();
                playSound = false;
            } else {
                playSound = true;
                soundClipManager = new SoundClipManager("GameMusic.wav", true, 0.2, true);
            }
        });
        toggleMute.setSelected(true);

        optionsMenu.getItems().addAll(toggleHighlight, toggleMoveHighlight, toggleBoardStatus, toggleMute);
        return optionsMenu;
    }

    /**
     * Create a file menu
     *
     * @return return populated file menu
     */
    private Menu createFileMenu() {
        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New game");
        newGame.setOnAction(event -> {
            gameStateManager.killAI();
            showStartMenu(screenWidth, screenHeight);
        });

        MenuItem highScores = new MenuItem("Highscores");
        highScores.setOnAction(event -> showHighScoreWindow());

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(event -> System.exit(0));

        fileMenu.getItems().addAll(newGame, highScores, exit);
        return fileMenu;
    }

    /**
     * Draw the pane that displays information about the games state
     *
     * @return VBox with nodes that belong to the state pane
     */
    private VBox drawStatusPane() {
        VBox statusPane = new VBox();
        statusPane.setStyle("-fx-border-color: black; -fx-background-color: radial-gradient(center 50% 50%, radius 140%, derive(darkslategray, -20%), black)");
        statusPane.setPadding(new Insets(30, 30, 0, 30));
        statusPane.setAlignment(Pos.TOP_CENTER);
        statusPane.setSpacing(10);

        Text title = new Text("GAME STATS");
        //Title styling
        title.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, screenWidth / 50));
        //Player names and scores
        if (whitePlayerName.length() >= 15) whitePlayerName = whitePlayerName.substring(0,12) + "... ";
        if (blackPlayerName.length() >= 15) blackPlayerName = blackPlayerName.substring(0,12) + "... ";
        Text whitePlayerText = new Text(whitePlayerName + ": " + whitePlayerScore + " | " + whitePlayerStats);
        Text blackPlayerText = new Text(blackPlayerName + ": " + blackPlayerScore + " | " + blackPlayerStats);
        //Player names and scores styling
        whitePlayerText.setFont(Font.font("Verdana", FontWeight.NORMAL, (screenWidth / 85) - whitePlayerText.getText().length() / 50));
        blackPlayerText.setFont(Font.font("Verdana", FontWeight.NORMAL, (screenWidth / 85) - blackPlayerText.getText().length() / 50));
        whitePlayerText.setUnderline(true);
        blackPlayerText.setUnderline(true);

        statusPane.getChildren().addAll(title, whitePlayerText, blackPlayerText);

        //Show the evaluation of the current board relative to the current player, can help you know how well you are doing
        if (boardStatusEnabled) {
            Color circleColor = Color.FORESTGREEN;
            if (gameStateManager.getBoardEvaluation() < 0) circleColor = Color.DARKRED;
            Circle circle = new Circle(screenWidth / 100, circleColor);
            //Add fade to circle
            FadeTransition fade = new FadeTransition(Duration.millis(1300), circle);
            fade.setFromValue(1.0);
            fade.setToValue(0.8);
            fade.setCycleCount(Timeline.INDEFINITE);
            fade.setAutoReverse(true);
            fade.play();

            Text boardStatusText = new Text("The AI thinks your chances are: ");
            boardStatusText.setFont(Font.font("Verdana", FontWeight.NORMAL, screenWidth / 95));
            boardStatusText.setFill(Color.WHITE);

            HBox boardStatusBox = new HBox();
            boardStatusBox.setAlignment(Pos.CENTER);
            boardStatusBox.getChildren().addAll(boardStatusText, circle);
            statusPane.getChildren().addAll(boardStatusBox);
        }

        //Show the previous moves made
        Text moveHistoryText = new Text("PREVIOUS MOVE: \n" + gameStateManager.getLastMoveText());
        moveHistoryText.setFont(Font.font("Verdana", FontWeight.NORMAL, screenWidth / 85));

        //Display if the current player is in check
        Text currentPlayerInCheck = new Text((gameStateManager.currentPlayerAlliance() + " in check: \n" + gameStateManager.currentPlayerInCheck()).toUpperCase());
        currentPlayerInCheck.setFont(Font.font("Verdana", FontWeight.NORMAL, screenWidth / 85));

        statusPane.getChildren().addAll(moveHistoryText, currentPlayerInCheck, createStatusPaneButtonBox());

        //Color all texts in the root node of status pane to the color white
        for (Node x : statusPane.getChildren()) {
            if (x instanceof Text) ((Text) x).setFill(Color.WHITE);
        }

        //Use setRight to update root pane when used as a redraw method
        this.gamePlayPane.setRight(statusPane);
        return statusPane;
    }

    /**
     * Creates the HBox with buttons to display in the status pane
     *
     * @return populated HBox
     */
    private HBox createStatusPaneButtonBox() {
        //Button scaling
        double buttonSize = ((screenHeight + screenWidth) / (BoardUtils.getWidth() * BoardUtils.getHeight()));

        //Hint button for player help
        ImageView image = new ImageView(resources.hint);
        image.setFitHeight(buttonSize);
        image.setPreserveRatio(true);
        Button hintButton = new Button("HINT", image);
        hintButton.setOnMouseEntered(event -> {
            Tooltip tp = new Tooltip("Let the AI suggest a move");
            Tooltip.install(hintButton, tp);
        });
        hintButton.setOnAction(event -> {
            //Empty any ongoing player move
            startTile = null;
            destinationTile = null;
            userMovedPiece = null;
            //Let AI find "best" move
            Move hintMove = gameStateManager.getHint(4, 1000);
            //Set coordinates found
            hintStartCoordinate = hintMove.getCurrentCoordinate();
            hintDestinationCoordinate = hintMove.getDestinationCoordinate();
            //Redraw to show coordinates found
            drawChessPane();
            //Reset hint variables
            hintStartCoordinate = null;
            hintDestinationCoordinate = null;
        });

        //Button for undoing a move
        image = new ImageView(resources.undo);
        image.setFitHeight(buttonSize);
        image.setPreserveRatio(true);
        Button backButton = new Button("", image);
        backButton.setOnMouseEntered(event -> {
            Tooltip tp = new Tooltip("Undo a move");
            Tooltip.install(backButton, tp);
        });
        backButton.setOnAction(event -> {
            gameStateManager.undoMove();
            drawChessPane();
        });
        if (gameStateManager.undoIsIllegal()) backButton.setDisable(true);

        //Extra button styling
        HBox buttonContainer = new HBox(backButton, hintButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets((screenHeight / 500) * 200, 0, 0, 0));
        buttonContainer.setSpacing(5);
        for (Node x : buttonContainer.getChildren()) {
            x.setStyle("-fx-focus-color: darkslategrey; -fx-faint-focus-color: transparent;");
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

    /**
     * Draws the chess board where the pieces are displayed
     */
    private void drawChessPane() {
        GridPane chessGridPane = new GridPane();
        chessGridPane.setAlignment(Pos.CENTER);
        chessGridPane.setVgap(5);
        chessGridPane.setHgap(5);
        for (int y = 0; y < BoardUtils.getHeight(); y++) {
            for (int x = 0; x < BoardUtils.getWidth(); x++) {
                int gridPaneX = x, gridPaneY = y;
                //Flip board if player plays against white ai
                if (gameStateManager.isWhiteAI() && !gameStateManager.isBlackAI()) {
                    gridPaneX = BoardUtils.getWidth() - (x + 1);
                    gridPaneY = BoardUtils.getHeight() - (y + 1);
                }
                chessGridPane.add(new ChessTile(new Coordinate(x, y)), gridPaneX, gridPaneY);
            }
        }
        //Remove old grid-pane, replace for update
        if (gameBackgroundContainer.getChildren().size() > 1) {
            gameBackgroundContainer.getChildren().remove(1);
        }
        gameBackgroundContainer.getChildren().add(chessGridPane);
        this.gamePlayPane.setCenter(gameBackgroundContainer);
        //Update the other panes when redrawing chess pane
        drawStatusPane();
        drawTakenPiecesPane();
    }

    /**
     * Draws the pane which will display the pieces taken by the players
     * @return populated VBox displaying the pieces that have been taken during the current round of chess
     */
    private VBox drawTakenPiecesPane() {
        VBox basePane = new VBox();
        basePane.setStyle("-fx-border-color: black; -fx-background-color: radial-gradient(center 50% 50%, radius 120%, derive(darkslategray, -20%), black)");
        basePane.setMaxWidth(screenWidth / 16);
        basePane.setAlignment(Pos.CENTER);

        final double IMAGE_WIDTH = basePane.getMaxWidth() / 3.5;
        FlowPane whitePiecesBox = new FlowPane();
        whitePiecesBox.setPrefWrapLength(IMAGE_WIDTH * 2.1);
        whitePiecesBox.setAlignment(Pos.CENTER);
        FlowPane blackPieceBox = new FlowPane();
        blackPieceBox.setPrefWrapLength(IMAGE_WIDTH * 2.1);
        blackPieceBox.setAlignment(Pos.CENTER);

        //Sorts pieces by value
        Comparator<Piece> chessCompare = Comparator.comparingInt(o -> o.getPieceType().getPieceValue());
        List<Piece> takenPieces = gameStateManager.getTakenPieces();
        takenPieces.sort(chessCompare);

        for (Piece taken : takenPieces) {
            ImageView takenImage = new ImageView(resources.getPieceImage(taken));
            takenImage.setFitWidth(IMAGE_WIDTH);
            takenImage.setPreserveRatio(true);
            if (taken.getPieceAlliance() == Alliance.WHITE) whitePiecesBox.getChildren().add(takenImage);
            else blackPieceBox.getChildren().add(takenImage);
        }

        basePane.getChildren().addAll(whitePiecesBox, blackPieceBox);
        //Use setLeft to update root pane when used as a redraw method
        this.gamePlayPane.setLeft(basePane);
        return basePane;
    }

    /**
     * Shows a pop menu where the player can choose what type of piece they want to promote to
     *
     * @return PieceType which the user selected
     */
    static PieceType showPromotionWindow() {
        Stage menuStage = new Stage();
        menuStage.initStyle(StageStyle.UNDECORATED);
        FlowPane menuRoot = new FlowPane();
        menuRoot.setAlignment(Pos.CENTER);
        menuRoot.setPadding(new Insets(0));

        //Give buttons a size in relation to screen dimensions
        double buttonSize = ((screenHeight + screenWidth) * 4 / (BoardUtils.getWidth() * BoardUtils.getHeight()));

        //Fetch images for promotion and scale them to fit within buttons
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
        menuRoot.getChildren().addAll(queen, knight, bishop, rook);

        //Style buttons
        menuRoot.setPrefWrapLength(buttonSize);
        for (Button button : Arrays.asList(queen, knight, bishop, rook)) {
            button.setPrefWidth(buttonSize);
            button.setPrefHeight(buttonSize / 2);
            button.setFocusTraversable(false);
        }
        //Set values when selecting button
        final PieceType[] x = new PieceType[1];
        queen.setOnAction(event -> {
            x[0] = PieceType.QUEEN;
            menuStage.close();
        });
        knight.setOnAction(event -> {
            x[0] = PieceType.KNIGHT;
            menuStage.close();
        });
        bishop.setOnAction(event -> {
            x[0] = PieceType.BISHOP;
            menuStage.close();
        });
        rook.setOnAction(event -> {
            x[0] = PieceType.ROOK;
            menuStage.close();
        });

        //Scaling and positioning
        menuStage.setWidth(bishop.getPrefWidth() * 2 + 10);
        menuStage.setHeight(bishop.getPrefHeight() * 2 + 10);
        menuStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - menuStage.getWidth() / 2);
        menuStage.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - menuStage.getHeight() / 2);
        //Window settings
        menuStage.initModality(Modality.APPLICATION_MODAL);
        menuStage.setResizable(false);
        menuStage.setScene(new Scene(menuRoot));
        menuStage.showAndWait();

        return x[0];
    }

    /**
     * Shows the highscore scene for the application
     */
    private void showHighScoreWindow() {
        final Stage dialog = new Stage();

        VBox hsRoot = new VBox();
        hsRoot.setSpacing(5);
        hsRoot.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("HIGHSCORES");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        title.setTextAlignment(TextAlignment.CENTER);
        hsRoot.getChildren().add(title);

        HBox list = new HBox();
        list.setAlignment(Pos.TOP_CENTER);
        list.setSpacing(5);
        hsRoot.getChildren().add(list);

        VBox names = new VBox(), scores = new VBox(), record = new VBox();
        Text nameTitle = new Text("Name"), scoreTitle = new Text("Score"), recordTitle = new Text("Record");

        nameTitle.setUnderline(true);
        scoreTitle.setUnderline(true);
        recordTitle.setUnderline(true);

        names.getChildren().add(nameTitle);
        scores.getChildren().add(scoreTitle);
        record.getChildren().add(recordTitle);

        list.getChildren().addAll(names, scores, record);

        ArrayList<String> userNames = scoreSystem.getScoreboard();
        int counter = 0;
        for (String u : userNames) {
            counter++;
            Text nameText = new Text(counter + ": " + u + " ");
            Text scoreText = new Text(scoreSystem.getScore(u) + " |");
            Text recordText = new Text(" " + scoreSystem.getStats(u));
            names.getChildren().add(nameText);
            scores.getChildren().add(scoreText);
            record.getChildren().add(recordText);
        }

        Scene settingsScene = new Scene(new ScrollPane(hsRoot), 210, 330);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(settingsScene);
        dialog.initOwner(primaryStage);
        dialog.show();
    }

    /**
     * Shows the game over pane for the application, which has options for going back to
     * the start menu, exiting, or continuing with a new game with the same settings.
     */
    private void showGameOverPane() {
        FlowPane gameOverRoot = new FlowPane();
        gameOverRoot.setPadding(new Insets(3, 0, 2, 0));
        gameOverRoot.setAlignment(Pos.CENTER);

        //Text
        Text title = new Text("GAME OVER - ");
        if (gameStateManager.currentPlayerInCheckMate()) title = new Text("CHECKMATE - ");
        else if (gameStateManager.currentPlayerInStaleMate()) title = new Text("STALEMATE - ");
        else if (gameStateManager.isDraw()) title = new Text("DRAW - ");
        title.setFont(Font.font("Arial", FontWeight.BOLD, screenWidth / 85));
        Text t1 = new Text("UPDATED SCORES: ");
        t1.setFont(Font.font("Arial", FontWeight.BOLD, screenWidth / 85));
        Text t2 = new Text(whitePlayerName + ": " + whitePlayerScore + " /");
        Text t3 = new Text(blackPlayerName + ": " + blackPlayerScore + " ");
        int length = t2.getText().length() + t3.getText().length();
        t2.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, screenWidth / 85 - length / 50));
        t3.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, screenWidth / 85 - length / 50));
        gameOverRoot.getChildren().addAll(title, t1, t2, t3);

        //Buttons
        Button newGame = new Button("NEW GAME"), newRound = new Button("NEXT ROUND"), quit = new Button("QUIT");
        newGame.setOnAction(e -> {
            gameStateManager.killAI();
            //This option allows user/settings change
            showStartMenu(screenWidth, screenHeight);
            gamePlayPane.setBottom(null);
        });
        newRound.setOnAction(e -> {
            //Construct new game state manager with settings from last rounds game state manager
            gameStateManager = new GameStateManager(gameStateManager.isWhiteAI(), gameStateManager.isBlackAI(),
                    gameStateManager.getAiDepth(), gameStateManager.getBoardType());
            //Removes game over pane
            gamePlayPane.setBottom(null);
            //Redraw
            drawChessPane();
            //Makes the first move in new round if AI is white
            if (gameStateManager.isWhiteAI()) doAiMove();
            //Play game-music
            if (playSound && soundClipManager != null) soundClipManager.clear();
            soundClipManager = new SoundClipManager("GameMusic.wav", true, SOUNDTRACK_VOLUME, playSound);
        });
        quit.setOnAction(e -> System.exit(0));

        //Button container
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setSpacing(10);
        buttonContainer.getChildren().addAll(newGame, newRound, quit);

        gameOverRoot.getChildren().addAll(buttonContainer);
        gamePlayPane.setBottom(gameOverRoot);
    }

    /**
     * This class extends the StackPane class and embeds the connection between
     * the tiles on data representation of the board and the gui representation of the board.
     */
    private class ChessTile extends StackPane {
        final double TILE_SIZE = ((screenHeight * 6.6) / (BoardUtils.getWidth() * BoardUtils.getHeight()));
        private final Coordinate coordinateId;

        /**
         * Constructor for ChessTile class
         *
         * @param coordinateId   coordinate of this tile on a chess board
         */
        private ChessTile(Coordinate coordinateId) {
            this.coordinateId = coordinateId;
            Rectangle rectangle = new Rectangle(TILE_SIZE, TILE_SIZE, assignTileColor());
            rectangle.setBlendMode(BlendMode.HARD_LIGHT);
            rectangle.setArcHeight(12);
            rectangle.setArcWidth(12);
            assignTileLabel();
            this.getChildren().add(rectangle);
            ImageView imageView = assignTilePieceImage(gameStateManager.getTile(coordinateId));
            assignTileAnimation(rectangle, imageView);
            if (imageView != null) this.getChildren().add(imageView);
            this.setOnMouseClicked(e -> onClickHandler(coordinateId));
        }

        /**
         * Assign an image to the tile, given the tiles content. Does not add an image if the tile is empty.
         *
         * @param tile to draw
         */
        private ImageView assignTilePieceImage(Tile tile) {
            if (tile.isEmpty()) return null;
            ImageView icon = new ImageView(resources.getPieceImage(tile.getPiece()));
            icon.setFitHeight(TILE_SIZE - 30);
            icon.setPreserveRatio(true);
            return icon;
        }

        /**
         * Assign labels to the tile, should only be called when we are at a tile that is in the rightmost column or the lower row
         */
        private void assignTileLabel() {
            Text xLabel = new Text(""), yLabel = new Text("");

            //if human plays black against CPU we flip
            if (gameStateManager.isWhiteAI() && !gameStateManager.isBlackAI()) {
                //the rightmost column
                if (coordinateId.getX() == BoardUtils.getWidth() - 1) {
                    yLabel = new Text(String.valueOf(Math.abs(coordinateId.getY() - BoardUtils.getHeight())));
                }
                //the lower row
                if (coordinateId.getY() == 0) {
                    String label = ((char) (coordinateId.getX() + 65)) + "";
                    xLabel = new Text(label);
                }
            } else {
                //the rightmost column
                if (coordinateId.getX() == 0) {
                    yLabel = new Text(String.valueOf(Math.abs(coordinateId.getY() - BoardUtils.getHeight())));
                }
                //the lower row
                if (coordinateId.getY() == BoardUtils.getHeight() - 1) {
                    String label = ((char) (coordinateId.getX() + 65)) + "";
                    xLabel = new Text(label);
                }
            }

            yLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, TILE_SIZE / 50 * 10));
            xLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, TILE_SIZE / 50 * 10));

            yLabel.setTranslateY(-TILE_SIZE / 3 - 3);
            yLabel.setTranslateX(-TILE_SIZE / 3 - 3);
            xLabel.setTranslateY(TILE_SIZE / 3 + 3);
            xLabel.setTranslateX(TILE_SIZE / 3 + 3);

            //if the board is really small
            if (TILE_SIZE < 50) {
                yLabel.setTranslateY(-TILE_SIZE / 3 + 3);
                yLabel.setTranslateX(-TILE_SIZE / 3 + 3);
                xLabel.setTranslateY(TILE_SIZE / 3 - 3);
                xLabel.setTranslateX(TILE_SIZE / 3 - 3);
            }

            if (assignTileColor() == Color.LIGHTGRAY) {
                xLabel.setFill(Color.DARKGRAY.darker().darker());
                yLabel.setFill(Color.DARKGRAY.darker().darker());
            } else {
                xLabel.setFill(Color.LIGHTGRAY);
                yLabel.setFill(Color.LIGHTGRAY);
            }
            this.getChildren().addAll(xLabel, yLabel);
        }

        /**
         * Assign a color to the tile based on its coordinates
         */
        private Color assignTileColor() {
            Color tileColor = (coordinateId.getY() % 2 == coordinateId.getX() % 2) ? Color.LIGHTGRAY.saturate() : Color.DARKGREY.saturate();

            Move lastMove = gameStateManager.getLastMove();
            if (lastMoveHighlightEnabled && lastMove != null) {
                Coordinate from = lastMove.getCurrentCoordinate(), to = lastMove.getDestinationCoordinate();
                if (coordinateId.equals(from)) tileColor = Color.rgb(255, 255, 160);
                else if (coordinateId.equals(to)) {
                    if (lastMove.isAttack()) tileColor = Color.rgb(255, 155, 155);
                    else tileColor = Color.rgb(255, 255, 160);
                }
            }
            if (availableMoveHighlightEnabled && startTile != null) {
                if (coordinateId.equals(startTile.getTileCoord())) tileColor = Color.LIGHTGREEN;
                else if (gameStateManager.getLegalMovesFromTile(startTile).contains(coordinateId)) {
                    tileColor = Color.LIGHTBLUE;
                    Piece pieceAtCoordinate = gameStateManager.getTile(coordinateId).getPiece();
                    if (pieceAtCoordinate != null && pieceAtCoordinate.getPieceAlliance() != gameStateManager.currentPlayerAlliance()) {
                        tileColor = Color.rgb(225, 215, 240);
                    }
                }
            } else if (hintStartCoordinate != null && hintDestinationCoordinate != null) {
                if (coordinateId.equals(hintStartCoordinate)) tileColor = Color.LIGHTGREEN;
                else if (coordinateId.equals(hintDestinationCoordinate)) tileColor = Color.GREENYELLOW;
            }

            return tileColor;
        }

        /**
         * Add an animation to the tile based on its colors and or position
         *
         * @param rectangle to add animation to
         * @param image to animate when moving
         */
        private void assignTileAnimation(Rectangle rectangle, ImageView image) {
            if (rectangle.getFill().equals(Color.LIGHTBLUE) || rectangle.getFill().equals(Color.rgb(225, 215, 240)) ||
                    rectangle.getFill().equals(Color.GREENYELLOW)) {
                FadeTransition fade = new FadeTransition(Duration.millis(1300), rectangle);
                fade.setFromValue(1.0);
                fade.setToValue(0.6);
                fade.setCycleCount(Timeline.INDEFINITE);
                fade.setAutoReverse(true);
                fade.play();
                if (this.coordinateId.equals(hintDestinationCoordinate)) {
                    RotateTransition rotate = new RotateTransition(Duration.millis(2300), rectangle);
                    rotate.setByAngle(180);
                    rotate.setCycleCount(Timeline.INDEFINITE);
                    rotate.setAutoReverse(true);
                    rotate.play();
                }
            }
            if (gameStateManager.getLastMove() != null && playMoveAnimation) {
                Move lastMove = gameStateManager.getLastMove();
                if (this.coordinateId.equals(lastMove.getDestinationCoordinate())) {
                    ScaleTransition scaleUp = new ScaleTransition(Duration.millis(300), image);
                    scaleUp.setToY(1.4f);
                    scaleUp.setToX(1.4f);
                    scaleUp.play();
                    scaleUp.setOnFinished(event -> {
                        ScaleTransition scaleBack = new ScaleTransition(Duration.millis(300), image);
                        scaleBack.setToY(1f);
                        scaleBack.setToX(1f);
                        scaleBack.setOnFinished(event1 -> playMoveAnimation = false);
                        scaleBack.play();
                    });

                    RotateTransition rotate = new RotateTransition(Duration.millis(600), image);
                    rotate.setAxis(Rotate.Y_AXIS);
                    rotate.setFromAngle(0);
                    rotate.setToAngle(360);
                    rotate.setInterpolator(Interpolator.LINEAR);
                    rotate.setOnFinished(event -> ChessGUI.playMoveAnimation = false);
                    rotate.play();

                    if (lastMove.isAttack()) {
                        Bloom bloom = new Bloom();
                        image.setEffect(bloom);
                    }
                }
            }
        }

        /**
         * Handles user input for a tile
         *
         * @param inputCoordinate Coordinate on the tile that the user triggered
         */
        private void onClickHandler(Coordinate inputCoordinate) {
            //Stop player from making moves when it is the AI's turn
            if (gameStateManager.currentPlayerAlliance() == Alliance.WHITE && gameStateManager.isWhiteAI() ||
                    gameStateManager.currentPlayerAlliance() == Alliance.BLACK && gameStateManager.isBlackAI())
                return;

            if (startTile == null) {
                //User select
                startTile = gameStateManager.getTile(coordinateId);
                if (startTile.getPiece() != null) {
                    if (startTile.getPiece().getPieceAlliance() == gameStateManager.currentPlayerAlliance()) {
                        userMovedPiece = startTile.getPiece();
                        drawChessPane();
                    } else {
                        startTile = null;
                    }
                } else {
                    startTile = null;
                }
            } else if (startTile.equals(gameStateManager.getTile(inputCoordinate))) {
                //User deselect
                startTile = null;
                drawChessPane();
            } else {
                //User select 'destination'
                destinationTile = gameStateManager.getTile(inputCoordinate);

                //User selected own piece as destination; let user switch between own pieces on the fly
                if (destinationTile.getPiece() != null && userMovedPiece != null) {
                    if (destinationTile.getPiece().getPieceAlliance() == userMovedPiece.getPieceAlliance()) {
                        startTile = destinationTile;
                        destinationTile = null;
                        drawChessPane();
                    }
                }

                //Attempt move
                if (destinationTile != null && !gameStateManager.isGameOver()) doHumanMove();
            }
        }
    }

    /**
     * Attempts to make a move based on the user input
     */
    private void doHumanMove() {
        boolean moved = gameStateManager.makeMove(startTile.getTileCoord(), destinationTile.getTileCoord());
        playMoveAnimation = true;
        //Reset user move related variables that were used for making this move
        this.startTile = null;
        this.destinationTile = null;
        this.userMovedPiece = null;
        //Redraw
        Platform.runLater(this::drawChessPane);
        //Play sound for moving piece
        if (moved) playSound("DropPieceNew.wav", 1);
        if (gameStateManager.isGameOver()) gameOverCalculations();
        else doAiMove();
    }

    /**
     * Lets the AI make a move on the board
     * Calls itself if AI vs AI is enabled
     */
    private void doAiMove() {
        new Thread(new Task() {
            @Override
            protected Object call() {
                boolean moved = gameStateManager.makeAIMove();
                playMoveAnimation = true;
                //Redraw
                Platform.runLater(ChessGUI.this::drawChessPane);
                //Play sound for moving piece
                if (moved) playSound("DropPieceNew.wav", 1);

                if (gameStateManager.isGameOver()) {
                    gameOverCalculations();
                } else if (gameStateManager.currentPlayerAlliance() == Alliance.WHITE && gameStateManager.isWhiteAI() ||
                        gameStateManager.currentPlayerAlliance() == Alliance.BLACK && gameStateManager.isBlackAI()) {
                    doAiMove();
                }
                return null;
            }
        }).start();
    }

    /**
     * When the game is over the scores are calculated and updated here
     */
    private void gameOverCalculations() {
        new Thread(new Task() {
            @Override
            protected Object call() {
                //Play game over sound
                if (playSound) soundClipManager.clear();
                playSound("GameOverSound.wav", SOUNDTRACK_VOLUME);

                int[] scores;
                if (gameStateManager.currentPlayerInStaleMate() || gameStateManager.isDraw()) {
                    scores = scoreSystem.matchRating(whitePlayerName, blackPlayerName, 0.5, 0.5);
                    if (gameStateManager.isWhiteAI() && gameStateManager.isBlackAI()) {
                        scoreSystem.addDraw(whitePlayerName);
                    } else {
                        scoreSystem.addDraw(whitePlayerName);
                        scoreSystem.addDraw(blackPlayerName);
                    }
                } else if (gameStateManager.currentPlayerAlliance() == Alliance.BLACK) {
                    scores = scoreSystem.matchRating(whitePlayerName, blackPlayerName, 1, 0);
                    scoreSystem.addWin(whitePlayerName);
                    scoreSystem.addLoss(blackPlayerName);
                } else {
                    scores = scoreSystem.matchRating(whitePlayerName, blackPlayerName, 0, 1);
                    scoreSystem.addLoss(whitePlayerName);
                    scoreSystem.addWin(blackPlayerName);
                }

                scoreSystem.updateHighscore(whitePlayerName, scores[0]);
                scoreSystem.updateHighscore(blackPlayerName, scores[1]);
                whitePlayerScore = scores[0];
                blackPlayerScore = scores[1];
                whitePlayerStats = scoreSystem.getStats(whitePlayerName);
                blackPlayerStats = scoreSystem.getStats(blackPlayerName);

                Platform.runLater(ChessGUI.this::drawChessPane);
                Platform.runLater(ChessGUI.this::showGameOverPane);
                return null;
            }
        }).start();
    }
}
