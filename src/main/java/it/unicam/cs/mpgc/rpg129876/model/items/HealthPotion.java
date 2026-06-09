package it.unicam.cs.mpgc.rpg129876.model.items;

import it.unicam.cs.mpgc.rpg129876.model.characters.Player;

/**
 * Rappresenta una pozione curativa utilizzabile dal giocatore.
 *
 * La pozione ripristina una quantità fissa di punti vita (HP) al personaggio.
 * Di default cura 20 HP, ma il valore può essere personalizzato in fase di costruzione.
 *
 * Le pozioni sono stackabili: più pozioni possono essere contenute in un singolo oggetto
 * grazie alla proprietà {@code quantity}.
 *
 * @author Filippo Mangialardo
 * @version 1.0
 */
public class HealthPotion implements Item {

    private int healAmount;
    private int quantity;

    /**
     * Costruisce uno stack di pozioni che curano 20 HP ciascuna.
     *
     * @param quantity numero di pozioni nello stack
     */
    public HealthPotion(int quantity) {
        this(quantity, 20);
    }

    /**
     * Costruisce uno stack di pozioni con quantità e cura personalizzate.
     *
     * @param quantity numero di pozioni nello stack
     * @param healAmount quantità di HP recuperati per ogni pozione
     */
    public HealthPotion(int quantity, int healAmount) {
        this.quantity = quantity;
        this.healAmount = healAmount;
    }

    /**
     * Restituisce il nome della pozione con l'indicazione della cura.
     *
     * @return stringa nel formato "Health Potion (+X HP)"
     */
    @Override
    public String getName() {
        return "Health Potion (+" + healAmount + " HP)";
    }

    /**
     * Restituisce una descrizione dettagliata della pozione.
     *
     * @return stringa con cura e quantità rimanente
     */
    @Override
    public String getDescription() {
        return "Restores " + healAmount + " HP. Quantity: " + quantity;
    }

    /**
     * Utilizza una pozione per curare il giocatore.
     *
     * Comportamento:
     * - Se il giocatore è già a piena vita, la pozione non viene consumata
     * - La cura effettiva è limitata agli HP mancanti (nessuno spreco)
     * - La quantità viene decrementata dopo l'uso
     *
     * @param player il giocatore da curare
     */
    @Override
    public void use(Player player) {
        if (quantity > 0 && player.isAlive()) {
            int currentHp = player.getHp();
            int maxHp = player.getMaxHp();
            int missingHp = maxHp - currentHp;

            System.out.println("DEBUG POZIONE: currentHp=" + currentHp + ", maxHp=" + maxHp + ", missingHp=" + missingHp + ", healAmount=" + healAmount);

            // Se è già a vita piena
            if (missingHp <= 0) {
                System.out.println("❌ Sei già a piena vita! Pozione non usata.");
                return;
            }

            // Calcola cura effettiva (senza sprechi)
            int actualHeal = Math.min(healAmount, missingHp);
            player.heal(actualHeal);
            quantity--;

            System.out.println("DEBUG POZIONE: healed=" + actualHeal + ", newHp=" + player.getHp());
        } else if (quantity <= 0) {
            System.out.println("❌ Nessuna pozione rimasta!");
        }
    }

    /**
     * Restituisce l'emoji/icona rappresentativa della pozione.
     *
     * @return icona della pozione (🧪)
     */
    @Override
    public String getIcon() { return "🧪"; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void addQuantity(int amount) { this.quantity += amount; }
    public boolean isEmpty() { return quantity <= 0; }
}