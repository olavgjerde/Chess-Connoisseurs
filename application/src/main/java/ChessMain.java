import board.Board;
import board.BoardUtils;
import board.Coordinate;
import board.Tile;
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

public class ChessMain extends Application {

    private final int SIZE = 50; //width and height of each tile

    private BorderPane root;

    private Board board;

    @Override
    public void start(Stage mainStage) throws Exception{

        root = new BorderPane();
        GridPane grid = new GridPane();

        root.setCenter(grid);

        //not sure where to put this just yet
        board = Board.createStandardBoard();

        //Top bar - HBox
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(5,5,5,5));
        topBar.setSpacing(5);
        root.setTop(topBar);

        draw(board, grid);

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

    }

    /**
     * Takes a tile and returns a StackPane representation of the tile so it can be put on the board.
     * @param tile a tile from the board
     * @param flip a boolean to decide if the tile should be black or white (true = white, false = black)
     * @return a visual representation of a tile
     */
    private StackPane makeStack (Tile tile, boolean flip){
        StackPane stack = new StackPane();

        Rectangle r = new Rectangle(SIZE,SIZE);
        if (flip){
            r.setFill(Color.LIGHTGRAY);
        } else {
            r.setFill(Color.DARKGRAY);
        }

        stack.getChildren().add(r);

        //if the tile has a piece add the icon to the stack
        if(!tile.isTileEmpty()){
            String url = "/images/" + tile.getPiece().getPieceAlliance().toString().substring(0, 1) + tile.getPiece().toString() + ".png";
            ImageView icon = new ImageView(url);
            icon.setFitHeight((double) SIZE - 10);
            icon.setFitWidth((double) SIZE - 10);
            icon.setPreserveRatio(true);

            stack.getChildren().add(icon);
        }

        stack.setOnMouseClicked(e -> r.setFill(Color.RED));

        return stack;
    }

    /**
     * Draws the board in the GridPane
     * @param board a board from the Board class
     */
    private void draw(Board board, GridPane grid){
        boolean flip = true;

        for (int i = 0; i < BoardUtils.getHeight(); i++) {
            flip=!flip;
            for (int j = 0; j < BoardUtils.getWidth(); j++) {
                flip=!flip;
                StackPane tile = makeStack(board.getTile(new Coordinate(i,j)), flip);
                grid.add(tile, i,j);
            }
        }
    }

    public static void main(String[] args) {launch(args);}
}
