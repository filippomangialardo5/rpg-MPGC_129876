package it.unicam.cs.mpgc.rpg129876.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Score implements Serializable {
    private String playerName;
    private String characterClass;
    private int level;
    private int enemiesDefeated;
    private int gold;
    private LocalDateTime date;

    public Score(String playerName, String characterClass, int level, int enemiesDefeated, int gold) {
        this.playerName = playerName;
        this.characterClass = characterClass;
        this.level = level;
        this.enemiesDefeated = enemiesDefeated;
        this.gold = gold;
        this.date = LocalDateTime.now();
    }

    // Getters
    public String getPlayerName() { return playerName; }
    public String getCharacterClass() { return characterClass; }
    public int getLevel() { return level; }
    public int getEnemiesDefeated() { return enemiesDefeated; }
    public int getGold() { return gold; }
    public LocalDateTime getDate() { return date; }

    // Calcolo punteggio
    public int getTotalScore() {
        return (level * 100) + (enemiesDefeated * 10) + gold;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - Livello %d - Punteggio: %d",
                playerName, characterClass, level, getTotalScore());
    }
}