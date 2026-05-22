package it.unicam.cs.mpgc.rpg129876.controller;

import it.unicam.cs.mpgc.rpg129876.model.characters.Merchant;
import it.unicam.cs.mpgc.rpg129876.model.characters.Player;
import it.unicam.cs.mpgc.rpg129876.model.characters.Enemy;
import it.unicam.cs.mpgc.rpg129876.model.combat.CombatSystem;
import it.unicam.cs.mpgc.rpg129876.model.items.HealthPotion;
import it.unicam.cs.mpgc.rpg129876.model.items.Item;
import it.unicam.cs.mpgc.rpg129876.model.world.Direction;
import it.unicam.cs.mpgc.rpg129876.model.world.Dungeon;
import it.unicam.cs.mpgc.rpg129876.model.world.Room;
import javafx.application.Platform;
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

    private Merchant currentMerchant;  // Semplice variabile

    public Merchant getCurrentMerchant() {
        return currentMerchant;
    }

    public void setCurrentMerchant(Merchant merchant) {
        this.currentMerchant = merchant;
    }

    public GameController() {
        // Inizializzazione vuota, il gioco parte con newGame()
    }

    // Setter
    public void setGameWon(boolean won) { this.gameWon = won; }
    public void setGameOver(boolean over) { this.gameOver = over; }


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

    private void setInCombat(boolean value) {
        this.inCombat.set(value);
    }

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
        player.addItem(new HealthPotion(4));
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

        System.out.println("=== checkRoomEvents ===");
        System.out.println("Stanza: " + currentRoom.getName());
        System.out.println("Has enemy: " + currentRoom.hasEnemy());
        System.out.println("Is door room: " + currentRoom.isDoorRoom());

        // Se è la stanza della porta e tutti i draghi sono sconfitti -> VITTORIA
        if (currentRoom.isDoorRoom() && dungeon.areAllDragonsDefeated()) {
            addGameMessage("🎉 HAI APERTO LA PORTA DEL TESORO! HAI VINTO IL GIOCO! 🎉");
            gameWon = true;
            inCombat.set(false);
            return;
        }

        // Controlla se c'è un mercante
        if (currentRoom.hasMerchant()) {
            setCurrentMerchant(currentRoom.getMerchant());
            addGameMessage("🏪 Entri in un negozio! " + currentRoom.getMerchant().getName() + " ti aspetta.");
            return;
        }

        // Controlla se ci sono tesori
        if (currentRoom.hasTreasures()) {
            collectTreasures();
            // Dopo aver raccolto tesori, controlla se c'è anche un nemico
            if (currentRoom.hasEnemy()) {
                System.out.println("Tesoro raccolto, ora inizia combattimento con: " + currentRoom.getEnemy().getName());
                startCombat(currentRoom.getEnemy());
            }
            return;
        }

        // CONTROLLA NEMICO (priorità massima)
        if (currentRoom.hasEnemy()) {
            System.out.println("NEMICO TROVATO! Avvio combattimento con: " + currentRoom.getEnemy().getName());
            startCombat(currentRoom.getEnemy());
            return;
        }

        // Controlla se è la stanza del boss (solo se non ci sono nemici)
        if (dungeon.isBossRoom()) {
            addGameMessage("👑 Sei entrato nella sala del trono... La porta si apre!");
            // Vittoria già gestita sopra
            return;
        }

        addGameMessage("🔍 Esplori la stanza... " + currentRoom.getDescription());
    }

    public void openMerchantDialog(Merchant merchant) {
        // Notifica la UI che c'è un mercante
        this.currentMerchant = merchant;
        // Questo verrà gestito dal MainController
    }

    private void collectTreasures() {
        Room currentRoom = dungeon.getCurrentRoom();
        var treasures = currentRoom.collectTreasures();

        if (treasures.isEmpty()) {
            return;
        }

        for (Item item : treasures) {
            player.addItem(item);
            addGameMessage("💰 Trovi un tesoro! Ottieni: " + item.getIcon() + " " + item.getName());

            // Se è oro, aggiungi direttamente al contatore
            if (item.getName().contains("Gold") || item.getIcon().equals("💰")) {
                int goldAmount = 30;  // Oro trovato
                player.addGold(goldAmount);
                addGameMessage("💰 +" + goldAmount + " monete d'oro!");
            }
        }

        // Aggiorna la UI
        updatePlayerStats();
    }

    private void startCombat(Enemy enemy) {
        System.out.println("=== startCombat ===");
        System.out.println("Nemico: " + enemy.getName());

        this.currentCombat = new CombatSystem(player, enemy);
        setInCombat(true);  // Usa il setter della property

        addGameMessage("⚔️ COMBATTIMENTO INIZIATO! ⚔️");
        addGameMessage("🛡 Nemico: " + enemy.getName() + " (HP: " + enemy.getHp() + "/" + enemy.getMaxHp() + ")");
    }

    public void playerAttack() {
        System.out.println("=== playerAttack chiamato ===");
        System.out.println("currentCombat: " + currentCombat);
        System.out.println("isInCombat: " + (currentCombat != null && currentCombat.isInCombat()));

        if (currentCombat != null && currentCombat.isInCombat()) {
            CombatSystem.CombatResult result = currentCombat.playerAttack();
            System.out.println("Risultato attacco: " + result.getMessage());
            System.out.println("Combat ended: " + result.isCombatEnded());

            addGameMessage(result.getMessage());
            combatLog.set(result.getMessage());

            if (result.isCombatEnded() && result.isPlayerWon()) {
                System.out.println("PLAYER HA VINTO! Chiamo endCombat");
                endCombat(true);
            } else if (currentCombat.isInCombat()) {
                System.out.println("Turno del nemico");
                CombatSystem.CombatResult enemyResult = currentCombat.enemyTurn();
                addGameMessage(enemyResult.getMessage());
                combatLog.set(enemyResult.getMessage());

                if (enemyResult.isCombatEnded() && !enemyResult.isPlayerWon()) {
                    System.out.println("PLAYER HA PERSO! Chiamo endCombat");
                    endCombat(false);
                }
            }
        } else {
            System.out.println("ERRORE: currentCombat è null o non in combat");
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
            int oldHp = player.getHp();
            CombatSystem.CombatResult result = currentCombat.useItem(item);
            addGameMessage(result.getMessage());
            combatLog.set(result.getMessage());

            int newHp = player.getHp();
            int healed = newHp - oldHp;

            if (healed > 0) {
                addGameMessage("🧪 La pozione ti ha curato " + healed + " HP! (da " + oldHp + " a " + newHp + ")");
            } else if (healed == 0) {
                addGameMessage("❌ Non hai bisogno di cura! Sei già a piena vita.");
            }

            // Aggiorna l'inventario
            if (item instanceof HealthPotion && ((HealthPotion) item).isEmpty()) {
                player.removeItem(item);
                addGameMessage("📦 La pozione è terminata!");
            }

            updatePlayerStats();

            if (currentCombat.getEnemy().isAlive()) {
                enemyTurn();
            }
            updateCombatLog();
        } else {
            int oldHp = player.getHp();
            item.use(player);
            int newHp = player.getHp();
            int healed = newHp - oldHp;

            if (healed > 0) {
                addGameMessage("🧪 Usato: " + item.getName() + " +" + healed + " HP (da " + oldHp + " a " + newHp + ")");
            } else if (healed == 0) {
                addGameMessage("❌ Non hai bisogno di cura! Sei già a piena vita.");
            }

            if (item instanceof HealthPotion && ((HealthPotion) item).isEmpty()) {
                player.removeItem(item);
            }
        }
        updatePlayerStats();
    }

    public void flee() {
        if (currentCombat != null && currentCombat.isInCombat()) {
            CombatSystem.CombatResult result = currentCombat.flee();
            addGameMessage(result.getMessage());
            combatLog.set(result.getMessage());

            if (!currentCombat.isInCombat()) {
                setInCombat(false);  // Importante: notifica la UI
                currentCombat = null;
                addGameMessage("🏃 Sei riuscito a fuggire! Il combattimento è terminato.");
            } else {
                updatePlayerStats();
                addGameMessage("❤️ HP rimanenti dopo fuga fallita: " + player.getHp() + "/" + player.getMaxHp());
            }
        }
    }

    private void endCombat(boolean playerWon) {
        System.out.println("=== endCombat ===");
        System.out.println("playerWon: " + playerWon);

        if (!playerWon) {
            addGameMessage("💀 GAME OVER - Sei stato sconfitto... 💀");
            gameOver = true;
            inCombat.set(false);
            currentCombat = null;
            return;
        }

        if (playerWon && currentCombat != null) {
            int oldLevel = player.getLevel();

            currentCombat.awardRewards();
            addGameMessage("✨ VITTORIA! ✨");
            addGameMessage("🏆 Guadagnati: " + currentCombat.getEnemy().getExperienceReward() + " XP e " +
                    currentCombat.getEnemy().getGoldReward() + " monete d'oro!");

            enemiesDefeated++;

            // Rimuovi il nemico dalla stanza
            dungeon.getCurrentRoom().setEnemy(null);

            // CONTROLLA VITTORIA DEL GIOCO - quando si apre la porta
            if (dungeon.getCurrentRoom().isDoorRoom() && dungeon.areAllDragonsDefeated()) {
                addGameMessage("🎉🎉🎉 CONGRATULAZIONI! HAI APERTO LA PORTA DEL TESORO E VINTO IL GIOCO! 🎉🎉🎉");
                gameWon = true;
                inCombat.set(false);
                currentCombat = null;
                return;
            }

            if (player.getLevel() > oldLevel) {
                addGameMessage("🎉 Congratulazioni! Sei salito al livello " + player.getLevel() + "! 🎉");
            }
        }

        inCombat.set(false);
        currentCombat = null;
        updatePlayerStats();
    }

    // Conta quanti draghi sono ancora vivi intorno alla porta
    private int countRemainingDragons() {
        int bossX = dungeon.getWidth() - 1;
        int bossY = dungeon.getHeight() - 1;
        int count = 0;

        if (bossX - 1 >= 0) {
            Room leftRoom = dungeon.getRoomAt(bossX - 1, bossY);
            if (leftRoom.hasDragon() && leftRoom.hasEnemy()) count++;
        }
        if (bossY - 1 >= 0) {
            Room upRoom = dungeon.getRoomAt(bossX, bossY - 1);
            if (upRoom.hasDragon() && upRoom.hasEnemy()) count++;
        }
        if (bossX - 1 >= 0 && bossY - 1 >= 0) {
            Room diagRoom = dungeon.getRoomAt(bossX - 1, bossY - 1);
            if (diagRoom.hasDragon() && diagRoom.hasEnemy()) count++;
        }
        return count;
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