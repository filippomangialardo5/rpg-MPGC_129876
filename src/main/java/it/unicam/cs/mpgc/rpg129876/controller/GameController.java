package it.unicam.cs.mpgc.rpg129876.controller;

import it.unicam.cs.mpgc.rpg129876.model.characters.Player;
import it.unicam.cs.mpgc.rpg129876.model.characters.Enemy;
import it.unicam.cs.mpgc.rpg129876.model.combat.CombatSystem;
import it.unicam.cs.mpgc.rpg129876.model.items.HealthPotion;
import it.unicam.cs.mpgc.rpg129876.model.items.Item;
import it.unicam.cs.mpgc.rpg129876.model.world.Direction;
import it.unicam.cs.mpgc.rpg129876.model.world.Dungeon;
import it.unicam.cs.mpgc.rpg129876.model.world.Room;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GameController {

    private Player player;
    private Dungeon dungeon;
    private CombatSystem currentCombat;

    private boolean gameWon = false;
    private boolean gameOver = false;

    public boolean isGameWon() { return gameWon; }
    public boolean isGameOver() { return gameOver; }
    public void resetGameFlags() { gameWon = false; gameOver = false; }

    // Observable properties per il binding con la UI
    private final ObjectProperty<Player> currentPlayer = new SimpleObjectProperty<>();
    private final BooleanProperty inCombat = new SimpleBooleanProperty(false);
    private final StringProperty combatLog = new SimpleStringProperty("");
    private final StringProperty roomDescription = new SimpleStringProperty("");
    private final ObservableList<String> gameMessages = FXCollections.observableArrayList();

    private int enemiesDefeated = 0;

    public GameController() {
        // Inizializzazione vuota, il gioco parte con newGame()
    }

    // Property getters per binding JavaFX
    public ObjectProperty<Player> currentPlayerProperty() { return currentPlayer; }
    public BooleanProperty inCombatProperty() { return inCombat; }
    public StringProperty combatLogProperty() { return combatLog; }
    public StringProperty roomDescriptionProperty() { return roomDescription; }
    public ObservableList<String> getGameMessages() { return gameMessages; }

    public Player getPlayer() { return player; }
    public Dungeon getDungeon() { return dungeon; }
    public boolean isInCombat() { return inCombat.get(); }

    public int getEnemiesDefeated() { return enemiesDefeated; }

    // Nuova partita
    public void startNewGame(String playerName, String characterClass) {
        this.player = new Player(playerName, characterClass);
        this.dungeon = new Dungeon(8, 8);  // Mappa 8x8 (puoi cambiare dimensione)
        this.currentPlayer.set(player);

        addStartingItems();
        updateRoomInfo();
        addGameMessage("✨ Benvenuto " + playerName + " il " + characterClass + "!");
        addGameMessage("🏰 La tua avventura nel dungeon di " + dungeon.getWidth() + "x" + dungeon.getHeight() + " stanze ha inizio!");
        addGameMessage("📍 Ti trovi nella stanza: " + dungeon.getCurrentRoom().getName());
    }

    private void addStartingItems() {
        player.addItem(new HealthPotion(3));
    }

    // Movimento
    public boolean move(Direction direction) {
        if (inCombat.get()) {
            addGameMessage("⚠️ Non puoi muoverti durante il combattimento!");
            return false;
        }

        if (dungeon.move(direction)) {
            addGameMessage("🚶 Ti muovi verso " + direction.getDisplayName());
            updateRoomInfo();
            checkRoomEvents();
            return true;
        } else {
            addGameMessage("🚫 Non puoi andare in quella direzione!");
            return false;
        }
    }

    private void updateRoomInfo() {
        Room currentRoom = dungeon.getCurrentRoom();
        String info = "📍 " + currentRoom.getName() + "\n" +
                currentRoom.getDescription() + "\n" +
                "🚪 Uscite: " + currentRoom.getExitsDescription();
        roomDescription.set(info);
    }

    private void checkRoomEvents() {
        Room currentRoom = dungeon.getCurrentRoom();

        // Controlla se c'è un nemico
        if (currentRoom.hasEnemy()) {
            startCombat(currentRoom.getEnemy());
        }
        // Controlla se ci sono tesori
        else if (currentRoom.hasTreasures()) {
            collectTreasures();
        }
        // Controlla se è la stanza del boss
        else if (dungeon.isBossRoom()) {
            addGameMessage("👑 Sei entrato nella sala del trono... Il Drago si sveglia!");
            startCombat(currentRoom.getEnemy());
        }
        else {
            addGameMessage("🔍 Esplori la stanza... " + currentRoom.getDescription());
        }
    }

    private void collectTreasures() {
        Room currentRoom = dungeon.getCurrentRoom();
        var treasures = currentRoom.collectTreasures();

        for (Item item : treasures) {
            player.addItem(item);
            addGameMessage("💰 Trovi un tesoro! Ottieni: " + item.getIcon() + " " + item.getName());
        }
    }

    // Sistema di combattimento
    private void startCombat(Enemy enemy) {
        this.currentCombat = new CombatSystem(player, enemy);
        this.inCombat.set(true);
        addGameMessage("⚔ COMBATTIMENTO INIZIATO! ⚔");
        addGameMessage("🛡 Nemico: " + enemy.getName() + " (HP: " + enemy.getHp() + "/" + enemy.getMaxHp() + ")");
        updateCombatLog();
    }

    public void playerAttack() {
        if (currentCombat != null && currentCombat.isInCombat()) {
            CombatSystem.CombatResult result = currentCombat.playerAttack();

            // Mostra il messaggio dell'attacco
            addGameMessage(result.getMessage());
            combatLog.set(result.getMessage());

            // NON chiamare updateCombatUI qui - è responsabilità del MainController

            if (result.isCombatEnded() && result.isPlayerWon()) {
                endCombat(true);
            } else if (currentCombat.isInCombat()) {
                // Turno del nemico
                CombatSystem.CombatResult enemyResult = currentCombat.enemyTurn();
                addGameMessage(enemyResult.getMessage());
                combatLog.set(enemyResult.getMessage());

                if (enemyResult.isCombatEnded() && !enemyResult.isPlayerWon()) {
                    endCombat(false);
                }
            }
        }
    }
    private void enemyTurn() {
        if (currentCombat != null && currentCombat.isInCombat()) {
            CombatSystem.CombatResult result = currentCombat.enemyTurn();
            addGameMessage(result.getMessage());

            if (result.isCombatEnded() && !result.isPlayerWon()) {
                endCombat(false);
            }
            updateCombatLog();
        }
    }

    public void useItem(Item item) {
        if (currentCombat != null && currentCombat.isInCombat()) {
            CombatSystem.CombatResult result = currentCombat.useItem(item);
            addGameMessage(result.getMessage());

            // Aggiorna l'inventario se la pozione è finita
            if (item instanceof HealthPotion && ((HealthPotion) item).isEmpty()) {
                player.removeItem(item);
            }

            if (currentCombat.getEnemy().isAlive()) {
                enemyTurn();
            }
            updateCombatLog();
        } else {
            item.use(player);
            if (item instanceof HealthPotion && ((HealthPotion) item).isEmpty()) {
                player.removeItem(item);
            }
            addGameMessage("🧪 Usato: " + item.getName());
        }
        updatePlayerStats();
    }

    public void flee() {
        if (currentCombat != null && currentCombat.isInCombat()) {
            CombatSystem.CombatResult result = currentCombat.flee();
            addGameMessage(result.getMessage());
            combatLog.set(result.getMessage());

            if (!currentCombat.isInCombat()) {
                inCombat.set(false);
                currentCombat = null;
                addGameMessage("🏃 Sei uscito dal combattimento!");
            } else {
                // Aggiorna la UI per mostrare i danni subiti
                updatePlayerStats();
                addGameMessage("❤️ HP rimanenti: " + player.getHp() + "/" + player.getMaxHp());
            }
        }
    }

    private void endCombat(boolean playerWon) {
        if (playerWon && currentCombat != null) {
            enemiesDefeated++;
            currentCombat.awardRewards();
            addGameMessage("✨ VITTORIA! ✨");
            addGameMessage("🏆 Guadagnati: " + currentCombat.getEnemy().getExperienceReward() + " XP e " +
                    currentCombat.getEnemy().getGoldReward() + " monete d'oro!");

            // Rimuovi il nemico dalla stanza
            dungeon.getCurrentRoom().setEnemy(null);

            // CONTROLLA SE IL GIOCATORE HA VINTO IL GIOCO
            if (dungeon.isBossRoom() && currentCombat.getEnemy() instanceof Dragon) {
                addGameMessage("🎉🎉🎉 CONGRATULAZIONI! HAI SCONFITTO IL DRAGO E VINTO IL GIOCO! 🎉🎉🎉");
                gameWon = true;
                inCombat.set(false);
                currentCombat = null;
                // Notifica la UI della vittoria
                return;
            }

            if (player.getLevel() > oldLevel) {
                addGameMessage("🎉 Congratulazioni! Sei salito al livello " + player.getLevel() + "! 🎉");
            }
        } else if (!playerWon) {
            addGameMessage("💀 GAME OVER - Sei stato sconfitto... 💀");
            gameOver = true;
            inCombat.set(false);
            currentCombat = null;
        }

        inCombat.set(false);
        currentCombat = null;
        updatePlayerStats();
    }

    private void updateCombatLog() {
        if (currentCombat != null) {
            combatLog.set(currentCombat.getCombatStatus());
        }
    }

    private void updatePlayerStats() {
        currentPlayer.set(player);  // Forza aggiornamento binding
    }

    private void addGameMessage(String message) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
        gameMessages.add(0, "[" + timestamp + "] " + message);
        if (gameMessages.size() > 50) {
            gameMessages.remove(50);
        }
    }

    // Metodi per la mappa
    public Room getCurrentRoom() {
        return dungeon.getCurrentRoom();
    }

    public Room getRoomAt(int x, int y) {
        return dungeon.getRoomAt(x, y);
    }

    public int getDungeonWidth() {
        return dungeon.getWidth();
    }

    public int getDungeonHeight() {
        return dungeon.getHeight();
    }

    public boolean isPlayerAlive() {
        return player != null && player.isAlive();
    }

    public CombatSystem getCurrentCombat() {
        return currentCombat;
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
}