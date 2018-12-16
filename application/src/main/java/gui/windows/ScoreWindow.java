package gui.windows;

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
import player.Score;

import java.util.ArrayList;

public class ScoreWindow {
    private Stage scoreStage;

    public ScoreWindow(double pStageX, double pStageY, double parentWidth, double parentHeight) {
        this.scoreStage = new Stage();
        scoreStage.initStyle(StageStyle.UNDECORATED);

        VBox rootBox = createHighscoreContent(parentWidth, parentHeight);
        scoreStage.setWidth(rootBox.getMaxWidth());
        scoreStage.setHeight(rootBox.getMaxHeight());
        scoreStage.setX(pStageX + parentWidth / 2 - scoreStage.getWidth() / 2);
        scoreStage.setY(pStageY + parentHeight / 2 - scoreStage.getHeight() / 2);
        //Window settings
        scoreStage.initModality(Modality.APPLICATION_MODAL);
        scoreStage.setResizable(false);
        ScrollPane scrollPane = new ScrollPane(rootBox);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scoreStage.setScene(new Scene(scrollPane));
        scoreStage.show();
    }

    private VBox createHighscoreContent(double windowWidth, double windowHeight) {
        VBox rootBox = new VBox();
        rootBox.setSpacing(5);
        rootBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: radial-gradient(center 50% 50%, radius 180%, derive(darkslategray, 20%), black)");
        rootBox.setMaxHeight(windowHeight / 2);
        rootBox.setMaxWidth(windowWidth / 3);
        rootBox.setAlignment(Pos.CENTER);
        rootBox.setOnMouseClicked(event -> scoreStage.close());

        Text title = new Text("HIGHSCORES");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        title.setFill(Color.WHITE);
        title.setTextAlignment(TextAlignment.CENTER);
        rootBox.getChildren().add(title);

        HBox listBox = new HBox();
        listBox.setAlignment(Pos.CENTER);
        listBox.setSpacing(5);
        rootBox.getChildren().add(listBox);

        VBox names = new VBox(), scores = new VBox(), record = new VBox();
        Text nameTitle = new Text("Name"), scoreTitle = new Text("Score"), recordTitle = new Text("Record");
        nameTitle.setUnderline(true);
        scoreTitle.setUnderline(true);
        recordTitle.setUnderline(true);
        names.getChildren().add(nameTitle);
        scores.getChildren().add(scoreTitle);
        record.getChildren().add(recordTitle);

        listBox.getChildren().addAll(names, scores, record);

        ArrayList<String> userNames = Score.getInstance().getScoreboard();
        for (int i = 0; i < userNames.size(); i++) {
            String userName = userNames.get(i);
            Text nameText = new Text(i + 1 + ": " + userName + " ");
            Text scoreText = new Text(Score.getInstance().getScore(userName) + " | ");
            Text recordText = new Text(Score.getInstance().getStats(userName));
            names.getChildren().add(nameText);
            scores.getChildren().add(scoreText);
            record.getChildren().add(recordText);
        }

        //Change font color of text in score table
        for (Node n : names.getChildren()) {
            if (n instanceof Text) {
                ((Text) n).setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
                ((Text) n).setFill(Color.WHITE);
            }
        }
        for (Node n : scores.getChildren()) {
            if (n instanceof Text) {
                ((Text) n).setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
                ((Text) n).setFill(Color.WHITE);
            }
        }
        for (Node n : record.getChildren()) {
            if (n instanceof Text) {
                ((Text) n).setFont(Font.font("Verdana", FontWeight.NORMAL, 15));
                ((Text) n).setFill(Color.WHITE);
            }
        }

        return rootBox;
    }

    public void showHighscoreWindow() {
        scoreStage.show();
    }
}
