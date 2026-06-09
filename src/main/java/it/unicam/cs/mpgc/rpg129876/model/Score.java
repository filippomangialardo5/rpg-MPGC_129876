package it.unicam.cs.mpgc.rpg129876.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Rappresenta il punteggio di un giocatore per la classifica.
 *
 * Questa classe viene utilizzata per salvare e caricare i punteggi
 * nel file `scores.json`. Ogni volta che un giocatore vince una partita,
 * viene creato un nuovo oggetto Score e aggiunto alla classifica.
 *
 * La classifica mostra i Top 10 punteggi, ordinati dal più alto al più basso.
 *
 * @author Filippo Mangialardo
 * @version 1.0
 */
public class Score implements Serializable {
    private final String playerName;
    private final String characterClass;
    private final int level;
    private final int enemiesDefeated;
    private final int gold;
    private final String date;

    /**
     * Costruisce un nuovo punteggio con i dati del giocatore.
     * La data viene automaticamente impostata al momento della creazione
     * nel formato "dd/MM/yyyy HH:mm".
     *
     * @param playerName nome del giocatore
     * @param characterClass classe del personaggio (Warrior, Mage, Rogue)
     * @param level livello raggiunto
     * @param enemiesDefeated numero di nemici sconfitti
     * @param gold oro accumulato
     */
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

    /**
     * Calcola il punteggio totale del giocatore.
     *
     * La formula utilizzata è:
     * Punteggio = (Livello × 100) + (Nemici sconfitti × 10) + Oro
     *
     * In questo modo:
     * - Salire di livello dà un grande contributo al punteggio (100 punti per livello)
     * - Sconfiggere nemici dà un contributo medio (10 punti per nemico)
     * - Accumulare oro dà un contributo lineare (1 punto per oro)
     *
     * @return punteggio totale calcolato
     */
    public int getTotalScore() {
        return (level * 100) + (enemiesDefeated * 10) + gold;
    }

    /**
     * Restituisce una rappresentazione testuale del punteggio per la classifica.
     *
     * Formato: "Nome (Classe) - Livello X - Punteggio: Y"
     *
     * @return stringa formattata per la visualizzazione in classifica
     */
    @Override
    public String toString() {
        return String.format("%s (%s) - Liv.%d - %d punti - %s",
                playerName, characterClass, level, getTotalScore(), date);
    }
}