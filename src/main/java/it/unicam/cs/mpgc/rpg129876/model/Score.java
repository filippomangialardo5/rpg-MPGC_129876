package it.unicam.cs.mpgc.rpg129876.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Score implements Serializable {
    private final String playerName;
    private final String characterClass;
    private final int level;
    private final int enemiesDefeated;
    private final int gold;
    private final String date;

    public Score(String playerName, String characterClass, int level, int enemiesDefeated, int gold) {
        this.playerName = playerName;
        this.characterClass = characterClass;
        this.level = level;
        this.enemiesDefeated = enemiesDefeated;
        this.gold = gold;
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    // Getters
    public String getPlayerName() { return playerName; }
    public String getCharacterClass() { return characterClass; }
    public int getLevel() { return level; }

    // Calcolo punteggio totale
    public int getTotalScore() {
        return (level * 100) + (enemiesDefeated * 10) + gold;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - Liv.%d - %d punti - %s",
                playerName, characterClass, level, getTotalScore(), date);
    }
}