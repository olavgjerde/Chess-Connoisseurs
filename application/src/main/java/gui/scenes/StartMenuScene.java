package gui.scenes;

import board.GameMode;
import gui.ChessGame;
import gui.GameStateManager;
import gui.extra.CircleAnimation;
import gui.extra.ResourceLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import player.Score;
import java.util.ArrayList;
import java.util.List;

public class StartMenuScene {
    private double sceneWidth;
    private double sceneHeight;
    private VBox menuParent;
    private ResourceLoader resources;

    private TextField whiteNameField, blackNameField;
    private ToggleGroup whiteOptions, blackOptions, aiOptions, boardStateOptions;
    private RadioButton whiteHumanOption, whiteAiOption, blackHumanOption, blackAiOption, tutorOption;
    private List<RadioButton> difficultyButtons;
    private ChessGame parentGui;

    public StartMenuScene(double sceneWidth, double sceneHeight, ChessGame parentGui) {
        this.parentGui = parentGui;
        this.resources = ResourceLoader.getInstance();
        this.sceneWidth = sceneWidth;
        this.sceneHeight = sceneHeight;

        menuParent = new VBox();
        menuParent.setMaxWidth(sceneWidth / 1.6);
        menuParent.setMaxHeight(sceneHeight / 1.45);
        menuParent.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-background-radius: 10;");
        menuParent.setAlignment(Pos.CENTER);
        menuParent.setSpacing(7);

        menuParent.getChildren().addAll(new ImageView(resources.ConnoisseurChess),
                                        createWhitePlayerField(menuParent.getMaxWidth()),
                                        createBlackPlayerField(menuParent.getMaxWidth()),
                                        createBoardStateField(),
                                        createAiField(),
                                        createConfirmButton());
        setInterfaceActions();
    }

    private HBox createWhitePlayerField(double parentBoxWidth) {
        HBox whiteOptionBox = new HBox();
        whiteOptionBox.setAlignment(Pos.CENTER);
        whiteOptionBox.setSpacing(5);
        ImageView whiteImage = new ImageView(resources.WK);
        whiteImage.setFitHeight(50);
        whiteImage.setPreserveRatio(true);
        Text whiteOptionText = new Text("WHITE PLAYER");
        whiteOptionText.setFont(new Font("Arial", sceneWidth / 60));
        this.whiteNameField = new TextField("Player1");
        whiteNameField.setMaxWidth(parentBoxWidth / 4);

        this.whiteOptions = new ToggleGroup();
        this.whiteHumanOption = new RadioButton("Human");
        whiteHumanOption.setToggleGroup(whiteOptions);
        whiteHumanOption.setUserData(false);
        whiteHumanOption.setSelected(true);
        this.whiteAiOption = new RadioButton("AI");
        whiteAiOption.setToggleGroup(whiteOptions);
        whiteAiOption.setUserData(true);
        whiteOptionBox.getChildren().addAll(whiteImage, whiteOptionText, whiteNameField, whiteHumanOption, whiteAiOption);

        return whiteOptionBox;
    }

    private HBox createBlackPlayerField(double parentBoxWidth) {
        HBox blackOptionBox = new HBox();
        blackOptionBox.setAlignment(Pos.CENTER);
        blackOptionBox.setSpacing(5);
        ImageView blackImage = new ImageView(resources.BK);
        blackImage.setFitHeight(50);
        blackImage.setPreserveRatio(true);
        Text blackOptionText = new Text("BLACK PLAYER");
        blackOptionText.setFont(new Font("Arial", sceneWidth / 60));
        this.blackNameField = new TextField("Player2");
        blackNameField.setMaxWidth(parentBoxWidth / 4);

        this.blackOptions = new ToggleGroup();
        this.blackHumanOption = new RadioButton("Human");
        blackHumanOption.setToggleGroup(blackOptions);
        blackHumanOption.setUserData(false);
        blackHumanOption.setSelected(true);
        this.blackAiOption = new RadioButton("AI");
        blackAiOption.setToggleGroup(blackOptions);
        blackAiOption.setUserData(true);
        blackOptionBox.getChildren().addAll(blackImage, blackOptionText, blackNameField, blackHumanOption, blackAiOption);

        return blackOptionBox;
    }

    private HBox createBoardStateField() {
        HBox boardStateBox = new HBox();
        boardStateBox.setAlignment(Pos.CENTER);
        boardStateBox.setSpacing(5);

        this.boardStateOptions = new ToggleGroup();
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

        this.tutorOption = new RadioButton("Tutor Mode");
        tutorOption.setToggleGroup(boardStateOptions);
        tutorOption.setUserData(GameMode.TUTOR);
        tutorOption.setSelected(false);

        Text gameMode = new Text("GAME MODE");
        gameMode.setFont(new Font(18));

        boardStateBox.getChildren().addAll(gameMode, standardOption, randomOption,
                                           hordeOption, lightBrigadeOption, tutorOption);

        return boardStateBox;
    }

    private HBox createAiField() {
        // Create and add difficulty buttons
        HBox aiOptionBox = new HBox();
        aiOptionBox.setAlignment(Pos.CENTER);
        aiOptionBox.setSpacing(5);

        this.aiOptions = new ToggleGroup();
        String[] levelPrefix = {"Easy", "Intermediate", "Expert", "Experimental"};
        this.difficultyButtons = new ArrayList<>();
        for (int i = 0; i < levelPrefix.length; i++) {
            difficultyButtons.add(new RadioButton(levelPrefix[i]));
            if (levelPrefix[i].equals("Intermediate")) difficultyButtons.get(i).setSelected(true);
            difficultyButtons.get(i).setUserData(i + 2);
            difficultyButtons.get(i).setDisable(true);
            difficultyButtons.get(i).setToggleGroup(aiOptions);
        }

        Text aiDifficulty = new Text("AI DIFFICULTY");
        aiDifficulty.setFont(new Font(18));

        aiOptionBox.getChildren().add(aiDifficulty);
        aiOptionBox.getChildren().addAll(difficultyButtons);

        return aiOptionBox;
    }

    private void setInterfaceActions() {
        whiteHumanOption.setOnAction(e -> {
            whiteNameField.setDisable(false);
            whiteNameField.setText("Player1");
            for (RadioButton x : difficultyButtons) if (!blackAiOption.isSelected()) x.setDisable(true);
        });
        whiteAiOption.setOnAction(e -> {
            whiteNameField.setDisable(true);
            whiteNameField.setText("CPU");
            for (RadioButton x : difficultyButtons) x.setDisable(false);
        });

        blackHumanOption.setOnAction(e -> {
            blackNameField.setDisable(false);
            blackNameField.setText("Player2");
            for (RadioButton x : difficultyButtons) if (!whiteAiOption.isSelected()) x.setDisable(true);
        });
        blackAiOption.setOnAction(e -> {
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
    }

    private Button createConfirmButton() {
        Button confirmSettings = new Button("Confirm");
        confirmSettings.setOnAction(e -> {
            boolean isWhiteAI = (boolean) whiteOptions.getSelectedToggle().getUserData();
            boolean isBlackAI = (boolean) blackOptions.getSelectedToggle().getUserData();
            int aiDepth = (int) aiOptions.getSelectedToggle().getUserData();

            String suffix;
            int rating;
            switch (aiDepth) {
                case 2: suffix = "Easy"; rating = 1200; break;
                case 3: suffix = "Intermediate"; rating = 1500; break;
                case 4: suffix = "Expert"; rating = 1800; break;
                case 5: suffix = "Experimental"; rating = 2000; break;
                default: suffix = "Error"; rating = 0;
            }

            String whiteUsername, blackUserName;
            Score scoreSystem = Score.getInstance();
            if (isWhiteAI) {
                whiteUsername = "CPU(" + suffix + ")";
                scoreSystem.addUsername(whiteUsername);
                scoreSystem.updateHighscore(whiteUsername, rating);
            } else {
                whiteUsername = whiteNameField.getText();
                scoreSystem.addUsername(whiteUsername);
            }
            if (isBlackAI) {
                blackUserName = "CPU(" + suffix + ")";
                scoreSystem.addUsername(blackUserName);
                scoreSystem.updateHighscore(blackUserName, rating);
            } else {
                blackUserName = blackNameField.getText().trim();
                scoreSystem.addUsername(blackUserName);
            }

            GameMode boardType = (GameMode) boardStateOptions.getSelectedToggle().getUserData();

            // Construct new game state manager with settings from start menu
            GameStateManager gameStateManager = new GameStateManager(whiteUsername, blackUserName, isWhiteAI, isBlackAI, aiDepth, boardType);
            parentGui.setGameManager(gameStateManager);
            parentGui.showGameScene();
        });

        return confirmSettings;
    }

    public Scene getMenuScene() {
        return new Scene(new StackPane(CircleAnimation.createCirclePane(sceneHeight, sceneWidth), menuParent));
    }
}
