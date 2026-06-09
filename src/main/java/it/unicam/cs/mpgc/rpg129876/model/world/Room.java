package it.unicam.cs.mpgc.rpg129876.model.world;

import it.unicam.cs.mpgc.rpg129876.model.characters.Enemy;
import it.unicam.cs.mpgc.rpg129876.model.characters.Merchant;
import it.unicam.cs.mpgc.rpg129876.model.items.Item;
import java.util.*;

/**
 * Rappresenta una singola stanza del dungeon.
 *
 * Ogni stanza ha:
 * - Coordinate (x, y) sulla griglia della mappa
 * - Un nome e una descrizione (es. "Cripta Antica", "L'aria è fredda...")
 * - Uscite nelle quattro direzioni (Nord, Sud, Est, Ovest)
 * - Opzionalmente: nemici, tesori, mercanti
 *
 * Le stanze possono essere esplorate (visitate) e diventano visibili sulla mappa.
 *
 * @author Filippo Mangialardo
 * @version 1.0
 */
public class Room {

    private final int x;
    private final int y;
    private String name;
    private String description;
    private boolean explored;
    private boolean visited;
    private Enemy enemy;
    private final List<Item> treasures;
    private final Map<Direction, Room> exits;
    private boolean isDoorRoom;
    private boolean hasDragon;
    private Merchant merchant;
    private boolean hasMerchant;

    /**
     * Costruisce una nuova stanza con le coordinate specificate.
     * La stanza viene creata con nome e descrizione predefiniti,
     * senza nemici, senza tesori e senza mercante.
     *
     * @param x coordinata orizzontale
     * @param y coordinata verticale
     */
    public Room(int x, int y) {
        this.x = x;
        this.y = y;
        this.name = "Stanza sconosciuta";
        this.description = "Una stanza buia e silenziosa...";
        this.explored = false;
        this.visited = false;
        this.treasures = new ArrayList<>();
        this.exits = new HashMap<>();
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isExplored() { return explored; }
    public void setExplored(boolean explored) { this.explored = explored; }
    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }
    public boolean hasEnemy() { return enemy != null && enemy.isAlive(); }
    public Enemy getEnemy() { return enemy; }
    public void setEnemy(Enemy enemy) { this.enemy = enemy; }
    public boolean hasTreasures() { return !treasures.isEmpty(); }

    public void setDragonCount(int count) {
    }

    public void setDoorRoom(boolean isDoor) { this.isDoorRoom = isDoor; }
    public boolean isDoorRoom() { return isDoorRoom; }
    public void setHasDragon(boolean has) { this.hasDragon = has; }
    public boolean hasDragon() { return hasDragon; }
    public void setDragonsDefeated(boolean defeated) {
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
        this.hasMerchant = true;
    }
    public boolean hasMerchant() { return hasMerchant; }
    public Merchant getMerchant() { return merchant; }

    /**
     * Aggiunge un tesoro alla stanza.
     * Aggiorna automaticamente la descrizione per segnalare la presenza del forziere.
     *
     * @param item il tesoro da aggiungere
     */
    public void addTreasure(Item item) {
        treasures.add(item);
        description = "C'è un forziere qui dentro! Contiene: " + item.getName();
    }

    /**
     * Raccoglie tutti i tesori presenti nella stanza.
     * I tesori vengono rimossi dalla stanza e restituiti come lista.
     *
     * @return lista dei tesori raccolti
     */
    public List<Item> collectTreasures() {
        List<Item> collected = new ArrayList<>(treasures);
        treasures.clear();
        return collected;
    }

    /**
     * Imposta un'uscita nella direzione specificata.
     * La connessione è bidirezionale: se stanza A punta a B,
     * automaticamente B punta ad A nella direzione opposta.
     *
     * @param direction direzione in cui si trova l'uscita
     * @param room stanza di destinazione
     */
    public void setExit(Direction direction, Room room) {
        exits.put(direction, room);
        if (room != null) {
            room.exits.put(direction.getOpposite(), this);
        }
    }

    /**
     * Restituisce la stanza raggiungibile nella direzione specificata.
     *
     * @param direction direzione in cui muoversi
     * @return stanza adiacente, o null se non esiste uscita
     */
    public Room getExit(Direction direction) {
        return exits.get(direction);
    }

    /**
     * Restituisce una stringa descrittiva delle uscite disponibili.
     *
     * @return elenco delle direzioni, o "Nessuna uscita"
     */
    public String getExitsDescription() {
        if (exits.isEmpty()) return "Nessuna uscita";
        return String.join(", ", exits.keySet().stream()
                .map(Direction::getDisplayName)
                .toArray(String[]::new));
    }

    /**
     * Segna la stanza come visitata ed esplorata.
     * Viene chiamato quando il giocatore entra nella stanza per la prima volta.
     */
    public void enter() {
        visited = true;
        explored = true;
    }

    /**
     * Restituisce una rappresentazione testuale completa della stanza.
     * Formato: "[x,y] Nome - Descrizione\nUscite: dir1, dir2, ..."
     *
     * @return stringa con coordinate, nome, descrizione e uscite
     */
    @Override
    public String toString() {
        return String.format("[%d,%d] %s - %s\nUscite: %s",
                x, y, name, description, getExitsDescription());
    }
}