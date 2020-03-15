package io.github.vhow.merge;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("JavaFX 2048");
        stage.setScene(new GameManager().buildScene());
        stage.setResizable(false);
        stage.show();
    }
}
