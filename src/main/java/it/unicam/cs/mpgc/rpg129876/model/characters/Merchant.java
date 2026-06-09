package it.unicam.cs.mpgc.rpg129876.model.characters;

import it.unicam.cs.mpgc.rpg129876.model.items.HealthPotion;

/**
 * Rappresenta un mercante che vende pozioni curative all'interno del dungeon.
 *
 * Il mercante appare in stanze casuali (massimo 2 per dungeon) e offre
 * un numero limitato di pozioni (8 in totale). Ogni pozione costa 40 oro
 * e cura 20 HP.
 *
 * Il giocatore può acquistare fino a un massimo di 10 pozioni totali
 * nell'inventario.
 *
 * @author Filippo Mangialardo
 * @version 1.0
 */
public class Merchant {

    private final String name;
    private int potionsAvailable;  // Massimo 8 pozioni
    private final int POTION_PRICE = 60;
    private final int POTION_HEAL = 20;

    /**
     * Costruisce un nuovo mercante con il nome specificato.
     * Il mercante inizia con 8 pozioni disponibili alla vendita.
     *
     * @param name il nome del mercante
     */
    public Merchant(String name) {
        this.name = name;
        this.potionsAvailable = 5;  // 5 invece di 8
    }

    public String getName() { return name; }
    public int getPotionsAvailable() { return potionsAvailable; }
    public int getPotionPrice() { return POTION_PRICE; }
    public int getPotionHeal() { return POTION_HEAL; }

    /**
     * Verifica se il mercante può vendere la quantità richiesta di pozioni.
     *
     * @param quantity quantità di pozioni richiesta
     * @return true se il mercante ha abbastanza pozioni, false altrimenti
     */
    public boolean canSell(int quantity) {
        return potionsAvailable >= quantity;
    }

    /**
     * Riduce l'inventario del mercante dopo una vendita.
     * Viene chiamato solo dopo aver verificato la disponibilità con canSell().
     *
     * @param quantity quantità di pozioni vendute
     */
    public void sellPotion(int quantity) {
        if (potionsAvailable >= quantity) {
            potionsAvailable -= quantity;
            System.out.println("DEBUG: Pozioni vendute=" + quantity +
                    ", rimaste al mercante=" + potionsAvailable);
        } else {
            System.out.println("❌ Il mercante non ha abbastanza pozioni!");
        }
    }

    /**
     * Crea una nuova pozione curativa con la quantità specificata.
     * La pozione cura 20 HP (valore definito da POTION_HEAL).
     *
     * @param quantity numero di pozioni da creare (stack)
     * @return una nuova istanza di HealthPotion con la quantità specificata
     */
    public HealthPotion createPotion(int quantity) {
        return new HealthPotion(quantity, POTION_HEAL);
    }
}