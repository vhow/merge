package io.github.vhow.merge;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

class Util {

    private static Map<Integer, Color> sNumColorMap = new HashMap<>();
    private static Color sEmptyBgColor = Color.web("rgba(238, 228, 218, 0.35)");
    private static Color sGridBgColor = Color.web("#bbada0");
    private static Color sNewGameColor = Color.web("#8f7a66");
    private static Color sScoreBgColor = Color.web("#bbada0");

    static {
        sNumColorMap.put(2, Color.web("#eee4da"));
        sNumColorMap.put(4, Color.web("#ede0c8"));
        sNumColorMap.put(8, Color.web("#f2b179"));
        sNumColorMap.put(16, Color.web("#f59563"));
        sNumColorMap.put(32, Color.web("#f67c5f"));
        sNumColorMap.put(64, Color.web("#f65e3b"));
        sNumColorMap.put(128, Color.web("#edcf72"));
        sNumColorMap.put(256, Color.web("#edcc61"));
        sNumColorMap.put(512, Color.web("#edc850"));
        sNumColorMap.put(1024, Color.web("#edc53f"));
        sNumColorMap.put(2048, Color.web("#edc22e"));
        sNumColorMap.put(4096, Color.web("#eee4da"));
        sNumColorMap.put(8192, Color.web("#eee4da"));
    }

    static ScaleTransition buildScaleTransition(Node node) {
        final ScaleTransition scale = new ScaleTransition(Duration.millis(100), node);
        scale.setFromX(0.618);
        scale.setFromY(0.618);
        scale.setToX(1);
        scale.setToY(1);
        return scale;
    }

    static ScaleTransition buildMergedScaleTransition(Node node) {
        final ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
        scale.setFromX(0.618);
        scale.setFromY(0.618);
        scale.setToX(1.2);
        scale.setToY(1.2);
        scale.setOnFinished(event -> {
            node.setScaleX(1);
            node.setScaleY(1);
        });
        return scale;
    }

    static TranslateTransition buildTranslateTransition(Node node, int x, int y) {
        final TranslateTransition tr = new TranslateTransition(Duration.millis(100), node);
        tr.setByX(x);
        tr.setByY(y);
        tr.setOnFinished(event -> {
            node.setTranslateX(0);
            node.setTranslateY(0);
        });
        return tr;
    }

    static Color getColor(int num) {
        if (sNumColorMap.containsKey(num)) {
            return sNumColorMap.get(num);
        }
        return Color.LIGHTGRAY;
    }

    static Color getEmptyColor() {
        return sEmptyBgColor;
    }

    static Color getGridBgColor() {
        return sGridBgColor;
    }

    static Color getNewGameBgColor() {
        return sNewGameColor;
    }

    static Color getScoreBgColor() {
        return sScoreBgColor;
    }

}
