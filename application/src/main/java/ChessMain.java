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

public class ChessMain extends Application {

    private GridPane grid;
    private Board board;
    private VBox status;
    private Stage mainStage;

    private Coordinate selectedTile;
    private boolean highlightEnabled;
    private boolean statusEnabled;

    private boolean isWhiteAI;
    private boolean isBlackAI;

    private double screenWidth = Screen.getPrimary().getBounds().getWidth();
    private double screenHeight = Screen.getPrimary().getBounds().getHeight();

    private Score scoreSystem;
    private String whitePlayerName = "", blackPlayerName= "";
    private int whitePlayerScore, blackPlayerScore;

    private ArrayList<Board> boardHistory = new ArrayList<>();
    private int equalBoardStateCounter = 0;

    private int aiDepth;

    //this method runs before the start method
    @Override
    public void init() {
        selectedTile = null;
        highlightEnabled = true;

        //default difficulty
        aiDepth = 2;

        //read the highscores from the file
        scoreSystem = new Score();
        scoreSystem.readHighscore();
    }

    @Override
    public void start(Stage mainStage) {

        this.mainStage = mainStage;

        BorderPane root = new BorderPane();
        grid = new GridPane();
        status = new VBox();

        //color of the background
        root.setStyle("-fx-background-color: white;");

        board = Board.createStandardBoard();

        //Top bar - HBox - root
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(5,5,5,5));
        topBar.setSpacing(5);
        root.setTop(topBar);

        //Center - GridPane - root
        grid.setPadding(new Insets(5));
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: radial-gradient(radius 180%, darkslategray, derive(black, -30%), derive(darkslategray, 30%));");
        grid.setVgap(5);
        grid.setHgap(5);
        root.setLeft(grid);

        //Right - VBox - root
        status.setPadding(new Insets(30));
        status.setAlignment(Pos.TOP_CENTER);
        status.setSpacing(10);
        status.setStyle("-fx-border-width: 3; -fx-border-color: black;");
        root.setCenter(status);

        //Menu bar
        MenuBar menuBar = populateMenuBar();
        root.setTop(menuBar);

        //Create main scene
        Scene mainScene = new Scene(root, screenWidth = screenWidth / 2, screenHeight = screenHeight / 2);

        //Listeners for window size change

        mainScene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            screenWidth = newSceneWidth.intValue();
            Platform.runLater(() -> draw(board));
        });

        mainScene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
            screenHeight = newSceneHeight.intValue();
            Platform.runLater(() -> draw(board));
        });

        mainStage.setOnCloseRequest(e -> System.exit(0));

        mainStage.setTitle("Chess Application");
        mainStage.setScene(mainScene);
        mainStage.show();

        createOptionsDialog(mainStage);

        draw(board);
    }

    /**
     * Creates an options dialog box for determining what the player wants to do
     * @param stage the main stage of the application
     */
    private void createOptionsDialog(Stage stage){

        final Stage dialog = new Stage();

        //Settings box - HBox
        VBox settingsRoot = new VBox();
        settingsRoot.setPadding(new Insets(5,15,5,15));
        settingsRoot.setSpacing(5);

        HBox aiDifficultyPane = new HBox();
        VBox whiteOptionsPane = new VBox();
        VBox blackOptionsPane = new VBox();

        //Text
        Text whiteOptionsText = new Text("WHITE PLAYER");
        whiteOptionsText.setFont(new Font(15));

        Text blackOptionsText = new Text("BLACK PLAYER");
        blackOptionsText.setFont(new Font(15));

        Text aiDifficulty = new Text("AI DIFFICULTY");
        aiDifficulty.setFont(new Font(13));

        //Text fields
        TextField whitePlayerNameField = new TextField("Player1");
        whitePlayerNameField.setMaxWidth(150);

        TextField blackPlayerNameField = new TextField("Player2");
        blackPlayerNameField.setMaxWidth(150);

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
            setOptions(whiteOptions, blackOptions, aiOptions, whitePlayerNameField, blackPlayerNameField, dialog);
            drawStatusPane(board);
        });

        //Makes the exit button on the options box do the same as confirm button
        dialog.setOnCloseRequest(e -> {
            setOptions(whiteOptions, blackOptions, aiOptions, whitePlayerNameField, blackPlayerNameField, dialog);
            drawStatusPane(board);
        });

        Scene settingsScene = new Scene(settingsRoot, 230, 310);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(settingsScene);
        dialog.initOwner(stage);
        dialog.show();
    }

    /**
     * Takes the options set in the options dialog and applies them
     * @param whiteOptions if white is Player or AI
     * @param blackOptions if black is Player or AI
     * @param aiOptions the AI difficulty
     * @param whitePlayerNameField the name of the white player
     * @param blackPlayerNameField the name of the black player
     * @param stage the stage of the options dialog
     */
    private void setOptions(ToggleGroup whiteOptions, ToggleGroup blackOptions, ToggleGroup aiOptions, TextField whitePlayerNameField, TextField blackPlayerNameField, Stage stage){
        isWhiteAI = (boolean) whiteOptions.getSelectedToggle().getUserData();
        isBlackAI = (boolean) blackOptions.getSelectedToggle().getUserData();
        aiDepth = (int) aiOptions.getSelectedToggle().getUserData();

        String suffix;
        int rating;
        switch(aiDepth){
            case 1: {
                suffix = "Easy";
                rating = 1200;
                break;
            }
            case 2:
                suffix = "Medium";{
                rating = 1500;
                break;
            }
            case 3: {
                suffix = "Hard";
                rating = 1800;
                break;
            }
            default: {
                suffix = "Error";
                rating = 9999;
                break;
            }
        }

        if(isWhiteAI){
            whitePlayerName = "CPU(" + suffix +")";
            scoreSystem.updateHighscore(whitePlayerName, rating);
        } else{
            whitePlayerName = whitePlayerNameField.getText();
        }


        if(isBlackAI) {
            blackPlayerName = "CPU(" + suffix +")";
            scoreSystem.updateHighscore(blackPlayerName, rating);
        }
        else{
            blackPlayerName = blackPlayerNameField.getText();
        }

        scoreSystem.addUsername(whitePlayerName);
        scoreSystem.addUsername(blackPlayerName);

        whitePlayerScore = scoreSystem.getScore(whitePlayerName);
        blackPlayerScore = scoreSystem.getScore(blackPlayerName);

        stage.hide();

        //If white is AI make a move for the AI
        if(isWhiteAI)
            makeAIMove();
    }

    /**
     * Takes a tile and returns a StackPane representation of the tile so it can be put on the board.
     * @param tile a tile from the board
     * @param flip a boolean to decide if the tile should be black or white (true = white, false = black)
     * @param selected a boolean to decide if the tile should be marked as selected
     * @param highlight a boolean to decide if the tile should be highlighted
     * @return a visual representation of a tile
     */
    private StackPane makeStack (Tile tile, boolean flip, boolean selected, boolean highlight, boolean attackHighlight){
        final int TILE_SIZE = (int) ((screenHeight + screenWidth) * 2.6 /
                (BoardUtils.getWidth() * BoardUtils.getHeight()));
        StackPane stack = new StackPane();

        Rectangle r = new Rectangle(TILE_SIZE,TILE_SIZE);
        r.setArcHeight(10);
        r.setArcWidth(10);

        if(selected){
            r.setFill(Color.LIGHTGREEN);
        } else if (highlight){
            r.setFill(Color.LIGHTBLUE);
        } else if (attackHighlight){
            r.setFill(Color.rgb(225, 215, 240));
        } else if (flip){
            r.setFill(Color.LIGHTGRAY);
        } else {
            r.setFill(Color.DARKGRAY);
        }
        // set blending to background colors
        r.setBlendMode(BlendMode.HARD_LIGHT);

        stack.getChildren().add(r);

        //if the tile has a piece add the icon to the stack
        //TODO: maybe make all the images be loaded into variables in init() to improve performance (instead of doing it every redraw here)
        if(!tile.isEmpty()){
            String url = "/images/" + tile.getPiece().getPieceAlliance().toString().substring(0, 1) + tile.getPiece().toString() + ".png";
            ImageView icon = new ImageView(url);
            icon.setFitHeight(TILE_SIZE - 15);
            icon.setFitWidth(TILE_SIZE - 15);
            icon.setPreserveRatio(true);

            stack.getChildren().add(icon);
        }

        stack.setOnMouseClicked(e -> onClickHandler(tile.getTileCoord()));

        return stack;
    }

    /**
     * Draws the board in the GridPane
     * @param board a board from the Board class
     */
    private void draw(Board board){
        grid.getChildren().clear();
        //flip is used to keep track on the tile color
        boolean flip = true;

        Collection legalMoves = listLegalMoves(selectedTile);

        for (int i = 0; i < BoardUtils.getHeight(); i++) {
            flip=!flip;
            for (int j = 0; j < BoardUtils.getWidth(); j++) {
                flip=!flip;

                boolean selected = checkSelected(selectedTile, i, j);

                boolean highlight = false;
                boolean attackHighlight = false;
                Coordinate c = new Coordinate(i,j);
                if(legalMoves != null)
                    if (legalMoves.contains(c))
                        if (!board.getTile(c).isEmpty() && board.getTile(c).getPiece().getPieceAlliance() != board.currentPlayer().getAlliance())
                            attackHighlight = true;
                        else
                            highlight = true;

                StackPane tile = makeStack(board.getTile(new Coordinate(i,j)), flip, selected, highlight, attackHighlight);
                grid.add(tile, i,j);
            }
        }

        drawStatusPane(board);
    }

    /**
     * Handles what to do when a tile is clicked
     * @param c the coordinate of the clicked tile
     */
    private void onClickHandler(Coordinate c){

        Piece piece = board.getTile(c).getPiece();

        if (selectedTile == null){
            if(!board.getTile(c).isEmpty()){
                if(board.currentPlayer().getAlliance() == piece.getPieceAlliance()){
                    selectedTile = c;
                    draw(board);

                }
            }
        } else if (selectedTile == c) {
            selectedTile = null;
            draw(board);
        } else {
            if(board.getTile(c).isEmpty()){
                attemptMove(c);
            } else if(board.currentPlayer().getAlliance() != piece.getPieceAlliance()){
                if (piece.getPieceAlliance() != board.getTile(selectedTile).getPiece().getPieceAlliance()){
                    attemptMove(c);
                } else {
                    selectedTile = null;
                    draw(board);
                }
            } else {
                selectedTile = c;
                draw(board);
            }
        }
    }

    /**
     * Checks if a given coordinate has the following x and y coordinates and is not empty
     * @param c coordinate to be checked
     * @param x x coordinate
     * @param y y coordinate
     * @return
     */
    private boolean checkSelected(Coordinate c, int x, int y) {
        return c != null && !board.getTile(c).isEmpty() && (c.getX() == x) && (c.getY() == y);
    }

    /**
     * Makes a list of all legal moves from the given board coordinate
     * @param c coordinate on the board
     * @return a list of legal moves from that board position
     */
    private Collection<Coordinate> listLegalMoves (Coordinate c){
        if (c == null) return null;

        //Collection<Move> temp = board.getTile(c).getPiece().calculateLegalMoves(board);
        // Note: this is a bit heavy on the system, since we are making every move and checking
        // the status of it to remove highlighting tiles which sets the player in check
        List<Move> temp = new ArrayList<>(board.currentPlayer().getLegalMovesForPiece(board.getTile(c).getPiece()));
        List<Coordinate> coordinatesToHighlight = new ArrayList<>();
        for (Move move : temp) {
            if(board.currentPlayer().makeMove(move).getMoveStatus().isDone()) {
                coordinatesToHighlight.add(move.getDestinationCoordinate());
            }
        }

        return coordinatesToHighlight;
    }

    /**
     * Attempts to make a move to the given coordinate, from the tile which is selected. If the move is illegal nothing happens.
     * @param c the coordinate to attempt to move to
     */
    private void attemptMove (Coordinate c){

        final Move move = Move.MoveFactory.createMove(board, selectedTile, c);
        final MoveTransition newBoard = board.currentPlayer().makeMove(move);

        selectedTile = null;

        if (newBoard.getMoveStatus().isDone())
            board = newBoard.getTransitionBoard();

        draw(board);

        if(gameIsOver(board)){
            gameOver();
        } else {
            Platform.runLater(this::makeAIMove);
        }
    }

    /**
     * Looks at the board anc calculates a move for the AI based on the aiDepth
     */
    private void makeAIMove(){
        if((board.currentPlayer().getAlliance() == Alliance.WHITE && isWhiteAI) || board.currentPlayer().getAlliance() == Alliance.BLACK && isBlackAI){
            final MoveStrategy moveStrategy = new MiniMax(aiDepth);
            final Move AIMove = moveStrategy.execute(board);
            final MoveTransition newBoard = board.currentPlayer().makeMove(AIMove);

            selectedTile = null;

            if (newBoard.getMoveStatus().isDone())
                board = newBoard.getTransitionBoard();

            draw(board);

            if(gameIsOver(board)){
                gameOver();
            } else {
                Platform.runLater(this::makeAIMove);
            }
        }
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
        toggleHighlight.setOnAction(event -> highlightEnabled = !highlightEnabled);
        toggleHighlight.setSelected(true);

        CheckMenuItem toggleBoardStatus = new CheckMenuItem("Show board status");
        toggleHighlight.setOnAction(event -> statusEnabled = !statusEnabled);
        toggleHighlight.setSelected(true);

        optionsMenu.getItems().addAll(toggleHighlight,toggleBoardStatus);
        return optionsMenu;
    }

    /**
     * Create a file menu
     * @return return populated file menu
     */
    private Menu createFileMenu() {
        Menu fileMenu = new Menu("File");
        MenuItem login = new MenuItem("Login");
        login.setOnAction(event -> System.out.println("login feature here ->"));

        MenuItem newGame = new MenuItem("New game");
        newGame.setOnAction(event -> {
            createOptionsDialog(mainStage);
            board = Board.createStandardBoard();

            //Clear info about previous board states
            boardHistory.clear();
            equalBoardStateCounter = 0;

            draw(board);
        });

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(event -> System.exit(0));

        fileMenu.getItems().addAll(login, newGame, exit);
        return fileMenu;
    }

    /**
     * Draws the game status pane on the right side of the board in the gui
     * @param board the game board
     */
    private void drawStatusPane(Board board) {
        status.getChildren().clear();

        Text title = new Text("GAME STATS");
        // title styling
        title.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 40));
        DropShadow ds = new DropShadow(5, Color.color(0.4f, 0.4f, 0.4f));
        ds.setOffsetY(4.0f);
        title.setEffect(ds);
        // player names and scores
        Text whitePlayerText = new Text(whitePlayerName + ": " + whitePlayerScore);
        Text blackPlayerText = new Text(blackPlayerName + ": " + blackPlayerScore);
        // player names and scores styling
        whitePlayerText.setFont(new Font(17));
        blackPlayerText.setFont(new Font(17));
        whitePlayerText.setUnderline(true);
        blackPlayerText.setUnderline(true);

        // show the evaluation of the current board relative to the current player, can help you know how well you are doing
        // TODO: make only display this if statusEnabled == true
        BoardEvaluator boardEvaluator = new RegularBoardEvaluator();
        Text boardStatusText = new Text((board.currentPlayer().getAlliance() + " board status: " + boardEvaluator.evaluate(board, 3)).toUpperCase());
        if (board.currentPlayer().getAlliance() == Alliance.BLACK)
            boardStatusText = new Text((board.currentPlayer().getAlliance() + " board status: " + boardEvaluator.evaluate(board, 3) * -1).toUpperCase());

        boardStatusText.setFont(new Font(14));

        // display if the current player is in check
        Text currentPlayerInCheck = new Text((board.currentPlayer().getAlliance() + " in check: " + board.currentPlayer().isInCheck()).toUpperCase());
        currentPlayerInCheck.setFont(new Font(14));

        status.getChildren().addAll(title, whitePlayerText, blackPlayerText, boardStatusText, currentPlayerInCheck);
    }

    /**
     * check if a single board state is repeated within the last 5 turns to check if its a draw
     * @return true if its a draw, false if otherwise
     */
    private boolean checkForDrawByRepetition(){
        if (!boardHistory.isEmpty())

            //no moves were made
            if (boardHistory.get(boardHistory.size()-1).toString().equals(board.toString()))
                return false;

            for (Board b : boardHistory){
                if (board.toString().equals(b.toString())) {
                    equalBoardStateCounter++;
                    break;
                }
            }
        if (equalBoardStateCounter >= 3){
            return true;
        }

        if (boardHistory.size() < 5)
            boardHistory.add(board);
        else {
            for (int i=1; i<boardHistory.size(); i++){
                boardHistory.set(i-1, boardHistory.get(i));
            }
            boardHistory.add(board);
        }
        return false;
    }

    private boolean gameIsOver(Board board){
        boolean checkmate = board.currentPlayer().isInCheckmate();
        boolean stalemate = board.currentPlayer().isInStalemate();
        boolean repetition = checkForDrawByRepetition();
        return checkmate || stalemate || repetition;
    }

    /**
     * This dialog box pops up when the game ends
     * @param stage the main stage of the application
     */
    private void createGameOverDialog(Stage stage){
        final Stage dialog = new Stage();

        //Text box - HBox
        VBox gameOverRoot = new VBox();
        gameOverRoot.setPadding(new Insets(5,15,5,15));
        gameOverRoot.setSpacing(5);
        gameOverRoot.setAlignment(Pos.TOP_CENTER);

        //Text
        Text go = new Text("GAME OVER");
        go.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        Text t1 = new Text("Your updated scores are:");
        Text t2 = new Text(whitePlayerName + ": " + whitePlayerScore);
        Text t3 = new Text(blackPlayerName + ": " + blackPlayerScore);
        t1.setFont(new Font(15));
        t1.setUnderline(true);
        t2.setFont(new Font(13));
        t3.setFont(new Font(13));
        gameOverRoot.getChildren().addAll(go, t1, t2, t3);

        //Button container
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.BOTTOM_CENTER);
        buttonContainer.setSpacing(10);
        buttonContainer.setPadding(new Insets(10,0,0,0));

        //Button1
        Button newGame = new Button("New Game!");
        newGame.setAlignment(Pos.BASELINE_LEFT);
        buttonContainer.getChildren().add(newGame);

        //Button2
        Button quit = new Button("Quit :(");
        buttonContainer.getChildren().add(quit);
        quit.setAlignment(Pos.BASELINE_RIGHT);
        gameOverRoot.getChildren().addAll(buttonContainer);

        newGame.setOnAction(e -> {
            createOptionsDialog(mainStage);
            board = Board.createStandardBoard();

            //Clear info about previous board states
            boardHistory.clear();
            equalBoardStateCounter = 0;

            draw(board);
            dialog.hide();
        });

        quit.setOnAction(e -> System.exit(0));

        dialog.setOnCloseRequest(e -> {
            createOptionsDialog(mainStage);
            board = Board.createStandardBoard();

            //Clear info about previous board states
            boardHistory.clear();
            equalBoardStateCounter = 0;

            draw(board);
            dialog.hide();
        });

        Scene gameOverScene = new Scene(gameOverRoot, 200, 150);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(gameOverScene);
        dialog.initOwner(stage);
        dialog.show();
    }

    /**
     * When the game is over the scores are calculated and updated here
     */
    private void gameOver(){
        int[] scores;

        if(board.currentPlayer().isInStalemate() || checkForDrawByRepetition()){
            scores = scoreSystem.matchRating(whitePlayerName, blackPlayerName, 0.5, 0.5);
        } else if(board.currentPlayer().getAlliance() == Alliance.BLACK){
            scores = scoreSystem.matchRating(whitePlayerName, blackPlayerName, 1, 0);
        } else {
            scores = scoreSystem.matchRating(whitePlayerName, blackPlayerName, 0, 1);
        }

        scoreSystem.updateHighscore(whitePlayerName, scores[0]);
        scoreSystem.updateHighscore(blackPlayerName, scores[1]);
        whitePlayerScore = scores[0];
        blackPlayerScore = scores[1];

        drawStatusPane(board);
        createGameOverDialog(mainStage);
    }

    public static void main(String[] args) {launch(args);}
}