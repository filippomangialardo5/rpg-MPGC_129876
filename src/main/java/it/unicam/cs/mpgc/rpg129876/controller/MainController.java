package it.unicam.cs.mpgc.rpg129876.controller;

import it.unicam.cs.mpgc.rpg129876.model.characters.Player;
import it.unicam.cs.mpgc.rpg129876.model.items.Item;
import it.unicam.cs.mpgc.rpg129876.model.world.Direction;
import it.unicam.cs.mpgc.rpg129876.model.world.Room;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MainController {

    private GameController gameController;
    private Timeline combatAnimation;

    // UI Components - Schermate
    @FXML private VBox startScreen;
    @FXML private VBox gameScreen;
    @FXML private Button startGameBtn;

    // UI Components - Statistiche
    @FXML private Label playerNameLabel;
    @FXML private Label playerClassLabel;
    @FXML private ProgressBar healthBar;
    @FXML private Label healthLabel;
    @FXML private Label levelLabel;
    @FXML private Label expLabel;
    @FXML private Label goldLabel;
    @FXML private Label attackLabel;
    @FXML private Label defenseLabel;

    // UI Components - Mappa
    @FXML private GridPane mapGrid;
    @FXML private TextArea roomDescriptionArea;

    // UI Components - Combattimento
    @FXML private VBox combatDialog;
    @FXML private Label combatTitle;
    @FXML private Label combatEnemyLabel;
    @FXML private ProgressBar enemyHealthBar;
    @FXML private Label enemyHealthLabel;
    @FXML private Label combatMessage;
    @FXML private Button attackBtn;
    @FXML private Button itemBtn;
    @FXML private Button fleeBtn;

    // UI Components - Altro
    @FXML private VBox inventoryList;
    @FXML private ListView<String> messageListView;

    @FXML
    public void initialize() {
        gameController = new GameController();

        // Binding delle proprietà
        bindPlayerStats();
        setupMessageListener();

        // Nascondi pannelli di gioco all'inizio
        gameScreen.setVisible(false);
        gameScreen.setManaged(false);
        combatDialog.setVisible(false);
        combatDialog.setManaged(false);

        // Messaggio iniziale
        addGameMessage("✨ Benvenuto in Dungeon Explorer RPG!");
        addGameMessage("🖱️ Clicca 'Inizia Avventura' per iniziare");
    }

    @FXML
    private void onStartGameFromButton() {
        showCharacterCreation();
    }

    private void bindPlayerStats() {
        gameController.currentPlayerProperty().addListener((obs, old, newPlayer) -> {
            if (newPlayer != null) {
                updatePlayerUI(newPlayer);
            }
        });

        gameController.inCombatProperty().addListener((obs, old, inCombat) -> {
            if (inCombat) {
                showCombatDialog();
            } else {
                hideCombatDialog();
            }
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
                itemBtn.setOnAction(e -> {
                    gameController.useItem(item);
                    addGameMessage("🧪 Usato: " + item.getName());
                    updateInventory();
                });
                itemBtn.getStyleClass().add("inventory-button");
                inventoryList.getChildren().add(itemBtn);
            }
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
                mapGrid.add(cell, x, y);
            }
        }
    }

    private void setupMessageListener() {
        messageListView.setItems(gameController.getGameMessages());
    }

    private void addGameMessage(String message) {
        // Il GameController già aggiunge timestamp, quindi aggiungiamo solo se necessario
        Platform.runLater(() -> {
            // Forza refresh della lista
            messageListView.scrollTo(0);
        });
    }

    private void showCharacterCreation() {
        // Nascondi schermata iniziale
        startScreen.setVisible(false);
        startScreen.setManaged(false);

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
                showGameUI();
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

    private void showGameUI() {
        gameScreen.setVisible(true);
        gameScreen.setManaged(true);

        updatePlayerUI(gameController.getPlayer());
        updateInventory();
        updateMap();
        roomDescriptionArea.setText(
                gameController.getCurrentRoom().getName() + "\n" +
                        gameController.getCurrentRoom().getDescription()
        );

        addGameMessage("🏰 La tua avventura ha inizio!");
        addGameMessage("📍 Ti trovi in: " + gameController.getCurrentRoom().getName());
    }

    private void showCombatDialog() {
        combatDialog.setVisible(true);
        combatDialog.setManaged(true);

        // Animazione di entrata
        combatDialog.setScaleX(0);
        combatDialog.setScaleY(0);
        ScaleTransition st = new ScaleTransition(Duration.millis(300), combatDialog);
        st.setToX(1);
        st.setToY(1);
        st.play();

        updateCombatUI();
    }

    private void updateCombatUI() {
        if (gameController.getPlayer() != null && gameController.isInCombat()) {
            var combat = gameController.getCurrentCombat();
            if (combat != null) {
                var enemy = combat.getEnemy();
                combatEnemyLabel.setText("👹 " + enemy.getName());
                enemyHealthBar.setProgress((double) enemy.getHp() / enemy.getMaxHp());
                enemyHealthLabel.setText(enemy.getHp() + "/" + enemy.getMaxHp());
            }
        }
    }

    private void hideCombatDialog() {
        if (combatAnimation != null) {
            combatAnimation.stop();
        }

        // Animazione di uscita
        ScaleTransition st = new ScaleTransition(Duration.millis(200), combatDialog);
        st.setToX(0);
        st.setToY(0);
        st.setOnFinished(e -> {
            combatDialog.setVisible(false);
            combatDialog.setManaged(false);
            combatMessage.setText("");
        });
        st.play();

        updateMap();
        updateInventory();
        updatePlayerUI(gameController.getPlayer());
    }

    private void showCombatMessage(String message, boolean isPlayerAction) {
        combatMessage.setText(message);
        combatMessage.setStyle("-fx-text-fill: " + (isPlayerAction ? "#a0f0a0" : "#ffaa66") + "; -fx-font-size: 14px;");

        // Animazione del messaggio
        FadeTransition ft = new FadeTransition(Duration.millis(500), combatMessage);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        // Flash sulla barra della vita del nemico
        if (!isPlayerAction) {
            FlashTransition flash = new FlashTransition(enemyHealthBar);
            flash.play();
        }

        updateCombatUI();
        addGameMessage(message);
    }

    // Animazione personalizzata per il flash
    private class FlashTransition extends Transition {
        private final ProgressBar bar;
        private final String originalStyle;

        public FlashTransition(ProgressBar bar) {
            this.bar = bar;
            this.originalStyle = bar.getStyle();
            setCycleDuration(Duration.millis(300));
        }

        @Override
        protected void interpolate(double frac) {
            if (frac < 0.5) {
                bar.setStyle("-fx-accent: #ff4444;");
            } else {
                bar.setStyle(originalStyle);
            }
        }
    }

    // Azioni movimento
    @FXML
    private void onMoveNorth() {
        if (gameController.getPlayer() != null && gameController.isPlayerAlive()) {
            gameController.move(Direction.NORTH);
            updateMap();
            updateInventory();
            updatePlayerUI(gameController.getPlayer());
        }
    }

    @FXML
    private void onMoveSouth() {
        if (gameController.getPlayer() != null && gameController.isPlayerAlive()) {
            gameController.move(Direction.SOUTH);
            updateMap();
            updateInventory();
            updatePlayerUI(gameController.getPlayer());
        }
    }

    @FXML
    private void onMoveEast() {
        if (gameController.getPlayer() != null && gameController.isPlayerAlive()) {
            gameController.move(Direction.EAST);
            updateMap();
            updateInventory();
            updatePlayerUI(gameController.getPlayer());
        }
    }

    @FXML
    private void onMoveWest() {
        if (gameController.getPlayer() != null && gameController.isPlayerAlive()) {
            gameController.move(Direction.WEST);
            updateMap();
            updateInventory();
            updatePlayerUI(gameController.getPlayer());
        }
    }

    // Azioni combattimento
    @FXML
    private void onAttack() {
        if (gameController.getPlayer() != null && gameController.isInCombat()) {
            gameController.playerAttack();
            updateCombatUI();
            updatePlayerUI(gameController.getPlayer());
        }
    }

    @FXML
    private void onUseItem() {
        if (gameController.getPlayer() != null) {
            // Mostra dialog per scegliere item
            ChoiceDialog<Item> dialog = new ChoiceDialog<>(null, gameController.getPlayer().getInventory());
            dialog.setTitle("Usa Oggetto");
            dialog.setHeaderText("Scegli un oggetto da usare");

            dialog.showAndWait().ifPresent(item -> {
                gameController.useItem(item);
                updateInventory();
                updatePlayerUI(gameController.getPlayer());
                if (gameController.isInCombat()) {
                    updateCombatUI();
                }
            });
        }
    }

    @FXML
    private void onFlee() {
        if (gameController.getPlayer() != null && gameController.isInCombat()) {
            gameController.flee();
            updateCombatUI();
        }
    }

    // Azioni menu
    @FXML
    private void onNewGame() {
        showCharacterCreation();
    }

    @FXML
    private void onExit() {
        Platform.exit();
    }

    @FXML
    private void onHelp() {
        showHelpDialog();
    }

    @FXML
    private void onAbout() {
        showAboutDialog();
    }

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