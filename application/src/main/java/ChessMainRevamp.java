import board.*;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import pieces.Alliance;
import pieces.Piece;
import player.MoveTransition;
import player.Score;
import player.basicAI.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ChessMainRevamp extends Application {
    //Main window stage for application
    private Stage mainStage;
    //Scene for main game interaction
    private Scene gameScene;
    //Different panes that make up the application
    private BorderPane gamePlayPane;
    private GridPane chessGridPane;
    private VBox statusPane;
    //Chess board data representation
    private Board chessDataBoard;
    //Screen dimensions
    private double screenWidth = Screen.getPrimary().getBounds().getWidth();
    private double screenHeight = Screen.getPrimary().getBounds().getHeight();
    //Handles user scores
    private Score scoreSystem;
    //Information toggles
    private boolean availableMoveHighlightEnabled = true;
    private boolean lastMoveHighlightEnabled = true;
    private boolean boardStatusEnabled = true;
    //Player movement
    private Tile startCoordinate;
    private Tile destinationCoordinate;
    private Piece userMovedPiece;
    //Hint coordinates
    private Coordinate hintStartCoordinate;
    private Coordinate hintDestinationCoordinate;
    //Player scores
    private String whitePlayerName;
    private String whitePlayerStats;
    private int whitePlayerScore;
    private String blackPlayerName;
    private String blackPlayerStats;
    private int blackPlayerScore;
    //Depth of AI search
    private int aiDepth;
    //Ai toggles
    private boolean isWhiteAI;
    private boolean isBlackAI;
    //Keep count of board history (board states)
    private ArrayList<Board> boardHistory = new ArrayList<>();
    private int rewindCounter = 0;
    private int equalBoardStateCounter = 0;
    //Move history, even = white moves, odd = black moves
    private ArrayList<Move> moveHistory = new ArrayList<>();
    //List of all the dead pieces
    private ArrayList<Piece> deadPieces = new ArrayList<>();
    //Toggle random board
    private boolean boardIsRandom = false;
    //Sound handler
    private SoundClipManager soundClipManager;
    private boolean playSound = true;

    @Override
    public void init() {
        scoreSystem = new Score();
        scoreSystem.readHighscore();
    }

    @Override
    public void start(Stage primaryStage) {
        this.mainStage = primaryStage;
        this.gamePlayPane = new BorderPane();
        this.chessGridPane = new GridPane();
        this.statusPane = new VBox();

        // add menu bar
        MenuBar menuBar = populateMenuBar();
        gamePlayPane.setTop(menuBar);
        // add parts to gameplay pane
        gamePlayPane.setRight(statusPane);
        gamePlayPane.setCenter(chessGridPane);
        // style chess grid pane
        chessGridPane.setAlignment(Pos.CENTER);
        chessGridPane.setStyle("-fx-background-color: radial-gradient(radius 180%, darkslategray, derive(black, -30%), derive(darkslategray, 30%));");
        chessGridPane.setVgap(5);
        chessGridPane.setHgap(5);
        // style status pane
        statusPane.setPadding(new Insets(30));
        statusPane.setAlignment(Pos.TOP_CENTER);
        statusPane.setSpacing(10);

        // construct game scene
        this.gameScene = new Scene(gamePlayPane, screenWidth = screenWidth / 1.8, screenHeight = screenHeight / 1.55);

        // listeners for window size change
        gameScene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            screenWidth = newSceneWidth.intValue();
            Platform.runLater(this::drawChessGridPane);
            Platform.runLater(this::drawTakenPiecesPane);
        });
        gameScene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
            screenHeight = newSceneHeight.intValue();
            Platform.runLater(this::drawChessGridPane);
            Platform.runLater(this::drawTakenPiecesPane);
        });
        mainStage.setOnCloseRequest(e -> System.exit(0));

        mainStage.setTitle("Connoisseur Chess");
        createStartMenuScene();

        mainStage.show();
    }

    /**
     * Play sounds like buttonClicks and PieceDrop without interrupting main music.
     * @param name
     * @param volume
     */
    private void playSound(String name, double volume) {
        SoundClipManager tempSoundClipManager = new SoundClipManager(name, false, volume,playSound);
    }

    /**
     * Populate the menu-bar with different segments and options
     * @return populated MenuBar
     */
    private MenuBar populateMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(createFileMenu(), createOptionMenu());
        return menuBar;
    }

    /**
     * Create an options menu
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
            drawChessGridPane();
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
            if(playSound) {
                soundClipManager.clear();
                playSound = false;
            } else {
                playSound = true;
                soundClipManager = new SoundClipManager("GameMusic.wav", true,0.08, playSound);
            }
        });
        toggleMute.setSelected(true);

        optionsMenu.getItems().addAll(toggleHighlight, toggleMoveHighlight, toggleBoardStatus, toggleMute);
        return optionsMenu;
    }

    /**
     * Create a file menu
     * @return return populated file menu
     */
    private Menu createFileMenu() {
        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New game");
        newGame.setOnAction(event -> {
            soundClipManager.clear();
            //Stop AI calculation from running in the background
            isWhiteAI = false;
            isBlackAI = false;
            boardHistory.clear();
            moveHistory.clear();
            rewindCounter = 0;
            createStartMenuScene();
        });

        MenuItem highScores = new MenuItem("Highscores");
        highScores.setOnAction(event -> createHighscoreScene());

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(event -> System.exit(0));

        fileMenu.getItems().addAll(newGame, highScores, exit);
        return fileMenu;
    }

    /**
     * Shows the start menu for the application
     */
    private void createStartMenuScene() {
        // Play menu music
        soundClipManager = new SoundClipManager("MenuMusic.wav", true,0.2, playSound);

        //Menu Text
        Text whiteOptionsText = new Text("WHITE PLAYER");
        whiteOptionsText.setFont(new Font(30));
        Text blackOptionsText = new Text("BLACK PLAYER");
        blackOptionsText.setFont(new Font(30));
        Text aiDifficulty = new Text("AI DIFFICULTY");
        aiDifficulty.setFont(new Font(18));

        //Settings root pane
        VBox settingsRoot = new VBox(new HBox(), new HBox(), new HBox(), aiDifficulty, new HBox());
        settingsRoot.setAlignment(Pos.CENTER);
        settingsRoot.setSpacing(5);
        //Center sub panes of root pane
        for (Node x : settingsRoot.getChildren()) {
            if (x instanceof HBox) {
                ((HBox) x).setAlignment(Pos.CENTER);
            }
        }

        //Text fields
        TextField whitePlayerNameField = new TextField("Player1");
        whitePlayerNameField.setMaxWidth(gameScene.getWidth() / 4);
        TextField blackPlayerNameField = new TextField("Player2");
        blackPlayerNameField.setMaxWidth(gameScene.getWidth() / 4);

        //Options for AI
        final ToggleGroup aiOptions = new ToggleGroup();
        //Radio buttons for options
        String[] levelPrefix = {"Easy", "Intermediate", "Expert", "Experimental"};
        List<RadioButton> aiOptionList = new ArrayList<>();
        for (int i = 0; i < levelPrefix.length; i++) {
            aiOptionList.add(new RadioButton(levelPrefix[i]));
            if (levelPrefix[i].equals("Intermediate")) aiOptionList.get(i).setSelected(true);
            aiOptionList.get(i).setUserData(i+2);
            aiOptionList.get(i).setDisable(true);
            aiOptionList.get(i).setToggleGroup(aiOptions);
        }
        for (RadioButton allAIOptions : aiOptionList) {
            allAIOptions.setOnAction(event -> playSound("ButtonClick.wav", 1));
        }

        //Buttons for white
        final ToggleGroup whiteOptions = new ToggleGroup();
        RadioButton whiteHumanOption = new RadioButton("Human");
        whiteHumanOption.setToggleGroup(whiteOptions);
        whiteHumanOption.setUserData(false);
        whiteHumanOption.setSelected(true);
        RadioButton whiteAiOption = new RadioButton("AI");
        whiteAiOption.setToggleGroup(whiteOptions);
        whiteAiOption.setUserData(true);

        //Buttons for black
        final ToggleGroup blackOptions = new ToggleGroup();
        RadioButton blackHumanOption = new RadioButton("Human");
        blackHumanOption.setToggleGroup(blackOptions);
        blackHumanOption.setUserData(false);
        blackHumanOption.setSelected(true);
        RadioButton blackAiOption = new RadioButton("AI");
        blackAiOption.setToggleGroup(blackOptions);
        blackAiOption.setUserData(true);

        //AI Button actions
        whiteHumanOption.setOnAction(e -> {
            playSound("ButtonClick.wav",1);
            whitePlayerNameField.setDisable(false);
            whitePlayerNameField.setText("Player1");
            for (RadioButton x : aiOptionList)
                if (!blackAiOption.isSelected()) x.setDisable(true);
        });
        blackHumanOption.setOnAction(e -> {
            playSound("ButtonClick.wav",1);
            blackPlayerNameField.setDisable(false);
            blackPlayerNameField.setText("Player2");
            for (RadioButton x : aiOptionList)
                if (!whiteAiOption.isSelected()) x.setDisable(true);
        });
        whiteAiOption.setOnAction(e -> {
            playSound("ButtonClick.wav",1);
            whitePlayerNameField.setDisable(true);
            whitePlayerNameField.setText("CPU");
            for (RadioButton x : aiOptionList) x.setDisable(false);
        });
        blackAiOption.setOnAction(e -> {
            playSound("ButtonClick.wav",1);
            blackPlayerNameField.setDisable(true);
            blackPlayerNameField.setText("CPU");
            for (RadioButton x : aiOptionList) x.setDisable(false);
        });

        //Options for the starting board state
        final ToggleGroup boardStateOptions = new ToggleGroup();
        RadioButton boardStateOption1 = new RadioButton("Standard board");
        boardStateOption1.setToggleGroup(boardStateOptions);
        boardStateOption1.setUserData(true);
        boardStateOption1.setSelected(true);
        boardStateOption1.setOnAction(e -> playSound("ButtonClick.wav", 1));
        RadioButton boardStateOption2 = new RadioButton("Random board");
        boardStateOption2.setToggleGroup(boardStateOptions);
        boardStateOption2.setUserData(false);
        boardStateOption2.setSelected(false);
        boardStateOption2.setOnAction(e -> playSound("ButtonClick.wav", 1));

        //White option pane
        if (settingsRoot.getChildren().get(0) instanceof HBox)
            ((HBox) settingsRoot.getChildren().get(0)).getChildren().addAll(whiteOptionsText, whitePlayerNameField, whiteHumanOption, whiteAiOption);
        //Black option pane
        if (settingsRoot.getChildren().get(1) instanceof HBox)
            ((HBox) settingsRoot.getChildren().get(1)).getChildren().addAll(blackOptionsText, blackPlayerNameField, blackHumanOption, blackAiOption);
        //Board option pane
        if (settingsRoot.getChildren().get(2) instanceof HBox)
            ((HBox) settingsRoot.getChildren().get(2)).getChildren().addAll(boardStateOption1, boardStateOption2);
        //Ai option pane
        if (settingsRoot.getChildren().get(4) instanceof HBox)
            ((HBox) settingsRoot.getChildren().get(4)).getChildren().addAll(aiOptionList);
        //Sub pane styling
        for (Node x : settingsRoot.getChildren()) {
            if (x instanceof HBox) {
                ((HBox) x).setPadding(new Insets(0, 0, 10, 0));
                ((HBox) x).setSpacing(5);
            }
        }

        //Confirm settings button
        Button confirmSettings = new Button("Confirm");
        confirmSettings.setMaxWidth(100);
        //Confirm button action
        confirmSettings.setOnAction(e -> {
            isWhiteAI = (boolean) whiteOptions.getSelectedToggle().getUserData();
            isBlackAI = (boolean) blackOptions.getSelectedToggle().getUserData();
            aiDepth = (int) aiOptions.getSelectedToggle().getUserData();

            String suffix; int rating;
            switch(aiDepth){
                case 2: { suffix = "Easy"; rating = 1200; break; }
                case 3: { suffix = "Intermediate"; rating = 1500; break; }
                case 4: { suffix = "Expert"; rating = 1800; break; }
                case 5: { suffix = "Experimental"; rating = 2000; break; }
                default: { suffix = "Error"; rating = 9999; break; }
            }

            if (isWhiteAI){
                whitePlayerName = "CPU(" + suffix +")";
                scoreSystem.addUsername(whitePlayerName);
                scoreSystem.updateHighscore(whitePlayerName, rating);
            } else {
                whitePlayerName = whitePlayerNameField.getText().replaceAll("\\s","");
                scoreSystem.addUsername(whitePlayerName);
            }
            if (isBlackAI) {
                blackPlayerName = "CPU(" + suffix +")";
                scoreSystem.addUsername(blackPlayerName);
                scoreSystem.updateHighscore(blackPlayerName, rating);
            } else {
                blackPlayerName = blackPlayerNameField.getText().trim();
                scoreSystem.addUsername(blackPlayerName);
            }

            whitePlayerScore = scoreSystem.getScore(whitePlayerName);
            blackPlayerScore = scoreSystem.getScore(blackPlayerName);
            whitePlayerStats = scoreSystem.getStats(whitePlayerName);
            blackPlayerStats = scoreSystem.getStats(blackPlayerName);

            //Removes game over pane if present
            gamePlayPane.setBottom(null);

            //Reset board and redraw
            if ((boolean) boardStateOptions.getSelectedToggle().getUserData()){
                boardIsRandom = false;
                chessDataBoard = Board.createStandardBoard();
            } else {
                boardIsRandom = true;
                chessDataBoard = Board.createRandomBoard();
            }

            boardHistory.clear();
            moveHistory.clear();
            equalBoardStateCounter = 0;
            deadPieces.clear();
            equalBoardStateCounter = 0;
            drawChessGridPane();
            mainStage.setScene(gameScene);

            boardHistory.add(chessDataBoard);
            // Set GameMusic
            if (playSound) {
                soundClipManager.clear();
                soundClipManager = new SoundClipManager("GameMusic.wav",true,0.05, playSound);
            }

            //Set off ai vs ai match
            if (isWhiteAI) {
                new Thread(new Task() {
                    @Override
                    protected Object call() {
                        makeAIMove();
                        return null;
                    }
                }).start();
            }
        });

        settingsRoot.getChildren().addAll(confirmSettings);
        Scene settingsScene = new Scene(settingsRoot, gameScene.getWidth(), gameScene.getHeight());
        mainStage.setScene(settingsScene);
    }

    /**
     * Shows the highscore scene for the application
     */
    private void createHighscoreScene() {
        final Stage dialog = new Stage();

        /* Uncomment when needed for sql work
        SQL conn = new SQL();
        try{
            conn.getAllScores();
        } catch (SQLException e ) {
            System.out.println(e);
        }
        */

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

        VBox names = new VBox();
        VBox scores = new VBox();
        VBox record = new VBox();

        Text nameTitle = new Text("Name");
        Text scoreTitle = new Text("Score");
        Text recordTitle = new Text("Record");

        nameTitle.setUnderline(true);
        scoreTitle.setUnderline(true);
        recordTitle.setUnderline(true);

        names.getChildren().add(nameTitle);
        scores.getChildren().add(scoreTitle);
        record.getChildren().add(recordTitle);

        list.getChildren().addAll(names, scores, record);

        ArrayList<String> userNames = scoreSystem.getScoreboard();
        int counter = 0;
        for (String u : userNames){
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
        dialog.initOwner(mainStage);
        dialog.show();
    }

    /**
     * Shows the game over pane for the application
     */
    private void createGameOverPane() {
        //Play game over sound
        soundClipManager.clear();
        playSound("GameOverSound.wav",0.4);

        //Text box - HBox
        HBox gameOverRoot = new HBox();
        gameOverRoot.setPadding(new Insets(3,0,2,0));
        gameOverRoot.setSpacing(5);
        gameOverRoot.setAlignment(Pos.CENTER);

        //Text
        Text title = new Text("GAME OVER - ");
        if (chessDataBoard.currentPlayer().isInCheckmate()) title = new Text("CHECKMATE - ");
        else if (chessDataBoard.currentPlayer().isInStalemate()) title = new Text("STALEMATE - ");
        else if (checkForDrawByRepetition()) title = new Text("DRAW - ");
        title.setFont(Font.font("Arial", FontWeight.BOLD, screenWidth/650 * 13));

        Text t1 = new Text("UPDATED SCORES: ");
        t1.setFont(Font.font("Arial", FontWeight.BOLD, screenWidth/650 * 13));
        Text t2 = new Text(whitePlayerName + ": " + whitePlayerScore + " /");
        Text t3 = new Text(blackPlayerName + ": " + blackPlayerScore + " ");
        int length = t2.getText().length() + t3.getText().length();
        t2.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, screenWidth/650 * 15 - length/5));
        t3.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, screenWidth/650 * 15 - length/5));
        gameOverRoot.getChildren().addAll(title, t1, t2, t3);

        //Button container
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setSpacing(10);

        //Buttons
        Button newGame = new Button("NEW GAME");
        Button newRound = new Button("NEXT ROUND");
        Button quit = new Button("QUIT");

        buttonContainer.getChildren().addAll(newGame, newRound, quit);
        gameOverRoot.getChildren().addAll(buttonContainer);

        newGame.setOnAction(e -> {
            soundClipManager.clear();
            //This option allows user/settings change
            createStartMenuScene();
        });
        newRound.setOnAction(e -> {
            //This lets the user continue with another round
            if (boardIsRandom) chessDataBoard = Board.createRandomBoard();
            else chessDataBoard = Board.createStandardBoard();

            //Clear info about previous board states
            boardHistory.clear();
            moveHistory.clear();
            deadPieces.clear();
            equalBoardStateCounter = 0;
            //Removes game over pane
            gamePlayPane.setBottom(null);
            drawChessGridPane();

            // Makes the first move in new round
            if(isWhiteAI) {
                makeAIMove();
            }

            //Play game-music
            soundClipManager.clear();
            soundClipManager = new SoundClipManager("GameMusic.wav", true,0.05, playSound);
        });
        quit.setOnAction(e -> System.exit(0));

        gamePlayPane.setBottom(gameOverRoot);
    }

    /**
     * Draw the pane that displays information about the games state
     */
    private void drawStatusPane() {
        statusPane.getChildren().clear();

        Text title = new Text("GAME STATS");
        //Title styling
        title.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, screenWidth/650 * 20));
        //Player names and scores
        Text whitePlayerText = new Text(whitePlayerName + ": " + whitePlayerScore + " | " + whitePlayerStats);
        Text blackPlayerText = new Text(blackPlayerName + ": " + blackPlayerScore + " | " + blackPlayerStats);
        //Player names and scores styling
        whitePlayerText.setFont(Font.font("Verdana", FontWeight.NORMAL, (screenWidth/650 * 14)-whitePlayerText.getText().length()/4));
        blackPlayerText.setFont(Font.font("Verdana", FontWeight.NORMAL, (screenWidth/650 * 14)-blackPlayerText.getText().length()/4));
        whitePlayerText.setUnderline(true);
        blackPlayerText.setUnderline(true);

        statusPane.getChildren().addAll(title, whitePlayerText, blackPlayerText);

        //Show the evaluation of the current board relative to the current player, can help you know how well you are doing
        if(boardStatusEnabled){
            BoardEvaluator boardEvaluator = new RegularBoardEvaluator(true);
            if (boardIsRandom) boardEvaluator = new RegularBoardEvaluator(false);
            Text boardStatusText = new Text((chessDataBoard.currentPlayer().getAlliance() +
                    " board status: \n" + boardEvaluator.evaluate(chessDataBoard, 3)).toUpperCase());
            if (chessDataBoard.currentPlayer().getAlliance() == Alliance.BLACK)
                boardStatusText = new Text((chessDataBoard.currentPlayer().getAlliance() +
                        " board status: \n" + boardEvaluator.evaluate(chessDataBoard, 3) * -1).toUpperCase());

            boardStatusText.setFont(Font.font("Verdana", FontWeight.NORMAL, screenWidth/650 * 10));
            statusPane.getChildren().add(boardStatusText);

            //show the previous moves made
            Text moveHistoryText = new Text("PREVIOUS MOVE: \n");
            if (!moveHistory.isEmpty()) {
                moveHistoryText = new Text("PREVIOUS MOVE: \n" + moveHistory.get(moveHistory.size() - 1).toString());
            }
            moveHistoryText.setFont(Font.font("Verdana", FontWeight.NORMAL, screenWidth/650 * 10));
            statusPane.getChildren().add(moveHistoryText);
        }

        //Display if the current player is in check
        Text currentPlayerInCheck = new Text((chessDataBoard.currentPlayer().getAlliance() + " in check: \n" +
                chessDataBoard.currentPlayer().isInCheck()).toUpperCase());
        currentPlayerInCheck.setFont(Font.font("Verdana", FontWeight.NORMAL, screenWidth/650 * 10));

        //Button scaling
        double buttonSize = ((screenHeight + screenWidth) * 1 / (BoardUtils.getWidth() * BoardUtils.getHeight()));
        //Hint button for player help
        ImageView image = GuiHelper.imageFinder("/images/GUI/hint.png", buttonSize, buttonSize, true);
        Button hintButton = new Button("HINT", image);
        hintButton.setFont(Font.font("Verdana", screenWidth/650 * 5));
        //Disable hint when not human players turn, or the game has ended
        if ((chessDataBoard.currentPlayer().getAlliance() == Alliance.WHITE && isWhiteAI) ||
            (chessDataBoard.currentPlayer().getAlliance() == Alliance.BLACK && isBlackAI) ||
             chessDataBoard.currentPlayer().isInCheckmate() || chessDataBoard.currentPlayer().isInStalemate()) {
            hintButton.setDisable(true);
        }
        hintButton.setOnAction(event -> {
            //Empty any ongoing player move
            startCoordinate = null;
            destinationCoordinate = null;
            userMovedPiece = null;
            //Let AI find "best" move
            MoveStrategy moveStrategy = new MiniMax(4, 1000, true, true, false);
            final Move AIMove = moveStrategy.execute(chessDataBoard);
            //Set coordinates found
            hintStartCoordinate = AIMove.getCurrentCoordinate();
            hintDestinationCoordinate = AIMove.getDestinationCoordinate();
            //Redraw to show coordinates found
            drawChessGridPane();
            //Reset hint variables
            hintStartCoordinate = null;
            hintDestinationCoordinate = null;
        });
        hintButton.setOnMouseEntered(event -> {
            Tooltip tp = new Tooltip("Let the AI suggest a move");
            Tooltip.install(hintButton, tp);
        });

        //button for undoing a move
        image = GuiHelper.imageFinder("/images/GUI/undo.png", buttonSize, buttonSize, true);
        Button backButton = new Button("", image);
        backButton.setOnMouseEntered(event -> {
            Tooltip tp = new Tooltip("Undo a move");
            Tooltip.install(backButton, tp);
        });
        if (rewindCounter >= boardHistory.size()-1 || chessDataBoard.currentPlayer().isInCheckmate() ||
            chessDataBoard.currentPlayer().isInStalemate()) {
            backButton.setDisable(true);
        }
        backButton.setOnAction(event -> {
            chessDataBoard = boardHistory.get((boardHistory.size()-1)-(++rewindCounter));
            drawChessGridPane();
        });

        //button for redoing a move
        image = GuiHelper.imageFinder("/images/GUI/redo.png", buttonSize, buttonSize, true);
        Button forwardButton = new Button("", image);
        forwardButton.setOnMouseEntered(event -> {
            Tooltip tp = new Tooltip("Redo a move");
            Tooltip.install(forwardButton, tp);
        });
        if (rewindCounter <= 0) forwardButton.setDisable(true);
        forwardButton.setOnAction(event -> {
            chessDataBoard = boardHistory.get((boardHistory.size()-1)-(--rewindCounter));
            drawChessGridPane();
        });

        //extra button styling
        HBox buttonContainer = new HBox(backButton, hintButton, forwardButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(200, 0, 0 , 0));
        buttonContainer.setSpacing(5);
        for (Node x : buttonContainer.getChildren()) {
            x.setStyle("-fx-focus-color: darkslategrey; -fx-faint-focus-color: transparent;");
        }

        statusPane.getChildren().addAll(currentPlayerInCheck, buttonContainer);
    }

    /**
     * Draws the pane which will display the pieces taken by the players
     */
    private void drawTakenPiecesPane() {
        FlowPane basePane = new FlowPane();
        basePane.setPrefWrapLength(screenWidth / 25 * 2);
        basePane.setAlignment(Pos.CENTER);
        VBox whitePiecesBox = new VBox();
        whitePiecesBox.setAlignment(Pos.TOP_CENTER);
        VBox blackPieceBox = new VBox();
        blackPieceBox.setAlignment(Pos.BOTTOM_CENTER);

        //Sorts pieces by value
        Comparator<Piece> chessCompare = Comparator.comparingInt(o -> o.getPieceType().getPieceValue());
        deadPieces.sort(chessCompare);

        for (Piece taken : deadPieces) {
            Alliance takenAlliance = taken.getPieceAlliance();
            String url = "/images/" + takenAlliance.toString().substring(0, 1) + taken.toString() + ".png";
            ImageView takenImage = new ImageView(url);
            takenImage.setFitHeight(basePane.getPrefWrapLength() / 2 - 15);
            takenImage.setFitWidth(basePane.getMaxWidth());
            takenImage.setPreserveRatio(true);
            if (taken.getPieceAlliance() == Alliance.WHITE) whitePiecesBox.getChildren().add(takenImage);
            else blackPieceBox.getChildren().add(takenImage);
        }

        basePane.setStyle("-fx-border-color: black; -fx-background-color: radial-gradient(radius 180%, black, derive(darkslategray, -30%));");
        basePane.getChildren().addAll(whitePiecesBox, blackPieceBox);
        gamePlayPane.setLeft(basePane);
    }

    /**
     * Draw the pane where the chess pieces are displayed
     */
    private void drawChessGridPane() {
        chessGridPane.getChildren().clear();
        for (int y = 0; y < BoardUtils.getHeight(); y++) {
            for (int x = 0; x < BoardUtils.getWidth(); x++) {
                int gridPaneX = x, gridPaneY = y;
                //Flip board if player plays against white ai
                if (isWhiteAI && !isBlackAI) {
                    gridPaneX = BoardUtils.getWidth() - (x + 1);
                    gridPaneY = BoardUtils.getHeight() - (y + 1);
                }
                chessGridPane.add(new ChessTile(new Coordinate(x,y)), gridPaneX, gridPaneY);
            }
        }
        drawStatusPane();
    }

    /**
     * This class extends the StackPane class and embeds the connection between
     * the tiles on data representation of the board and the gui representation of the board.
     */
    private class ChessTile extends StackPane {
        final double TILE_SIZE = ((screenHeight + screenWidth) * 2.6 / (BoardUtils.getWidth() * BoardUtils.getHeight()));
        private final Coordinate coordinateId;

        private ChessTile(Coordinate coordinateId) {
            this.coordinateId = coordinateId;

            Color colorOfTile = assignTileColor();
            boolean animateTile = false;
            if (moveHistory.size()-rewindCounter >= 1 && lastMoveHighlightEnabled) {
                //highlight the previous move
                Move m = moveHistory.get((moveHistory.size()-1)-rewindCounter);
                Coordinate to = m.getDestinationCoordinate();
                Coordinate from = m.getCurrentCoordinate();
                if (coordinateId.equals(from)) colorOfTile = Color.rgb(255, 255, 160);
                else if (coordinateId.equals(to))
                    if (m.isAttack())
                        colorOfTile = Color.rgb(255, 155, 155);
                    else
                        colorOfTile = Color.rgb(255, 255, 160);
            }
            if (availableMoveHighlightEnabled && startCoordinate != null) {
                //Highlight selected tile
                if (coordinateId.equals(startCoordinate.getTileCoord())) colorOfTile = Color.LIGHTGREEN;
                //Highlight legal moves
                if (listLegalMoves(startCoordinate).contains(coordinateId)) {
                    animateTile = true;
                    colorOfTile = Color.LIGHTBLUE;
                    //Highlight attackmoves
                    if (chessDataBoard.getTile(coordinateId).getPiece() != null) {
                        if (chessDataBoard.getTile(coordinateId).getPiece().getPieceAlliance() !=
                            chessDataBoard.currentPlayer().getAlliance()) {
                            colorOfTile = Color.rgb(225, 215, 240);
                        }
                    }
                }
            } else if (hintStartCoordinate != null && hintDestinationCoordinate != null) {
                //Highlight hint move
                if (coordinateId.equals(hintStartCoordinate)) colorOfTile = Color.LIGHTGREEN;
                else if (coordinateId.equals(hintDestinationCoordinate)) {
                    animateTile = true;
                    colorOfTile = Color.GREENYELLOW;
                }
                else if (coordinateId.equals(hintDestinationCoordinate)) colorOfTile = Color.GREENYELLOW;
            }

            Rectangle rectangle = new Rectangle(TILE_SIZE, TILE_SIZE, colorOfTile);
            rectangle.setBlendMode(BlendMode.HARD_LIGHT);
            rectangle.setArcHeight(12);
            rectangle.setArcWidth(12);

            //Add fade animation to tile
            if (animateTile) {
                FadeTransition fade = new FadeTransition(Duration.millis(1300), rectangle);
                fade.setFromValue(1.0);
                fade.setToValue(0.6);
                fade.setCycleCount(Timeline.INDEFINITE);
                fade.setAutoReverse(true);
                fade.play();
                //Add rotation animation to tile
                if (hintStartCoordinate != null && hintDestinationCoordinate != null) {
                    RotateTransition rotate = new RotateTransition(Duration.millis(2300), rectangle);
                    rotate.setByAngle(180);
                    rotate.setCycleCount(Timeline.INDEFINITE);
                    rotate.setAutoReverse(true);
                    rotate.play();
                }
            }

            this.getChildren().add(rectangle);

            assignTileLabel();

            if (!chessDataBoard.getTile(coordinateId).isEmpty())
                assignTilePieceImage(chessDataBoard.getTile(coordinateId));
            this.setOnMouseClicked(e -> onClickHandler(coordinateId));
        }

        /**
         * Makes a list of all legal moves from the given board tile
         *
         * @param tile tile on the board
         * @return a list of legal moves avaiable from a given tile
         */
        private Collection<Coordinate> listLegalMoves(Tile tile) {
            //Collection<Move> temp = board.getTile(c).getPiece().calculateLegalMoves(board);
            //Note: this is a bit heavy on the system, since we are making every move and checking
            //the status of it to remove highlighting tiles which sets the player in check
            List<Move> temp = new ArrayList<>(chessDataBoard.currentPlayer().getLegalMovesForPiece(tile.getPiece()));
            List<Coordinate> coordinatesToHighlight = new ArrayList<>();
            for (Move move : temp) {
                if (chessDataBoard.currentPlayer().makeMove(move).getMoveStatus().isDone()) {
                    coordinatesToHighlight.add(move.getDestinationCoordinate());
                }
            }
            return coordinatesToHighlight;
        }

        /**
         * Assign an image to the tile, given the tiles content
         *
         * @param tile to draw
         */
        private void assignTilePieceImage(Tile tile) {
            String url = "/images/" + tile.getPiece().getPieceAlliance().toString().substring(0, 1) + tile.getPiece().toString() + ".png";
            ImageView icon = new ImageView(url);
            icon.setFitHeight(TILE_SIZE - 30);
            icon.setFitWidth(TILE_SIZE - 30);
            icon.setPreserveRatio(true);
            this.getChildren().add(icon);
        }

        /**
         * assign labels to the tile, should only be called when we are at a tile that is in the rightmost column or the lower row
         */
        private void assignTileLabel() {

            Text xLabel = new Text("");
            Text yLabel = new Text("");

            //if human plays black against CPU we flip
            if (isWhiteAI && !isBlackAI) {
                //the rightmost column
                if (coordinateId.getX() == BoardUtils.getWidth()-1) {
                    yLabel = new Text(String.valueOf(Math.abs(coordinateId.getY()-BoardUtils.getHeight())));
                }
                //the lower row
                if (coordinateId.getY() == 0) {
                    String label = ((char)(coordinateId.getX()+65)) + "";
                    xLabel = new Text(label);
                }
            }
            else {
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

            yLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, TILE_SIZE/50 * 10));
            xLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, TILE_SIZE/50 * 10));

            yLabel.setTranslateY(-TILE_SIZE/3-3);
            yLabel.setTranslateX(-TILE_SIZE/3-3);
            xLabel.setTranslateY(TILE_SIZE/3+3);
            xLabel.setTranslateX(TILE_SIZE/3+3);

            //if the board is really small
            if (TILE_SIZE < 50) {
                yLabel.setTranslateY(-TILE_SIZE/3+3);
                yLabel.setTranslateX(-TILE_SIZE/3+3);
                xLabel.setTranslateY(TILE_SIZE/3-3);
                xLabel.setTranslateX(TILE_SIZE/3-3);
            }

            if (assignTileColor() == Color.LIGHTGRAY) {
                xLabel.setFill(Color.DARKGRAY.darker().darker());
                yLabel.setFill(Color.DARKGRAY.darker().darker());
            }
            else {
                xLabel.setFill(Color.LIGHTGRAY);
                yLabel.setFill(Color.LIGHTGRAY);
            }

            this.getChildren().add(xLabel);
            this.getChildren().add(yLabel);
        }

        /**
         * Assign a color to the tile based on its coordinates
         */
        private Color assignTileColor() {
            if ((coordinateId.getY() % 2) == (coordinateId.getX() % 2)) {
                return Color.LIGHTGRAY;
            } else {
                return Color.DARKGRAY;
            }
        }

        /**
         * Handles user input for a tile
         *
         * @param inputCoordinate Coordinate on the tile that the user triggered
         */
        private void onClickHandler(Coordinate inputCoordinate) {
            if (startCoordinate == null) {
                //User select
                startCoordinate = chessDataBoard.getTile(inputCoordinate);
                if (startCoordinate.getPiece() != null) {
                    if (startCoordinate.getPiece().getPieceAlliance() == chessDataBoard.currentPlayer().getAlliance()) {
                        userMovedPiece = startCoordinate.getPiece();
                        drawChessGridPane();
                    } else {
                        startCoordinate = null;
                    }
                } else {
                    startCoordinate = null;
                }
            } else if (startCoordinate.equals(chessDataBoard.getTile(inputCoordinate))) {
                //User deselect
                startCoordinate = null;
                drawChessGridPane();
            } else {
                //User select 'destination'
                destinationCoordinate = chessDataBoard.getTile(inputCoordinate);

                //User selected own piece as destination; let user switch between own pieces on the fly
                if (destinationCoordinate.getPiece() != null && userMovedPiece != null) {
                    if (destinationCoordinate.getPiece().getPieceAlliance() == userMovedPiece.getPieceAlliance()) {
                        startCoordinate = destinationCoordinate;
                        destinationCoordinate = null;
                        drawChessGridPane();
                    }
                }
                if (destinationCoordinate != null) attemptMove();
            }
        }

        /**
         * Attempts to make a move from the tile (startCoordinate) which is selected. If the move is illegal nothing happens.
         */
        private void attemptMove() {
            final Move move = Move.MoveFactory.createMove(chessDataBoard, startCoordinate.getTileCoord(), destinationCoordinate.getTileCoord());
            final MoveTransition newBoard = chessDataBoard.currentPlayer().makeMove(move);

            if (newBoard.getMoveStatus().isDone()) {
                playSound("DropPieceNew.wav",0.4);
                //clear out undone boards and moves
                if (rewindCounter > 0) {
                    for (int i=0; i<rewindCounter; i++) {
                        boardHistory.remove(boardHistory.size()-1);
                        if (!moveHistory.isEmpty()) moveHistory.remove(moveHistory.size()-1);
                    }
                    rewindCounter=0;
                }
                chessDataBoard = newBoard.getTransitionBoard();
                moveHistory.add(move);
                boardHistory.add(chessDataBoard);
                if (move.isAttack()) {
                    deadPieces.add(move.getAttackedPiece());
                    Platform.runLater(ChessMainRevamp.this::drawTakenPiecesPane);
                }
            }

            //Reset user move related variables
            startCoordinate = null;
            destinationCoordinate = null;
            userMovedPiece = null;
            drawChessGridPane();

            if (gameIsOver()) {
                gameOverCalculations();
            } else {
                Thread AIThread = new Thread(new Task() {
                    @Override
                    protected Object call() {
                        makeAIMove();
                        return null;
                    }
                });
                AIThread.setPriority(Thread.MAX_PRIORITY);
                AIThread.start();
            }
        }
    }

    /**
     * Looks at the board anc calculates a move for the AI based on the aiDepth
     */
    private void makeAIMove() {
        if ((chessDataBoard.currentPlayer().getAlliance() == Alliance.WHITE && isWhiteAI) ||
            (chessDataBoard.currentPlayer().getAlliance() == Alliance.BLACK && isBlackAI)) {

            MoveStrategy moveStrategy = new MiniMax(aiDepth, 1000, true, true, false);
            final Move AIMove = moveStrategy.execute(chessDataBoard);
            final MoveTransition newBoard = chessDataBoard.currentPlayer().makeMove(AIMove);

            if (newBoard.getMoveStatus().isDone()) {
                playSound("DropPieceNew.wav",1);
                //clear out undone boards and moves
                if (rewindCounter > 0) {
                    for (int i=0; i<rewindCounter; i++) {
                        boardHistory.remove(boardHistory.size()-1);
                        if (!moveHistory.isEmpty()) moveHistory.remove(moveHistory.size()-1);
                    }
                    rewindCounter=0;
                }
                chessDataBoard = newBoard.getTransitionBoard();
                moveHistory.add(AIMove);
                boardHistory.add(chessDataBoard);
                if (AIMove.isAttack()) {
                    deadPieces.add(AIMove.getAttackedPiece());
                    Platform.runLater(this::drawTakenPiecesPane);
                }
            }

            Platform.runLater(this::drawChessGridPane);
            if (gameIsOver()) {
                gameOverCalculations();
            } else if ((chessDataBoard.currentPlayer().getAlliance() == Alliance.WHITE && isWhiteAI) ||
                       (chessDataBoard.currentPlayer().getAlliance() == Alliance.BLACK && isBlackAI)){
                Thread AIThread = new Thread(new Task() {
                    @Override
                    protected Object call() {
                        makeAIMove();
                        return null;
                    }
                });
                AIThread.setPriority(Thread.MAX_PRIORITY);
                AIThread.start();
            }
        }
    }

    /**
     * Check if the game is over
     * @return true if there are no further moves for the player
     */
    private boolean gameIsOver(){
        boolean checkmate = chessDataBoard.currentPlayer().isInCheckmate();
        boolean stalemate = chessDataBoard.currentPlayer().isInStalemate();
        boolean repetition = checkForDrawByRepetition();
        return checkmate || stalemate || repetition;
    }

    /**
     * check if a single board state is repeated within the last 5 turns to check if its a draw
     * @return true if its a draw, false otherwise
     */
    private boolean checkForDrawByRepetition(){
        if (boardHistory.size() >= 5) {
            for (int i = boardHistory.size() - 1; i >= boardHistory.size() - 6; i--) {
                if (chessDataBoard.toString().equals(boardHistory.get(i).toString())) {
                    equalBoardStateCounter++;
                    break;
                }
            }
            if (equalBoardStateCounter >= 3) {
                return true;
            }
        }
        return false;
    }

    /**
     * When the game is over the scores are calculated and updated here
     */
    private void gameOverCalculations(){
        int[] scores;

        if(chessDataBoard.currentPlayer().isInStalemate() || checkForDrawByRepetition()){
            scores = scoreSystem.matchRating(whitePlayerName, blackPlayerName, 0.5, 0.5);
            if(isWhiteAI && isBlackAI){
                scoreSystem.addDraw(whitePlayerName);
            } else {
                scoreSystem.addDraw(whitePlayerName);
                scoreSystem.addDraw(blackPlayerName);
            }
        } else if(chessDataBoard.currentPlayer().getAlliance() == Alliance.BLACK){
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

        Platform.runLater(this::drawStatusPane);
        Platform.runLater(this::createGameOverPane);
    }
}
