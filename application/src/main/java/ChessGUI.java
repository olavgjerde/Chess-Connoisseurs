import board.*;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
    private static double windowWidth = Screen.getPrimary().getBounds().getWidth(), windowHeight = Screen.getPrimary().getBounds().getHeight();
    private static Stage primaryStage;
    private BorderPane gamePlayPane;
    private StackPane centerPaneContainer;
    private Scene gameScene, startScene;
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
    private static final ResourceLoader resources = new ResourceLoader();
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
        primaryStage.getIcons().add(resources.ConnoisseurChess);
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            windowWidth = newValue.doubleValue();
            if (gameStateManager != null) {
                Platform.runLater(this::drawChessPane);
                if (smallMode()) {
                    gamePlayPane.setLeft(null);
                    gamePlayPane.setRight(null);
                }
            }
        });
        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
            windowHeight = newValue.doubleValue();
            if (gameStateManager != null) Platform.runLater(this::drawChessPane);
        });

        primaryStage.setTitle("Connoisseur Chess");
        primaryStage.setOnCloseRequest(event -> Platform.exit());

        showStartMenu(windowWidth / 1.6, windowHeight / 1.4);

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
     * Check if the GUI should use a smaller layout then normal
     * @return true if GUI should use small mode
     */
    private boolean smallMode() {
        return windowWidth <= Screen.getPrimary().getBounds().getWidth() / 2;
    }

    /**
     * Shows the start menu for the application
     */
    private void showStartMenu(double sceneWidth, double sceneHeight) {
        VBox menuBox = new VBox();
        menuBox.setMaxWidth(sceneWidth / 1.6);
        menuBox.setMaxHeight(sceneHeight / 1.45);
        menuBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-background-radius: 10;");
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setSpacing(7);

        //Play menu music
        if (playSound && soundClipManager != null) soundClipManager.clear();
        soundClipManager = new SoundClipManager("MenuMusic.mp3", true, SOUNDTRACK_VOLUME, playSound);

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
        whiteOptionText.setFont(new Font("Arial", sceneWidth / 60));
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
        blackOptionText.setFont(new Font("Arial", sceneWidth / 60));
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
        RadioButton standardOption = new RadioButton("Standard");
        standardOption.setToggleGroup(boardStateOptions);
        standardOption.setUserData(GameMode.NORMAL);
        standardOption.setSelected(true);
        RadioButton randomOption = new RadioButton("Random");
        randomOption.setToggleGroup(boardStateOptions);
        randomOption.setUserData(GameMode.RANDOM);
        randomOption.setSelected(false);
        RadioButton hordeOption = new RadioButton("Horde");
        hordeOption.setToggleGroup(boardStateOptions);
        hordeOption.setUserData(GameMode.HORDE);
        hordeOption.setSelected(false);
        RadioButton lightBrigadeOption = new RadioButton("Light Brigade");
        lightBrigadeOption.setToggleGroup(boardStateOptions);
        lightBrigadeOption.setUserData(GameMode.LIGHTBRIGADE);
        lightBrigadeOption.setSelected(false);
        RadioButton tutorOption = new RadioButton("Tutor Mode");
        tutorOption.setToggleGroup(boardStateOptions);
        tutorOption.setUserData(GameMode.TUTOR);
        tutorOption.setSelected(false);
        // Add to boardStateBox, then to the root pane for the scene
        boardStateBox.getChildren().addAll(standardOption, randomOption, hordeOption, lightBrigadeOption, tutorOption);
        Text gameMode = new Text("GAME MODE");
        gameMode.setFont(new Font(18));
        menuBox.getChildren().addAll(gameMode, boardStateBox);

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
            for (RadioButton x : difficultyButtons) if (!blackAiOption.isSelected()) x.setDisable(true);
        });
        blackHumanOption.setOnAction(e -> {
            playSound("ButtonClick.wav", 1);
            blackNameField.setDisable(false);
            blackNameField.setText("Player2");
            for (RadioButton x : difficultyButtons) if (!whiteAiOption.isSelected()) x.setDisable(true);
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

        //Set default settings for tutor mode
        tutorOption.setOnAction(event -> {
            whiteHumanOption.setSelected(true);
            blackHumanOption.setSelected(false);
            blackHumanOption.setDisable(true);
            whiteAiOption.setSelected(false);
            whiteAiOption.setDisable(true);
            blackAiOption.setSelected(true);
            difficultyButtons.get(1).setSelected(true);
            for (RadioButton b : difficultyButtons) b.setDisable(true);
        });
        //Re-enable human/ai options when changing to modes that are not tutor mode
        for (Toggle t : boardStateOptions.getToggles()) {
            if (t instanceof RadioButton) {
                if (!((RadioButton) t).getText().equals("Tutor Mode")) {
                    ((RadioButton) t).setOnAction(event -> {
                        whiteAiOption.setDisable(false);
                        blackAiOption.setDisable(false);
                        whiteHumanOption.setDisable(false);
                        blackHumanOption.setDisable(false);
                        for (RadioButton b : difficultyButtons)
                            if (whiteAiOption.isSelected() || blackAiOption.isSelected())
                                b.setDisable(false);
                    });
                }
            }
        }

        menuBox.getChildren().add(createStartMenuConfirmButton(whiteOptions, blackOptions, aiOptions, boardStateOptions, whiteNameField, blackNameField));
        StackPane root = new StackPane(createStartMenuBackground(), menuBox);
        this.startScene = new Scene(root, sceneWidth, sceneHeight);
        primaryStage.setScene(startScene);
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
     *
     * @param circleContainer pane which should contain the circles
     */
    private void spawnBackgroundCircle(Pane circleContainer, double startOpacity) {
        Color[] colors = {
                new Color(0.1, 0.6, 0.5, startOpacity).saturate().brighter().brighter(),
                new Color(0.2, 0.3, 0.3, startOpacity).saturate().brighter().brighter(),
                new Color(0.4, 0.4, 0.4, startOpacity).saturate().brighter().brighter(),
                new Color(0.2, 0.4, 0.4, startOpacity).saturate().brighter().brighter(),
                new Color(0.1, 0.6, 0.3, startOpacity).saturate().brighter().brighter()};
        Circle circle = new Circle(0);
        circle.setManaged(true);
        //Pick randomly from color array
        circle.setFill(colors[ThreadLocalRandom.current().nextInt(colors.length)]);
        //Take a random position within window size
        circle.setCenterX(ThreadLocalRandom.current().nextDouble(windowWidth));
        circle.setCenterY(ThreadLocalRandom.current().nextDouble(windowHeight));
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
                        new KeyValue(circle.centerXProperty(), ThreadLocalRandom.current().nextDouble() * windowWidth),
                        new KeyValue(circle.centerYProperty(), ThreadLocalRandom.current().nextDouble() * windowHeight),
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

            GameMode boardType = (GameMode) boardStateOptions.getSelectedToggle().getUserData();

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
        this.gamePlayPane.setTop(populateMenuBar());

        //Add background pane for chess board and draw the chessboard itself
        this.centerPaneContainer = new StackPane();
        this.centerPaneContainer.setStyle("-fx-background-color: radial-gradient(center 50% 50% , radius 80% , darkslategray, black);");
        Pane circleContainer = new Pane();
        for (int i = 0; i < 100; i++) {
            spawnBackgroundCircle(circleContainer, 0.5);
        }
        this.centerPaneContainer.getChildren().addAll(circleContainer);
        this.gamePlayPane.setCenter(centerPaneContainer);
        drawChessPane();

        //Play game music
        if (this.playSound) {
            soundClipManager.clear();
            soundClipManager = new SoundClipManager("GameMusic.mp3", true, SOUNDTRACK_VOLUME, playSound);
        }

        //Create this scene with dimensions of start menu scene
        this.gameScene = new Scene(gamePlayPane, startScene.getWidth(), startScene.getHeight());
        primaryStage.setScene(gameScene);
        //Set off white AI (in case of human vs white ai / ai vs ai)
        if (gameStateManager.currentPlayerAlliance() == Alliance.WHITE && gameStateManager.isWhiteAI()) doAiMove();
        else if (gameStateManager.currentPlayerAlliance() == Alliance.BLACK && gameStateManager.isBlackAI()) doAiMove();
        //Show hint on startup if in tutor mode
        else if (gameStateManager.isTutorMode()) showMoveHint();
    }

    /**
     * Populate the menu-bar with different segments and options
     *
     * @return populated MenuBar
     */
    private MenuBar populateMenuBar() {
        GameMode typeOfGame = gameStateManager.getBoardType();
        MenuBar menuBar = new MenuBar();
        if (typeOfGame.equals(GameMode.NORMAL) || typeOfGame.equals(GameMode.RANDOM)) {
            menuBar.getMenus().addAll(createFileMenu(), createOptionMenu());
        } else {
            menuBar.getMenus().addAll(createFileMenu(), createOptionMenu(), createHelpMenu());
        }
        return menuBar;
    }

    /**
     * Create an help menu
     *
     * @return return populated help menu
     */
    private Menu createHelpMenu() {
            Menu optionsMenu = new Menu("Help");
            MenuItem toggleRules = new MenuItem("Rules");
            toggleRules.setOnAction(event -> showRuleWindow());
            optionsMenu.getItems().addAll(toggleRules);
            return optionsMenu;

    }

    /**
     * Shows the rule window for the application
     */
    private void showRuleWindow() {
        Stage ruleStage = new Stage();
        ruleStage.initStyle(StageStyle.UNDECORATED);

        VBox rootBox = new VBox();
        rootBox.setSpacing(5);
        rootBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: radial-gradient(center 50% 50%, radius 180%, derive(darkslategray, 20%), black)");
        rootBox.setMaxHeight(windowHeight / 2);
        rootBox.setMaxWidth(windowWidth / 3);
        rootBox.setAlignment(Pos.CENTER);
        rootBox.setOnMouseClicked(event -> ruleStage.close());

        Text title = new Text("RULES");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        title.setFill(Color.WHITE);
        title.setTextAlignment(TextAlignment.CENTER);
        rootBox.getChildren().add(title);

        HBox ruleBox = new HBox();
        ruleBox.setAlignment(Pos.CENTER);
        ruleBox.setSpacing(5);
        rootBox.getChildren().add(ruleBox);

        VBox rulesVBox = new VBox(), rules = new VBox();
        ruleBox.getChildren().addAll(rulesVBox, rules);

        String text = resources.lightBrigade;
        if (gameStateManager.getBoardType().equals(GameMode.HORDE)) text = resources.horde;
        else if (gameStateManager.getBoardType().equals(GameMode.TUTOR)) text = resources.tutor;
        Text ruleText = new Text(text);
        ruleText.setTextAlignment(TextAlignment.CENTER);
        rulesVBox.getChildren().add(ruleText);

        //Change font color of text
        for (Node n : rulesVBox.getChildren()) {
            if (n instanceof Text) {
                ((Text) n).setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
                ((Text) n).setFill(Color.WHITE);
            }
        }

        ruleStage.setWidth(rootBox.getMaxWidth());
        ruleStage.setHeight(rootBox.getMaxHeight());
        ruleStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - ruleStage.getWidth() / 2);
        ruleStage.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - ruleStage.getHeight() / 2);
        //Window settings
        ruleStage.initModality(Modality.APPLICATION_MODAL);
        ruleStage.setResizable(false);
        ScrollPane scrollPane = new ScrollPane(rootBox);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        ruleStage.setScene(new Scene(scrollPane));
        ruleStage.show();
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
                soundClipManager = new SoundClipManager("GameMusic.mp3", true, 0.2, true);
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
            //Show new scene with dimensions of old scene
            showStartMenu(gameScene.getWidth(), gameScene.getHeight());
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
    private void drawStatusPane() {
        VBox statusPaneRoot = new VBox();
        statusPaneRoot.setStyle("-fx-border-color: black; -fx-background-color: radial-gradient(center 50% 50%, radius 140%, derive(darkslategray, -20%), black)");
        statusPaneRoot.setPadding(new Insets(30, 30, 0, 30));
        statusPaneRoot.setMaxWidth(windowWidth / 8);
        statusPaneRoot.setAlignment(Pos.TOP_CENTER);
        statusPaneRoot.setSpacing(20);
        // Title for "Game Stats"
        ImageView gameStatImage = new ImageView(resources.gameStats);
        gameStatImage.setPreserveRatio(true);
        gameStatImage.setFitWidth(statusPaneRoot.getMaxWidth() + 100);
        // WK and BK image in front of player name and score wrapped in a HBox
        HBox whiteScore = new HBox();
        HBox blackScore = new HBox();
        ImageView blackKingImg = new ImageView(resources.BK);
        ImageView whiteKingImg = new ImageView(resources.WK);
        whiteKingImg.setPreserveRatio(true);
        blackKingImg.setPreserveRatio(true);
        whiteKingImg.setFitHeight(statusPaneRoot.getMaxWidth() / 5);
        blackKingImg.setFitHeight(statusPaneRoot.getMaxWidth() / 5);
        //Player names and scores
        String whiteDisplayName = whitePlayerName.length() >= 15 ? whitePlayerName.substring(0, 12) + "..." : whitePlayerName;
        String blackDisplayName = blackPlayerName.length() >= 15 ? blackPlayerName.substring(0, 12) + "..." : blackPlayerName;
        Text whitePlayerText = new Text(whiteDisplayName + ": " + whitePlayerScore + " | " + whitePlayerStats);
        Text blackPlayerText = new Text(blackDisplayName + ": " + blackPlayerScore + " | " + blackPlayerStats);
        //Player names and scores styling
        whitePlayerText.setFont(Font.font("Verdana", FontWeight.NORMAL, (statusPaneRoot.getMaxWidth() / 9) - whitePlayerText.getText().length() / 50.0));
        blackPlayerText.setFont(Font.font("Verdana", FontWeight.NORMAL, (statusPaneRoot.getMaxWidth() / 9) - blackPlayerText.getText().length() / 50.0));
        whiteScore.setAlignment(Pos.CENTER);
        blackScore.setAlignment(Pos.CENTER);

        whitePlayerText.setFill(Color.WHITE);
        blackPlayerText.setFill(Color.WHITE);
        whiteScore.setSpacing(5);
        blackScore.setSpacing(5);
        whiteScore.getChildren().addAll(whiteKingImg,whitePlayerText);
        blackScore.getChildren().addAll(blackKingImg,blackPlayerText);

        statusPaneRoot.getChildren().addAll(gameStatImage, whiteScore, blackScore);

        //Show the evaluation of the current board relative to the current player, can help you know how well you are doing
        if (boardStatusEnabled) {
            Color circleColor = Color.FORESTGREEN;
            if (gameStateManager.getBoardEvaluation() < 0) circleColor = Color.DARKRED;
            Circle circle = new Circle(statusPaneRoot.getMaxWidth() / 12, circleColor);
            //Add fade to circle
            FadeTransition fade = new FadeTransition(Duration.millis(1300), circle);
            fade.setFromValue(1.0);
            fade.setToValue(0.8);
            fade.setCycleCount(Timeline.INDEFINITE);
            fade.setAutoReverse(true);
            fade.play();

            Text boardStatusText = new Text("ESTIMATED ODDS ");
            boardStatusText.setFont(Font.font("Verdana", FontWeight.NORMAL, statusPaneRoot.getMaxWidth() / 10));
            boardStatusText.setFill(Color.WHITE);

            HBox boardStatusBox = new HBox();
            boardStatusBox.setAlignment(Pos.CENTER);
            boardStatusBox.getChildren().addAll(boardStatusText, circle);
            statusPaneRoot.getChildren().addAll(boardStatusBox);
        }

        //Show the previous moves made
        Text moveHistoryText = new Text("PREVIOUS MOVE: " + gameStateManager.getLastMoveText());
        moveHistoryText.setFont(Font.font("Verdana", FontWeight.NORMAL, statusPaneRoot.getMaxWidth() / 11));

        statusPaneRoot.getChildren().addAll(moveHistoryText, createStatusPaneButtonBox(statusPaneRoot.getMaxWidth() / 4));

        //Color all texts in the root node of status pane to the color white
        for (Node x : statusPaneRoot.getChildren()) {
            if (x instanceof Text) ((Text) x).setFill(Color.WHITE);
        }

        //Use setRight to update root pane when used as a redraw method
        this.gamePlayPane.setRight(statusPaneRoot);
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
        hintButton.setOnAction(event -> showMoveHint());

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
            drawChessPane();
        });
        backButton.setOnMouseExited(event -> {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(0);
            backButton.setEffect(colorAdjust);
        });
        if (gameStateManager.undoIsIllegal()) backButton.setDisable(true);

        //Extra button styling
        HBox buttonContainer = new HBox(backButton, hintButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets((windowHeight / 500) * 165, 0, 0, 0));
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

    /**
     * Draws the chess board where the pieces are displayed
     */
    private void drawChessPane() {
        GridPane chessGridPane = new GridPane();
        chessGridPane.setAlignment(Pos.CENTER);
        chessGridPane.setVgap(5);
        chessGridPane.setHgap(5);
        for (int y = 0; y < BoardUtils.getInstance().getHeight(); y++) {
            for (int x = 0; x < BoardUtils.getInstance().getWidth(); x++) {
                int gridPaneX = x, gridPaneY = y;
                //Flip board if player plays against white ai
                if (gameStateManager.isWhiteAI() && !gameStateManager.isBlackAI()) {
                    gridPaneX = BoardUtils.getInstance().getWidth() - (x + 1);
                    gridPaneY = BoardUtils.getInstance().getHeight() - (y + 1);
                }
                chessGridPane.add(new ChessTile(new Coordinate(x, y)), gridPaneX, gridPaneY);
            }
        }
        //Remove old grid-pane, replace for update
        if (this.centerPaneContainer.getChildren().size() > 1) {
            this.centerPaneContainer.getChildren().remove(1);
        }
        this.centerPaneContainer.getChildren().add(chessGridPane);
        this.gamePlayPane.setCenter(centerPaneContainer);
        //Update the other panes when redrawing chess pane
        if (!smallMode()) {
            drawStatusPane();
            drawTakenPiecesPane();
        }
        drawInfoPane();
    }

    /**
     * Draws the pane which will display the pieces taken by the players
     *
     */
    private void drawTakenPiecesPane() {
        VBox basePane = new VBox();
        basePane.setStyle("-fx-border-color: black; -fx-background-color: radial-gradient(center 50% 50%, radius 120%, derive(darkslategray, -20%), black)");
        basePane.setMaxWidth(windowWidth / 16);
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
    }

    /**
     * Draws a pane which displays which players turn it is
     *
     * @return populated FlowPane
     */
    private FlowPane drawInfoPane() {
        //Do not draw if game has ended (would overwrite game over pane)
        if (gameStateManager.isGameOver()) return null;

        FlowPane infoRoot = new FlowPane();
        infoRoot.setPadding(new Insets(3, 0, 2, 0));
        infoRoot.setAlignment(Pos.CENTER);

        if (gameStateManager.currentPlayerInCheck() || gameStateManager.currentPlayerInStaleMate()) {
            infoRoot.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 120%, derive(darkred, -20%), black)");
        } else {
            infoRoot.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 120%, green, black)");
        }

        String player = gameStateManager.currentPlayerAlliance() == Alliance.WHITE ? whitePlayerName : blackPlayerName;
        String turnText = "IT'S " + player.toUpperCase() + "'S TURN";
        Text infoText = new Text(turnText);
        infoText.setFont(Font.font("Verdana", FontWeight.BOLD, windowWidth / 85));
        infoText.setFill(Color.WHITE);

        infoRoot.getChildren().add(infoText);
        gamePlayPane.setBottom(infoRoot);
        return infoRoot;
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
        double buttonSize = ((windowHeight + windowWidth) * 4 / (BoardUtils.getInstance().getWidth() * BoardUtils.getInstance().getHeight()));

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

        //Promotion conditions for light brigade
        if (gameStateManager.getBoardType().equals(GameMode.LIGHTBRIGADE)) {
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
     * Shows the highscore window for the application
     */
    private void showHighScoreWindow() {
        Stage highScoreStage = new Stage();
        highScoreStage.initStyle(StageStyle.UNDECORATED);

        VBox rootBox = new VBox();
        rootBox.setSpacing(5);
        rootBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: radial-gradient(center 50% 50%, radius 180%, derive(darkslategray, 20%), black)");
        rootBox.setMaxHeight(windowHeight / 2);
        rootBox.setMaxWidth(windowWidth / 3);
        rootBox.setAlignment(Pos.CENTER);
        rootBox.setOnMouseClicked(event -> highScoreStage.close());

        Text title = new Text("HIGHSCORES");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        title.setFill(Color.WHITE);
        title.setTextAlignment(TextAlignment.CENTER);
        rootBox.getChildren().add(title);

        HBox listBox = new HBox();
        listBox.setAlignment(Pos.CENTER);
        listBox.setSpacing(5);
        rootBox.getChildren().add(listBox);

        VBox names = new VBox(), scores = new VBox(), record = new VBox();
        Text nameTitle = new Text("Name"), scoreTitle = new Text("Score"), recordTitle = new Text("Record");
        nameTitle.setUnderline(true);
        scoreTitle.setUnderline(true);
        recordTitle.setUnderline(true);
        names.getChildren().add(nameTitle);
        scores.getChildren().add(scoreTitle);
        record.getChildren().add(recordTitle);

        listBox.getChildren().addAll(names, scores, record);

        ArrayList<String> userNames = scoreSystem.getScoreboard();
        for (int i = 0; i < userNames.size(); i++) {
            String userName = userNames.get(i);
            Text nameText = new Text(i + 1 + ": " + userName + " ");
            Text scoreText = new Text(scoreSystem.getScore(userName) + " | ");
            Text recordText = new Text(scoreSystem.getStats(userName));
            names.getChildren().add(nameText);
            scores.getChildren().add(scoreText);
            record.getChildren().add(recordText);
        }

        //Change font color of text in score table
        for (Node n : names.getChildren()) {
            if (n instanceof Text) {
                ((Text) n).setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
                ((Text) n).setFill(Color.WHITE);
            }
        }
        for (Node n : scores.getChildren()) {
            if (n instanceof Text) {
                ((Text) n).setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
                ((Text) n).setFill(Color.WHITE);
            }
        }
        for (Node n : record.getChildren()) {
            if (n instanceof Text) {
                ((Text) n).setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
                ((Text) n).setFill(Color.WHITE);
            }
        }

        highScoreStage.setWidth(rootBox.getMaxWidth());
        highScoreStage.setHeight(rootBox.getMaxHeight());
        highScoreStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - highScoreStage.getWidth() / 2);
        highScoreStage.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - highScoreStage.getHeight() / 2);
        //Window settings
        highScoreStage.initModality(Modality.APPLICATION_MODAL);
        highScoreStage.setResizable(false);
        ScrollPane scrollPane = new ScrollPane(rootBox);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        highScoreStage.setScene(new Scene(scrollPane));
        highScoreStage.show();
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
        if (gameStateManager.getBoardType().equals(GameMode.HORDE)) title = new Text("GAME OVER - ");
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
        gameOverRoot.getChildren().addAll(title, t1, t2, t3);

        //Buttons
        Button newRound = new Button("NEXT ROUND"), quit = new Button("QUIT");
        newRound.setOnAction(e -> {
            //Construct new game state manager with settings from last rounds game state manager
            gameStateManager = new GameStateManager(gameStateManager.isWhiteAI(), gameStateManager.isBlackAI(),
                                                    gameStateManager.getAiDepth(), gameStateManager.getBoardType());
            //Removes game over pane
            gamePlayPane.setBottom(drawInfoPane());
            //Redraw
            drawChessPane();
            //Makes the first move in new round if AI is white
            if (gameStateManager.currentPlayerAlliance() == Alliance.WHITE && gameStateManager.isWhiteAI()) doAiMove();
            else if (gameStateManager.currentPlayerAlliance() == Alliance.BLACK && gameStateManager.isBlackAI()) doAiMove();
            //Play game-music
            if (playSound && soundClipManager != null) soundClipManager.clear();
            soundClipManager = new SoundClipManager("GameMusic.mp3", true, SOUNDTRACK_VOLUME, playSound);
        });
        quit.setOnAction(e -> System.exit(0));

        //Button container
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setSpacing(10);
        buttonContainer.getChildren().addAll(newRound, quit);

        gameOverRoot.getChildren().addAll(buttonContainer);
        gamePlayPane.setBottom(gameOverRoot);
    }

    /**
     * This class extends the StackPane class and embeds the connection between
     * the tiles on data representation of the board and the gui representation of the board.
     */
    private class ChessTile extends StackPane {
        final double TILE_SIZE = ((windowHeight * 6.4) / (BoardUtils.getInstance().getWidth() * BoardUtils.getInstance().getHeight()));
        private final Coordinate coordinateId;

        /**
         * Constructor for ChessTile class
         *
         * @param coordinateId coordinate of this tile on a chess board
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
                if (coordinateId.getX() == BoardUtils.getInstance().getWidth() - 1) {
                    yLabel = new Text(String.valueOf(Math.abs(coordinateId.getY() - BoardUtils.getInstance().getHeight())));
                }
                //the lower row
                if (coordinateId.getY() == 0) {
                    String label = ((char) (coordinateId.getX() + 65)) + "";
                    xLabel = new Text(label);
                }
            } else {
                //the rightmost column
                if (coordinateId.getX() == 0) {
                    yLabel = new Text(String.valueOf(Math.abs(coordinateId.getY() - BoardUtils.getInstance().getHeight())));
                }
                //the lower row
                if (coordinateId.getY() == BoardUtils.getInstance().getHeight() - 1) {
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
         * @param image     to animate when moving
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
                gameStateManager.currentPlayerAlliance() == Alliance.BLACK && gameStateManager.isBlackAI() ||
                gameStateManager.isGameOver())
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
                Platform.runLater(ChessGUI.this::drawChessPane);
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
                if (destinationTile != null) doHumanMove();
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
        Task AITask = new Task() {
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
        };
        //Show hint after ai move if tutor mode is enabled
        if (gameStateManager.isTutorMode()) AITask.setOnSucceeded(event -> showMoveHint());
        Thread AIThread = new Thread(AITask);
        AIThread.start();
    }

    /**
     * Lets the AI calculate the best move on the current board for the current player and displays it.
     */
    private void showMoveHint() {
        new Thread(new Task() {
            @Override
            protected Object call() {
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
                Platform.runLater(() -> {
                    drawChessPane();
                    hintStartCoordinate = null;
                    hintDestinationCoordinate = null;
                });
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

                if (!smallMode()) Platform.runLater(ChessGUI.this::drawStatusPane);
                Platform.runLater(ChessGUI.this::showGameOverPane);
                return null;
            }
        }).start();
    }
}
