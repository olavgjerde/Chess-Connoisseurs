import board.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pieces.Alliance;
import pieces.Piece;
import player.MoveTransition;
import player.basicAI.MiniMax;
import player.basicAI.MoveStrategy;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

public class DemoGUI extends Application {
    Board chessBoard;

    BorderPane borderPane;
    GridPane chessBoardPane;
    VBox statusPane;
    Scene mainScene;
    Stage primaryStage;

    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    private int screenWidth = gd.getDisplayMode().getWidth();
    private int screenHeight = gd.getDisplayMode().getHeight();

    private boolean highlightEnabled;

    // player input
    private Tile sourceTile, destinationTile;
    private Piece userMovedPiece;

    // player stats
    private String whitePlayerName = "", blackPlayerName = "";
    private int whitePlayerScore = 0, blackPlayerScore = 0;

    // ai settings
    private boolean isWhiteAI;
    private boolean isBlackAI;
    private int aiDepth = 3;
    // ai difficulties
    private final int EASY_AI = 1;
    private final int INTERMEDIATE_AI = 2;
    private final int HARD_AI = 3;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // set basic layout
        this.borderPane = new BorderPane();
        this.chessBoardPane = new GridPane();
        this.statusPane = new VBox();
        this.primaryStage = primaryStage;
        // chessboard pane styling
        chessBoardPane.setPadding(new Insets(5,5,5,5));
        chessBoardPane.setVgap(4);
        chessBoardPane.setHgap(4);
        chessBoardPane.setAlignment(Pos.CENTER);
        chessBoardPane.setStyle("-fx-background-color: black;");
        // status pane styling
        statusPane.setPadding(new Insets(20,5,5,5));
        statusPane.setStyle("-fx-background-color: white;");
        statusPane.setAlignment(Pos.TOP_CENTER);
        // menu creation
        MenuBar menuBar = populateMenuBar();
        // border pane settings
        borderPane.setCenter(statusPane);
        borderPane.setLeft(chessBoardPane);
        borderPane.setTop(menuBar);

        // set primary stage to mainScene
        mainScene = new Scene(borderPane, screenWidth = screenWidth / 2, screenHeight = screenHeight / 2);
        // listeners to let the chessboard adapt
        mainScene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            screenWidth = newSceneWidth.intValue();
            Platform.runLater(() -> drawGridPane(chessBoard));
        });
        mainScene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
            screenHeight = newSceneHeight.intValue();
            Platform.runLater(() -> drawGridPane(chessBoard));
        });

        // create standard board
        chessBoard = Board.createStandardBoard();

        // draw the gui representation of the board
        drawGridPane(chessBoard);

        primaryStage.setTitle("Connoisseur Chess");
        primaryStage.setScene(mainScene);
        primaryStage.show();

        createOptionsDialog(primaryStage);

        // if both the whiteai and blackai is set -> set them off
        if (isWhiteAI && isBlackAI) makeAIMove();
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

        MenuItem aiSettings = new MenuItem("AI Settings");
        aiSettings.setOnAction(event -> System.out.println("OPEN AI WINDOW"));

        CheckMenuItem toggleHighlight = new CheckMenuItem("Enable highlighting");
        toggleHighlight.setOnAction(event -> highlightEnabled = !highlightEnabled);

        optionsMenu.getItems().addAll(aiSettings, toggleHighlight);
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
            createOptionsDialog(primaryStage);
            chessBoard = Board.createStandardBoard();
            drawGridPane(chessBoard);
        });

        MenuItem save = new MenuItem("Save");
        save.setOnAction(event -> {
            // todo save game
        });

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(event -> System.exit(0));

        fileMenu.getItems().addAll(login, newGame, save, exit);
        return fileMenu;
    }

    private void createOptionsDialog(Stage stage){
        final Stage startupDialog = new Stage();

        //Settings box - HBox - settings
        VBox settingsRoot = new VBox();
        settingsRoot.setPadding(new Insets(5,15,5,15));
        settingsRoot.setSpacing(5);

        //Radio buttons for options - settings
        final ToggleGroup whiteOptions = new ToggleGroup();

        Text whiteOptionsText = new Text("White player:");
        RadioButton whiteOption1 = new RadioButton("Player");
        TextField whitePlayerName = new TextField("Player 1");
        whiteOption1.setUserData(false);
        whiteOption1.setToggleGroup(whiteOptions);
        whiteOption1.setSelected(true);

        RadioButton whiteOption2 = new RadioButton("AI");
        whiteOption2.setUserData(true);
        whiteOption2.setToggleGroup(whiteOptions);

        Text blackOptionsText = new Text("Black player:");
        TextField blackPlayerName = new TextField("Player 2");
        final ToggleGroup blackOptions = new ToggleGroup();

        RadioButton blackOption1 = new RadioButton("Player");
        blackOption1.setUserData(false);
        blackOption1.setSelected(true);
        blackOption1.setToggleGroup(blackOptions);

        RadioButton blackOption2 = new RadioButton("AI");
        blackOption2.setUserData(true);
        blackOption2.setToggleGroup(blackOptions);

        settingsRoot.getChildren().addAll(whiteOptionsText,whitePlayerName, whiteOption1, whiteOption2,
                                          blackOptionsText, blackPlayerName,blackOption1, blackOption2);

        //Confirm settings button - settings
        Button confirmSettings = new Button();
        confirmSettings.setText("Confirm");
        confirmSettings.setMaxWidth(100);
        settingsRoot.getChildren().add(confirmSettings);

        confirmSettings.setOnAction(e -> {
            isWhiteAI = (boolean) whiteOptions.getSelectedToggle().getUserData();
            isBlackAI = (boolean) blackOptions.getSelectedToggle().getUserData();
            if (!isWhiteAI) this.whitePlayerName = whitePlayerName.getText();
            else this.whitePlayerName = "CPU";
            if (!isBlackAI) this.blackPlayerName = blackPlayerName.getText();
            else this.blackPlayerName = "CPU";
            startupDialog.hide();
            drawStatusPane(chessBoard);
        });

        Scene settingsScene = new Scene(settingsRoot, 200, 250);
        startupDialog.initModality(Modality.APPLICATION_MODAL);
        startupDialog.setScene(settingsScene);
        startupDialog.initOwner(stage);
        startupDialog.show();
    }

    private void drawStatusPane(Board chessBoard) {
        statusPane.getChildren().clear();
        Text title = new Text("GAME STATS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 35));
        Label whitePlayer = new Label(whitePlayerName + " : " + whitePlayerScore);
        Label blackPlayer = new Label(blackPlayerName + " : " + blackPlayerScore);
        Label currentPlayerCheck = new Label(chessBoard.currentPlayer().getAlliance() + " check-status: " + chessBoard.currentPlayer().isInCheck());
        // todo: add movelog to statuspane
        statusPane.getChildren().addAll(title, whitePlayer, blackPlayer, currentPlayerCheck);
    }

    private void drawGridPane(Board board) {
        chessBoardPane.getChildren().clear();
        for (int i = 0; i < BoardUtils.getHeight(); i++) {
            for (int j = 0; j < BoardUtils.getWidth(); j++) {
                chessBoardPane.add(new ChessTile(new Coordinate(j,i), board), j,i);
            }
        }
        drawStatusPane(board);
    }

    private class ChessTile extends StackPane {
        private final double TILE_SIZE = (screenHeight + screenWidth) * 2.4 /
                                          (BoardUtils.getWidth() * BoardUtils.getHeight());
        private final Coordinate coordinateId;

        private ChessTile(Coordinate coordinateId, Board board) {
            this.coordinateId = coordinateId;
            Rectangle rectangle = new Rectangle(TILE_SIZE, TILE_SIZE, assignTileColor());
            this.getChildren().addAll(rectangle);
            if (!board.getTile(coordinateId).isEmpty()) assignTilePieceImage(board.getTile(coordinateId));
            this.setOnMouseClicked(e -> onClickHandler(coordinateId));
        }

        /**
         * Assign an image to the tile, given the tiles content
         * @param tile to draw
         */
        private void assignTilePieceImage(Tile tile) {
            String url = "/imageFullResBackup/" + tile.getPiece().getPieceAlliance().toString().substring(0, 1) + tile.getPiece().toString() + ".png";
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
                return Color.DARKSLATEGRAY;
            }
        }

        /**
         * Handles user input for a tile
         * @param coord Coordinate on the tile that the user triggered
         */
        private void onClickHandler(Coordinate coord){
            if (sourceTile == null) {
                sourceTile = chessBoard.getTile(coord);
                userMovedPiece = sourceTile.getPiece();
                if (userMovedPiece == null) sourceTile = null;
            } else {
                destinationTile = chessBoard.getTile(coord);
                final Move userMove = Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoord(), destinationTile.getTileCoord());
                final MoveTransition boardChange = chessBoard.currentPlayer().makeMove(userMove);
                if (boardChange.getMoveStatus().isDone()) {
                    // move was allowed
                    chessBoard = boardChange.getTransitionBoard();
                    drawGridPane(chessBoard);
                }
                // reset selection
                sourceTile = null;
                destinationTile = null;
                userMovedPiece = null;
            }
            Platform.runLater(() -> makeAIMove());
        }
    }

    private List<Coordinate> legalPieceMoves(Tile tile) {
        List<Move> temp = new ArrayList<>(chessBoard.currentPlayer().getLegalMovesForPiece(tile.getPiece()));
        List<Coordinate> coordinatesToHighlight = new ArrayList<>();
        for (Move move : temp) {
            if(chessBoard.currentPlayer().makeMove(move).getMoveStatus().isDone()) {
                coordinatesToHighlight.add(move.getDestinationCoordinate());
            }
        }
        return coordinatesToHighlight;
    }

    private void makeAIMove(){
        if((chessBoard.currentPlayer().getAlliance() == Alliance.WHITE && isWhiteAI) ||
            chessBoard.currentPlayer().getAlliance() == Alliance.BLACK && isBlackAI){

            final MoveStrategy moveStrategy = new MiniMax(aiDepth);
            final Move AIMove = moveStrategy.execute(chessBoard);
            chessBoard = chessBoard.currentPlayer().makeMove(AIMove).getTransitionBoard();
            drawGridPane(chessBoard);

            Platform.runLater(() -> makeAIMove());
        }

    }


}
