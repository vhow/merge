package io.github.vhow.merge;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class Cell extends StackPane {
    static final int WIDTH = 80;
    static final int HEIGHT = 80;

    private final Label mLabel = new Label();
    private final Rectangle mRect;
    boolean isMerged;
    private int num;

    Cell(boolean isBackground) {
        setPadding(new Insets(WIDTH >> 4));
        setAlignment(Pos.CENTER);

        mRect = new Rectangle(WIDTH, HEIGHT);
        mRect.setArcWidth(10);
        mRect.setArcHeight(10);
        if (isBackground) {
            mRect.setFill(Util.getEmptyColor());
        }
        getChildren().add(mRect);

        mLabel.setFont(Font.font(20));
        final DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(1);
        shadow.setOffsetX(1);
        shadow.setOffsetY(1);
        mLabel.setEffect(shadow);
        mLabel.setTextFill(Color.WHITE);

        getChildren().add(mLabel);
    }

    void refresh() {
        if (this.num == 0) {
            setOpacity(0);
        } else {
            setOpacity(1);
            mLabel.setText(String.valueOf(this.num));
        }
        if (isMerged) {
            Util.buildMergedScaleTransition(this).play();
        }
        isMerged = false;

        mRect.setFill(Util.getColor(this.num));
    }

    int getNum() {
        return num;
    }

    void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return String.format("%-4d", this.num);
    }
}
