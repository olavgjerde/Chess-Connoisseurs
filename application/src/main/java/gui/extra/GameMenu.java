package gui.extra;

import board.GameMode;
import gui.ChessGame;
import gui.GameStateManager;
import gui.scenes.StartMenuScene;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/**
 * Class that creates a menu bar to be used in the chess game's main UI
 */
public class GameMenu extends MenuBar {
    private final Stage parentStage;
    private final GameStateManager gameStateManager;
    private final InformationToggle informationToggle;
    private final ChessGame parentGui;

    public GameMenu(final Stage parentStage,
                    final GameStateManager gameStateManager, final InformationToggle informationToggle,
                    final ChessGame parentGui) {
        this.parentStage = parentStage;
        this.gameStateManager = gameStateManager;
        this.informationToggle = informationToggle;
        this.parentGui = parentGui;

        GameMode gameMode = gameStateManager.getGameMode();
        if (gameMode.equals(GameMode.NORMAL) || gameMode.equals(GameMode.RANDOM)) {
            this.getMenus().addAll(createFileMenu(), createOptionMenu());
        } else {
            this.getMenus().addAll(createFileMenu(), createOptionMenu(), createHelpMenu());
        }
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
            Scene startMenu = new StartMenuScene(parentStage.getWidth(), parentStage.getHeight(), parentGui).getMenuScene();
            parentStage.setScene(startMenu);
        });

        MenuItem highScores = new MenuItem("Highscores");
        highScores.setOnAction(event -> parentGui.showHighScoreWindow());

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(event -> System.exit(0));

        fileMenu.getItems().addAll(newGame, highScores, exit);
        return fileMenu;
    }

    /**
     * Create an options menu
     *
     * @return return populated options menu
     */
    private Menu createOptionMenu() {
        Menu optionsMenu = new Menu("Options");

        CheckMenuItem toggleHighlight = new CheckMenuItem("Highlight available moves");
        toggleHighlight.setOnAction(e -> informationToggle.toggleHighlight());
        toggleHighlight.setSelected(true);

        CheckMenuItem toggleMoveHighlight = new CheckMenuItem("Highlight previous move");
        toggleMoveHighlight.setOnAction(event -> {
            informationToggle.toggleLastMoveHighlight();
            parentGui.drawChessPane();
        });
        toggleMoveHighlight.setSelected(true);

        CheckMenuItem toggleBoardStatus = new CheckMenuItem("Show board status");
        toggleBoardStatus.setOnAction(event -> {
            informationToggle.toggleBoardStatus();
            parentGui.drawStatusPane();
        });
        toggleBoardStatus.setSelected(true);

        CheckMenuItem toggleMute = new CheckMenuItem("Toggle sound");
        toggleMute.setOnAction(e -> informationToggle.toggleSound());
        toggleMute.setSelected(true);

        optionsMenu.getItems().addAll(toggleHighlight, toggleMoveHighlight, toggleBoardStatus, toggleMute);
        return optionsMenu;
    }

    /**
     * Create an help menu
     *
     * @return return populated help menu
     */
    private Menu createHelpMenu() {
        Menu optionsMenu = new Menu("Help");
        MenuItem toggleRules = new MenuItem("Rules");
        toggleRules.setOnAction(event -> parentGui.showRuleWindow());
        optionsMenu.getItems().addAll(toggleRules);
        return optionsMenu;
    }
}
