package it.unicam.cs.mpgc.rpg129876.model.world;

import it.unicam.cs.mpgc.rpg129876.model.characters.Enemy;
import it.unicam.cs.mpgc.rpg129876.model.items.Item;
import java.util.*;

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

    public Room(int x, int y, String name, String description) {
        this(x, y);
        this.name = name;
        this.description = description;
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
    public List<Item> getTreasures() { return treasures; }
    public boolean hasTreasures() { return !treasures.isEmpty(); }

    public void addTreasure(Item item) {
        treasures.add(item);
        description = "C'è un forziere qui dentro! Contiene: " + item.getName();
    }

    public List<Item> collectTreasures() {
        List<Item> collected = new ArrayList<>(treasures);
        treasures.clear();
        return collected;
    }

    public void setExit(Direction direction, Room room) {
        exits.put(direction, room);
        if (room != null) {
            room.exits.put(direction.getOpposite(), this);
        }
    }

    public Room getExit(Direction direction) {
        return exits.get(direction);
    }

    public Map<Direction, Room> getExits() { return exits; }

    public List<Direction> getAvailableDirections() {
        return new ArrayList<>(exits.keySet());
    }

    public String getExitsDescription() {
        if (exits.isEmpty()) return "Nessuna uscita";
        return String.join(", ", exits.keySet().stream()
                .map(Direction::getDisplayName)
                .toArray(String[]::new));
    }

    public void enter() {
        visited = true;
        explored = true;
    }

    @Override
    public String toString() {
        return String.format("[%d,%d] %s - %s\nUscite: %s",
                x, y, name, description, getExitsDescription());
    }
}