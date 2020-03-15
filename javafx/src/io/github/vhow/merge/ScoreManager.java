package io.github.vhow.merge;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

class ScoreManager {

    private final StackPane mRoot;

    private final Label mScoreLabel;

    ScoreManager() {
        final Background bg = new Background(new BackgroundFill(Util.getScoreBgColor(),
                new CornerRadii(10), null));
        final VBox scoreContainer = new VBox();
        scoreContainer.setAlignment(Pos.CENTER);
        scoreContainer.setBackground(bg);
        scoreContainer.setPadding(new Insets(4, 20, 4, 20));
        mScoreLabel = new Label("0");
        mScoreLabel.setFont(Font.font(20));
        mScoreLabel.setTextFill(Color.WHITE);
        final Label label = new Label("SCORE");
        label.setFont(Font.font(16));
        label.setTextFill(Color.WHITE);
        scoreContainer.getChildren().addAll(label, mScoreLabel);
        mRoot = new StackPane(scoreContainer);
    }

    void updateScore(long score) {
        mScoreLabel.setText(String.valueOf(score));
    }


    Node getScoreView() {
        return mRoot;
    }

}
