package it.unicam.cs.mpgc.rpg129876;

import it.unicam.cs.mpgc.rpg129876.controller.MainController;
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
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        // Ottieni il controller e passagli la scena per i key bindings
        MainController controller = loader.getController();
        Scene scene = new Scene(root, 1100, 700);
        controller.setScene(scene);

        URL cssUrl = getClass().getResource("/css/application.css");
        scene.getStylesheets().add(cssUrl.toExternalForm());

        primaryStage.setTitle("Shadow of the Dungeon - Matricola 129876");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}