package it.unicam.cs.mpgc.rpg129876.model.characters;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Classe astratta che rappresenta un personaggio generico del gioco.
 *
 * Fornisce le proprietà di base comuni a tutti i personaggi (giocatore e nemici):
 * - Nome
 * - Punti vita (HP) correnti e massimi
 * - Attacco
 * - Difesa
 *
 * Utilizza le JavaFX Property per consentire il binding automatico con l'interfaccia grafica.
 *
 * @author Filippo Mangialardo
 * @version 1.0
 */
public abstract class GameCharacter {

    // Observable properties per JavaFX binding
    protected final StringProperty name = new SimpleStringProperty();
    protected final IntegerProperty hp = new SimpleIntegerProperty();
    protected final IntegerProperty maxHp = new SimpleIntegerProperty();
    protected final IntegerProperty attack = new SimpleIntegerProperty();
    protected final IntegerProperty defense = new SimpleIntegerProperty();

    /**
     * Costruisce un nuovo personaggio con le statistiche di base.
     * I punti vita correnti vengono inizializzati al valore massimo.
     *
     * @param name nome del personaggio
     * @param maxHp punti vita massimi
     * @param attack valore di attacco
     * @param defense valore di difesa
     */
    public GameCharacter(String name, int maxHp, int attack, int defense) {
        this.name.set(name);
        this.maxHp.set(maxHp);
        this.hp.set(maxHp);
        this.attack.set(attack);
        this.defense.set(defense);
    }

    // Property getters per binding (JavaFX)
    public IntegerProperty hpProperty() { return hp; }
    public IntegerProperty maxHpProperty() { return maxHp; }

    // Getters
    public String getName() { return name.get(); }
    public int getHp() { return hp.get(); }
    public int getMaxHp() { return maxHp.get(); }
    public int getAttack() { return attack.get(); }
    public int getDefense() { return defense.get(); }

    // Setters with bounds

    /**
     * Imposta i punti vita correnti, assicurandosi che rimangano
     * nell'intervallo [0, maxHp].
     *
     * @param hp nuovi punti vita
     */
    public void setHp(int hp) { this.hp.set(Math.max(0, Math.min(hp, maxHp.get()))); }

    public void setMaxHp(int maxHp) { this.maxHp.set(maxHp); }
    public void setAttack(int attack) { this.attack.set(attack); }
    public void setDefense(int defense) { this.defense.set(defense); }

    /**
     * Infligge danno al personaggio.
     * Il danno effettivo viene calcolato come:
     *
     * dannoEffettivo = max(1, danno - difesa)
     *
     * @param damage danno base inflitto (viene ridotto dalla difesa)
     */
    public void takeDamage(int damage) {
        int actualDamage = Math.max(1, damage - getDefense());
        setHp(getHp() - actualDamage);
    }

    /**
     * Cura il personaggio di una quantità specifica di punti vita.
     * La cura non può superare il valore massimo di HP.
     *
     * @param amount quantità di HP da recuperare
     */
    public void heal(int amount) {
        setHp(getHp() + amount);
    }

    /**
     * Verifica se il personaggio è ancora vivo.
     * Un personaggio è considerato vivo se i suoi HP sono maggiori di 0.
     *
     * @return true se i punti vita sono maggiori di 0, false altrimenti
     */
    public boolean isAlive() {
        return getHp() > 0;
    }

    /**
     * Restituisce una rappresentazione testuale del personaggio.
     * Formato: "Nome [HP: X/Y]"
     *
     * @return stringa descrittiva del personaggio con nome e salute
     */
    @Override
    public String toString() {
        return getName() + " [HP: " + getHp() + "/" + getMaxHp() + "]";
    }
}