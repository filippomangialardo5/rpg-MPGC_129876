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
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Controller della logica di gioco.
 * Gestisce il movimento nel dungeon, il combattimento e la progressione del giocatore.
 * È indipendente dall'interfaccia grafica.
 *
 * @author Filippo Mangialardo
 * @version 1.0
 */
public class GameController {

    private Player player;
    private Dungeon dungeon;
    private CombatSystem currentCombat;

    private boolean gameWon = false;
    private boolean gameOver = false;

    // Observable properties per il binding con la UI
    private final ObjectProperty<Player> currentPlayer = new SimpleObjectProperty<>();
    private final BooleanProperty inCombat = new SimpleBooleanProperty(false);
    private final StringProperty combatLog = new SimpleStringProperty("");
    private final StringProperty roomDescription = new SimpleStringProperty("");
    private final ObservableList<String> gameMessages = FXCollections.observableArrayList();

    private int enemiesDefeated = 0;

    private Merchant currentMerchant;

    public boolean isGameWon() { return gameWon; }

    public boolean isGameOver() { return gameOver; }

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
    public StringProperty roomDescriptionProperty() { return roomDescription; }
    public ObservableList<String> getGameMessages() { return gameMessages; }

    public Player getPlayer() { return player; }
    public Dungeon getDungeon() { return dungeon; }
    public boolean isInCombat() { return inCombat.get(); }

    public int getEnemiesDefeated() { return enemiesDefeated; }

    private void setInCombat(boolean value) {
        this.inCombat.set(value);
    }

    private void addStartingItems() {
        player.addItem(new HealthPotion(4));
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

    /**
     * Avvia una nuova partita con il nome e la classe specificati.
     * Crea un nuovo giocatore, genera il dungeon e inizializza le variabili di gioco.
     *
     * @param playerName nome del giocatore
     * @param characterClass classe del personaggio (Warrior, Mage, Rogue)
     */
    public void startNewGame(String playerName, String characterClass) {
        this.player = new Player(playerName, characterClass);
        this.dungeon = new Dungeon(8, 8);  // Mappa 8x8 (puoi cambiare dimensione)
        this.currentPlayer.set(player);

        addStartingItems();
        updateRoomInfo();
        addGameMessage("✨ Benvenuto " + playerName + " il " + getItalianClassName(characterClass) + "!");
        addGameMessage("🏰 La tua avventura nel dungeon di " + dungeon.getWidth() + "x" + dungeon.getHeight() + " stanze ha inizio!");
        addGameMessage("📍 Ti trovi nella stanza: " + dungeon.getCurrentRoom().getName());
    }

    /**
     * Converte il nome della classe dall'inglese all'italiano per i messaggi di gioco.
     *
     * @param englishClass la classe in inglese (Warrior, Mage, Rogue)
     * @return la classe tradotta in italiano (Guerriero, Mago, Ladro)
     */
    private String getItalianClassName(String englishClass) {
        switch(englishClass) {
            case "Warrior": return "Guerriero";
            case "Mage": return "Mago";
            case "Rogue": return "Ladro";
            default: return englishClass;
        }
    }

    /**
     * Sposta il giocatore nella direzione specificata.
     * Verifica se il movimento è possibile e controlla gli eventi della nuova stanza.
     *
     * @param direction direzione del movimento (NORD, SUD, EST, OVEST)
     */
    public void move(Direction direction) {
        if (inCombat.get()) {
            addGameMessage("⚠ Non puoi muoverti durante il combattimento!");
            return;
        }

        if (dungeon.move(direction)) {
            addGameMessage("🚶 Ti muovi verso " + direction.getDisplayName());
            updateRoomInfo();
            checkRoomEvents();
        } else {
            addGameMessage("🚫 Non puoi andare in quella direzione!");
        }
    }

    /**
     * Aggiorna le informazioni della stanza corrente nell'interfaccia.
     * Compone il messaggio con nome, descrizione e uscite disponibili.
     */
    private void updateRoomInfo() {
        Room currentRoom = dungeon.getCurrentRoom();
        String info = "📍 " + currentRoom.getName() + "\n" +
                currentRoom.getDescription() + "\n" +
                "🚪 Uscite: " + currentRoom.getExitsDescription();
        roomDescription.set(info);
    }

    /**
     * Controlla gli eventi presenti nella stanza corrente.
     * Gestisce in ordine di priorità:
     * - Drago (priorità massima - non deve essere oscurato dal mercante)
     * - Nemici normali
     * - Mercante
     * - Tesori
     * - Stanza vuota
     */
    private void checkRoomEvents() {
        Room currentRoom = dungeon.getCurrentRoom();

        System.out.println("=== checkRoomEvents ===");
        System.out.println("Stanza: " + currentRoom.getName());
        System.out.println("Has enemy: " + currentRoom.hasEnemy());
        System.out.println("Has dragon: " + currentRoom.hasDragon());
        System.out.println("Has merchant: " + currentRoom.hasMerchant());
        System.out.println("Is door room: " + currentRoom.isDoorRoom());

        // Se è la stanza della porta e tutti i draghi sono sconfitti -> VITTORIA
        if (currentRoom.isDoorRoom() && dungeon.areAllDragonsDefeated()) {
            addGameMessage("🎉 HAI APERTO LA PORTA DEL TESORO! HAI VINTO IL GIOCO! 🎉");
            gameWon = true;
            inCombat.set(false);
            return;
        }

        // PRIORITÀ 1: Drago (più importante del mercante!)
        if (currentRoom.hasDragon() && currentRoom.hasEnemy() && currentRoom.getEnemy().isAlive()) {
            System.out.println("DRAGO TROVATO! Avvio combattimento con: " + currentRoom.getEnemy().getName());
            startCombat(currentRoom.getEnemy());
            return;
        }

        // PRIORITÀ 2: Altri nemici
        if (currentRoom.hasEnemy()) {
            System.out.println("NEMICO TROVATO! Avvio combattimento con: " + currentRoom.getEnemy().getName());
            startCombat(currentRoom.getEnemy());
            return;
        }

        // PRIORITÀ 3: Mercante (solo se non ci sono nemici)
        if (currentRoom.hasMerchant()) {
            setCurrentMerchant(currentRoom.getMerchant());
            addGameMessage("🏪 Entri in un negozio! " + currentRoom.getMerchant().getName() + " ti aspetta.");
            return;
        }

        // PRIORITÀ 4: Tesori
        if (currentRoom.hasTreasures()) {
            collectTreasures();
            return;
        }

        addGameMessage("🔍 Esplori la stanza... " + currentRoom.getDescription());
    }

    /**
     * Raccoglie automaticamente i tesori presenti nella stanza.
     * L'oro e le pozioni vengono aggiunti all'inventario.
     */
    private void collectTreasures() {
        Room currentRoom = dungeon.getCurrentRoom();
        var treasures = currentRoom.collectTreasures();

        if (treasures.isEmpty()) {
            return;
        }

        for (Item item : treasures) {
            if (item instanceof HealthPotion) {
                player.addItem(item);
                addGameMessage("🧪 Trovi una pozione curativa!");
            } else {
                // È oro
                String name = item.getName();
                int goldAmount = Integer.parseInt(name.replaceAll("[^0-9]", ""));
                player.addGold(goldAmount);
                addGameMessage("💰 Trovi " + goldAmount + " monete d'oro!");
            }
        }

        updatePlayerStats();
    }

    /**
     * Avvia un nuovo combattimento con il nemico specificato.
     * Crea il sistema di combattimento, imposta il flag inCombat e mostra i messaggi iniziali.
     *
     * @param enemy il nemico con cui combattere
     */
    private void startCombat(Enemy enemy) {
        System.out.println("=== startCombat ===");
        System.out.println("Nemico: " + enemy.getName());

        this.currentCombat = new CombatSystem(player, enemy);
        setInCombat(true);  // Usa il setter della property

        addGameMessage("⚔ COMBATTIMENTO INIZIATO! ⚔");
        addGameMessage("🛡 Nemico: " + enemy.getName() + " (HP: " + enemy.getHp() + "/" + enemy.getMaxHp() + ")");
    }

    /**
     * Esegue l'attacco del giocatore contro il nemico corrente.
     * Calcola il danno, aggiorna gli HP del nemico e verifica la fine del combattimento.
     */
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

    /**
     * Esegue il turno del nemico durante il combattimento.
     * Calcola il danno e applica le ferite al giocatore.
     */
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

    /**
     * Utilizza un oggetto dall'inventario.
     * Gestisce sia l'uso in combattimento che fuori combattimento.
     * Per le pozioni, mostra la quantità di HP recuperati.
     *
     * @param item l'oggetto da utilizzare
     */
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

    /**
     * Tenta la fuga dal combattimento.
     * Successo: 50% di probabilità, termina il combattimento.
     * Fallimento: subisci un attacco, il combattimento continua.
     */
    public void flee() {
        if (currentCombat != null && currentCombat.isInCombat()) {
            CombatSystem.CombatResult result = currentCombat.flee();
            addGameMessage(result.getMessage());
            combatLog.set(result.getMessage());

            if (!currentCombat.isInCombat()) {
                // Combattimento terminato (fuga riuscita O giocatore morto)
                if (!player.isAlive()) {
                    addGameMessage("💀 GAME OVER - Sei stato sconfitto... 💀");
                    gameOver = true;
                } else {
                    addGameMessage("🏃 Sei uscito dal combattimento!");
                }
                setInCombat(false);
                currentCombat = null;
            } else {
                // Fuga fallita ma giocatore ancora vivo, il combattimento continua
                updatePlayerStats();
                addGameMessage("❤️ HP rimanenti: " + player.getHp() + "/" + player.getMaxHp());
            }
        }
    }

    /**
     * Termina il combattimento, gestendo vittoria o sconfitta.
     * - Se il giocatore vince: assegna ricompense (XP, oro), rimuove il nemico dalla stanza
     * - Se il giocatore perde: imposta il flag gameOver
     * - Se viene sconfitto l'ultimo drago, attiva la vittoria del gioco
     *
     * @param playerWon true se il giocatore ha vinto, false se ha perso
     */
    private void endCombat(boolean playerWon) {
        System.out.println("=== endCombat ===");
        System.out.println("playerWon: " + playerWon);

        // Evita chiamate multiple
        if (!inCombat.get()) {
            System.out.println("endCombat: già uscito dal combattimento");
            return;
        }

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
            dungeon.getCurrentRoom().setEnemy(null);

            // VITTORIA DEL GIOCO (solo se si apre la porta)
            if (dungeon.getCurrentRoom().isDoorRoom() && dungeon.areAllDragonsDefeated()) {
                if (!gameWon) {
                    addGameMessage("🎉🎉🎉 CONGRATULAZIONI! HAI VINTO IL GIOCO! 🎉🎉🎉");
                    gameWon = true;
                }
            }

            if (player.getLevel() > oldLevel) {
                addGameMessage("🎉 Congratulazioni! Sei salito al livello " + player.getLevel() + "! 🎉");
            }
        }

        inCombat.set(false);
        currentCombat = null;
        updatePlayerStats();
    }

    /**
     * Aggiorna il log del combattimento con lo stato attuale.
     * Mostra gli HP di giocatore e nemico.
     */
    private void updateCombatLog() {
        if (currentCombat != null) {
            combatLog.set(currentCombat.getCombatStatus());
        }
    }

    /**
     * Aggiorna le statistiche del giocatore nell'interfaccia.
     * Forza l'aggiornamento del binding delle proprietà.
     */
    private void updatePlayerStats() {
        currentPlayer.set(player);
    }

    /**
     * Aggiunge un messaggio al log di gioco con timestamp.
     * I messaggi vengono mostrati in ordine cronologico inverso (i più recenti in alto).
     *
     * @param message il messaggio da aggiungere al log
     */
    private void addGameMessage(String message) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
        gameMessages.add(0, "[" + timestamp + "] " + message);
        if (gameMessages.size() > 50) {
            gameMessages.remove(50);
        }
    }
}