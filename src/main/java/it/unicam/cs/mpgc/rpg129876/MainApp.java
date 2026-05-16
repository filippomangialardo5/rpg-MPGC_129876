package it.unicam.cs.mpgc.rpg129876;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label(" RPG Funzionante -  Matricola 129876");
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: green; -fx-font-weight: bold;");

        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 500, 300);

        stage.setTitle("Dungeon Explorer RPG - 129876");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}