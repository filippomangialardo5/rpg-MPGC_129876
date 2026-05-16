package it.unicam.cs.mpgc.rpg129876.controller;

import javafx.fxml.FXML;
import javafx.application.Platform;

public class MainController {

    @FXML
    private void onStartGame() {
        System.out.println("Start Game cliccato!");
        // Qui poi metterai la logica per iniziare il gioco
    }

    @FXML
    private void onExit() {
        Platform.exit();
    }
}