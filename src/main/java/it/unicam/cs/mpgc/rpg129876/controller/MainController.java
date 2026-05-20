package it.unicam.cs.mpgc.rpg129876.controller;

import it.unicam.cs.mpgc.rpg129876.model.characters.Player;
import it.unicam.cs.mpgc.rpg129876.model.items.HealthPotion;
import it.unicam.cs.mpgc.rpg129876.model.items.Item;
import it.unicam.cs.mpgc.rpg129876.model.world.Direction;
import it.unicam.cs.mpgc.rpg129876.model.world.Room;
import it.unicam.cs.mpgc.rpg129876.utils.ImageLoader;
import it.unicam.cs.mpgc.rpg129876.utils.ImageManager;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.ScaleTransition;
import javafx.scene.layout.HBox;

public class MainController {

    private GameController gameController;

    // UI Components - Schermate
    @FXML private VBox startScreen;
    @FXML private BorderPane gameScreen;
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
    @FXML private VBox combatPanel;
    @FXML private Label combatStatusLabel;
    @FXML private ProgressBar playerCombatHealthBar;
    @FXML private Label playerCombatHealthLabel;
    @FXML private Label enemyNameLabel;
    @FXML private ProgressBar enemyHealthBar;
    @FXML private Label enemyHealthLabel;
    @FXML private Label combatMessage;
    @FXML private Button attackBtn;
    @FXML private Button useItemBtn;
    @FXML private Button fleeBtn;

    // UI Components - Altro
    @FXML private VBox inventoryList;
    @FXML private ListView<String> messageListView;

    // ImageView per le immagini
    @FXML private ImageView playerImageView;
    @FXML private ImageView playerCombatImageView;
    @FXML private ImageView enemyImageView;
    @FXML private ImageView mapBackground;


    @FXML
    public void testButtons() {
        System.out.println("=== TEST BUTTONS ===");
        System.out.println("attackBtn: " + attackBtn);
        System.out.println("useItemBtn: " + useItemBtn);
        System.out.println("fleeBtn: " + fleeBtn);
        System.out.println("combatPanel: " + combatPanel);

        if (attackBtn != null) {
            attackBtn.setVisible(true);
            attackBtn.setText("✅ TEST");
        }
    }

    private Scene currentScene;

    @FXML
    public void initialize() {
        gameController = new GameController();

        bindPlayerStats();
        setupMessageListener();

        // Nascondi schermata di gioco all'inizio
        gameScreen.setVisible(false);
        gameScreen.setManaged(false);

        addGameMessage("✨ Benvenuto in Dungeon Explorer RPG!");
        addGameMessage("🖱️ Clicca 'Inizia Avventura' per iniziare");
    }

    public void setScene(Scene scene) {
        this.currentScene = scene;
        setupKeyBindings();
    }

    private void setupKeyBindings() {
        if (currentScene == null) return;

        currentScene.setOnKeyPressed(event -> {
            if (!gameScreen.isVisible() || gameController.getPlayer() == null) return;
            if (gameController.isInCombat()) return;

            KeyCode code = event.getCode();

            // WASD
            if (code == KeyCode.W || code == KeyCode.UP) {
                onMoveNorth();
            } else if (code == KeyCode.S || code == KeyCode.DOWN) {
                onMoveSouth();
            } else if (code == KeyCode.A || code == KeyCode.LEFT) {
                onMoveWest();
            } else if (code == KeyCode.D || code == KeyCode.RIGHT) {
                onMoveEast();
            }
        });
    }

    @FXML
    private void onStartGameFromButton() {
        showCharacterCreation();
    }

    private void bindPlayerStats() {
        gameController.currentPlayerProperty().addListener((obs, old, newPlayer) -> {
            if (newPlayer != null) {
                Platform.runLater(() -> updatePlayerUI(newPlayer));
            }
        });

        gameController.inCombatProperty().addListener((obs, old, inCombat) -> {
            Platform.runLater(() -> {
                if (inCombat) {
                    enableCombatMode();
                } else {
                    disableCombatMode();
                }
            });
        });

        gameController.roomDescriptionProperty().addListener((obs, old, desc) -> {
            Platform.runLater(() -> {
                roomDescriptionArea.setText(desc);
                updateMap();
            });
        });
    }

    private void updatePlayerUI(Player player) {
        playerNameLabel.setText(player.getName());
        playerClassLabel.setText(player.getCharacterClass());

        healthBar.progressProperty().unbind();
        healthBar.progressProperty().bind(
                player.hpProperty().divide(player.maxHpProperty())
        );
        healthLabel.textProperty().unbind();
        healthLabel.textProperty().bind(
                player.hpProperty().asString().concat("/").concat(player.maxHpProperty().asString())
        );

        levelLabel.setText("⭐ Livello: " + player.getLevel());
        expLabel.setText("📈 Esperienza: " + player.getExperience() + "/" + (100 + (player.getLevel() - 1) * 50));
        goldLabel.setText("💰 Oro: " + player.getGold());
        attackLabel.setText("⚔ Attacco: " + player.getAttack());
        defenseLabel.setText("🛡 Difesa: " + player.getDefense());

        updateInventory();
        updateCombatUI();
    }

    private void updateCombatUI() {
        if (gameController.getPlayer() != null) {
            Player p = gameController.getPlayer();
            playerCombatHealthBar.setProgress((double) p.getHp() / p.getMaxHp());
            playerCombatHealthLabel.setText(p.getHp() + "/" + p.getMaxHp());
        }

        if (gameController.isInCombat() && gameController.getCurrentCombat() != null) {
            var enemy = gameController.getCurrentCombat().getEnemy();
            enemyNameLabel.setText("👹 " + enemy.getName());
            enemyHealthBar.setProgress((double) enemy.getHp() / enemy.getMaxHp());
            enemyHealthLabel.setText(enemy.getHp() + "/" + enemy.getMaxHp());

            // Aggiorna anche l'icona del nemico nel pannello combattimento
            String enemyIcon = getEnemyIcon(enemy.getName());
            enemyNameLabel.setText(enemyIcon + " " + enemy.getName());

            // Animazione se il nemico ha pochi HP
            if (enemy.getHp() < enemy.getMaxHp() / 3) {
                enemyHealthBar.setStyle("-fx-accent: #ff4444;");
            } else {
                enemyHealthBar.setStyle("");
            }
        }
    }

    private void enableCombatMode() {
        combatPanel.setVisible(true);
        combatPanel.setManaged(true);

        attackBtn.setVisible(true);
        attackBtn.setManaged(true);
        attackBtn.setDisable(false);
        attackBtn.setOpacity(1.0);

        useItemBtn.setVisible(true);
        useItemBtn.setManaged(true);
        useItemBtn.setDisable(false);
        useItemBtn.setOpacity(1.0);

        fleeBtn.setVisible(true);
        fleeBtn.setManaged(true);
        fleeBtn.setDisable(false);
        fleeBtn.setOpacity(1.0);

        // Stampa di debug per conferma
        System.out.println("=== BOTTONI COMBATTIMENTO ATTIVATI ===");
        System.out.println("attackBtn visible: " + attackBtn.isVisible());
        System.out.println("useItemBtn visible: " + useItemBtn.isVisible());
        System.out.println("fleeBtn visible: " + fleeBtn.isVisible());

        // DEBUG: verifica che i bottoni esistano
        System.out.println("=== ENABLE COMBAT MODE ===");
        System.out.println("attackBtn is null? " + (attackBtn == null));
        System.out.println("useItemBtn is null? " + (useItemBtn == null));
        System.out.println("fleeBtn is null? " + (fleeBtn == null));
        System.out.println("combatPanel visible: " + combatPanel.isVisible());

        // Forza i bottoni a essere visibili
        if (attackBtn != null) {
            attackBtn.setVisible(true);
            attackBtn.setManaged(true);
            attackBtn.setDisable(false);
        }
        if (useItemBtn != null) {
            useItemBtn.setVisible(true);
            useItemBtn.setManaged(true);
            useItemBtn.setDisable(false);
        }
        if (fleeBtn != null) {
            fleeBtn.setVisible(true);
            fleeBtn.setManaged(true);
            fleeBtn.setDisable(false);
        }

        // Carica l'immagine del nemico
        if (gameController.getCurrentCombat() != null) {
            String enemyName = gameController.getCurrentCombat().getEnemy().getName();
            loadEnemyImage(enemyName);
        }

        updateCombatUI();

        if (gameController.getCurrentCombat() != null) {
            String enemyName = gameController.getCurrentCombat().getEnemy().getName();
            combatMessage.setText("⚔️ UN " + enemyName.toUpperCase() + " SELVAGGIO APPARE! ⚔️\nScegli la tua azione!");
            addGameMessage("⚔️ INIZIA COMBATTIMENTO contro " + enemyName + "!");
        }
    }

    private void disableCombatMode() {
        combatPanel.setVisible(false);
        combatPanel.setManaged(false);
        updateMap();
        updateInventory();
        updatePlayerUI(gameController.getPlayer());
    }

    private void updateInventory() {
        inventoryList.getChildren().clear();
        if (gameController.getPlayer() != null) {
            int potionCount = 0;
            for (Item item : gameController.getPlayer().getInventory()) {
                if (item instanceof HealthPotion) {
                    potionCount += ((HealthPotion) item).getQuantity();
                }
            }

            if (potionCount > 0) {
                HBox potionBox = new HBox(10);
                potionBox.setAlignment(Pos.CENTER_LEFT);

                // Immagine pozione (opzionale - se non c'è usa solo testo)
                ImageView potionImg = null;
                try {
                    potionImg = new ImageView(ImageLoader.getPotionImage());
                    if (potionImg.getImage() != null) {
                        potionImg.setFitWidth(30);
                        potionImg.setFitHeight(30);
                        potionBox.getChildren().add(potionImg);
                    }
                } catch (Exception e) {
                    // Immagine non disponibile, usa solo testo
                }

                Button potionBtn = new Button("🧪 Pozione x" + potionCount);
                potionBtn.setOnAction(e -> {
                    for (Item item : gameController.getPlayer().getInventory()) {
                        if (item instanceof HealthPotion) {
                            gameController.useItem(item);
                            addGameMessage("🧪 Usata una pozione curativa!");
                            updateInventory();
                            updatePlayerUI(gameController.getPlayer());
                            if (gameController.isInCombat()) {
                                updateCombatUI();
                                combatMessage.setText("🧪 POZIONE USATA! Hai recuperato 30 HP!");
                            }
                            break;
                        }
                    }
                });
                potionBtn.getStyleClass().add("inventory-button");

                if (potionImg != null && potionImg.getImage() != null) {
                    potionBox.getChildren().add(potionBtn);
                } else {
                    potionBox.getChildren().add(potionBtn);
                }
                inventoryList.getChildren().add(potionBox);
            } else {
                Label emptyLabel = new Label("❌ Nessuna pozione");
                emptyLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 11px;");
                inventoryList.getChildren().add(emptyLabel);
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
                cell.setStyle("-fx-cursor: hand; -fx-font-size: 16px;");

                // Stanza corrente (giocatore)
                if (room == gameController.getCurrentRoom()) {
                    // Mostra l'icona del giocatore in base alla classe
                    String playerClass = gameController.getPlayer().getCharacterClass();
                    String playerIcon = getPlayerIcon(playerClass);
                    cell.setText(playerIcon);
                    cell.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-size: 18px; -fx-cursor: hand;");
                    cell.getStyleClass().add("map-cell-current");
                }
                // Stanza con nemico vivo
                else if (room.hasEnemy() && room.getEnemy().isAlive()) {
                    String enemyIcon = getEnemyIcon(room.getEnemy().getName());
                    cell.setText(enemyIcon);
                    cell.setStyle("-fx-background-color: #3a1a1a; -fx-font-size: 18px; -fx-cursor: hand;");
                }
                // Stanza esplorata con tesoro
                else if (room.isExplored() && room.hasTreasures()) {
                    cell.setText("💰");
                    cell.setStyle("-fx-background-color: #2a4a2a; -fx-font-size: 18px; -fx-cursor: hand;");
                }
                // Stanza esplorata vuota
                else if (room.isExplored()) {
                    cell.setText("⬜");
                    cell.setStyle("-fx-background-color: #2a2a3a; -fx-font-size: 14px; -fx-cursor: hand;");
                }
                // Stanza non esplorata
                else {
                    cell.setText("❓");
                    cell.setStyle("-fx-background-color: #1a1a2a; -fx-font-size: 16px; -fx-cursor: hand;");
                    cell.getStyleClass().add("map-cell-hidden");
                }

                final int finalX = x;
                final int finalY = y;
                cell.setOnAction(e -> {
                    // Opzionale: cliccare su una cella per muoversi (utile per debug)
                    if (Math.abs(finalX - gameController.getCurrentRoom().getX()) <= 1 &&
                            Math.abs(finalY - gameController.getCurrentRoom().getY()) <= 1) {
                        // Movimento adiacente
                        if (finalX > gameController.getCurrentRoom().getX()) onMoveEast();
                        else if (finalX < gameController.getCurrentRoom().getX()) onMoveWest();
                        else if (finalY > gameController.getCurrentRoom().getY()) onMoveSouth();
                        else if (finalY < gameController.getCurrentRoom().getY()) onMoveNorth();
                    }
                });

                mapGrid.add(cell, x, y);
            }
        }
    }

    // Metodo per ottenere l'icona del giocatore in base alla classe
    private String getPlayerIcon(String playerClass) {
        switch(playerClass.toLowerCase()) {
            case "warrior": return "⚔️";
            case "mage": return "🔮";
            case "rogue": return "🗡️";
            default: return "⭐";
        }
    }

    // Metodo per ottenere l'icona del nemico
    private String getEnemyIcon(String enemyName) {
        switch(enemyName.toLowerCase()) {
            case "goblin": return "👺";
            case "orc": return "👹";
            case "skeleton": return "💀";
            case "dragon": return "🐉";
            case "wolf": return "🐺";
            case "dark knight": return "⚔️";
            default: return "👾";
        }
    }
    private void setupMessageListener() {
        messageListView.setItems(gameController.getGameMessages());
        messageListView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    if (item.contains("VITTORIA") || item.contains("vittoria")) {
                        setStyle("-fx-text-fill: #00ff00; -fx-font-weight: bold; -fx-font-size: 11px;");
                    } else if (item.contains("sconfitto") || item.contains("Game Over")) {
                        setStyle("-fx-text-fill: #ff4444; -fx-font-weight: bold; -fx-font-size: 11px;");
                    } else if (item.contains("COMBATTIMENTO")) {
                        setStyle("-fx-text-fill: #ffaa44; -fx-font-weight: bold; -fx-font-size: 11px;");
                    } else {
                        setStyle("-fx-text-fill: #a0f0a0; -fx-font-size: 11px;");
                    }
                }
            }
        });
    }

    private void addGameMessage(String message) {
        // handled by GameController
    }

    private void showCharacterCreation() {
        startScreen.setVisible(false);
        startScreen.setManaged(false);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("✨ Nuova Avventura ✨");
        dialog.setHeaderText("Crea il tuo eroe");

        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new javafx.geometry.Insets(20));

        Label nameLabel = new Label("📝 Nome:");
        nameLabel.setStyle("-fx-font-weight: bold;");
        TextField nameField = new TextField();
        nameField.setPromptText("Inserisci il nome");
        nameField.setPrefWidth(250);

        Label classLabel = new Label("⚔️ Classe:");
        classLabel.setStyle("-fx-font-weight: bold;");
        ComboBox<String> classBox = new ComboBox<>();
        classBox.getItems().addAll("Warrior", "Mage", "Rogue");
        classBox.setValue("Warrior");
        classBox.setPrefWidth(250);

        Label classDesc = new Label();
        classDesc.setStyle("-fx-text-fill: #c0c0c0; -fx-padding: 5 0 0 0;");
        classBox.setOnAction(e -> {
            switch(classBox.getValue()) {
                case "Warrior": classDesc.setText("💪 +HP, +Difesa, Attacco potente"); break;
                case "Mage": classDesc.setText("🔮 +Attacco magico, -Difesa"); break;
                case "Rogue": classDesc.setText("🗡️ Equilibrato, alta critica"); break;
            }
        });
        classDesc.setText("💪 Guerriero: Alto HP, buona difesa");

        Button startBtn = new Button("⚔️ INIZIA ⚔️");
        startBtn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        startBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty() && classBox.getValue() != null) {
                gameController.startNewGame(name, classBox.getValue());
                dialog.close();
                showGameUI();
            } else if (name.isEmpty()) {
                nameField.setPromptText("Inserisci un nome!");
                nameField.setStyle("-fx-border-color: red;");
            }
        });

        content.getChildren().addAll(
                nameLabel, nameField,
                new Separator(),
                classLabel, classBox, classDesc,
                new Separator(),
                startBtn
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.showAndWait();
    }

    private void animateAttack() {
        // Animazione del bottone
        attackBtn.setScaleX(0.9);
        attackBtn.setScaleY(0.9);
        ScaleTransition st = new ScaleTransition(Duration.millis(100), attackBtn);
        st.setToX(1);
        st.setToY(1);
        st.play();

        // Animazione del messaggio di combattimento
        combatMessage.setStyle("-fx-text-fill: #ffaa00; -fx-font-size: 14px; -fx-font-weight: bold;");
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> combatMessage.setText("⚔️ COLPO INFERTO! ⚔️")),
                new KeyFrame(Duration.seconds(0.5), e -> combatMessage.setText("")),
                new KeyFrame(Duration.seconds(1), e -> combatMessage.setText("⚔️ CONTINUA IL COMBATTIMENTO! ⚔️"))
        );
        timeline.setCycleCount(1);
        timeline.play();
    }

    private void showGameUI() {
        gameScreen.setVisible(true);
        gameScreen.setManaged(true);

        // Carica le immagini
        loadPlayerImage(gameController.getPlayer().getCharacterClass());
        loadMapBackground();

        updatePlayerUI(gameController.getPlayer());
        updateInventory();
        updateMap();
        roomDescriptionArea.setText(
                gameController.getCurrentRoom().getName() + "\n" +
                        gameController.getCurrentRoom().getDescription()
        );

        // Setup key bindings dopo che la scena è visibile
        if (currentScene == null) {
            currentScene = gameScreen.getScene();
            setupKeyBindings();
        }

        addGameMessage("🏰 La tua avventura ha inizio!");
        addGameMessage("📍 Ti trovi in: " + gameController.getCurrentRoom().getName());
        addGameMessage("🎮 Usa WASD o le FRECCE per muoverti!");
    }

    // Azioni movimento
    @FXML
    private void onMoveNorth() {
        if (canMove()) {
            gameController.move(Direction.NORTH);
            updateAfterMove();
        }
    }

    @FXML
    private void onMoveSouth() {
        if (canMove()) {
            gameController.move(Direction.SOUTH);
            updateAfterMove();
        }
    }

    @FXML
    private void onMoveEast() {
        if (canMove()) {
            gameController.move(Direction.EAST);
            updateAfterMove();
        }
    }

    @FXML
    private void onMoveWest() {
        if (canMove()) {
            gameController.move(Direction.WEST);
            updateAfterMove();
        }
    }

    private boolean canMove() {
        return gameController.getPlayer() != null &&
                gameController.isPlayerAlive() &&
                !gameController.isInCombat();
    }

    private void updateAfterMove() {
        updateMap();
        updateInventory();
        updatePlayerUI(gameController.getPlayer());
        updateCombatUI();
        roomDescriptionArea.setText(
                gameController.getCurrentRoom().getName() + "\n" +
                        gameController.getCurrentRoom().getDescription()
        );
    }

    @FXML
    private void onAttack() {
        if (gameController.getPlayer() != null && gameController.isInCombat() && gameController.isPlayerAlive()) {
            animateAttack();
            gameController.playerAttack();
            updateCombatUI();  // Aggiorna la UI
            updatePlayerUI(gameController.getPlayer());  // Aggiorna statistiche
            updateMap();
        }
    }

    @FXML
    private void onUseItem() {
        if (gameController.getPlayer() != null) {
            for (Item item : gameController.getPlayer().getInventory()) {
                if (item instanceof HealthPotion && ((HealthPotion) item).getQuantity() > 0) {
                    gameController.useItem(item);
                    addGameMessage("🧪 Usata una pozione curativa!");
                    updateInventory();
                    updatePlayerUI(gameController.getPlayer());
                    updateCombatUI();

                    if (gameController.isInCombat()) {
                        combatMessage.setText("🧪 POZIONE USATA! +30 HP!");
                    }
                    return;
                }
            }
            addGameMessage("❌ Nessuna pozione nell'inventario!");
            if (gameController.isInCombat()) {
                combatMessage.setText("❌ NESSUNA POZIONE!");
            }
        }
    }

    @FXML
    private void onFlee() {
        if (gameController.getPlayer() != null && gameController.isInCombat()) {
            gameController.flee();
            updateCombatUI();
            updatePlayerUI(gameController.getPlayer());
            updateMap();

            if (!gameController.isInCombat()) {
                combatMessage.setText("🏃 SEI RIUSCITO A FUGGIRE! 🏃");
                // Disabilita il pannello combattimento
                combatPanel.setVisible(false);
                combatPanel.setManaged(false);
            } else {
                combatMessage.setText("⚠️ FUGA FALLITA! Subisci un attacco! ⚠️");
                // Forza aggiornamento delle barre
                updateCombatUI();
            }
        }
    }

    // Azioni menu
    @FXML
    private void onNewGame() {
        gameScreen.setVisible(false);
        gameScreen.setManaged(false);
        startScreen.setVisible(true);
        startScreen.setManaged(true);
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

    private void loadPlayerImage(String characterClass) {
        Image img = ImageLoader.getPlayerImage(characterClass);
        if (img != null) {
            playerImageView.setImage(img);
            playerCombatImageView.setImage(img);
        } else {
            // Fallback a emoji
            String emoji = getPlayerIcon(characterClass);
            playerImageView.setImage(null);
            // Puoi anche impostare un testo alternativo se vuoi
        }
    }

    private void loadEnemyImage(String enemyName) {
        Image img = ImageLoader.getEnemyImage(enemyName);
        if (img != null) {
            enemyImageView.setImage(img);
        } else {
            // Fallback a emoji
            String emoji = getEnemyIcon(enemyName);
            enemyNameLabel.setText(emoji + " " + enemyName);
            enemyImageView.setImage(null);
        }
    }

    private void loadItemImages() {
        // Per l'inventario - opzionale
        Image potionImg = ImageLoader.getPotionImage();
        // Non usiamo immagini per i bottoni, solo testo
    }

    private void loadMapBackground() {
        try {
            var inputStream = getClass().getResourceAsStream("/images/map_background.png");
            if (inputStream != null) {
                javafx.scene.image.Image img = new javafx.scene.image.Image(inputStream);
                mapBackground.setImage(img);
            }
        } catch (Exception e) {
            // Ignora - usa solo colore di sfondo
        }
    }

    private void showHelpDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("📖 Guida");
        alert.setHeaderText("🎮 COME GIOCARE");
        alert.setContentText("""
            ═══════════════════════════════════════
            
            🗺️ MOVIMENTO:
            • Tasti WASD o FRECCE per muoverti
            • Clicca sui bottoni direzionali
            
            ⚔️ COMBATTIMENTO:
            • ATTACCA - Colpisci il nemico
            • POZIONE - Cura 30 HP
            • FUGGI - Scappa (50% successo)
            
            💰 RICOMPENSE:
            • Sconfiggi nemici per XP e oro
            • Salendo di livello aumentano le statistiche
            • Trova tesori nelle stanze
            
            👑 OBIETTIVO:
            Sconfiggi il Drago (angolo in basso a destra del dungeon)!
            
            ═══════════════════════════════════════
            """);
        alert.showAndWait();
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ℹ️ Info");
        alert.setHeaderText("🏰 DUNGEON EXPLORER RPG");
        alert.setContentText("""
            Versione: 1.0
            Matricola: 129876
            Corso: Metodologie di Programmazione AA 2025/26
            
            Un gioco di ruolo a turni con esplorazione.
            
            🎮 Buon divertimento!
            """);
        alert.showAndWait();
    }
}