package it.unicam.cs.mpgc.rpg129876.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;

public class SimpleController {

    @FXML
    private Button startBtn;

    @FXML
    public void initialize() {
        System.out.println("SimpleController inizializzato!");

        if (startBtn != null) {
            startBtn.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Inizio");
                alert.setContentText("Gioco avviato!");
                alert.showAndWait();
            });
        }
    }
}