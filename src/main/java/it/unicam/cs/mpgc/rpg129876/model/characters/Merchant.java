package it.unicam.cs.mpgc.rpg129876.model.characters;

import it.unicam.cs.mpgc.rpg129876.model.items.HealthPotion;
import it.unicam.cs.mpgc.rpg129876.model.items.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Merchant {

    private String name;
    private int potionsAvailable;  // Massimo 8 pozioni
    private final int POTION_PRICE = 60;
    private final int POTION_HEAL = 20;

    public Merchant(String name) {
        this.name = name;
        this.potionsAvailable = 8;  // 8 pozioni disponibili
    }

    public String getName() { return name; }
    public int getPotionsAvailable() { return potionsAvailable; }
    public int getPotionPrice() { return POTION_PRICE; }
    public int getPotionHeal() { return POTION_HEAL; }

    public boolean canSell(int quantity) {
        return potionsAvailable >= quantity;
    }

    public void sellPotion(int quantity) {
        potionsAvailable -= quantity;
    }

    public HealthPotion createPotion(int quantity) {
        return new HealthPotion(quantity, POTION_HEAL);
    }
}