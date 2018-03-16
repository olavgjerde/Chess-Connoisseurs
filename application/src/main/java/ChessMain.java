import board.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import pieces.Piece;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChessMain extends Application {

    private final int SIZE = 50; //width and height of each tile

    private BorderPane root;
    private GridPane grid;
    private Board board;
    private Tile selectedTile;

    @Override
    public void start(Stage mainStage) throws Exception{

        root = new BorderPane();
        grid = new GridPane();

        root.setCenter(grid);

        board = Board.createStandardBoard();

        //Top bar - HBox
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(5,5,5,5));
        topBar.setSpacing(5);
        root.setTop(topBar);

        //Center - GridPane
        grid.setPadding(new Insets(5,5,5,5));

        draw(board, null);

        //Button
        Button testButton = new Button();
        testButton.setText("Test");
        testButton.setMaxWidth(100);
        topBar.getChildren().addAll(testButton);

        //set button action using lambda
        testButton.setOnAction(e -> testMethod());

        Scene mainScene = new Scene(root, 700, 500);

        mainStage.setTitle("Chess Application");
        mainStage.setScene(mainScene);
        mainStage.show();
    }

    //mostly going to be used for debugging
    private void testMethod() {
        System.out.println();
    }

    //this method runs before the start method
    @Override
    public void init() throws Exception{
        selectedTile = null;
    }

    /**
     * Takes a tile and returns a StackPane representation of the tile so it can be put on the board.
     * @param tile a tile from the board
     * @param flip a boolean to decide if the tile should be black or white (true = white, false = black)
     * @param selected a boolean to decide if the tile should be highlighted
     * @return a visual representation of a tile
     */
    private StackPane makeStack (Tile tile, boolean flip, boolean selected, boolean highlight){
        StackPane stack = new StackPane();

        Rectangle r = new Rectangle(SIZE,SIZE);

        if(selected){
            r.setFill(Color.LIGHTGREEN);
        } else if (highlight){
            r.setFill(Color.LIGHTBLUE);
        } else if (flip){
            r.setFill(Color.LIGHTGRAY);
        } else {
            r.setFill(Color.DARKGRAY);
        }

        stack.getChildren().add(r);

        //if the tile has a piece add the icon to the stack
        //TODO: maybe make all the images be loaded into variables in init() to improve performance (instead of doing it every redraw here)
        if(!tile.isTileEmpty()){
            String url = "/images/" + tile.getPiece().getPieceAlliance().toString().substring(0, 1) + tile.getPiece().toString() + ".png";
            ImageView icon = new ImageView(url);
            icon.setFitHeight((double) SIZE - 10);
            icon.setFitWidth((double) SIZE - 10);
            icon.setPreserveRatio(true);

            stack.getChildren().add(icon);
        }

        stack.setOnMouseClicked(e -> onClickHandler(tile.getTileCoord(), tile.getPiece()));

        return stack;
    }

    /**
     * Draws the board in the GridPane
     * @param board a board from the Board class
     * @param selectedPos position of the tile that is to be highlighted
     */
    private void draw(Board board, Coordinate selectedPos){
        //flip is used to keep track on the tile color
        boolean flip = true;

        Collection legalMoves = listLegalMoves(selectedPos);

        for (int i = 0; i < BoardUtils.getHeight(); i++) {
            flip=!flip;
            for (int j = 0; j < BoardUtils.getWidth(); j++) {
                flip=!flip;

                boolean selected = checkHightlight(selectedPos, i, j);

                boolean highlight = false;
                if(legalMoves != null)
                    if (legalMoves.contains(new Coordinate(i,j)))
                        highlight = true;

                StackPane tile = makeStack(board.getTile(new Coordinate(i,j)), flip, selected, highlight);
                grid.add(tile, i,j);
            }
        }
    }

    private void onClickHandler(Coordinate coord, Piece piece){
        if (selectedTile == null){
            if (piece == null){
                draw(board, null);
            } else {
                selectedTile = board.getTile(coord);
                draw(board, coord);
            }
        } else {
            if (piece == null){
                selectedTile = null;
                draw(board, null);
            } else if (coord != selectedTile.getTileCoord()){
                draw(board, coord);
            }
        }
    }

    private boolean checkHightlight(Coordinate c, int x, int y){
        if (c == null)
            return false;

        if(board.getTile(c).isTileEmpty())
            return false;

        if((c.getX() == x) && (c.getY() == y))
            return true;

        return false;
    }

    private Collection<Coordinate> listLegalMoves (Coordinate c){

        if (c == null)
            return null;

        Collection<Move> temp = board.getTile(c).getPiece().calculateLegalMoves(board);
        Collection<Coordinate> list = new ArrayList<>();

        for (Move m : temp)
            list.add(m.getDestinationCoordinate());

        return list;
    }

    public static void main(String[] args) {launch(args);}
}