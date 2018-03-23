import board.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
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
import player.basicAI.BoardEvaluator;
import player.basicAI.MiniMax;
import player.basicAI.MoveStrategy;
import player.basicAI.RegularBoardEvaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChessMainRevamp extends Application {
    // main window stage for application
    private Stage mainStage;
    // scene for main game interaction
    private Scene gameScene;
    // different panes that make up the application
    private BorderPane gameplayPane;
    private GridPane chessGridPane;
    private VBox statusPane;
    // chess board data representation
    private Board chessDataBoard;
    // screen dimensions
    private double screenWidth = Screen.getPrimary().getBounds().getWidth();
    private double screenHeight = Screen.getPrimary().getBounds().getHeight();
    // handles user scores
    private Score scoreSystem;
    // information toggles
    private boolean highlightEnabled = true;
    private boolean statusEnabled = true;
    // player movement
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece userMovedPiece;
    // player scores
    private String whitePlayerName;
    private String whitePlayerStats;
    private int whitePlayerScore;
    private String blackPlayerName;
    private String blackPlayerStats;
    private int blackPlayerScore;
    // depth of AI search
    private int aiDepth;
    // ai toggles
    private boolean isWhiteAI;
    private boolean isBlackAI;
    // keep count of board history (board states)
    private ArrayList<Board> boardHistory = new ArrayList<>();
    private int equalBoardStateCounter = 0;

    @Override
    public void init() {
        scoreSystem = new Score();
        scoreSystem.readHighscore();
    }

    @Override
    public void start(Stage primaryStage) {
        this.mainStage = primaryStage;
        this.gameplayPane = new BorderPane();
        this.chessGridPane = new GridPane();
        this.statusPane = new VBox();
        this.chessDataBoard = Board.createStandardBoard();

        // add menu bar
        MenuBar menuBar = populateMenuBar();
        gameplayPane.setTop(menuBar);
        // add parts to gameplay pane
        gameplayPane.setCenter(statusPane);
        gameplayPane.setLeft(chessGridPane);
        // style chess grid pane
        chessGridPane.setPadding(new Insets(5));
        chessGridPane.setAlignment(Pos.CENTER);
        chessGridPane.setStyle("-fx-background-color: radial-gradient(radius 180%, darkslategray, derive(black, -30%), derive(darkslategray, 30%));");
        chessGridPane.setVgap(5);
        chessGridPane.setHgap(5);
        // style status pane
        statusPane.setPadding(new Insets(30));
        statusPane.setAlignment(Pos.TOP_CENTER);
        statusPane.setSpacing(10);

        // construct game scene
        this.gameScene = new Scene(gameplayPane, screenWidth = screenWidth / 2, screenHeight = screenHeight / 1.75);

        // listeners for window size change
        gameScene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            screenWidth = newSceneWidth.intValue();
            Platform.runLater(() -> drawChessGridPane());
        });
        gameScene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
            screenHeight = newSceneHeight.intValue();
            Platform.runLater(() -> drawChessGridPane());
        });
        mainStage.setOnCloseRequest(e -> System.exit(0));

        mainStage.setTitle("Chess Application");
        createStartMenuScene();

        // draw board
        drawChessGridPane();

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
            statusEnabled = !statusEnabled;
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
            chessDataBoard = Board.createStandardBoard();
            createStartMenuScene();
            drawChessGridPane();
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
        settingsRoot.setPadding(new Insets(5,15,5,15));
        settingsRoot.setSpacing(5);

        HBox aiDifficultyPane = new HBox();
        aiDifficultyPane.setAlignment(Pos.CENTER);
        HBox whiteOptionsPane = new HBox();
        whiteOptionsPane.setAlignment(Pos.CENTER);
        HBox blackOptionsPane = new HBox();
        blackOptionsPane.setAlignment(Pos.CENTER);

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

        RadioButton aiOption1 = new RadioButton("Easy");
        aiOption1.setUserData(1);
        aiOption1.setToggleGroup(aiOptions);
        aiOption1.setDisable(true);

        RadioButton aiOption2 = new RadioButton("Medium");
        aiOption2.setUserData(2);
        aiOption2.setToggleGroup(aiOptions);
        aiOption2.setSelected(true);
        aiOption2.setDisable(true);

        RadioButton aiOption3 = new RadioButton("Hard");
        aiOption3.setUserData(3);
        aiOption3.setToggleGroup(aiOptions);
        aiOption3.setDisable(true);

        //Options for white
        final ToggleGroup whiteOptions = new ToggleGroup();

        RadioButton whiteOption1 = new RadioButton("HUMAN");
        whiteOption1.setToggleGroup(whiteOptions);
        whiteOption1.setUserData(false);
        whiteOption1.setSelected(true);
        whiteOption1.setOnAction(e -> {
            whitePlayerNameField.setDisable(false);
            whitePlayerNameField.setText("Player1");
            aiOption1.setDisable(true);
            aiOption2.setDisable(true);
            aiOption3.setDisable(true);
        });

        RadioButton whiteOption2 = new RadioButton("AI");
        whiteOption2.setToggleGroup(whiteOptions);
        whiteOption2.setUserData(true);
        whiteOption2.setOnAction(e -> {
            whitePlayerNameField.setDisable(true);
            whitePlayerNameField.setText("CPU");
            aiOption1.setDisable(false);
            aiOption2.setDisable(false);
            aiOption3.setDisable(false);
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
            aiOption1.setDisable(true);
            aiOption2.setDisable(true);
            aiOption3.setDisable(true);
        });

        RadioButton blackOption2 = new RadioButton("AI");
        blackOption2.setToggleGroup(blackOptions);
        blackOption2.setUserData(true);
        blackOption2.setOnAction(e -> {
            blackPlayerNameField.setDisable(true);
            blackPlayerNameField.setText("CPU");
            aiOption1.setDisable(false);
            aiOption2.setDisable(false);
            aiOption3.setDisable(false);
        });

        //Sub panes
        whiteOptionsPane.getChildren().addAll(whiteOptionsText, whitePlayerNameField, whiteOption1, whiteOption2);
        whiteOptionsPane.setPadding(new Insets(0,0,10,0));
        whiteOptionsPane.setSpacing(5);

        blackOptionsPane.getChildren().addAll(blackOptionsText, blackPlayerNameField, blackOption1, blackOption2);
        blackOptionsPane.setPadding(new Insets(0,0,10,0));
        blackOptionsPane.setSpacing(5);

        aiDifficultyPane.getChildren().addAll(aiOption1, aiOption2, aiOption3);
        aiDifficultyPane.setPadding(new Insets(0,0,10,0));
        aiDifficultyPane.setSpacing(5);

        //Confirm settings button
        Button confirmSettings = new Button();
        confirmSettings.setText("Confirm");
        confirmSettings.setMaxWidth(100);

        //Add all elements to the pane
        settingsRoot.getChildren().addAll(whiteOptionsPane, blackOptionsPane);
        settingsRoot.getChildren().addAll(aiDifficulty, aiDifficultyPane, confirmSettings);

        //Confirm button action
        confirmSettings.setOnAction(e -> {
            isWhiteAI = (boolean) whiteOptions.getSelectedToggle().getUserData();
            isBlackAI = (boolean) blackOptions.getSelectedToggle().getUserData();
            aiDepth = (int) aiOptions.getSelectedToggle().getUserData();

            String suffix;
            int rating;
            switch(aiDepth){
                case 1: { suffix = "Easy"; rating = 1200; break; }
                case 2: { suffix = "Medium"; rating = 1500; break; }
                case 3: { suffix = "Hard"; rating = 1800; break; }
                default: { suffix = "Error"; rating = 9999; break; }
            }

            if(isWhiteAI){
                whitePlayerName = "CPU(" + suffix +")";
                scoreSystem.addUsername(whitePlayerName);
                scoreSystem.updateHighscore(whitePlayerName, rating);
            } else{
                whitePlayerName = whitePlayerNameField.getText().trim();
                scoreSystem.addUsername(whitePlayerName);
            }
            if(isBlackAI) {
                blackPlayerName = "CPU(" + suffix +")";
                scoreSystem.addUsername(blackPlayerName);
                scoreSystem.updateHighscore(blackPlayerName, rating);
            }
            else{
                blackPlayerName = blackPlayerNameField.getText();
                scoreSystem.addUsername(blackPlayerName);
            }

            whitePlayerScore = scoreSystem.getScore(whitePlayerName);
            blackPlayerScore = scoreSystem.getScore(blackPlayerName);
            whitePlayerStats = scoreSystem.getStats(whitePlayerName);
            blackPlayerStats = scoreSystem.getStats(blackPlayerName);

            // set off ai vs ai match
            if (isWhiteAI || isBlackAI) makeAIMove();
            mainStage.setScene(gameScene);
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
     * Shows the game over scene for the application
     */
    private void createGameoverScene() {
        //Text box - HBox
        VBox gameOverRoot = new VBox();
        gameOverRoot.setPadding(new Insets(5,15,5,15));
        gameOverRoot.setSpacing(5);
        gameOverRoot.setAlignment(Pos.CENTER);

        //Text
        Text title = new Text("GAME OVER");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        Text t1 = new Text("Your updated scores are:");
        Text t2 = new Text(whitePlayerName + ": " + whitePlayerScore);
        Text t3 = new Text(blackPlayerName + ": " + blackPlayerScore);
        t1.setFont(new Font(20));
        t1.setUnderline(true);
        t2.setFont(new Font(20));
        t3.setFont(new Font(20));
        gameOverRoot.getChildren().addAll(title, t1, t2, t3);

        //Button container
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.BOTTOM_CENTER);
        buttonContainer.setSpacing(10);
        buttonContainer.setPadding(new Insets(10,0,0,0));

        //Button2
        Button newGame = new Button("NEW GAME!");
        //Button2
        Button newRound = new Button("NEXT ROUND");
        //Button3
        Button quit = new Button("QUIT :(");

        buttonContainer.getChildren().addAll(newGame, newRound, quit);
        gameOverRoot.getChildren().addAll(buttonContainer);

        newGame.setOnAction(event -> {
            // this option allows user/settings change
            createStartMenuScene();
            chessDataBoard = Board.createStandardBoard();
            boardHistory.clear();
            equalBoardStateCounter = 0;
            drawChessGridPane();
        });
        newRound.setOnAction(e -> {
            // this lets the user continue with another round
            chessDataBoard = Board.createStandardBoard();
            //Clear info about previous board states
            boardHistory.clear();
            equalBoardStateCounter = 0;

            drawChessGridPane();
            mainStage.setScene(gameScene);
        });

        quit.setOnAction(e -> System.exit(0));

        Scene gameOverScene = new Scene(gameOverRoot, gameScene.getWidth(), gameScene.getHeight());
        mainStage.setScene(gameOverScene);
    }

    private void drawStatusPane() {
        statusPane.getChildren().clear();

        Text title = new Text("GAME STATS");
        // title styling
        title.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 30));
        DropShadow ds = new DropShadow(5, Color.color(0.4f, 0.4f, 0.4f));
        ds.setOffsetY(4.0f);
        title.setEffect(ds);
        // player names and scores
        Text whitePlayerText = new Text(whitePlayerName + ": " + whitePlayerScore + " | " + whitePlayerStats);
        Text blackPlayerText = new Text(blackPlayerName + ": " + blackPlayerScore + " | " + blackPlayerStats);
        // player names and scores styling
        whitePlayerText.setFont(Font.font("Verdana", FontWeight.NORMAL, 17));
        blackPlayerText.setFont(Font.font("Verdana", FontWeight.NORMAL, 17));
        whitePlayerText.setUnderline(true);
        blackPlayerText.setUnderline(true);

        statusPane.getChildren().addAll(title, whitePlayerText, blackPlayerText);

        // show the evaluation of the current board relative to the current player, can help you know how well you are doing
        if(statusEnabled){
            BoardEvaluator boardEvaluator = new RegularBoardEvaluator();
            Text boardStatusText = new Text((chessDataBoard.currentPlayer().getAlliance() + " board status: " + boardEvaluator.evaluate(chessDataBoard, 3)).toUpperCase());
            if (chessDataBoard.currentPlayer().getAlliance() == Alliance.BLACK)
                boardStatusText = new Text((chessDataBoard.currentPlayer().getAlliance() + " board status: " + boardEvaluator.evaluate(chessDataBoard, 3) * -1).toUpperCase());

            boardStatusText.setFont(Font.font("Verdana", FontWeight.NORMAL, 14));
            statusPane.getChildren().add(boardStatusText);
        }

        // display if the current player is in check
        Text currentPlayerInCheck = new Text((chessDataBoard.currentPlayer().getAlliance() + " in check: " + chessDataBoard.currentPlayer().isInCheck()).toUpperCase());
        currentPlayerInCheck.setFont(Font.font("Verdana", FontWeight.NORMAL, 14));

        statusPane.getChildren().add(currentPlayerInCheck);
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
            if (highlightEnabled && sourceTile != null) {
                // highlight selected tile
                if (coordinateId.equals(sourceTile.getTileCoord())) colorOfTile = Color.LIGHTGREEN;
                // highlight legal moves
                if (listLegalMoves(sourceTile).contains(coordinateId)) {
                    colorOfTile = Color.LIGHTBLUE;
                    // highlight attackmoves
                    if (chessDataBoard.getTile(coordinateId).getPiece() != null) {
                        if (chessDataBoard.getTile(coordinateId).getPiece().getPieceAlliance() !=
                            chessDataBoard.currentPlayer().getAlliance()) {
                            colorOfTile = Color.rgb(225, 215, 240);
                        }
                    }
                }
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
            // Collection<Move> temp = board.getTile(c).getPiece().calculateLegalMoves(board);
            // Note: this is a bit heavy on the system, since we are making every move and checking
            // the status of it to remove highlighting tiles which sets the player in check
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
            if (sourceTile == null) {
                // user select
                sourceTile = chessDataBoard.getTile(inputCoordinate);
                if (sourceTile.getPiece() != null) {
                    if (chessDataBoard.currentPlayer().getAlliance() == sourceTile.getPiece().getPieceAlliance()) {
                        userMovedPiece = sourceTile.getPiece();
                        drawChessGridPane();
                    } else {
                        sourceTile = null;
                    }
                }
            } else if (sourceTile.equals(chessDataBoard.getTile(inputCoordinate))) {
                // user deselect
                sourceTile = null;
                drawChessGridPane();
            } else {
                // user select 'destination'
                destinationTile = chessDataBoard.getTile(inputCoordinate);

                // user selected own piece as destination; let user switch between own pieces on the fly
                if (destinationTile.getPiece() != null && userMovedPiece != null) {
                    if (destinationTile.getPiece().getPieceAlliance() == userMovedPiece.getPieceAlliance()) {
                        sourceTile = destinationTile;
                        destinationTile = null;
                        drawChessGridPane();
                    }
                }

                if (destinationTile != null) attemptMove();
            }
        }

        /**
         * Attempts to make a move from the tile (sourceTile) which is selected. If the move is illegal nothing happens.
         */
        private void attemptMove() {
            final Move move = Move.MoveFactory.createMove(chessDataBoard, sourceTile.getTileCoord(), destinationTile.getTileCoord());
            final MoveTransition newBoard = chessDataBoard.currentPlayer().makeMove(move);

            if (newBoard.getMoveStatus().isDone()) {
                chessDataBoard = newBoard.getTransitionBoard();
            }

            // reset user move related variables
            sourceTile = null;
            destinationTile = null;
            userMovedPiece = null;
            drawChessGridPane();

            Platform.runLater(() -> {
                if (gameIsOver()) gameOverCalculations();
                else makeAIMove();
            });
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
            //no moves were made
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
        createGameoverScene();
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

            Platform.runLater(() -> {
                if (gameIsOver()) gameOverCalculations();
                else makeAIMove();
            });
        }
    }

}
