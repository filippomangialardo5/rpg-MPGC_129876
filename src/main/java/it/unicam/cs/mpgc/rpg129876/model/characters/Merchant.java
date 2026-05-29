package it.unicam.cs.mpgc.rpg129876.model.characters;

import it.unicam.cs.mpgc.rpg129876.model.items.HealthPotion;

public class Merchant {

    private final String name;
    private int potionsAvailable;  // Massimo 8 pozioni
    private final int POTION_PRICE = 60;
    private final int POTION_HEAL = 20;

    public Merchant(String name) {
        this.name = name;
        this.potionsAvailable = 5;  // 5 invece di 8
    }

    public String getName() { return name; }
    public int getPotionsAvailable() { return potionsAvailable; }
    public int getPotionPrice() { return POTION_PRICE; }
    public int getPotionHeal() { return POTION_HEAL; }

    public boolean canSell(int quantity) {
        return potionsAvailable >= quantity;
    }

    public void sellPotion(int quantity) {
        if (potionsAvailable >= quantity) {
            potionsAvailable -= quantity;
            System.out.println("DEBUG: Pozioni vendute=" + quantity +
                    ", rimaste al mercante=" + potionsAvailable);
        } else {
            System.out.println("❌ Il mercante non ha abbastanza pozioni!");
        }
    }

    public HealthPotion createPotion(int quantity) {
        return new HealthPotion(quantity, POTION_HEAL);
    }
}