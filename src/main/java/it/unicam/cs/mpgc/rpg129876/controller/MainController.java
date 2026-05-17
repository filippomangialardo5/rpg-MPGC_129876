package it.unicam.cs.mpgc.rpg129876.controller;

import it.unicam.cs.mpgc.rpg129876.model.characters.Player;
import it.unicam.cs.mpgc.rpg129876.model.items.Item;
import it.unicam.cs.mpgc.rpg129876.model.world.Direction;
import it.unicam.cs.mpgc.rpg129876.model.world.Room;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {

    private GameController gameController;

    // UI Components
    @FXML private Label playerNameLabel;
    @FXML private Label playerClassLabel;
    @FXML private ProgressBar healthBar;
    @FXML private Label healthLabel;
    @FXML private Label levelLabel;
    @FXML private Label expLabel;
    @FXML private Label goldLabel;
    @FXML private Label attackLabel;
    @FXML private Label defenseLabel;
    @FXML private TextArea roomDescriptionArea;
    @FXML private GridPane mapGrid;
    @FXML private VBox inventoryList;
    @FXML private ListView<String> messageListView;
    @FXML private Button attackBtn;
    @FXML private Button useItemBtn;
    @FXML private Button fleeBtn;

    @FXML
    public void initialize() {
        gameController = new GameController();

        // Binding delle proprietà
        bindPlayerStats();
        setupMessageListener();

        // Non chiamare showCharacterCreation() qui!
        // Invece, mostra un messaggio che chiede di iniziare
        roomDescriptionArea.setText("✨ Benvenuto in Dungeon Explorer RPG!\n\n" +
                "Clicca su 'Gioco' → 'Nuova Partita' per iniziare la tua avventura!");
    }

    private void bindPlayerStats() {
        gameController.currentPlayerProperty().addListener((obs, old, newPlayer) -> {
            if (newPlayer != null) {
                updatePlayerUI(newPlayer);
            }
        });

        gameController.inCombatProperty().addListener((obs, old, inCombat) -> {
            updateCombatUI(inCombat);
        });

        gameController.roomDescriptionProperty().addListener((obs, old, desc) -> {
            roomDescriptionArea.setText(desc);
            updateMap();
        });
    }

    private void updatePlayerUI(Player player) {
        playerNameLabel.setText(player.getName());
        playerClassLabel.setText(player.getCharacterClass());

        healthBar.progressProperty().bind(
                player.hpProperty().divide(player.maxHpProperty())
        );
        healthLabel.textProperty().bind(
                player.hpProperty().asString().concat("/").concat(player.maxHpProperty().asString())
        );
        levelLabel.setText("⭐ Livello: " + player.getLevel());
        expLabel.setText("📈 Esperienza: " + player.getExperience());
        goldLabel.setText("💰 Oro: " + player.getGold());
        attackLabel.setText("⚔ Attacco: " + player.getAttack());
        defenseLabel.setText("🛡 Difesa: " + player.getDefense());

        updateInventory();
    }

    private void updateInventory() {
        inventoryList.getChildren().clear();
        if (gameController.getPlayer() != null) {
            for (Item item : gameController.getPlayer().getInventory()) {
                Button itemBtn = new Button(item.getIcon() + " " + item.getName());
                itemBtn.setOnAction(e -> gameController.useItem(item));
                itemBtn.getStyleClass().add("inventory-button");
                inventoryList.getChildren().add(itemBtn);
            }
        }
    }

    private void updateCombatUI(boolean inCombat) {
        attackBtn.setVisible(inCombat);
        fleeBtn.setVisible(inCombat);

        if (!inCombat && gameController.isPlayerAlive()) {
            updateMap();
        }
    }

    private void updateMap() {
        if (gameController.getDungeon() == null) return;

        mapGrid.getChildren().clear();
        int width = gameController.getDungeonWidth();
        int height = gameController.getDungeonHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Room room = gameController.getRoomAt(x, y);
                Button cell = new Button();
                cell.setMinSize(45, 45);
                cell.getStyleClass().add("map-cell");

                if (room == gameController.getCurrentRoom()) {
                    cell.setText("🗺️");
                    cell.getStyleClass().add("map-cell-current");
                } else if (room.isExplored()) {
                    if (room.hasEnemy()) {
                        cell.setText("👹");
                    } else if (room.hasTreasures()) {
                        cell.setText("💰");
                    } else {
                        cell.setText("⬜");
                    }
                    cell.getStyleClass().add("map-cell-visited");
                } else {
                    cell.setText("❓");
                    cell.getStyleClass().add("map-cell-hidden");
                }

                final int finalX = x;
                final int finalY = y;
                cell.setOnAction(e -> {
                    // Optional: teleport per debugging (puoi rimuovere)
                    // moveToRoom(finalX, finalY);
                });

                mapGrid.add(cell, x, y);
            }
        }
    }

    private void setupMessageListener() {
        messageListView.setItems(gameController.getGameMessages());
    }

    private void showCharacterCreation() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuova Avventura");
        dialog.setHeaderText("Crea il tuo eroe");

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new javafx.geometry.Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Nome dell'eroe");

        ComboBox<String> classBox = new ComboBox<>();
        classBox.getItems().addAll("Warrior", "Mage", "Rogue");
        classBox.setValue("Warrior");

        Label classDesc = new Label();
        classDesc.setStyle("-fx-text-fill: #e0e0e0;");
        classBox.setOnAction(e -> {
            switch(classBox.getValue()) {
                case "Warrior": classDesc.setText("💪 Guerriero: Alto HP, buona difesa"); break;
                case "Mage": classDesc.setText("🔮 Mago: Alto attacco, bassa difesa"); break;
                case "Rogue": classDesc.setText("🗡️ Ladro: Equilibrato, alta agilità"); break;
            }
        });
        classDesc.setText("💪 Guerriero: Alto HP, buona difesa");

        Button startBtn = new Button("Inizia Avventura!");
        startBtn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-weight: bold;");
        startBtn.setOnAction(e -> {
            if (!nameField.getText().isEmpty() && classBox.getValue() != null) {
                gameController.startNewGame(nameField.getText(), classBox.getValue());
                dialog.close();
                updateUIAfterStart();
            }
        });

        content.getChildren().addAll(
                new Label("Nome:"), nameField,
                new Label("Classe:"), classBox, classDesc,
                new Separator(),
                startBtn
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.showAndWait();
    }

    private void updateUIAfterStart() {
        if (gameController.getPlayer() != null) {
            updatePlayerUI(gameController.getPlayer());
            updateInventory();
            updateMap();
            roomDescriptionArea.setText(
                    gameController.getCurrentRoom().getName() + "\n" +
                            gameController.getCurrentRoom().getDescription()
            );
        }
    }

    // Azioni dei bottoni
    @FXML
    private void onMoveNorth() {
        if (gameController.getPlayer() != null && gameController.isPlayerAlive()) {
            gameController.move(Direction.NORTH);
            updateMap();
            updateInventory();
            updatePlayerUI(gameController.getPlayer());
        } else {
            showNewGamePrompt();
        }
    }

    @FXML
    private void onMoveSouth() {
        if (gameController.getPlayer() != null && gameController.isPlayerAlive()) {
            gameController.move(Direction.SOUTH);
            updateMap();
            updateInventory();
            updatePlayerUI(gameController.getPlayer());
        } else {
            showNewGamePrompt();
        }
    }

    @FXML
    private void onMoveEast() {
        if (gameController.getPlayer() != null && gameController.isPlayerAlive()) {
            gameController.move(Direction.EAST);
            updateMap();
            updateInventory();
            updatePlayerUI(gameController.getPlayer());
        } else {
            showNewGamePrompt();
        }
    }

    @FXML
    private void onMoveWest() {
        if (gameController.getPlayer() != null && gameController.isPlayerAlive()) {
            gameController.move(Direction.WEST);
            updateMap();
            updateInventory();
            updatePlayerUI(gameController.getPlayer());
        } else {
            showNewGamePrompt();
        }
    }

    private void showNewGamePrompt() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Nuova Partita");
        alert.setHeaderText("Nessuna partita in corso");
        alert.setContentText("Clicca su 'Gioco' → 'Nuova Partita' per iniziare l'avventura!");
        alert.showAndWait();
    }

    @FXML private void onAttack() { gameController.playerAttack(); updateInventory(); }
    @FXML private void onUseItem() { /* Gestito dai bottoni inventario */ }
    @FXML private void onFlee() { gameController.flee(); updateMap(); }

    @FXML private void onNewGame() { showCharacterCreation(); }
    @FXML private void onExit() { Platform.exit(); }
    @FXML private void onHelp() { showHelpDialog(); }
    @FXML private void onAbout() { showAboutDialog(); }

    private void showHelpDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Comandi");
        alert.setHeaderText("🎮 Come giocare");
        alert.setContentText("""
            🗺️ Muoviti usando i bottoni direzionali
            ⚔ Attacca i nemici per guadagnare XP e oro
            🧪 Usa pozioni per curarti
            🏃 Fuggì se il combattimento è troppo difficile
            💰 Esplora tutte le stanze per trovare tesori!
            👑 Sconfiggi il Drago nell'angolo in basso a destra per vincere!
            """);
        alert.showAndWait();
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informazioni");
        alert.setHeaderText("Dungeon Explorer RPG");
        alert.setContentText("""
            Versione: 1.0
            Matricola: 129876
            Corso: Metodologie di Programmazione
            Anno: 2025/26
            
            Un gioco di ruolo a turni con esplorazione in dungeon.
            """);
        alert.showAndWait();
    }
}