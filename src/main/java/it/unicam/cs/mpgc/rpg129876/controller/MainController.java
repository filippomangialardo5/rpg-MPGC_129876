package it.unicam.cs.mpgc.rpg129876.controller;

import it.unicam.cs.mpgc.rpg129876.model.Score;
import it.unicam.cs.mpgc.rpg129876.model.characters.Merchant;
import it.unicam.cs.mpgc.rpg129876.model.characters.Player;
import it.unicam.cs.mpgc.rpg129876.model.items.HealthPotion;
import it.unicam.cs.mpgc.rpg129876.model.items.Item;
import it.unicam.cs.mpgc.rpg129876.model.world.Direction;
import it.unicam.cs.mpgc.rpg129876.model.world.Room;
import it.unicam.cs.mpgc.rpg129876.utils.ImageLoader;
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
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.ScaleTransition;
import javafx.scene.layout.HBox;
import javafx.scene.input.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.*;


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

    @FXML private Label enemyAttackLabel;
    @FXML private Label enemyDefenseLabel;
    @FXML private Label potionSuggestion;

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

    private List<Score> scores = new ArrayList<>();
    private static final String SCORES_FILE = "scores.json";

    private Merchant currentMerchant;


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

        // Rimuovi eventuali listener esistenti per evitare duplicati
        currentScene.removeEventFilter(KeyEvent.KEY_PRESSED, keyEventHandler);

        // Usa EventFilter invece di setOnKeyPressed
        currentScene.addEventFilter(KeyEvent.KEY_PRESSED, keyEventHandler);
    }

    // Crea un handler separato per i tasti
    private final EventHandler<KeyEvent> keyEventHandler = event -> {
        if (!gameScreen.isVisible() || gameController.getPlayer() == null) return;
        if (gameController.isInCombat()) return;

        KeyCode code = event.getCode();

        // Consuma l'evento per evitare che venga processato due volte
        event.consume();

        switch(code) {
            case W: case UP:
                onMoveNorth();
                break;
            case S: case DOWN:
                onMoveSouth();
                break;
            case A: case LEFT:
                onMoveWest();
                break;
            case D: case RIGHT:
                onMoveEast();
                break;
            default: break;
        }
    };

    private void enableMapKeyBindings() {
        mapGrid.setOnKeyPressed(event -> {
            if (gameController.isInCombat()) return;

            switch(event.getCode()) {
                case W: case UP: onMoveNorth(); break;
                case S: case DOWN: onMoveSouth(); break;
                case A: case LEFT: onMoveWest(); break;
                case D: case RIGHT: onMoveEast(); break;
                default: break;
            }
        });
        mapGrid.setFocusTraversable(true);
    }

    @FXML
    private void onStartGameFromButton() {
        showCharacterCreation();
    }

    @FXML
    private void onMerchant() {
        if (gameController.getPlayer() == null) return;

        Room currentRoom = gameController.getCurrentRoom();
        if (!currentRoom.hasMerchant()) {
            addGameMessage("❌ Non c'è nessun mercante qui!");
            return;
        }

        Merchant merchant = currentRoom.getMerchant();
        Player player = gameController.getPlayer();

        // Dialogo del mercante
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("🏪 Mercante");
        dialog.setHeaderText(merchant.getName() + " - Benvenuto viaggiatore!");

        VBox content = new VBox(10);
        content.setPadding(new javafx.geometry.Insets(15));

        Label goldLabel = new Label("💰 Oro disponibile: " + player.getGold());
        goldLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label itemsLabel = new Label("📦 Oggetti in vendita:");
        itemsLabel.setStyle("-fx-font-weight: bold;");

        VBox itemsBox = new VBox(5);
        for (Item item : merchant.getItemsForSale()) {
            int price = (item instanceof HealthPotion && ((HealthPotion) item).getHealAmount() == 30) ? 30 : 60;
            Button buyBtn = new Button("🧪 " + item.getName() + " - " + price + "💰");
            buyBtn.setOnAction(e -> {
                if (merchant.buyItem(item, player)) {
                    addGameMessage("🏪 Hai comprato " + item.getName() + " per " + price + " oro!");
                    updatePlayerUI(player);
                    updateInventory();
                    dialog.close();
                } else {
                    addGameMessage("❌ Oro insufficiente! Ti mancano " + (price - player.getGold()) + " oro.");
                }
            });
            itemsBox.getChildren().add(buyBtn);
        }

        Button closeBtn = new Button("🚪 Uscita");
        closeBtn.setOnAction(e -> dialog.close());

        content.getChildren().addAll(goldLabel, itemsLabel, itemsBox, closeBtn);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.showAndWait();
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

            // SUGGERIMENTO POZIONE - se HP bassi
            if (p.getHp() < p.getMaxHp() / 3 && p.getHp() > 0) {
                potionSuggestion.setText("⚠️ HP BASSO! USA UNA POZIONE! ⚠️");
                potionSuggestion.setStyle("-fx-text-fill: #ff4444; -fx-font-weight: bold;");
            } else {
                potionSuggestion.setText("");
            }
        }

        if (gameController.isInCombat() && gameController.getCurrentCombat() != null) {
            var enemy = gameController.getCurrentCombat().getEnemy();
            String enemyIcon = getEnemyIcon(enemy.getName());
            enemyNameLabel.setText(enemyIcon + " " + enemy.getName());
            enemyHealthBar.setProgress((double) enemy.getHp() / enemy.getMaxHp());
            enemyHealthLabel.setText(enemy.getHp() + "/" + enemy.getMaxHp());

            // MOSTRA ATTACCO E DIFESA DEL NEMICO
            enemyAttackLabel.setText("⚔ Attacco: " + enemy.getAttack());
            enemyDefenseLabel.setText("🛡 Difesa: " + enemy.getDefense());

            // Cambia colore barra in base agli HP del nemico
            if (enemy.getHp() < enemy.getMaxHp() / 3) {
                enemyHealthBar.setStyle("-fx-accent: #ffaa00;");
            } else {
                enemyHealthBar.setStyle("-fx-accent: #ff4444;");
            }

            // SUGGERIMENTO STRATEGICO
            if (enemy.getAttack() > gameController.getPlayer().getDefense() + 10) {
                potionSuggestion.setText("⚠️ ATTACCO NEMICO ALTO! USA POZIONI! ⚠️");
            } else if (enemy.getHp() < 20) {
                potionSuggestion.setText("⚔️ NEMICO DEBOLE! ATTACCA! ⚔️");
            }
        }
    }

    private void enableCombatMode() {

        System.out.println("=== enableCombatMode chiamato ===");
        System.out.println("combatPanel è null? " + (combatPanel == null));

        if (combatPanel != null) {
            combatPanel.setVisible(true);
            combatPanel.setManaged(true);
            combatPanel.setDisable(false);
            System.out.println("combatPanel visible: " + combatPanel.isVisible());
        }

        // Carica l'immagine del nemico
        if (gameController.getCurrentCombat() != null) {
            var enemy = gameController.getCurrentCombat().getEnemy();
            String enemyName = enemy.getName();
            loadEnemyImage(enemyName);

            // Messaggio personalizzato in base al nemico
            String enemyMessage;
            switch(enemyName.toLowerCase()) {
                case "goblin": enemyMessage = "👺 Un Goblin debole appare! Facile da sconfiggere!"; break;
                case "orc": enemyMessage = "👹 Un Orco potente! Attento al suo attacco!"; break;
                case "skeleton": enemyMessage = "💀 Uno Scheletro! Ha difesa media!"; break;
                case "dragon": enemyMessage = "🐉 UN DRAGO!!! Molto potente, usa pozioni!"; break;
                case "wolf": enemyMessage = "🐺 Un Lupo veloce! Attacco rapido!"; break;
                default: enemyMessage = "⚔️ Un nemico selvaggio appare! Scegli la tua azione!";
            }
            combatMessage.setText("⚔️ " + enemyMessage + " ⚔️");
            addGameMessage("⚔️ INIZIA COMBATTIMENTO contro " + enemyName + "!");
        }

        updateCombatUI();
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
                cell.setMinSize(55, 55);
                cell.setPrefSize(55, 55);
                cell.setMaxSize(55, 55);
                cell.setStyle("-fx-cursor: hand; -fx-padding: 0; -fx-background-radius: 8;");

                // STANZA CORRENTE (GIOCATORE) - usa immagine personaggio
                if (room == gameController.getCurrentRoom()) {
                    String playerClass = gameController.getPlayer().getCharacterClass();
                    Image playerImg = ImageLoader.getPlayerImage(playerClass);
                    if (playerImg != null) {
                        ImageView playerView = new ImageView(playerImg);
                        playerView.setFitWidth(45);
                        playerView.setFitHeight(45);
                        cell.setGraphic(playerView);
                        cell.setText("");
                    } else {
                        cell.setText("⭐");
                    }
                    cell.setStyle("-fx-background-color: #e94560; -fx-cursor: hand; -fx-background-radius: 8;");
                }
                // PORTA DEL TESORO
                else if (room.isDoorRoom()) {
                    if (gameController.getDungeon().areAllDragonsDefeated()) {
                        cell.setText("🚪✨");
                        cell.setStyle("-fx-background-color: #ffd700; -fx-font-size: 20px; -fx-cursor: hand; -fx-background-radius: 8;");
                    } else {
                        cell.setText("🚪🔒");
                        cell.setStyle("-fx-background-color: #8B4513; -fx-font-size: 20px; -fx-cursor: hand; -fx-background-radius: 8;");
                    }
                }
                // DRAGO - usa immagine drago
                else if (room.hasDragon() && room.hasEnemy() && room.getEnemy().isAlive()) {
                    Image dragonImg = ImageLoader.getEnemyImage("Dragon");
                    if (dragonImg != null) {
                        ImageView dragonView = new ImageView(dragonImg);
                        dragonView.setFitWidth(45);
                        dragonView.setFitHeight(45);
                        cell.setGraphic(dragonView);
                        cell.setText("");
                    } else {
                        cell.setText("🐉");
                    }
                    cell.setStyle("-fx-background-color: #8B0000; -fx-cursor: hand; -fx-background-radius: 8;");
                }
                // ALTRI NEMICI - usa immagine del nemico
                else if (room.hasEnemy() && room.getEnemy().isAlive()) {
                    String enemyName = room.getEnemy().getName();
                    Image enemyImg = ImageLoader.getEnemyImage(enemyName);
                    if (enemyImg != null) {
                        ImageView enemyView = new ImageView(enemyImg);
                        enemyView.setFitWidth(40);
                        enemyView.setFitHeight(40);
                        cell.setGraphic(enemyView);
                        cell.setText("");
                    } else {
                        cell.setText(getEnemyIcon(enemyName));
                    }
                    cell.setStyle("-fx-background-color: #3a1a1a; -fx-cursor: hand; -fx-background-radius: 8;");
                }
                // MERCANTE
                else if (room.hasMerchant()) {
                    Image merchantImg = ImageLoader.getMerchantImage();
                    if (merchantImg != null) {
                        ImageView merchantView = new ImageView(merchantImg);
                        merchantView.setFitWidth(40);
                        merchantView.setFitHeight(40);
                        cell.setGraphic(merchantView);
                        cell.setText("");
                    } else {
                        cell.setText("🏪");
                    }
                    cell.setStyle("-fx-background-color: #2a5a5a; -fx-font-size: 18px; -fx-cursor: hand;");
                }
                // TESORO - usa immagine tesoro/pozione
                else if (room.isExplored() && room.hasTreasures()) {
                    Image goldImg = ImageLoader.getGoldImage();
                    if (goldImg != null) {
                        ImageView goldView = new ImageView(goldImg);
                        goldView.setFitWidth(35);
                        goldView.setFitHeight(35);
                        cell.setGraphic(goldView);
                        cell.setText("");
                    } else {
                        cell.setText("💰");
                    }
                    cell.setStyle("-fx-background-color: #2a4a2a; -fx-cursor: hand; -fx-background-radius: 8;");
                }
                // ESPLORATA VUOTA
                else if (room.isExplored()) {
                    cell.setText("⬜");
                    cell.setStyle("-fx-background-color: #2a2a3a; -fx-font-size: 20px; -fx-cursor: hand; -fx-background-radius: 8;");
                }
                // NON ESPLORATA
                else {
                    cell.setText("❓");
                    cell.setStyle("-fx-background-color: #1a1a2a; -fx-font-size: 20px; -fx-cursor: hand; -fx-background-radius: 8;");
                }

                // Movimento cliccando sulla cella
                final int fx = x;
                final int fy = y;
                cell.setOnAction(e -> {
                    Room current = gameController.getCurrentRoom();
                    int dx = Math.abs(fx - current.getX());
                    int dy = Math.abs(fy - current.getY());

                    if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
                        if (fx > current.getX()) onMoveEast();
                        else if (fx < current.getX()) onMoveWest();
                        else if (fy > current.getY()) onMoveSouth();
                        else if (fy < current.getY()) onMoveNorth();
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

        loadPlayerImage(gameController.getPlayer().getCharacterClass());
        updatePlayerUI(gameController.getPlayer());
        updateInventory();
        updateMap();
        enableMapKeyBindings();

        // FORZA IL FOCUS SULLA MAPPA per i tasti WASD
        Platform.runLater(() -> {
            mapGrid.requestFocus();
            mapGrid.setFocusTraversable(true);
            System.out.println("Mappa focalizzata per i tasti WASD");
        });

        addGameMessage("🏰 La tua avventura ha inizio!");
        addGameMessage("📍 Ti trovi in: " + gameController.getCurrentRoom().getName());
        addGameMessage("🎮 Usa WASD per muoverti!");
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

    // Aggiungi questo metodo
    private void checkForMerchant() {
        Merchant merchant = gameController.getCurrentMerchant();
        if (merchant != null) {
            showMerchantDialog(merchant);
            gameController.setCurrentMerchant(null);
        }
    }

    private void showMerchantDialog(Merchant merchant) {
        if (gameController.getPlayer() == null) return;

        Player player = gameController.getPlayer();

        // Dialogo del mercante
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("🏪 Mercante");
        dialog.setHeaderText(merchant.getName() + " - Benvenuto viaggiatore!");

        VBox content = new VBox(10);
        content.setPadding(new javafx.geometry.Insets(15));

        Label goldLabel = new Label("💰 Oro disponibile: " + player.getGold());
        goldLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label itemsLabel = new Label("📦 Oggetti in vendita:");
        itemsLabel.setStyle("-fx-font-weight: bold;");

        VBox itemsBox = new VBox(5);

        // Pozione piccola (30 gold) - cura 30 HP
        Button smallPotionBtn = new Button("🧪 Pozione curativa (piccola) - 30💰");
        smallPotionBtn.setOnAction(e -> {
            if (player.getGold() >= 30) {
                player.addGold(-30);
                player.addItem(new HealthPotion(1, 30));
                addGameMessage("🏪 Hai comprato una pozione piccola per 30 oro!");
                updatePlayerUI(player);
                updateInventory();
                goldLabel.setText("💰 Oro disponibile: " + player.getGold());
                dialog.close();
            } else {
                addGameMessage("❌ Oro insufficiente! Ti mancano " + (30 - player.getGold()) + " oro.");
            }
        });

        // Pozione grande (60 gold) - cura 50 HP
        Button largePotionBtn = new Button("🧪 Pozione curativa (grande) - 60💰");
        largePotionBtn.setOnAction(e -> {
            if (player.getGold() >= 60) {
                player.addGold(-60);
                player.addItem(new HealthPotion(1, 50));
                addGameMessage("🏪 Hai comprato una pozione grande per 60 oro!");
                updatePlayerUI(player);
                updateInventory();
                goldLabel.setText("💰 Oro disponibile: " + player.getGold());
                dialog.close();
            } else {
                addGameMessage("❌ Oro insufficiente! Ti mancano " + (60 - player.getGold()) + " oro.");
            }
        });

        Button closeBtn = new Button("🚪 Uscita");
        closeBtn.setOnAction(e -> dialog.close());

        itemsBox.getChildren().addAll(smallPotionBtn, largePotionBtn);
        content.getChildren().addAll(goldLabel, itemsLabel, itemsBox, closeBtn);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.showAndWait();
    }

    // Chiamalo dopo ogni movimento (in updateAfterMove)
    private void updateAfterMove() {
        updateMap();
        updateInventory();
        updatePlayerUI(gameController.getPlayer());
        updateCombatUI();
        roomDescriptionArea.setText(
                gameController.getCurrentRoom().getName() + "\n" +
                        gameController.getCurrentRoom().getDescription()
        );
        checkForMerchant();  // ← Aggiungi questa riga
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
        gameController = new GameController();  // Ricrea il controller
        initialize();  // Re-inizializza
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
            // Usa emoji come fallback
            enemyNameLabel.setText(getEnemyIcon(enemyName) + " " + enemyName);
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

    // Aggiungi un timer che controlla periodicamente lo stato del gioco
    private void startGameStatusChecker() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> checkGameStatus())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Aggiungi un timer che controlla lo stato del gioco
    private void checkGameStatus() {
        if (gameController.isGameWon()) {
            showVictoryScreen();
            gameController.resetGameFlags();
        } else if (gameController.isGameOver()) {
            showGameOverScreen();
            gameController.resetGameFlags();
        }
    }

    private void showVictoryScreen() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("🏆 VITTORIA! 🏆");
            alert.setHeaderText("Hai completato l'avventura!");
            alert.setContentText(
                    "🎉 CONGRATULAZIONI! 🎉\n\n" +
                            "Hai sconfitto il Drago e salvato il regno!\n\n" +
                            "📊 Statistiche finali:\n" +
                            "⭐ Livello raggiunto: " + gameController.getPlayer().getLevel() + "\n" +
                            "👹 Nemici sconfitti: " + gameController.getEnemiesDefeated() + "\n" +
                            "💰 Oro accumulato: " + gameController.getPlayer().getGold()
            );

            ButtonType newGameBtn = new ButtonType("✨ Nuova Partita", ButtonBar.ButtonData.OK_DONE);
            ButtonType exitBtn = new ButtonType("❌ Esci", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(newGameBtn, exitBtn);

            alert.showAndWait().ifPresent(response -> {
                if (response == newGameBtn) {
                    onNewGame();
                } else {
                    Platform.exit();
                }
            });
        });
    }

    private void showGameOverScreen() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("💀 GAME OVER 💀");
            alert.setHeaderText("Sei stato sconfitto!");
            alert.setContentText(
                    "Il tuo eroe è caduto in battaglia...\n\n" +
                            "📊 Statistiche finali:\n" +
                            "⭐ Livello raggiunto: " + gameController.getPlayer().getLevel() + "\n" +
                            "👹 Nemici sconfitti: " + gameController.getEnemiesDefeated() + "\n" +
                            "💰 Oro accumulato: " + gameController.getPlayer().getGold()
            );

            ButtonType newGameBtn = new ButtonType("✨ Nuova Partita", ButtonBar.ButtonData.OK_DONE);
            ButtonType exitBtn = new ButtonType("❌ Esci", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(newGameBtn, exitBtn);

            alert.showAndWait().ifPresent(response -> {
                if (response == newGameBtn) {
                    onNewGame();
                } else {
                    Platform.exit();
                }
            });
        });
    }

    private void loadScores() {
        try {
            File file = new File(SCORES_FILE);
            if (file.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                scores = (List<Score>) ois.readObject();
                ois.close();
            }
        } catch (Exception e) {
            scores = new ArrayList<>();
        }
    }

    private void saveScore(Score score) {
        scores.add(score);
        // Ordina per punteggio decrescente
        scores.sort((a, b) -> Integer.compare(b.getTotalScore(), a.getTotalScore()));
        // Mantieni solo top 10
        if (scores.size() > 10) {
            scores = scores.subList(0, 10);
        }

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SCORES_FILE));
            oos.writeObject(scores);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLeaderboard() {
        loadScores();

        VBox content = new VBox(10);
        content.setPadding(new javafx.geometry.Insets(15));

        Label title = new Label("🏆 CLASSIFICA - TOP 10 🏆");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffd700;");

        ListView<String> listView = new ListView<>();

        if (scores.isEmpty()) {
            listView.getItems().add("📋 Nessun punteggio registrato ancora!");
        } else {
            for (int i = 0; i < scores.size(); i++) {
                Score s = scores.get(i);
                String medal = i == 0 ? "🥇 " : (i == 1 ? "🥈 " : (i == 2 ? "🥉 " : "   "));
                listView.getItems().add(String.format("%s%d. %s - %s (Lv.%d) - %d punti",
                        medal, i + 1, s.getPlayerName(), s.getCharacterClass(),
                        s.getLevel(), s.getTotalScore()));
            }
        }

        Button closeBtn = new Button("CHIUDI");
        closeBtn.setOnAction(e -> closeBtn.getScene().getWindow().hide());

        content.getChildren().addAll(title, listView, closeBtn);

        Scene scene = new Scene(content, 400, 400);
        Stage stage = new Stage();
        stage.setTitle("Classifica");
        stage.setScene(scene);
        stage.show();
    }

    @FXML private void onShowLeaderboard() {
        showLeaderboard();
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