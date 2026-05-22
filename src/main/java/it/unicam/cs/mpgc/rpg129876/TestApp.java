package it.unicam.cs.mpgc.rpg129876;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: #1a1a2e; -fx-alignment: center; -fx-padding: 50;");

        Label title = new Label("🏰 TEST DUNGEON RPG 🏰");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #e94560;");

        Button btn = new Button("✨ TEST ✨");
        btn.setStyle("-fx-font-size: 20px; -fx-padding: 15; -fx-background-color: #e94560; -fx-text-fill: white;");
        btn.setOnAction(e -> {
            title.setText("✅ FUNZIONA!");
        });

        root.getChildren().addAll(title, btn);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Test - Dungeon RPG");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}