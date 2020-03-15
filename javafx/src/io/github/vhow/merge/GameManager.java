package io.github.vhow.merge;

import javafx.animation.ParallelTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Random;

class GameManager {

    private static final int ROW = 4;
    private static final int COL = 4;
    private final TilePane mBackgroundTilePane;
    private final ScoreManager mScoreManager = new ScoreManager();
    private TilePane mTilePane;
    private Cell[][] mData = new Cell[ROW][COL];
    private long mCurrentScore;

    GameManager() {
        mTilePane = new TilePane();
        mTilePane.setPrefColumns(COL);
        mTilePane.setPrefRows(ROW);
        mBackgroundTilePane = new TilePane();
        mBackgroundTilePane.setPrefColumns(COL);
        mBackgroundTilePane.setPrefRows(ROW);
        for (int r = 0; r < ROW; r++) {
            for (int c = 0; c < COL; c++) {
                mData[r][c] = new Cell(false);
                mTilePane.getChildren().add(mData[r][c]);
                mData[r][c].refresh();
                mBackgroundTilePane.getChildren().add(new Cell(true));
            }
        }
        newGame();
    }

    private void newGame() {
        for (int r = 0; r < ROW; r++) {
            for (int c = 0; c < COL; c++) {
                mData[r][c].setNum(0);
                mData[r][c].refresh();
            }
        }
        newNum();
        newNum();
        mCurrentScore = 0;
        mScoreManager.updateScore(mCurrentScore);
    }

    private void dump() {
        for (int r = 0; r < ROW; r++) {
            for (int c = 0; c < COL; c++) {
                System.out.print(mData[r][c] + " ");
            }
            System.out.println();
        }
    }

    private void reLoad() {
        mTilePane.getChildren().clear();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                final Cell cell = mData[r][c];
                mTilePane.getChildren().add(cell);
                cell.refresh();
            }
        }
    }

    private Node buildGridView() {
        final StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(mBackgroundTilePane, mTilePane);

        final StackPane parent = new StackPane();

        final BackgroundFill fill = new BackgroundFill(
                Util.getGridBgColor(),
                new CornerRadii(10),
                null);
        final Background background = new Background(fill);
        parent.setBackground(background);

        parent.setPadding(new Insets(8));
        parent.getChildren().add(stackPane);

        return parent;
    }

    private Node buildNewGameView() {
        final BackgroundFill bg = new BackgroundFill(
                Util.getNewGameBgColor(), new CornerRadii(10), null);
        final StackPane newGame = new StackPane();
        final Label label = new Label("New Game");
        label.setFont(Font.font(16));
        label.setTextFill(Color.WHITE);
        newGame.setBackground(new Background(bg));
        newGame.setPadding(new Insets(8));
        newGame.getChildren().add(label);
        newGame.setOnMouseClicked(event -> handleNewGame());
        final HBox hBox = new HBox(newGame);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        return hBox;
    }

    private void handleNewGame() {
        newGame();
    }

    private Parent buildContent() {
        final Node newGame = buildNewGameView();
        final Node grid = buildGridView();

        final VBox root = new VBox();
        root.setSpacing(10);
        root.getChildren().addAll(mScoreManager.getScoreView(), newGame, grid);

        root.setPadding(new Insets(10));
        return root;
    }

    private void onKeyUp() {
        final ParallelTransition pt = new ParallelTransition();
        for (int c = 0; c < COL; c++) {
            for (int r = 1; r < ROW; r++) {
                final Cell key = mData[r][c];
                if (key.getNum() == 0) {
                    continue;
                }
                int step = 0;
                for (int tr = r; tr > 0; tr--) {
                    final Cell current = mData[tr][c];
                    final Cell above = mData[tr - 1][c];
                    if (above.getNum() == 0) {
                        swap(tr - 1, c, tr, c);
                        step++;
                    } else if (tryMerging(above, current)) {
                        step++;
                    }
                    pt.getChildren().addAll(Util.buildTranslateTransition(key, 0, -Cell.HEIGHT * step));
                }
            }
        }
        pt.play();
        pt.setOnFinished(event -> {
            reLoad();
            newNum();
        });
    }

    private boolean tryMerging(Cell target, Cell source) {
        if (target.getNum() != source.getNum()) {
            return false;
        }
        if (target.isMerged || source.isMerged) {
            return false;
        }
        final int num = target.getNum() << 1;
        target.setNum(num);
        source.setNum(0);
        mCurrentScore += num;
        mScoreManager.updateScore(mCurrentScore);
        target.isMerged = true;
        source.isMerged = true;
        return true;
    }

    private void onKeyDown() {
        final ParallelTransition pt = new ParallelTransition();
        for (int c = 0; c < COL; c++) {
            for (int r = 2; r >= 0; r--) {
                final Cell key = mData[r][c];
                if (key.getNum() == 0) {
                    continue;
                }
                int step = 0;
                for (int tr = r; tr < ROW - 1; tr++) {
                    final Cell current = mData[tr][c];
                    final Cell below = mData[tr + 1][c];
                    if (below.getNum() == 0) {
                        swap(tr, c, tr + 1, c);
                        step++;
                    } else if (tryMerging(below, current)) {
                        step++;
                    }
                }
                pt.getChildren().addAll(Util.buildTranslateTransition(key, 0, Cell.HEIGHT * step));
            }
        }
        pt.play();
        pt.setOnFinished(event -> {
            reLoad();
            newNum();
        });
    }

    private boolean isOver() {
        if (!isFull()) {
            return false;
        }

        for (int r = 0; r < ROW; r++) {
            Cell left = mData[r][0];
            for (int c = 1; c < COL; c++) {
                final Cell current = mData[r][c];
                if (current.getNum() == left.getNum()) {
                    return false;
                }
                left = current;
            }
        }

        for (int c = 0; c < COL; c++) {
            Cell above = mData[0][c];
            for (int r = 1; r < ROW; r++) {
                final Cell current = mData[r][c];
                if (current.getNum() == above.getNum()) {
                    return false;
                }
                above = current;
            }
        }

        return true;
    }

    private void showGameOverDialog() {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION, "Game Over");
        alert.getDialogPane().setHeaderText(null);
        alert.show();
    }

    private boolean isFull() {
        for (int r = 0; r < ROW; r++) {
            for (int c = 0; c < COL; c++) {
                if (mData[r][c].getNum() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void newNum() {
        if (isFull()) {
            return;
        }
        final Random random = new Random(System.currentTimeMillis());
        int count = 0;
        while (true) {
            final int id = random.nextInt(ROW * COL);
            final int r = id / ROW;
            final int c = id % COL;
            final Cell cell = mData[r][c];
            if (cell.getNum() != 0) {
                continue;
            }
            if (Math.random() > 0.8) {
                cell.setNum(4);
            } else {
                cell.setNum(2);
            }
            cell.refresh();
            Util.buildScaleTransition(cell).play();
            if (isFull() || (++count) > 0) {
                break;
            }
        }
    }

    private void swap(int r0, int c0, int r1, int c1) {
        final Cell tmp = mData[r0][c0];
        mData[r0][c0] = mData[r1][c1];
        mData[r1][c1] = tmp;
    }

    private void onKeyLeft() {
        final ParallelTransition pt = new ParallelTransition();
        for (int r = 0; r < ROW; r++) {
            for (int c = 1; c < COL; c++) {
                final Cell key = mData[r][c];
                if (key.getNum() == 0) {
                    continue;
                }
                int step = 0;
                for (int tc = c; tc > 0; tc--) {
                    final Cell current = mData[r][tc];
                    final Cell left = mData[r][tc - 1];
                    if (left.getNum() == 0) {
                        swap(r, tc - 1, r, tc);
                        step++;
                    } else if (tryMerging(left, current)) {
                        step++;
                    }
                }
                pt.getChildren().add(Util.buildTranslateTransition(key, -Cell.WIDTH * step, 0));
            }
        }
        pt.play();
        pt.setOnFinished(event -> {
            reLoad();
            newNum();
        });
    }

    private void onKeyRight() {
        final ParallelTransition pt = new ParallelTransition();
        for (int r = 0; r < ROW; r++) {
            for (int c = COL - 2; c >= 0; c--) {
                final Cell key = mData[r][c];
                if (key.getNum() == 0) {
                    continue;
                }
                int step = 0;
                for (int tc = c; tc < COL - 1; tc++) {
                    final Cell current = mData[r][tc];
                    final Cell right = mData[r][tc + 1];
                    if (right.getNum() == 0) {
                        swap(r, tc, r, tc + 1);
                        step++;
                    } else if (tryMerging(right, current)) {
                        step++;
                    }
                }
                pt.getChildren().add(Util.buildTranslateTransition(key, Cell.WIDTH * step, 0));
            }
        }
        pt.play();
        pt.setOnFinished(event -> {
            reLoad();
            newNum();
        });
    }

    Scene buildScene() {
        final Scene scene = new Scene(buildContent());
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            dump();
            if (isOver()) {
                showGameOverDialog();
            }
            switch (event.getCode()) {
                case UP:
                    System.out.println("up");
                    onKeyUp();
                    break;
                case DOWN:
                    System.out.println("down");
                    onKeyDown();
                    break;
                case LEFT:
                    System.out.println("<--");
                    onKeyLeft();
                    break;
                case RIGHT:
                    System.out.println("-->");
                    onKeyRight();
                    break;
            }
            dump();
        });
        return scene;
    }

}
