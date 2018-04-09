import board.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
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
import pieces.Alliance;
import pieces.Piece;
import player.MoveTransition;
import player.Score;
import player.basicAI.*;

import java.util.ArrayList;
import java.util.Collection;
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
    private boolean highlightEnabled = true;
    private boolean boardStatusEnabled = true;
    private boolean helpEnabled = false;
    //Player movement
    private Tile startCoordinate;
    private Tile destinationCoordinate;
    private Piece userMovedPiece;
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
    private int equalBoardStateCounter = 0;
    //Hint coordinates
    private Coordinate hintStartCoordinate;
    private Coordinate hintDestinationCoordinate;
    private boolean boardIsRandom = false;

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
        this.chessDataBoard = Board.createStandardBoard();

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
        this.gameScene = new Scene(gamePlayPane, screenWidth = screenWidth / 2, screenHeight = screenHeight / 1.75);

        // listeners for window size change
        gameScene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            screenWidth = newSceneWidth.intValue();
            Platform.runLater(this::drawChessGridPane);
        });
        gameScene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
            screenHeight = newSceneHeight.intValue();
            Platform.runLater(this::drawChessGridPane);
        });
        mainStage.setOnCloseRequest(e -> System.exit(0));

        mainStage.setTitle("Connoisseur Chess");
        createStartMenuScene();

        mainStage.show();
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

        CheckMenuItem toggleHighlight = new CheckMenuItem("Enable highlighting");
        toggleHighlight.setOnAction(e -> highlightEnabled = !highlightEnabled);
        toggleHighlight.setSelected(true);

        CheckMenuItem toggleBoardStatus = new CheckMenuItem("Show board status");
        toggleBoardStatus.setOnAction(event -> {
            boardStatusEnabled = !boardStatusEnabled;
            drawStatusPane();
        });
        toggleBoardStatus.setSelected(true);

        optionsMenu.getItems().addAll(toggleHighlight, toggleBoardStatus);
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
        //Settings box - HBox
        VBox settingsRoot = new VBox();
        settingsRoot.setAlignment(Pos.CENTER);
        settingsRoot.setSpacing(5);

        HBox whiteOptionsPane = new HBox();
        whiteOptionsPane.setAlignment(Pos.CENTER);
        HBox blackOptionsPane = new HBox();
        blackOptionsPane.setAlignment(Pos.CENTER);
        HBox boardStateOptionsPane = new HBox();
        boardStateOptionsPane.setAlignment(Pos.CENTER);
        HBox aiDifficultyPane = new HBox();
        aiDifficultyPane.setAlignment(Pos.CENTER);

        //Text
        Text whiteOptionsText = new Text("WHITE PLAYER");
        whiteOptionsText.setFont(new Font(30));
        Text blackOptionsText = new Text("BLACK PLAYER");
        blackOptionsText.setFont(new Font(30));
        Text aiDifficulty = new Text("AI DIFFICULTY");
        aiDifficulty.setFont(new Font(18));

        //Text fields
        TextField whitePlayerNameField = new TextField("Player1");
        whitePlayerNameField.setMaxWidth(gameScene.getWidth() / 4);
        TextField blackPlayerNameField = new TextField("Player2");
        blackPlayerNameField.setMaxWidth(gameScene.getWidth() / 4);

        //Radio buttons for options

        //Options for AI
        final ToggleGroup aiOptions = new ToggleGroup();

        String[] levelPrefix = {"Easy", "Medium", "Hard", "Expert"};
        List<RadioButton> aiOptionList = new ArrayList<>();
        for (int i = 0; i < levelPrefix.length; i++) {
            aiOptionList.add(new RadioButton(levelPrefix[i]));
            if (levelPrefix[i].equals("Medium")) aiOptionList.get(i).setSelected(true);
            aiOptionList.get(i).setUserData(i+2);
            aiOptionList.get(i).setDisable(true);
            aiOptionList.get(i).setToggleGroup(aiOptions);
        }

        //Options for white
        final ToggleGroup whiteOptions = new ToggleGroup();

        RadioButton whiteOption1 = new RadioButton("HUMAN");
        whiteOption1.setToggleGroup(whiteOptions);
        whiteOption1.setUserData(false);
        whiteOption1.setSelected(true);
        whiteOption1.setOnAction(e -> {
            whitePlayerNameField.setDisable(false);
            whitePlayerNameField.setText("Player1");
            for (RadioButton x : aiOptionList) x.setDisable(true);
        });

        RadioButton whiteOption2 = new RadioButton("AI");
        whiteOption2.setToggleGroup(whiteOptions);
        whiteOption2.setUserData(true);
        whiteOption2.setOnAction(e -> {
            whitePlayerNameField.setDisable(true);
            whitePlayerNameField.setText("CPU");
            for (RadioButton x : aiOptionList) x.setDisable(false);
        });

        //Options for black
        final ToggleGroup blackOptions = new ToggleGroup();

        RadioButton blackOption1 = new RadioButton("HUMAN");
        blackOption1.setToggleGroup(blackOptions);
        blackOption1.setUserData(false);
        blackOption1.setSelected(true);
        blackOption1.setOnAction(e -> {
            blackPlayerNameField.setDisable(false);
            blackPlayerNameField.setText("Player2");
            for (RadioButton x : aiOptionList) x.setDisable(true);
        });

        RadioButton blackOption2 = new RadioButton("AI");
        blackOption2.setToggleGroup(blackOptions);
        blackOption2.setUserData(true);
        blackOption2.setOnAction(e -> {
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

        RadioButton boardStateOption2 = new RadioButton("Random board");
        boardStateOption2.setToggleGroup(boardStateOptions);
        boardStateOption2.setUserData(false);
        boardStateOption2.setSelected(false);

        //Sub panes
        whiteOptionsPane.getChildren().addAll(whiteOptionsText, whitePlayerNameField, whiteOption1, whiteOption2);
        whiteOptionsPane.setPadding(new Insets(0,0,10,0));
        whiteOptionsPane.setSpacing(5);

        blackOptionsPane.getChildren().addAll(blackOptionsText, blackPlayerNameField, blackOption1, blackOption2);
        blackOptionsPane.setPadding(new Insets(0,0,10,0));
        blackOptionsPane.setSpacing(5);

        boardStateOptionsPane.getChildren().addAll(boardStateOption1, boardStateOption2);
        boardStateOptionsPane.setPadding(new Insets(0,0,10,0));
        boardStateOptionsPane.setSpacing(5);

        aiDifficultyPane.getChildren().addAll(aiOptionList);
        aiDifficultyPane.setPadding(new Insets(0,0,10,0));
        aiDifficultyPane.setSpacing(5);

        //Confirm settings button
        Button confirmSettings = new Button();
        confirmSettings.setText("Confirm");
        confirmSettings.setMaxWidth(100);

        //Add all elements to the pane
        settingsRoot.getChildren().addAll(whiteOptionsPane, blackOptionsPane, boardStateOptionsPane);
        settingsRoot.getChildren().addAll(aiDifficulty, aiDifficultyPane, confirmSettings);

        //Confirm button action
        confirmSettings.setOnAction(e -> {
            isWhiteAI = (boolean) whiteOptions.getSelectedToggle().getUserData();
            isBlackAI = (boolean) blackOptions.getSelectedToggle().getUserData();
            aiDepth = (int) aiOptions.getSelectedToggle().getUserData();

            String suffix;
            int rating;
            switch(aiDepth){
                case 2: { suffix = "Easy"; rating = 1200; break; }
                case 3: { suffix = "Medium"; rating = 1500; break; }
                case 4: { suffix = "Hard"; rating = 1800; break; }
                case 5: { suffix = "Expert"; rating = 2000; break; }
                default: { suffix = "Error"; rating = 9999; break; }
            }

            if(isWhiteAI){
                whitePlayerName = "CPU(" + suffix +")";
                scoreSystem.addUsername(whitePlayerName);
                scoreSystem.updateHighscore(whitePlayerName, rating);
            } else{
                whitePlayerName = whitePlayerNameField.getText().replaceAll("\\s","");
                scoreSystem.addUsername(whitePlayerName);
            }
            if(isBlackAI) {
                blackPlayerName = "CPU(" + suffix +")";
                scoreSystem.addUsername(blackPlayerName);
                scoreSystem.updateHighscore(blackPlayerName, rating);
            }
            else{
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
            if((boolean) boardStateOptions.getSelectedToggle().getUserData()){
                boardIsRandom = false;
                chessDataBoard = Board.createStandardBoard();
            } else {
                boardIsRandom = true;
                chessDataBoard = Board.createRandomBoard();
            }

            boardHistory.clear();
            equalBoardStateCounter = 0;
            drawChessGridPane();
            mainStage.setScene(gameScene);

            //Set off ai vs ai match
            if (isWhiteAI || isBlackAI) Platform.runLater(this::makeAIMove);
        });

        Scene settingsScene = new Scene(settingsRoot, gameScene.getWidth(), gameScene.getHeight());
        mainStage.setScene(settingsScene);
    }

    /**
     * Shows the highscore scene for the application
     */
    private void createHighscoreScene() {
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
        //Text box - HBox
        HBox gameOverRoot = new HBox();
        gameOverRoot.setPadding(new Insets(3,0,2,0));
        gameOverRoot.setSpacing(5);
        gameOverRoot.setAlignment(Pos.CENTER);

        //Text
        Text title = new Text("GAME OVER - ");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        Text t1 = new Text("UPDATED SCORES: ");
        t1.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        Text t2 = new Text(whitePlayerName + ": " + whitePlayerScore + " /");
        Text t3 = new Text(blackPlayerName + ": " + blackPlayerScore + " ");
        t2.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 20));
        t3.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 20));
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
            //This option allows user/settings change
            createStartMenuScene();
        });
        newRound.setOnAction(e -> {
            //This lets the user continue with another round
            if(boardIsRandom){
                chessDataBoard = Board.createRandomBoard();
            } else {
                chessDataBoard = Board.createStandardBoard();
            }

            //Clear info about previous board states
            boardHistory.clear();
            equalBoardStateCounter = 0;
            //Removes game over pane
            gamePlayPane.setBottom(null);
            drawChessGridPane();
        });
        quit.setOnAction(e -> System.exit(0));
        gamePlayPane.setBottom(gameOverRoot);
    }

    private void drawStatusPane() {
        statusPane.getChildren().clear();

        Text title = new Text("GAME STATS");
        //Title styling
        title.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 30));
        //Player names and scores
        Text whitePlayerText = new Text(whitePlayerName + ": " + whitePlayerScore + " | " + whitePlayerStats);
        Text blackPlayerText = new Text(blackPlayerName + ": " + blackPlayerScore + " | " + blackPlayerStats);
        //Player names and scores styling
        whitePlayerText.setFont(Font.font("Verdana", FontWeight.NORMAL, 17));
        blackPlayerText.setFont(Font.font("Verdana", FontWeight.NORMAL, 17));
        whitePlayerText.setUnderline(true);
        blackPlayerText.setUnderline(true);

        statusPane.getChildren().addAll(title, whitePlayerText, blackPlayerText);

        //Show the evaluation of the current board relative to the current player, can help you know how well you are doing
        if(boardStatusEnabled){
            BoardEvaluator boardEvaluator = new RegularBoardEvaluator();
            Text boardStatusText = new Text((chessDataBoard.currentPlayer().getAlliance() + " board status: \n" + boardEvaluator.evaluate(chessDataBoard, 3)).toUpperCase());
            if (chessDataBoard.currentPlayer().getAlliance() == Alliance.BLACK)
                boardStatusText = new Text((chessDataBoard.currentPlayer().getAlliance() + " board status: \n" + boardEvaluator.evaluate(chessDataBoard, 3) * -1).toUpperCase());

            boardStatusText.setFont(Font.font("Verdana", FontWeight.NORMAL, 14));
            statusPane.getChildren().add(boardStatusText);
        }

        //Display if the current player is in check
        Text currentPlayerInCheck = new Text((chessDataBoard.currentPlayer().getAlliance() + " in check: \n" + chessDataBoard.currentPlayer().isInCheck()).toUpperCase());
        currentPlayerInCheck.setFont(Font.font("Verdana", FontWeight.NORMAL, 14));

        //Hint button for player help
        Button hintButton = new Button("Hint");
        hintButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!gameIsOver()) {
                    //Empty any ongoing player move
                    startCoordinate = null;
                    destinationCoordinate = null;
                    userMovedPiece = null;
                    //Let AI find "best" move
                    final MoveStrategy moveStrategy = new MiniMax(3);
                    final Move AIMove = moveStrategy.execute(chessDataBoard);
                    //Set coordinates found
                    hintStartCoordinate = AIMove.getCurrentCoordinate();
                    hintDestinationCoordinate = AIMove.getDestinationCoordinate();
                    //Redraw to show coordinates found
                    drawChessGridPane();
                    //Reset hint variables
                    hintStartCoordinate = null;
                    hintDestinationCoordinate = null;
                }
            }
        });

        statusPane.getChildren().addAll(currentPlayerInCheck, hintButton);
    }

    private void drawChessGridPane() {
        chessGridPane.getChildren().clear();
        for (int i = 0; i < BoardUtils.getHeight(); i++) {
            for (int j = 0; j < BoardUtils.getWidth(); j++) {
                chessGridPane.add(new ChessTile(new Coordinate(j,i)), j,i);
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
            if (highlightEnabled && startCoordinate != null) {
                //Highlight selected tile
                if (coordinateId.equals(startCoordinate.getTileCoord())) colorOfTile = Color.LIGHTGREEN;
                //Highlight legal moves
                if (listLegalMoves(startCoordinate).contains(coordinateId)) {
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
                else if (coordinateId.equals(hintDestinationCoordinate)) colorOfTile = Color.GREENYELLOW;
            }

            Rectangle rectangle = new Rectangle(TILE_SIZE, TILE_SIZE, colorOfTile);
            rectangle.setBlendMode(BlendMode.HARD_LIGHT);
            rectangle.setArcHeight(10);
            rectangle.setArcWidth(10);
            this.getChildren().add(rectangle);

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
            icon.setFitHeight(TILE_SIZE - 20);
            icon.setFitWidth(TILE_SIZE - 20);
            icon.setPreserveRatio(true);
            this.getChildren().add(icon);
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
                    if (chessDataBoard.currentPlayer().getAlliance() == startCoordinate.getPiece().getPieceAlliance()) {
                        userMovedPiece = startCoordinate.getPiece();
                        drawChessGridPane();
                    } else {
                        startCoordinate = null;
                    }
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
                chessDataBoard = newBoard.getTransitionBoard();
            }

            //Reset user move related variables
            startCoordinate = null;
            destinationCoordinate = null;
            userMovedPiece = null;
            drawChessGridPane();

            if (gameIsOver()) gameOverCalculations();
            else Platform.runLater(ChessMainRevamp.this::makeAIMove);
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
        if (!boardHistory.isEmpty()) {
            //No moves were made
            if (boardHistory.get(boardHistory.size()-1).toString().equals(chessDataBoard.toString())) {
                return false;
            }
        }

        for (Board b : boardHistory){
            if (chessDataBoard.toString().equals(b.toString())) {
                equalBoardStateCounter++;
                break;
            }
        }
        if (equalBoardStateCounter >= 3){
            return true;
        }

        if (boardHistory.size() < 5)
            boardHistory.add(chessDataBoard);
        else {
            for (int i=1; i<boardHistory.size(); i++){
                boardHistory.set(i-1, boardHistory.get(i));
            }
            boardHistory.add(chessDataBoard);
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

        drawStatusPane();
        createGameOverPane();
    }

    /**
     * Looks at the board anc calculates a move for the AI based on the aiDepth
     */
    private void makeAIMove() {
        if ((chessDataBoard.currentPlayer().getAlliance() == Alliance.WHITE && isWhiteAI) ||
             chessDataBoard.currentPlayer().getAlliance() == Alliance.BLACK && isBlackAI) {

            final MoveStrategy moveStrategy = new MiniMax(aiDepth);
            final Move AIMove = moveStrategy.execute(chessDataBoard);
            final MoveTransition newBoard = chessDataBoard.currentPlayer().makeMove(AIMove);

            if (newBoard.getMoveStatus().isDone()) {
                chessDataBoard = newBoard.getTransitionBoard();
            }

            drawChessGridPane();

            if (gameIsOver()) gameOverCalculations();
            else Platform.runLater(this::makeAIMove);
        }
    }

}
