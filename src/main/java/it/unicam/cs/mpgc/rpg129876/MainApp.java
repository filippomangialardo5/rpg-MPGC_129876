package it.unicam.cs.mpgc.rpg129876;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlUrl = getClass().getResource("/fxml/main-view.fxml");
        Parent root = FXMLLoader.load(fxmlUrl);

        Scene scene = new Scene(root, 1200, 800);

        URL cssUrl = getClass().getResource("/css/application.css");
        scene.getStylesheets().add(cssUrl.toExternalForm());

        primaryStage.setTitle("Dungeon Explorer RPG - Matricola 129876");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}