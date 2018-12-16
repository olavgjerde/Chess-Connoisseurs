package gui.windows;

import board.GameMode;
import gui.extra.ResourceLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class RuleWindow {
    private Stage ruleStage;
    private GameMode gameMode;

    public RuleWindow(double pStageX, double pStageY, double parentWidth, double parentHeight, GameMode gameMode) {
        this.gameMode = gameMode;
        this.ruleStage = new Stage();
        ruleStage.initStyle(StageStyle.UNDECORATED);

        VBox rootBox = createRuleContent(parentWidth, parentHeight);
        ruleStage.setWidth(rootBox.getMaxWidth());
        ruleStage.setHeight(rootBox.getMaxHeight());
        ruleStage.setX(pStageX + parentWidth / 2 - ruleStage.getWidth() / 2);
        ruleStage.setY(pStageY + parentHeight / 2 - ruleStage.getHeight() / 2);
        //Window settings
        ruleStage.initModality(Modality.APPLICATION_MODAL);
        ruleStage.setResizable(false);
        ScrollPane scrollPane = new ScrollPane(rootBox);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        ruleStage.setScene(new Scene(scrollPane));
    }

    private VBox createRuleContent(double windowWidth, double windowHeight) {
        VBox rootBox = new VBox();
        rootBox.setSpacing(5);
        rootBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: radial-gradient(center 50% 50%, radius 180%, derive(darkslategray, 20%), black)");
        rootBox.setMaxHeight(windowHeight / 2);
        rootBox.setMaxWidth(windowWidth / 3);
        rootBox.setAlignment(Pos.CENTER);
        rootBox.setOnMouseClicked(event -> ruleStage.close());

        Text title = new Text("RULES");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        title.setFill(Color.WHITE);
        title.setTextAlignment(TextAlignment.CENTER);
        rootBox.getChildren().add(title);

        HBox ruleBox = new HBox();
        ruleBox.setAlignment(Pos.CENTER);
        ruleBox.setSpacing(5);
        rootBox.getChildren().add(ruleBox);

        VBox rulesVBox = new VBox(), rules = new VBox();
        ruleBox.getChildren().addAll(rulesVBox, rules);

        String text = ResourceLoader.getInstance().lightBrigade;
        if (gameMode.equals(GameMode.HORDE)) text = ResourceLoader.getInstance().horde;
        else if (gameMode.equals(GameMode.TUTOR)) text = ResourceLoader.getInstance().tutor;
        Text ruleText = new Text(text);
        ruleText.setTextAlignment(TextAlignment.CENTER);
        rulesVBox.getChildren().add(ruleText);

        //Change font color of text
        for (Node n : rulesVBox.getChildren()) {
            if (n instanceof Text) {
                ((Text) n).setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
                ((Text) n).setFill(Color.WHITE);
            }
        }

        return rootBox;
    }

    public void showRuleWindow() {
        ruleStage.show();
    }
}
