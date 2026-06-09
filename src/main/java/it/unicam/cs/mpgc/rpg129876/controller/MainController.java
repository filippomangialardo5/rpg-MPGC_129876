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
import javafx.scene.layout.HBox;
import javafx.scene.input.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.Separator;

/**
 * Controller principale dell'applicazione.
 * Gestisce l'interfaccia utente, gli eventi dei bottoni e la logica di presentazione.
 * Comunica con GameController per la logica di gioco.
 *
 * @author Filippo Mangialardo
 * @version 1.0
 */
public class MainController {

    private GameController gameController;

    // UI Components - Schermate
    @FXML private VBox startScreen;
    @FXML private BorderPane gameScreen;

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

    @FXML private ProgressBar playerCombatHealthBar;
    @FXML private Label playerCombatHealthLabel;
    @FXML private Label enemyNameLabel;
    @FXML private ProgressBar enemyHealthBar;
    @FXML private Label enemyHealthLabel;
    @FXML private Label combatMessage;
    @FXML private Button attackBtn;


    // UI Components - Altro
    @FXML private VBox inventoryList;
    @FXML private ListView<String> messageListView;

    // ImageView per le immagini
    @FXML private ImageView playerImageView;
    @FXML private ImageView playerCombatImageView;
    @FXML private ImageView enemyImageView;

    @FXML private Label enemyAttackLabel;
    @FXML private Label enemyDefenseLabel;
    @FXML private Label potionSuggestion;

    @FXML private VBox legendBox;

    private Scene currentScene;
    private static final String SCORES_FILE = "scores.json";
    private Timeline gameStatusChecker;
    private boolean keysRegistered = false;

    /**
     * Inizializza il controller.
     * Crea il GameController, imposta i listener e configura l'interfaccia iniziale.
     */
    @FXML
    public void initialize() {
        gameController = new GameController();

        bindPlayerStats();
        setupMessageListener();

        // Nascondi schermata di gioco all'inizio
        gameScreen.setVisible(false);
        gameScreen.setManaged(false);
        combatPanel.setVisible(false);
        combatPanel.setManaged(false);

        setupLegend();

        addGameMessage("✨ Benvenuto in Dungeon Explorer RPG!");
    }

    /**
     * Avvia una nuova partita resettando completamente lo stato del gioco.
     * Ferma il checker, resetta i binding, ricrea il controller e mostra la creazione personaggio.
     */
    @FXML
    private void onNewGame() {
        System.out.println("=== ON NEW GAME ===");

        if (gameStatusChecker != null) {
            gameStatusChecker.stop();
            gameStatusChecker = null;
        }

        // Unbind proprietà
        healthBar.progressProperty().unbind();
        playerCombatHealthBar.progressProperty().unbind();
        enemyHealthBar.progressProperty().unbind();
        healthLabel.textProperty().unbind();

        // Nascondi schermate
        gameScreen.setVisible(false);
        gameScreen.setManaged(false);
        combatPanel.setVisible(false);
        combatPanel.setManaged(false);

        // Crea NUOVO controller
        gameController = new GameController();
        keysRegistered = false;

        // RICHAMA IL BINDING DEI LISTENER
        bindPlayerStats();
        setupMessageListener();

        // Pulisci UI
        mapGrid.getChildren().clear();
        inventoryList.getChildren().clear();
        messageListView.getItems().clear();
        roomDescriptionArea.clear();

        // Resetta le barre
        healthBar.setProgress(0);
        playerCombatHealthBar.setProgress(0);
        enemyHealthBar.setProgress(0);
        healthLabel.setText("0/0");
        playerCombatHealthLabel.setText("0/0");
        enemyHealthLabel.setText("0/0");

        // Mostra schermata iniziale
        startScreen.setVisible(true);
        startScreen.setManaged(true);

        // Mostra il dialog di creazione
        showCharacterCreation();
    }

    /**
     * Avvia la creazione del personaggio quando si preme il bottone "Inizia Avventura".
     * Nasconde la schermata iniziale e mostra il dialog di creazione.
     */
    @FXML
    private void onStartGameFromButton() {
        showCharacterCreation();
    }

    /**
     * Sposta il giocatore verso Nord.
     * Verifica che il giocatore non sia in combattimento e sia vivo.
     */
    @FXML
    private void onMoveNorth() {
        if (gameController == null || gameController.getPlayer() == null) return;
        if (gameController.isInCombat()) {
            addGameMessage("⚠ Non puoi muoverti durante il combattimento!");
            return;
        }
        if (gameController.isPlayerAlive()) {
            gameController.move(Direction.NORTH);
            updateAfterMove();
        }
    }

    /**
     * Sposta il giocatore verso Sud.
     * Verifica che il giocatore non sia in combattimento e sia vivo.
     */
    @FXML
    private void onMoveSouth() {
        if (gameController == null || gameController.getPlayer() == null) return;
        if (gameController.isInCombat()) {
            addGameMessage("⚠ Non puoi muoverti durante il combattimento!");
            return;
        }
        if (gameController.isPlayerAlive()) {
            gameController.move(Direction.SOUTH);
            updateAfterMove();
        }
    }

    /**
     * Sposta il giocatore verso Est.
     * Verifica che il giocatore non sia in combattimento e sia vivo.
     */
    @FXML
    private void onMoveEast() {
        if (gameController == null || gameController.getPlayer() == null) return;
        if (gameController.isInCombat()) {
            addGameMessage("⚠ Non puoi muoverti durante il combattimento!");
            return;
        }
        if (gameController.isPlayerAlive()) {
            gameController.move(Direction.EAST);
            updateAfterMove();
        }
    }

    /**
     * Sposta il giocatore verso Ovest.
     * Verifica che il giocatore non sia in combattimento e sia vivo.
     */
    @FXML
    private void onMoveWest() {
        if (gameController == null || gameController.getPlayer() == null) return;
        if (gameController.isInCombat()) {
            addGameMessage("⚠ Non puoi muoverti durante il combattimento!");
            return;
        }
        if (gameController.isPlayerAlive()) {
            gameController.move(Direction.WEST);
            updateAfterMove();
        }
    }

    /**
     * Gestisce l'attacco del giocatore durante il combattimento.
     * Calcola il danno inflitto e aggiorna l'interfaccia.
     */
    @FXML
    private void onAttack() {
        System.out.println("=== ON ATTACK CLICCATO ===");

        if (gameController == null) return;
        if (!gameController.isInCombat()) return;

        // Salva stato prima dell'attacco
        boolean wasEnemyAlive = gameController.getCurrentCombat() != null &&
                gameController.getCurrentCombat().getEnemy().isAlive();
        int oldEnemyHp = wasEnemyAlive ? gameController.getCurrentCombat().getEnemy().getHp() : 0;

        gameController.playerAttack();

        // Controlla se il nemico è morto
        boolean isEnemyAlive = gameController.getCurrentCombat() != null &&
                gameController.getCurrentCombat().getEnemy().isAlive();
        int newEnemyHp = isEnemyAlive ? gameController.getCurrentCombat().getEnemy().getHp() : 0;

        if (wasEnemyAlive && !isEnemyAlive) {
            // Nemico sconfitto
            addGameMessage("⚔ Vittoria! Nemico sconfitto!");
            // Non mostrare messaggio nel pannello perché si chiuderà
        } else {
            // Messaggio casuale per l'attacco
            String[] attackMessages = {
                    "⚔ COLPO CRITICO!",
                    "💥 ATTACCO POTENTE!",
                    "⚔ BEL COLPO!",
                    "💪 CONTINUA COSÌ!"
            };
            String randomMsg = attackMessages[(int)(Math.random() * attackMessages.length)];

            // Calcola danno approssimativo
            int damageDealt = oldEnemyHp - newEnemyHp;
            if (damageDealt > 0) {
                combatMessage.setText(randomMsg + " (" + damageDealt + " danni)");
            } else {
                combatMessage.setText(randomMsg);
            }

            // Suggerimento tattico in base al nemico
            if (gameController.getCurrentCombat() != null) {
                var enemy = gameController.getCurrentCombat().getEnemy();
                if (enemy.getAttack() > gameController.getPlayer().getDefense() + 10) {
                    combatMessage.setText(combatMessage.getText() + "\n⚠ ATTACCO NEMICO ALTO! USA POZIONI!");
                }
            }
        }

        // Controlla se il giocatore è a rischio
        int newPlayerHp = gameController.getPlayer().getHp();
        int maxPlayerHp = gameController.getPlayer().getMaxHp();

        if (newPlayerHp < maxPlayerHp / 3 && newPlayerHp > 0) {
            combatMessage.setText(combatMessage.getText() + "\n⚠ HP BASSO! USA UNA POZIONE! ⚠");
        }

        updateCombatUI();
        updatePlayerUI(gameController.getPlayer());
        updateMap();
    }

    /**
     * Usa una pozione curativa.
     * Controlla se il giocatore ha pozioni e se ha bisogno di cure.
     * Funziona sia dentro che fuori dal combattimento.
     */
    @FXML
    private void onUseItem() {
        if (gameController.getPlayer() != null) {
            for (Item item : gameController.getPlayer().getInventory()) {
                if (item instanceof HealthPotion && ((HealthPotion) item).getQuantity() > 0) {
                    int oldHp = gameController.getPlayer().getHp();
                    int maxHp = gameController.getPlayer().getMaxHp();

                    if (oldHp >= maxHp) {
                        addGameMessage("❌ Sei già a piena vita!");
                        combatMessage.setText("❌ SEI GIÀ A PIENA VITA!");
                        return;
                    }

                    gameController.useItem(item);

                    int newHp = gameController.getPlayer().getHp();
                    int healed = newHp - oldHp;

                    if (healed > 0) {
                        addGameMessage("⚗ Usata una pozione! +" + healed + " HP");
                        combatMessage.setText("⚗ POZIONE USATA! +" + healed + " HP");

                        // Suggerimento se ancora a rischio
                        if (newHp < maxHp / 3) {
                            combatMessage.setText(combatMessage.getText() + "\n⚠ ANCORA A RISCHIO! USA UN'ALTRA POZIONE!");
                        }
                    }

                    updateInventory();
                    updatePlayerUI(gameController.getPlayer());
                    if (gameController.isInCombat()) {
                        updateCombatUI();
                    }
                    return;
                }
            }
            addGameMessage("❌ Nessuna pozione nell'inventario!");
            combatMessage.setText("❌ NESSUNA POZIONE DISPONIBILE!");
        }
    }

    /**
     * Tenta la fuga dal combattimento.
     * La fuga ha il 50% di successo. Se fallisce, il giocatore subisce un attacco.
     */
    @FXML
    private void onFlee() {
        if (gameController == null) return;
        if (gameController.getPlayer() == null) return;
        if (!gameController.isInCombat()) return;

        System.out.println("=== FUGGI ===");

        // Salva lo stato prima della fuga
        boolean wasAlive = gameController.isPlayerAlive();

        gameController.flee();

        // Aggiorna UI
        updateCombatUI();
        updatePlayerUI(gameController.getPlayer());
        updateMap();

        // Mostra messaggio appropriato
        if (!gameController.isInCombat()) {
            if (!gameController.isPlayerAlive()) {
                combatMessage.setText("💀 SEI MORTO! GAME OVER! 💀");
            } else {
                combatMessage.setText("🏃 SEI RIUSCITO A FUGGIRE! 🏃");
            }
        } else {
            combatMessage.setText("⚠ FUGA FALLITA! Subisci un attacco! ⚠");
        }
    }

    @FXML
    private void onExit() {
        Platform.exit();
    }

    /**
     * Mostra il dialog di aiuto con tutti i comandi del gioco.
     * Spiega i controlli di movimento, combattimento e l'obiettivo.
     */
    @FXML
    private void onHelp() {
        showHelpDialog();
    }

    @FXML
    private void onAbout() {
        showAboutDialog();
    }


    @FXML
    private void onShowLeaderboard() {
        showLeaderboard();
    }

    /**
     * Imposta la scena principale per i key bindings.
     * Permette al controller di ricevere gli eventi da tastiera.
     *
     * @param scene la scena principale dell'applicazione
     */
    public void setScene(Scene scene) {
        this.currentScene = scene;
        setupKeyBindings();
    }

    /**
     * Registra i key bindings per i controlli da tastiera.
     * Supporta WASD e frecce per il movimento, Z/X/C per le azioni di combattimento.
     */
    private void setupKeyBindings() {
        if (keysRegistered) return;

        Scene scene = mapGrid.getScene();
        if (scene == null) {
            Platform.runLater(() -> setupKeyBindings());
            return;
        }

        // Rimuovi eventuali listener esistenti
        scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyEventHandler);

        // Aggiungi il listener unificato
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEventHandler);

        keysRegistered = true;
        System.out.println("✅ Key bindings registrati:");
        System.out.println("   MOVIMENTO: WASD o FRECCE");
        System.out.println("   ATTACCA: Z");
        System.out.println("   POZIONE: X");
        System.out.println("   FUGGI: C");
    }

    /**
     * Handler unificato per tutti gli eventi da tastiera.
     * Gestisce:
     * - Movimento: WASD e frecce (solo se non in combattimento)
     * - Attacco: Z
     * - Pozione: X
     * - Fuga: C
     */
    private final EventHandler<KeyEvent> keyEventHandler = event -> {
        if (!gameScreen.isVisible() || gameController.getPlayer() == null) return;

        KeyCode code = event.getCode();
        event.consume();

        // MOVIMENTO (solo se non in combattimento)
        if (!gameController.isInCombat()) {
            switch(code) {
                case W: case UP:
                    onMoveNorth();
                    return;
                case S: case DOWN:
                    onMoveSouth();
                    return;
                case A: case LEFT:
                    onMoveWest();
                    return;
                case D: case RIGHT:
                    onMoveEast();
                    return;
                default: break;
            }
        }

        // AZIONI DI COMBATTIMENTO
        switch(code) {
            case Z:
                if (gameController.isInCombat()) {
                    System.out.println("🔴 Tasto Z: ATTACCA");
                    onAttack();
                } else {
                    addGameMessage("⚠ Non sei in combattimento!");
                }
                break;
            case X:
                System.out.println("🟢 Tasto X: POZIONE");
                onUseItem();
                break;
            case C:
                if (gameController.isInCombat()) {
                    System.out.println("🟡 Tasto C: FUGGI");
                    onFlee();
                } else {
                    addGameMessage("⚠ Non sei in combattimento!");
                }
                break;
            default: break;
        }
    };

    /**
     * Collega le proprietà del GameController con gli elementi dell'interfaccia.
     * Utilizza i listener JavaFX per aggiornare automaticamente la UI quando i dati cambiano.
     * Gestisce:
     * - Aggiornamento statistiche giocatore
     * - Attivazione/disattivazione modalità combattimento
     * - Aggiornamento descrizione stanza e mappa
     */
    private void bindPlayerStats() {
        gameController.currentPlayerProperty().addListener((obs, old, newPlayer) -> {
            if (newPlayer != null) {
                Platform.runLater(() -> updatePlayerUI(newPlayer));
            }
        });

        gameController.inCombatProperty().addListener((obs, old, inCombat) -> {
            Platform.runLater(() -> {
                System.out.println("inCombat cambiato: " + inCombat);
                if (inCombat) {
                    enableCombatMode();  // <-- QUI VIENE CHIAMATO
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

    /**
     * Aggiorna tutti gli elementi dell'interfaccia relativi alle statistiche del giocatore.
     * Include: nome, classe, salute, livello, esperienza, oro, attacco, difesa.
     *
     * @param player il giocatore di cui aggiornare le statistiche
     */
    private void updatePlayerUI(Player player) {
        playerNameLabel.setText(player.getName());
        playerClassLabel.setText(player.getCharacterClass());
        // Aggiorna l'immagine nella legenda quando cambia personaggio
        updateLegendPlayerImage(player.getCharacterClass());

        loadPlayerImage(player.getCharacterClass());

        int currentExp = player.getExperience();
        int currentLevel = player.getLevel();
        int requiredExp = 150 + (currentLevel - 1) * 100;

        // Se l'XP è uguale o supera il required, mostra il prossimo livello
        if (currentExp >= requiredExp) {
            currentExp = currentExp - requiredExp;
            currentLevel++;
            requiredExp = 150 + (currentLevel - 1) * 100;
        }

        // Assicura che l'XP non sia negativo
        if (currentExp < 0) {
            currentExp = 0;
        }

        levelLabel.setText("⭐ Livello: " + currentLevel);
        expLabel.setText("📈 Esperienza: " + currentExp + "/" + requiredExp);
        goldLabel.setText("💰 Oro: " + player.getGold());
        attackLabel.setText("⚔ Attacco: " + player.getAttack());
        defenseLabel.setText("🛡 Difesa: " + player.getDefense());

        updateInventory();
    }

    /**
     * Aggiorna l'immagine del giocatore nella legenda quando cambia classe.
     *
     * @param characterClass la classe del giocatore (Warrior, Mage, Rogue)
     */
    private void updateLegendPlayerImage(String characterClass) {
        // Cerca il primo HBox nella legendBox (dovrebbe essere quello del giocatore)
        if (legendBox.getChildren().size() > 2) {
            Object firstItem = legendBox.getChildren().get(2);
            if (firstItem instanceof HBox) {
                HBox playerBox = (HBox) firstItem;
                if (playerBox.getChildren().get(0) instanceof ImageView) {
                    ImageView playerImg = (ImageView) playerBox.getChildren().get(0);
                    Image newImg = ImageLoader.loadImage("/images/" + characterClass.toLowerCase() + ".png");
                    if (newImg != null) {
                        playerImg.setImage(newImg);
                    }
                }
            }
        }
    }

    /**
     * Aggiorna l'interfaccia del pannello di combattimento.
     * Mostra le barre della vita di giocatore e nemico, i suggerimenti strategici.
     */
    private void updateCombatUI() {
        System.out.println("=== updateCombatUI ===");

        if (gameController.getPlayer() != null) {
            Player p = gameController.getPlayer();
            playerCombatHealthBar.setProgress((double) p.getHp() / p.getMaxHp());
            playerCombatHealthLabel.setText(p.getHp() + "/" + p.getMaxHp());

            // SUGGERIMENTO POZIONE - se HP bassi
            if (p.getHp() < p.getMaxHp() / 3 && p.getHp() > 0) {
                potionSuggestion.setText("⚠ HP BASSO! USA UNA POZIONE! ⚠");
                potionSuggestion.setStyle("-fx-text-fill: #ff4444; -fx-font-weight: bold;");
            } else {
                potionSuggestion.setText("");
            }

            loadPlayerImage(p.getCharacterClass());
        }

        if (gameController.isInCombat() && gameController.getCurrentCombat() != null) {
            var enemy = gameController.getCurrentCombat().getEnemy();
            System.out.println("Nemico: " + enemy.getName() + " HP: " + enemy.getHp() + "/" + enemy.getMaxHp());
            enemyNameLabel.setText("👹 " + enemy.getName());
            enemyHealthBar.setProgress((double) enemy.getHp() / enemy.getMaxHp());
            enemyHealthLabel.setText(enemy.getHp() + "/" + enemy.getMaxHp());
            enemyAttackLabel.setText("⚔ Attacco: " + enemy.getAttack());
            enemyDefenseLabel.setText("🛡 Difesa: " + enemy.getDefense());

            // SUGGERIMENTO STRATEGICO in base al nemico
            if (enemy.getAttack() > gameController.getPlayer().getDefense() + 10) {
                potionSuggestion.setText("⚠ ATTACCO NEMICO ALTO! USA POZIONI! ⚠");
                potionSuggestion.setStyle("-fx-text-fill: #ffaa44; -fx-font-weight: bold;");
            } else if (enemy.getHp() < 20) {
                potionSuggestion.setText("⚔ NEMICO DEBOLE! ATTACCA! ⚔");
                potionSuggestion.setStyle("-fx-text-fill: #00ff00; -fx-font-weight: bold;");
            }

            loadEnemyImage(enemy.getName());
        } else {
            System.out.println("Nessun combattimento attivo");
        }
    }

    /**
     * Attiva la modalità combattimento, rendendo visibile il pannello di combattimento.
     * Resetta il messaggio iniziale e carica l'immagine del nemico.
     */
    private void enableCombatMode() {
        System.out.println("=== enableCombatMode ===");

        combatPanel.setVisible(true);
        combatPanel.setManaged(true);
        combatPanel.setDisable(false);

        // RESETTA IL MESSAGGIO ALL'INIZIO DI OGNI COMBATTIMENTO
        combatMessage.setText("⚔ Scegli la tua azione! ⚔");
        combatMessage.setStyle("-fx-text-fill: yellow; -fx-font-size: 13px; -fx-font-weight: bold;");

        // Carica l'immagine del nemico
        if (gameController.getCurrentCombat() != null) {
            var enemy = gameController.getCurrentCombat().getEnemy();
            String enemyName = enemy.getName();
            System.out.println("Attivazione combattimento contro: " + enemyName);
            loadEnemyImage(enemyName);
            updateCombatUI();

            addGameMessage("⚔ INIZIA COMBATTIMENTO contro " + enemyName + "!");
        } else {
            System.out.println("ERRORE: currentCombat è null!");
        }
    }

    /**
     * Disattiva la modalità combattimento, nascondendo il pannello.
     * Resetta tutti i messaggi e le immagini del nemico.
     */
    private void disableCombatMode() {
        System.out.println("=== disableCombatMode ===");

        combatPanel.setVisible(false);
        combatPanel.setManaged(false);
        combatPanel.setDisable(true);

        // Resetta TUTTI i messaggi
        combatMessage.setText("");
        potionSuggestion.setText("");
        combatMessage.setStyle("-fx-text-fill: #a0f0a0; -fx-font-size: 12px;");

        // Pulisci le immagini
        enemyImageView.setImage(null);
        enemyNameLabel.setText("NEMICO");
        enemyHealthBar.setProgress(0);
        enemyHealthLabel.setText("0/0");
        enemyAttackLabel.setText("⚔ Attacco: -");
        enemyDefenseLabel.setText("🛡 Difesa: -");

        // Forza l'aggiornamento della mappa e UI
        updateMap();
        updateInventory();
        updatePlayerUI(gameController.getPlayer());

        // Aggiorna la descrizione della stanza
        if (gameController.getCurrentRoom() != null) {
            roomDescriptionArea.setText(
                    gameController.getCurrentRoom().getName() + "\n" +
                            gameController.getCurrentRoom().getDescription()
            );
        }

        System.out.println("Combat mode disabilitata");
    }

    /**
     * Aggiorna il pannello dell'inventario mostrando le pozioni disponibili.
     * Visualizza l'immagine della pozione, il contatore e il bottone per usarle.
     */
    private void updateInventory() {
        inventoryList.getChildren().clear();
        if (gameController.getPlayer() != null) {
            int potionCount = gameController.getPlayer().getPotionCount();
            int maxPotions = gameController.getPlayer().getMaxPotions();

            // ASSICURA CHE IL CONTATORE NON SUPERA IL MASSIMO
            if (potionCount > maxPotions) {
                System.out.println("⚠ WARNING: Pozioni (" + potionCount + ") superano il massimo (" + maxPotions + ")!");
            }

            HBox potionContainer = new HBox(10);
            potionContainer.setAlignment(Pos.CENTER_LEFT);
            potionContainer.setStyle("-fx-padding: 5;");

            Image potionImg = ImageLoader.getPotionImage();
            if (potionImg != null) {
                ImageView potionView = new ImageView(potionImg);
                potionView.setFitWidth(32);
                potionView.setFitHeight(32);
                potionContainer.getChildren().add(potionView);
            } else {
                Label potionEmoji = new Label("⚗");
                potionEmoji.setStyle("-fx-font-size: 24px;");
                potionContainer.getChildren().add(potionEmoji);
            }

            Label potionInfo = new Label("Pozioni: " + potionCount + "/" + maxPotions);
            potionInfo.setStyle("-fx-text-fill: #ffaa66; -fx-font-size: 12px; -fx-font-weight: bold;");

            if (potionCount > maxPotions) {
                potionInfo.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 12px; -fx-font-weight: bold;");
            }

            potionContainer.getChildren().add(potionInfo);
            inventoryList.getChildren().add(potionContainer);

            if (potionCount > 0) {
                Button potionBtn = new Button("⚗ Usa pozione (+20 HP)");
                potionBtn.setOnAction(e -> {
                    if (gameController.getPlayer() != null) {
                        for (Item item : gameController.getPlayer().getInventory()) {
                            if (item instanceof HealthPotion) {
                                int oldHp = gameController.getPlayer().getHp();
                                int maxHp = gameController.getPlayer().getMaxHp();

                                if (oldHp >= maxHp) {
                                    addGameMessage("❌ Sei già a piena vita!");
                                    return;
                                }

                                gameController.useItem(item);

                                int newHp = gameController.getPlayer().getHp();
                                int healed = newHp - oldHp;

                                if (healed > 0) {
                                    addGameMessage("⚗ Usata una pozione! +" + healed + " HP");
                                }

                                updateInventory();
                                updatePlayerUI(gameController.getPlayer());
                                if (gameController.isInCombat()) {
                                    updateCombatUI();
                                    combatMessage.setText("⚗ Usata pozione! +" + healed + " HP");
                                }
                                break;
                            }
                        }
                    }
                });
                potionBtn.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-padding: 8; -fx-cursor: hand; -fx-background-radius: 8;");
                inventoryList.getChildren().add(potionBtn);
            }
        }
    }

    /**
     * Aggiorna la visualizzazione della mappa del dungeon.
     * Ogni cella viene colorata in base al tipo di stanza (nemico, tesoro, esplorata, ecc.).
     */
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

                // STANZA CORRENTE (GIOCATORE)
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
                        cell.setText("🚪");
                        cell.setStyle("-fx-background-color: #ffd700; -fx-font-size: 20px; -fx-cursor: hand; -fx-background-radius: 8;");
                    } else {
                        cell.setText("🚪");
                        cell.setStyle("-fx-background-color: #8B4513; -fx-font-size: 20px; -fx-cursor: hand; -fx-background-radius: 8;");
                    }
                }
                // DRAGO
                else if (room.hasDragon() && room.hasEnemy() && room.getEnemy().isAlive()) {
                    Image dragonImg = ImageLoader.getEnemyImage("Drago");
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
                // ALTRI NEMICI
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
                // TESORO
                else if (room.isExplored() && room.hasTreasures()) {
                    // Prova a caricare l'immagine del tesoro
                    Image treasureImg = ImageLoader.getGoldImage();
                    if (treasureImg != null) {
                        ImageView treasureView = new ImageView(treasureImg);
                        treasureView.setFitWidth(35);
                        treasureView.setFitHeight(35);
                        cell.setGraphic(treasureView);
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

    /**
     * Restituisce l'emoji corrispondente alla classe del giocatore.
     * Usata come fallback quando le immagini non sono disponibili.
     *
     * @param playerClass la classe del giocatore
     * @return l'emoji corrispondente (⚔️ per Guerriero, 🔮 per Mago, 🗡️ per Ladro, ⭐ default)
     */
    private String getPlayerIcon(String playerClass) {
        switch(playerClass.toLowerCase()) {
            case "warrior": return "⚔";
            case "mage": return "🔮";
            case "rogue": return "🗡️";
            default: return "⭐";
        }
    }

    /**
     * Restituisce l'emoji corrispondente alla classe del nemico.
     * Usata come fallback quando le immagini non sono disponibili.
     *
     * @param enemyName la classe del nemico
     * @return l'emoji corrispondente
     */
    private String getEnemyIcon(String enemyName) {
        switch(enemyName.toLowerCase()) {
            case "goblin": return "👺";
            case "orco": return "👹";
            case "scheletro": return "💀";
            case "drago": return "🐉";
            case "lupo": return "🐺";
            case "cavaliere oscuro": return "⚔";
            default: return "👾";
        }
    }

    /**
     * Configura la ListView dei messaggi di gioco.
     * Applica colori diversi in base al tipo di messaggio (VITTORIA = verde, GAME OVER = rosso, ecc.).
     */
    private void setupMessageListener() {
        messageListView.setItems(gameController.getGameMessages());
        messageListView.setCellFactory(lv -> new ListCell<>() {
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

    /**
     * Mostra l'interfaccia di gioco dopo la creazione del personaggio.
     * Inizializza i binding, carica le immagini e avvia i controlli.
     */
    private void showGameUI() {
        System.out.println("=== SHOW GAME UI ===");

        gameScreen.setVisible(true);
        gameScreen.setManaged(true);

        // Riavvio i binding
        healthBar.progressProperty().bind(
                gameController.getPlayer().hpProperty().divide(gameController.getPlayer().maxHpProperty())
        );
        healthLabel.textProperty().bind(
                gameController.getPlayer().hpProperty().asString().concat("/").concat(gameController.getPlayer().maxHpProperty().asString())
        );

        loadPlayerImage(gameController.getPlayer().getCharacterClass());

        // Forza l'aggiornamento
        updatePlayerUI(gameController.getPlayer());
        updateInventory();
        updateMap();

        // Riavvio key bindings
        setupKeyBindings();

        // Riavvio checker stato
        startGameStatusChecker();

        addGameMessage("🏰 La tua avventura ha inizio!");
        addGameMessage("📍 Ti trovi in: " + gameController.getCurrentRoom().getName());
        addGameMessage("🎮 Usa WASD o le FRECCE per muoverti!");

        // Forza il focus sulla mappa per i tasti
        Platform.runLater(() -> {
                    mapGrid.requestFocus();
                    mapGrid.setFocusTraversable(true);
                    System.out.println("Focus impostato sulla mappa");});
    }

    /**
     * Verifica se nella stanza corrente è presente un mercante.
     * Se presente, apre automaticamente il dialog del mercante.
     */
    private void checkForMerchant() {
        Merchant merchant = gameController.getCurrentMerchant();
        if (merchant != null) {
            showMerchantDialog(merchant);
            gameController.setCurrentMerchant(null);
        }
    }

    /**
     * Mostra il dialog del mercante per acquistare pozioni.
     * Permette di selezionare la quantità in base all'oro disponibile e allo spazio nell'inventario.
     *
     * @param merchant il mercante da cui acquistare
     */
    private void showMerchantDialog(Merchant merchant) {
        if (gameController.getPlayer() == null) return;

        Player player = gameController.getPlayer();
        int currentPotionCount = player.getPotionCount();
        int maxPotions = player.getMaxPotions();
        int availableSlots = maxPotions - currentPotionCount;
        int merchantPotions = merchant.getPotionsAvailable();

        // Calcola massimo acquistabile (limite inventario)
        int maxBuyable = Math.min(availableSlots, merchantPotions);
        maxBuyable = Math.min(maxBuyable, player.getGold() / merchant.getPotionPrice());

        if (maxBuyable > availableSlots) {
            maxBuyable = availableSlots;
        }

        Stage stage = new Stage();
        stage.setTitle("🏪 Mercante");
        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new javafx.geometry.Insets(20));
        content.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 15;");

        Label title = new Label(merchant.getName());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffd700;");

        Label goldLabel = new Label("💰 Oro disponibile: " + player.getGold());
        goldLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label potionCountLabel = new Label("⚗ Pozioni in inventario: " + currentPotionCount + "/" + maxPotions);
        potionCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #cccccc;");

        Label merchantStockLabel = new Label("🏪 Pozioni del mercante: " + merchantPotions + "/8");
        merchantStockLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #cccccc;");

        Label priceLabel = new Label("💵 Prezzo per pozione: " + merchant.getPotionPrice() + " oro (cura " + merchant.getPotionHeal() + " HP)");
        priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ffaa66;");

        Label quantityLabel = new Label("📦 Quantità da acquistare:");
        quantityLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

        // ComboBox con stile visibile
        ComboBox<Integer> quantityBox = new ComboBox<>();
        quantityBox.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-font-size: 14px;");

        if (maxBuyable > 0) {
            for (int i = 1; i <= Math.min(5, maxBuyable); i++) {
                quantityBox.getItems().add(i);
            }
            if (maxBuyable > 5) {
                quantityBox.getItems().add(maxBuyable);
            }
            quantityBox.setValue(1);
        } else {
            quantityBox.getItems().add(0);
            quantityBox.setValue(0);
            quantityBox.setDisable(true);
        }

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ff6666; -fx-font-size: 12px;");

        if (maxBuyable == 0) {
            if (currentPotionCount >= maxPotions) {
                errorLabel.setText("⚠ Inventario pieno! Massimo " + maxPotions + " pozioni.");
            } else if (merchantPotions == 0) {
                errorLabel.setText("⚠ Il mercante non ha più pozioni!");
            } else if (player.getGold() < merchant.getPotionPrice()) {
                errorLabel.setText("⚠ Oro insufficiente! Servono " + merchant.getPotionPrice() + " oro per pozione.");
            }
        }

        Button buyBtn = new Button("🛒 ACQUISTA");
        buyBtn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand;");
        buyBtn.setOnAction(e -> {
            int quantity = quantityBox.getValue();
            if (quantity <= 0) return;

            int totalCost = quantity * merchant.getPotionPrice();

            if (player.canAddPotions(quantity) && merchant.canSell(quantity) && player.getGold() >= totalCost) {
                player.addGold(-totalCost);
                merchant.sellPotion(quantity);

                boolean found = false;
                for (Item item : player.getInventory()) {
                    if (item instanceof HealthPotion) {
                        ((HealthPotion) item).addQuantity(quantity);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    player.addItem(merchant.createPotion(quantity));
                }

                addGameMessage("🏪 Hai acquistato " + quantity + " pozione/i per " + totalCost + " oro!");
                updatePlayerUI(player);
                updateInventory();
                stage.close();
            } else {
                addGameMessage("❌ Non puoi acquistare! Controlla oro, spazio inventario e disponibilità mercante.");
            }
        });

        Button closeBtn = new Button("🚪 USCITA");
        closeBtn.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> stage.close());

        content.getChildren().addAll(title, goldLabel, potionCountLabel, merchantStockLabel, priceLabel,
                new Separator(), quantityLabel, quantityBox, buyBtn, errorLabel, closeBtn);

        Scene scene = new Scene(content, 350, 450);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * Aggiorna l'interfaccia dopo ogni movimento del giocatore.
     * Aggiorna mappa, inventario, statistiche e verifica la presenza del mercante.
     */
    private void updateAfterMove() {
        forceUIUpdate();
        checkForMerchant();
        updateMap();
        updateInventory();
        updatePlayerUI(gameController.getPlayer());
        updateCombatUI();

        roomDescriptionArea.setText(
                gameController.getCurrentRoom().getName() + "\n" +
                        gameController.getCurrentRoom().getDescription()
        );
    }

    /**
     * Forza un aggiornamento completo dell'interfaccia utente.
     * Utile per sincronizzare la UI dopo azioni che modificano lo stato.
     */
    private void forceUIUpdate() {
        Platform.runLater(() -> {
            if (gameController.getPlayer() != null) {
                updatePlayerUI(gameController.getPlayer());
                updateInventory();
                updateMap();
                updateCombatUI();

                if (!gameController.isInCombat()) {
                    combatPanel.setVisible(false);
                    combatPanel.setManaged(false);
                }
            }
        });
    }

    /**
     * Carica l'immagine del personaggio in base alla classe selezionata.
     * Le immagini sono nella cartella src/main/resources/images/
     *
     * @param characterClass la classe del personaggio (Warrior, Mage, Rogue)
     */
    private void loadPlayerImage(String characterClass) {
        Image img = ImageLoader.getPlayerImage(characterClass);
        if (img != null) {
            playerImageView.setImage(img);
            playerCombatImageView.setImage(img);
        } else {
            // Fallback a emoji
            playerImageView.setImage(null);
            playerCombatImageView.setImage(null);
        }
    }

    /**
     * Carica l'immagine del nemico
     * Le immagini sono nella cartella src/main/resources/images/
     *
     * @param enemyName la classe del nemico
     */
    private void loadEnemyImage(String enemyName) {
        Image img = ImageLoader.getEnemyImage(enemyName);
        if (img != null) {
            enemyImageView.setImage(img);
        } else {
            // Usa emoji come fallback
            enemyNameLabel.setText(getEnemyIcon(enemyName) + " " + enemyName);
            enemyImageView.setImage(null);
        }
    }

    /**
     * Avvia il checker periodico dello stato del gioco.
     * Controlla ogni 0.5 secondi se il gioco è stato vinto o perso.
     */
    private void startGameStatusChecker() {
        if (gameStatusChecker != null) {
            gameStatusChecker.stop();
        }

        gameStatusChecker = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> {
                    if (gameController != null) {
                        checkGameStatus();
                    }
                })
        );
        gameStatusChecker.setCycleCount(Timeline.INDEFINITE);
        gameStatusChecker.play();
    }

    /**
     * Controlla lo stato corrente del gioco.
     * Se il giocatore è morto, attiva il Game Over.
     * Se il giocatore ha vinto, attiva la schermata di vittoria.
     */
    private void checkGameStatus() {
        if (gameController == null) return;

        // Controlla se il giocatore è morto
        if (gameController.getPlayer() != null && !gameController.isPlayerAlive()) {
            if (!gameController.isGameOver()) {
                gameController.setGameOver(true);
            }
        }

        if (gameController.isGameWon()) {
            if (gameStatusChecker != null) {
                gameStatusChecker.stop();
            }
            gameController.setGameWon(false);
            showVictoryScreen();
        } else if (gameController.isGameOver()) {
            if (gameStatusChecker != null) {
                gameStatusChecker.stop();
            }
            gameController.setGameOver(false);
            showGameOverScreen();
        }
    }

    /**
     * Mostra la schermata di Game Over.
     * Visualizza le statistiche finali e offre le opzioni "Nuova Partita" o "Esci".
     */
    private void showGameOverScreen() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("GAME OVER");
            alert.setHeaderText("💀 SEI STATO SCONFITTO! 💀");

            Player p = gameController.getPlayer();
            String stats = String.format(
                    "\n📊 STATISTICHE FINALI:\n\n" +
                            "⭐ Nome: %s\n" +
                            "⚔ Classe: %s\n" +
                            "🏆 Livello: %d\n" +
                            "👹 Nemici sconfitti: %d\n" +
                            "💰 Oro: %d\n" +
                            "⚗ Pozioni rimaste: %d\n" +
                            "🏅 Punteggio: %d",
                    p.getName(),
                    p.getCharacterClass(),
                    p.getLevel(),
                    gameController.getEnemiesDefeated(),
                    p.getGold(),
                    p.getPotionCount(),
                    p.getLevel() * 100 + gameController.getEnemiesDefeated() * 10 + p.getGold()
            );

            alert.setContentText(stats);

            ButtonType newGameBtn = new ButtonType("✨ NUOVA PARTITA");
            ButtonType exitBtn = new ButtonType("❌ ESCI");
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

    /**
     * Mostra la schermata di Vittoria.
     * Salva il punteggio, visualizza le statistiche finali e offre le opzioni "Nuova Partita" o "Esci".
     */
    private void showVictoryScreen() {
        Platform.runLater(() -> {
            Player p = gameController.getPlayer();

            // Salva il punteggio
            Score score = new Score(
                    p.getName(),
                    p.getCharacterClass(),
                    p.getLevel(),
                    gameController.getEnemiesDefeated(),
                    p.getGold()
            );
            saveScore(score);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("VITTORIA!");
            alert.setHeaderText("🏆 HAI VINTO IL GIOCO! 🏆");

            String stats = String.format(
                    "\n📊 STATISTICHE FINALI:\n\n" +
                            "⭐ Nome: %s\n" +
                            "⚔ Classe: %s\n" +
                            "🏆 Livello: %d\n" +
                            "👹 Nemici sconfitti: %d\n" +
                            "💰 Oro: %d\n" +
                            "⚗ Pozioni rimaste: %d\n" +
                            "🏅 Punteggio: %d",
                    p.getName(),
                    p.getCharacterClass(),
                    p.getLevel(),
                    gameController.getEnemiesDefeated(),
                    p.getGold(),
                    p.getPotionCount(),
                    p.getLevel() * 100 + gameController.getEnemiesDefeated() * 10 + p.getGold()
            );

            alert.setContentText(stats);

            ButtonType newGameBtn = new ButtonType("✨ NUOVA PARTITA");
            ButtonType exitBtn = new ButtonType("❌ ESCI");
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

    /**
     * Mostra il dialog per la creazione del personaggio.
     * Permette di scegliere nome e classe (Guerriero, Mago, Ladro).
     */
    public void showCharacterCreation() {
        System.out.println("=== SHOW CHARACTER CREATION ===");

        startScreen.setVisible(false);
        startScreen.setManaged(false);

        Stage stage = new Stage();
        stage.setTitle("✨ Nuova Avventura ✨");
        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new javafx.geometry.Insets(20));
        content.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 15;");

        Label title = new Label("Crea il tuo eroe");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e94560;");

        Label nameLabel = new Label("📝 Nome:");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        TextField nameField = new TextField();
        nameField.setPromptText("Inserisci il nome");
        nameField.setPrefWidth(250);
        nameField.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-prompt-text-fill: #888888; -fx-border-color: #e94560; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10; -fx-font-size: 14px;");
        nameField.clear();

        Platform.runLater(() -> nameField.requestFocus());

        Label classLabel = new Label("⚔ Classe:");
        classLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        ComboBox<String> classBox = new ComboBox<>();
        classBox.getItems().addAll("Guerriero", "Mago", "Ladro");
        classBox.setValue("Guerriero");
        classBox.setPrefWidth(250);
        classBox.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-border-color: #e94560; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8;");

        Label classDesc = new Label();
        classDesc.setStyle("-fx-text-fill: #c0c0c0; -fx-padding: 5 0 0 0;");
        classBox.setOnAction(e -> {
            switch(classBox.getValue()) {
                case "Guerriero": classDesc.setText("💪 Guerriero: Alto HP, medio attacco, buona difesa"); break;
                case "Mago": classDesc.setText("🔮 Mago: Alto attacco, Basso hp, buona difesa"); break;
                case "Ladro": classDesc.setText("🗡Ladro: Attacco e difesa equilibrati, buon hp"); break;
            }
        });
        classDesc.setText("💪 Guerriero: Alto HP, buona difesa, attacco potente");

        Button startBtn = new Button("⚔ INIZIA L'AVVENTURA ⚔");
        startBtn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        startBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty() && classBox.getValue() != null) {

                String englishClass;
                switch(classBox.getValue()) {
                    case "Guerriero": englishClass = "Warrior"; break;
                    case "Mago": englishClass = "Mage"; break;
                    case "Ladro": englishClass = "Rogue"; break;
                    default: englishClass = "Warrior";
                }

                gameController.startNewGame(name, classBox.getValue());
                stage.close();
                showGameUI();
            } else if (name.isEmpty()) {
                nameField.setPromptText("Inserisci un nome!");
                nameField.setStyle("-fx-border-color: red; -fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-prompt-text-fill: #ff6666; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8;");
                Platform.runLater(() -> nameField.requestFocus());
            }
        });

        Button cancelBtn = new Button("Annulla");
        cancelBtn.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 10 20; -fx-background-radius: 8;");
        cancelBtn.setOnAction(e -> {
            stage.close();
            startScreen.setVisible(true);
            startScreen.setManaged(true);
        });

        HBox buttons = new HBox(15, startBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER);

        Separator sep = new Separator();

        content.getChildren().addAll(title, sep, nameLabel, nameField, classLabel, classBox, classDesc, buttons);

        Scene scene = new Scene(content, 380, 450);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * Salva il punteggio del giocatore nel file scores.json.
     * Mantiene solo i top 10 punteggi in ordine decrescente.
     *
     * @param score il punteggio da salvare
     */
     private void saveScore(Score score) {
        try {
            List<Score> scores = loadScoresFromFile();

            // Aggiungi sempre il nuovo punteggio
            scores.add(score);

            // Ordina per punteggio decrescente
            scores.sort((a, b) -> Integer.compare(b.getTotalScore(), a.getTotalScore()));

            // Mantieni solo top 10 (se vuoi solo i migliori)
            if (scores.size() > 10) {
                scores = scores.subList(0, 10);
            }

            File file = new File(SCORES_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(scores);
            oos.close();

            System.out.println("Punteggio salvato per: " + score.getPlayerName());
        } catch (Exception e) {
            System.err.println("Errore salvataggio: " + e.getMessage());
        }
    }

    /**
     * Carica i punteggi dal file scores.json.
     *
     * @return lista dei punteggi salvati, vuota se il file non esiste o è corrotto
     */
    private List<Score> loadScoresFromFile() {
        try {
            File file = new File("scores.json");
            if (file.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                List<Score> scores = (List<Score>) ois.readObject();
                ois.close();
                return scores;
            }
        } catch (Exception e) {
            System.err.println("Errore caricamento: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Mostra la classifica dei migliori punteggi in un Alert.
     * Visualizza i top 10 giocatori con nome, classe, livello e punteggio.
     */
    private void showLeaderboard() {
        List<Score> scores = loadScoresFromFile();

        StringBuilder message = new StringBuilder();
        message.append("🏆 CLASSIFICA - TOP 10 🏆\n\n");

        if (scores.isEmpty()) {
            message.append("📋 Nessun punteggio registrato ancora!\n");
            message.append("Gioca e batti i record!");
        } else {
            for (int i = 0; i < scores.size(); i++) {
                Score s = scores.get(i);
                String medal;
                if (i == 0) medal = "🥇 ";
                else if (i == 1) medal = "🥈 ";
                else if (i == 2) medal = "🥉 ";
                else medal = "   ";

                // Traduci la classe in italiano per visualizzazione
                String italianClass;
                switch(s.getCharacterClass()) {
                    case "Warrior": italianClass = "Guerriero"; break;
                    case "Mage": italianClass = "Mago"; break;
                    case "Rogue": italianClass = "Ladro"; break;
                    default: italianClass = s.getCharacterClass();
                }

                message.append(String.format("%s%d. %s - %s (Lv.%d) - %d punti\n",
                        medal, i + 1, s.getPlayerName(), italianClass,
                        s.getLevel(), s.getTotalScore()));
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Classifica");
        alert.setHeaderText("🏆 CLASSIFICA");
        alert.setContentText(message.toString());
        alert.showAndWait();
    }

    /**
     * Crea la legenda con le immagini dei personaggi e nemici.
     * Mostra anche l'ordine di difficoltà dei nemici dal più debole al più forte.
     */
    private void setupLegend() {
        legendBox.getChildren().clear();
        legendBox.setStyle("-fx-padding: 5;");

        Label title = new Label("📖 LEGENDA");
        title.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #e94560;");
        legendBox.getChildren().add(title);
        legendBox.getChildren().add(new Separator());

        // FRASE INFORMATIVA SULL'ORDINE DEI NEMICI - con più Label
        Label orderInfo1 = new Label("⚠ I nemici sono elencati in ordine");
        orderInfo1.setStyle("-fx-text-fill: #ffaa66; -fx-font-size: 10px;");
        legendBox.getChildren().add(orderInfo1);

        Label orderInfo2 = new Label("dal più debole al più forte!");
        orderInfo2.setStyle("-fx-text-fill: #ffaa66; -fx-font-size: 10px;");
        legendBox.getChildren().add(orderInfo2);

        Label orderInfo3 = new Label("Affrontali con cautela!");
        orderInfo3.setStyle("-fx-text-fill: #ffaa66; -fx-font-size: 10px; -fx-padding: 0 0 5 0;");
        legendBox.getChildren().add(orderInfo3);

        // Giocatore (USA L'IMMAGINE DELLA CLASSE SCELTA)
        HBox playerBox = new HBox(8);
        playerBox.setAlignment(Pos.CENTER_LEFT);

        // Carica l'immagine in base alla classe del giocatore
        String playerClass = "guerriero"; // default
        if (gameController != null && gameController.getPlayer() != null) {
            String englishClass = gameController.getPlayer().getCharacterClass();
            // Traduco inglese → italiano per il nome del file
            switch(englishClass) {
                case "Warrior": playerClass = "guerriero"; break;
                case "Mage": playerClass = "mago"; break;
                case "Rogue": playerClass = "ladro"; break;
                default: playerClass = "guerriero";
            }
        }

        ImageView playerImg = new ImageView(ImageLoader.loadImage("/images/" + playerClass + ".png"));
        playerImg.setFitWidth(24);
        playerImg.setFitHeight(24);
        playerBox.getChildren().addAll(playerImg, new Label("= Tu"));
        legendBox.getChildren().add(playerBox);

        // Goblin
        HBox goblinBox = new HBox(8);
        goblinBox.setAlignment(Pos.CENTER_LEFT);
        ImageView goblinImg = new ImageView(ImageLoader.loadImage("/images/goblin.png"));
        goblinImg.setFitWidth(24);
        goblinImg.setFitHeight(24);
        goblinBox.getChildren().addAll(goblinImg, new Label("= Goblin"));
        legendBox.getChildren().add(goblinBox);

        // Wolf
        HBox wolfBox = new HBox(8);
        wolfBox.setAlignment(Pos.CENTER_LEFT);
        ImageView wolfImg = new ImageView(ImageLoader.loadImage("/images/lupo.png"));
        wolfImg.setFitWidth(24);
        wolfImg.setFitHeight(24);
        wolfBox.getChildren().addAll(wolfImg, new Label("= Lupo"));
        legendBox.getChildren().add(wolfBox);

        // Skeleton
        HBox skeletonBox = new HBox(8);
        skeletonBox.setAlignment(Pos.CENTER_LEFT);
        ImageView skeletonImg = new ImageView(ImageLoader.loadImage("/images/scheletro.png"));
        skeletonImg.setFitWidth(24);
        skeletonImg.setFitHeight(24);
        skeletonBox.getChildren().addAll(skeletonImg, new Label("= Scheletro"));
        legendBox.getChildren().add(skeletonBox);

        // Orc
        HBox orcBox = new HBox(8);
        orcBox.setAlignment(Pos.CENTER_LEFT);
        ImageView orcImg = new ImageView(ImageLoader.loadImage("/images/orco.png"));
        orcImg.setFitWidth(24);
        orcImg.setFitHeight(24);
        orcBox.getChildren().addAll(orcImg, new Label("= Orco"));
        legendBox.getChildren().add(orcBox);

        // Dark Knight
        HBox knightBox = new HBox(8);
        knightBox.setAlignment(Pos.CENTER_LEFT);
        ImageView knightImg = new ImageView(ImageLoader.loadImage("/images/cavaliereoscuro.png"));
        knightImg.setFitWidth(24);
        knightImg.setFitHeight(24);
        knightBox.getChildren().addAll(knightImg, new Label("= Cavaliere Oscuro"));
        legendBox.getChildren().add(knightBox);

        // Dragon
        HBox dragonBox = new HBox(8);
        dragonBox.setAlignment(Pos.CENTER_LEFT);
        ImageView dragonImg = new ImageView(ImageLoader.loadImage("/images/drago.png"));
        dragonImg.setFitWidth(24);
        dragonImg.setFitHeight(24);
        dragonBox.getChildren().addAll(dragonImg, new Label("= Drago (Boss)"));
        legendBox.getChildren().add(dragonBox);

        // Merchant
        HBox merchantBox = new HBox(8);
        merchantBox.setAlignment(Pos.CENTER_LEFT);
        ImageView merchantImg = new ImageView(ImageLoader.loadImage("/images/mercante.png"));
        merchantImg.setFitWidth(24);
        merchantImg.setFitHeight(24);
        merchantBox.getChildren().addAll(merchantImg, new Label("= Mercante"));
        legendBox.getChildren().add(merchantBox);

        // Porta del Tesoro
        HBox doorBox = new HBox(8);
        doorBox.setAlignment(Pos.CENTER_LEFT);
        Label doorLabel = new Label("🚪");
        doorLabel.setStyle("-fx-font-size: 18px;");
        doorBox.getChildren().addAll(doorLabel, new Label("= Porta del Tesoro"));
        legendBox.getChildren().add(doorBox);

        // Pozione
        HBox potionBox = new HBox(8);
        potionBox.setAlignment(Pos.CENTER_LEFT);
        ImageView potionImg = new ImageView(ImageLoader.loadImage("/images/pozione.png"));
        potionImg.setFitWidth(24);
        potionImg.setFitHeight(24);
        potionBox.getChildren().addAll(potionImg, new Label("= Pozione curativa"));
        legendBox.getChildren().add(potionBox);
    }

    /**
     * Mostra il dialog di aiuto con tutti i comandi del gioco.
     */
    private void showHelpDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Comandi");
        alert.setHeaderText("🎮 COME GIOCARE");
        alert.setContentText("""
        ═══════════════════════════════════════
        
        🗺 MOVIMENTO:
        • Tasti WASD o FRECCE per muoverti
                    
        ⚔ COMBATTIMENTO (tasti rapidi):
        • Z = ATTACCA
        • X = USA POZIONE
        • C = FUGGI
        
        🔘 BOTTONI SCHERMO:
        • ATTACCA - Colpisci il nemico
        • POZIONE 
            - Cura 20 HP se usata non 
              durante un combattimento
            - Cura un valore minore di 20 HP in combattimento perché
              viene usata mentre il personaggio è stordito
        • FUGGI - Scappa (50% successo)
        
        💰 RICOMPENSE:
        • Sconfiggi nemici per XP e oro
        • Salendo di livello aumentano le statistiche del proprio personaggio 
        • Trova oro e pozioni nelle stanze
        
        👑 OBIETTIVO:
        Sconfiggi i 3 Draghi che circondano l'uscita e entra nella porta finale!
        
        ═══════════════════════════════════════
        """);
        alert.showAndWait();
    }

    /**
     * Mostra il dialog informativo sul gioco (versione, matricola, corso).
     */
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ℹ️ Info");
        alert.setHeaderText("🏰 DUNGEON EXPLORER RPG");
        alert.setContentText("""
            Versione: 1.0
            Matricola: 129876
            Corso: Metodologie di Programmazione AA 2025/26
            
            Un gioco di ruolo con esplorazione e nemici da sconfiggere.
            
            🎮 Buon divertimento!
            """);
        alert.showAndWait();
    }
}