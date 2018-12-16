package gui;

import board.*;
import gui.extra.GameMenu;
import gui.extra.InformationToggle;
import gui.extra.MoveDescription;
import gui.extra.ResourceLoader;
import gui.scenes.StartMenuScene;
import gui.panes.*;
import gui.windows.PromotionWindow;
import gui.windows.RuleWindow;
import gui.windows.ScoreWindow;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Bloom;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import pieces.Alliance;
import pieces.Piece;
import pieces.Piece.PieceType;

/**
 * Main GUI for chess application
 *
 * DISCLAIMER:
 * To those that may stumble upon this class; this is not a good example of how you should structure a GUI.
 * These GUI-classes are in need of refactoring and restructuring. It was mostly made by hack and slash coding with little
 * to no experience with javafx in a very short time period to finish a school project.
 * Hopefully the rest of the project is more readable than this class, and good luck with your future endeavours! :)
 */
public class ChessGame extends Application {
    private double windowWidth = Screen.getPrimary().getBounds().getWidth();
    private double windowHeight = Screen.getPrimary().getBounds().getHeight();
    private Stage primaryStage;
    private BorderPane gamePlayPane;
    private MoveDescription moveDescription = new MoveDescription();
    private InformationToggle informationToggle = new InformationToggle(true, true, true, true, false);
    private final ResourceLoader resources = ResourceLoader.getInstance();
    //Game state manager is set after confirming on start menu
    private GameStateManager gameStateManager;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Connoisseur Chess");
        primaryStage.getIcons().add(resources.ConnoisseurChess);
        primaryStage.setOnCloseRequest(event -> Platform.exit());
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

        StartMenuScene startScene = new StartMenuScene(windowWidth / 1.6, windowHeight / 1.45, this);
        primaryStage.setScene(startScene.getMenuScene());
        primaryStage.show();
    }

    /**
     * Allows another scene to set the game manager of the application
     * @param gameStateManager already constructed GameStateManager object
     */
    public void setGameManager(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
        gameStateManager.addMainGUIComponent(this);
    }

    /**
     * Check if the GUI should use a smaller layout then normal
     *
     * @return true if GUI should use small mode
     */
    private boolean smallMode() {
        return windowWidth <= Screen.getPrimary().getBounds().getWidth() / 2;
    }

    /**
     * Bootstrapper for game scene, creates initial layout and draws every pane once
     */
    public void showGameScene() {
        gamePlayPane = new BorderPane();
        gamePlayPane.setTop(new GameMenu(primaryStage, gameStateManager, informationToggle, this));
        drawChessPane();

        //Create this scene with dimensions of start menu scene
        Scene gameScene = new Scene(gamePlayPane, windowWidth, windowHeight);
        primaryStage.setScene(gameScene);
        //Set off white AI (in case of human vs white ai / ai vs ai)
        if (gameStateManager.currentPlayerAlliance() == Alliance.WHITE && gameStateManager.isWhiteAI()) doAiMove();
        else if (gameStateManager.currentPlayerAlliance() == Alliance.BLACK && gameStateManager.isBlackAI()) doAiMove();
            //Show hint on startup if in tutor mode
        else if (gameStateManager.isTutorMode()) showMoveHint();
    }

    /**
     * Shows the rule window for the application
     */
    public void showRuleWindow() {
        double parentStageX = primaryStage.getX();
        double parentStageY = primaryStage.getY();
        double parentWidth = primaryStage.getWidth();
        double parentHeight = primaryStage.getHeight();
        RuleWindow ruleWindow = new RuleWindow(parentStageX, parentStageY,
                parentWidth, parentHeight, gameStateManager.getGameMode());
        ruleWindow.showRuleWindow();
    }

    /**
     * Shows the highscore window for the application
     */
    public void showHighScoreWindow() {
        double parentStageX = primaryStage.getX();
        double parentStageY = primaryStage.getY();
        double parentWidth = primaryStage.getWidth();
        double parentHeight = primaryStage.getHeight();
        ScoreWindow scoreWindow = new ScoreWindow(parentStageX, parentStageY, parentWidth, parentHeight);
        scoreWindow.showHighscoreWindow();
    }

    /**
     * Shows a pop menu where the player can choose what type of piece they want to promote to
     *
     * @return PieceType which the user selected
     */
    PieceType showPromotionWindow() {
        double parentStageX = primaryStage.getX();
        double parentStageY = primaryStage.getY();
        double parentWidth = primaryStage.getWidth();
        double parentHeight = primaryStage.getHeight();
        PromotionWindow promotionWindow = new PromotionWindow(parentStageX, parentStageY,
                parentWidth, parentHeight, gameStateManager);
        promotionWindow.showPromotionWindow();
        return promotionWindow.getSelectedPiece();
    }

    /**
     * Draws the chess board where the pieces are displayed
     */
    public void drawChessPane() {
        GridPane chessGridPane = new GridPane();
        chessGridPane.setStyle("-fx-background-color: radial-gradient(center 50% 50% , radius 80% , darkslategray, black);");
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
        gamePlayPane.setCenter(chessGridPane);

        //Update the other panes when redrawing chess pane
        if (!smallMode()) {
            drawStatusPane();
            drawTakenPiecesPane();
        }
        drawInfoPane();
    }

    /**
     * Draws the pane which will display the pieces taken by the players
     */
    private void drawTakenPiecesPane() {
        gamePlayPane.setLeft(new TakenPiecesPane(windowWidth, gameStateManager.getTakenPieces()));
    }

    /**
     * Draw the pane that displays information about the games state
     */
    public void drawStatusPane() {
        gamePlayPane.setRight(new StatusPane(windowWidth, gameStateManager, informationToggle.isBoardStatusEnabled(), this));
    }

    /**
     * Shows the game over pane for the application, which has options for going back to
     * the start menu, exiting, or continuing with a new game with the same settings.
     */
    private void drawGameOverPane() {
        gamePlayPane.setBottom(new GameOverPane(windowWidth, gameStateManager, gamePlayPane, this));
    }

    /**
     * Draws a pane which displays which players turn it is
     */
    private void drawInfoPane() {
        gamePlayPane.setBottom(new InfoPane(windowWidth, gameStateManager));
    }

    /**
     * Attempts to make a move based on the user input
     */
    private void doHumanMove() {
        Coordinate start = moveDescription.getStartTile().getTileCoord();
        Coordinate end = moveDescription.getDestinationTile().getTileCoord();
        gameStateManager.makeMove(start, end);
        informationToggle.toggleMoveAnimation();
        //Reset user move related variables that were used for making this move
        moveDescription.resetDescription();
        //Redraw
        drawChessPane();
        if (gameStateManager.isGameOver()) gameOverCalculations();
        else if (gameStateManager.isBlackAI() || gameStateManager.isWhiteAI()) doAiMove();
    }

    /**
     * Lets the AI make a move on the board
     * Calls itself if AI vs AI is enabled
     */
    public void doAiMove() {
        Task AITask = new Task() {
            @Override
            protected Object call() {
                gameStateManager.makeAIMove();
                informationToggle.toggleMoveAnimation();
                //Redraw
                Platform.runLater(ChessGame.this::drawChessPane);
                //Play sound for moving piece
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
    public void showMoveHint() {
        new Thread(new Task() {
            @Override
            protected Object call() {
                //Empty any ongoing player move
                moveDescription.resetDescription();
                //Let AI find "best" move
                Move hintMove = gameStateManager.getHint(4, 1000);
                //Set coordinates found
                moveDescription.setHintStartCoordinate(hintMove.getCurrentCoordinate());
                moveDescription.setHintDestinationCoordinate(hintMove.getDestinationCoordinate());
                //Redraw to show coordinates found
                Platform.runLater(() -> {
                    drawChessPane();
                    moveDescription.resetHints();
                });
                return null;
            }
        }).start();
    }

    /**
     * When the game is over the scores are calculated and updated here
     */
    private void gameOverCalculations() {
        gameStateManager.gameOverCalculations();
        if (!smallMode()) Platform.runLater(this::drawStatusPane);
        Platform.runLater(this::drawGameOverPane);
    }

    /**
     * This class extends the StackPane class and embeds the connection between
     * the tiles on data representation of the board and the gui representation of the board.
     */
    class ChessTile extends StackPane {
        private final double TILE_SIZE;
        private final Coordinate coordinateId;


        ChessTile(Coordinate coordinateId) {
            this.TILE_SIZE = ((windowHeight * 6.4) / (BoardUtils.getInstance().getWidth() * BoardUtils.getInstance().getHeight()));
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
            ImageView icon = new ImageView(ResourceLoader.getInstance().getPieceImage(tile.getPiece()));
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
            Coordinate hintStartCoordinate = moveDescription.getHintStartCoordinate();
            Coordinate hintDestinationCoordinate = moveDescription.getHintDestinationCoordinate();
            Tile startTile = moveDescription.getStartTile();

            Move lastMove = gameStateManager.getLastMove();
            if (informationToggle.isLastMoveHighlightEnabled() && lastMove != null) {
                Coordinate from = lastMove.getCurrentCoordinate(), to = lastMove.getDestinationCoordinate();
                if (coordinateId.equals(from)) tileColor = Color.rgb(255, 255, 160);
                else if (coordinateId.equals(to)) {
                    if (lastMove.isAttack()) tileColor = Color.rgb(255, 155, 155);
                    else tileColor = Color.rgb(255, 255, 160);
                }
            }
            if (informationToggle.isMoveHighlightEnabled() && startTile != null) {
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
            if (rectangle.getFill().equals(Color.LIGHTBLUE) ||
                rectangle.getFill().equals(Color.rgb(225, 215, 240)) ||
                rectangle.getFill().equals(Color.GREENYELLOW)) {

                FadeTransition fade = new FadeTransition(Duration.millis(1300), rectangle);
                fade.setFromValue(1.0);
                fade.setToValue(0.6);
                fade.setCycleCount(Timeline.INDEFINITE);
                fade.setAutoReverse(true);
                fade.play();
                if (this.coordinateId.equals(moveDescription.getHintDestinationCoordinate())) {
                    RotateTransition rotate = new RotateTransition(Duration.millis(2300), rectangle);
                    rotate.setByAngle(180);
                    rotate.setCycleCount(Timeline.INDEFINITE);
                    rotate.setAutoReverse(true);
                    rotate.play();
                }
            }
            if (gameStateManager.getLastMove() != null && informationToggle.isMoveAnimationEnabled()) {
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
                        scaleBack.setOnFinished(event1 -> informationToggle.toggleMoveAnimation());
                        scaleBack.play();
                    });

                    RotateTransition rotate = new RotateTransition(Duration.millis(600), image);
                    rotate.setAxis(Rotate.Y_AXIS);
                    rotate.setFromAngle(0);
                    rotate.setToAngle(360);
                    rotate.setInterpolator(Interpolator.LINEAR);
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
                gameStateManager.isGameOver()) {
                return;
            }

            Tile startTile = moveDescription.getStartTile();
            if (startTile == null) {
                //User select
                moveDescription.setStartTile(gameStateManager.getTile(coordinateId));
                startTile = moveDescription.getStartTile();
                if (startTile.getPiece() != null) {
                    if (startTile.getPiece().getPieceAlliance() == gameStateManager.currentPlayerAlliance()) {
                        moveDescription.setUserMovedPiece(moveDescription.getStartTile().getPiece());
                        drawChessPane();
                    } else {
                        moveDescription.setStartTile(null);
                    }
                } else {
                    moveDescription.setStartTile(null);
                }
            } else if (startTile.equals(gameStateManager.getTile(inputCoordinate))) {
                //User deselect
                moveDescription.setStartTile(null);
                Platform.runLater(ChessGame.this::drawChessPane);
            } else {
                //User select 'destination'
                moveDescription.setDestinationTile(gameStateManager.getTile(inputCoordinate));

                //User selected own piece as destination; let user switch between own pieces on the fly
                if (moveDescription.swapBetweenPieces()) drawChessPane();

                //Attempt move
                if (moveDescription.getDestinationTile() != null) doHumanMove();
            }
        }
    }
}
