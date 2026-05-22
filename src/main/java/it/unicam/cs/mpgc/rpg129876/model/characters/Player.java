package it.unicam.cs.mpgc.rpg129876.model.characters;

import it.unicam.cs.mpgc.rpg129876.model.items.HealthPotion;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import it.unicam.cs.mpgc.rpg129876.model.items.Item;

public class Player extends GameCharacter {

    // Proprietà aggiuntive del giocatore
    private final IntegerProperty level = new SimpleIntegerProperty(1);
    private final IntegerProperty experience = new SimpleIntegerProperty(0);
    private final IntegerProperty gold = new SimpleIntegerProperty(80);
    private final ObservableList<Item> inventory = FXCollections.observableArrayList();
    private String characterClass;
    private static final int MAX_POTIONS = 10;

    public Player(String name, String characterClass) {
        super(name, 100, 15, 10);  // Valori base, verranno sovrascritti
        this.characterClass = characterClass;
        initializeClassStats();
    }

    private void initializeClassStats() {
        switch(characterClass) {
            case "Warrior":
                setMaxHp(120);
                setHp(120);
                setAttack(20);
                setDefense(15);
                break;
            case "Mage":
                setMaxHp(80);
                setHp(80);
                setAttack(25);
                setDefense(8);
                break;
            case "Rogue":
                setMaxHp(100);
                setHp(100);
                setAttack(22);
                setDefense(12);
                break;
            default:
                // Valori base già impostati nel costruttore di GameCharacter
                break;
        }
    }

    // Property getters per binding JavaFX
    public IntegerProperty levelProperty() { return level; }
    public IntegerProperty experienceProperty() { return experience; }
    public IntegerProperty goldProperty() { return gold; }
    public ObservableList<Item> getInventory() { return inventory; }

    // Getters
    public int getLevel() { return level.get(); }
    public int getExperience() { return experience.get(); }
    public int getGold() { return gold.get(); }
    public String getCharacterClass() { return characterClass; }

    // Setters
    public void setLevel(int level) { this.level.set(level); }
    public void setExperience(int experience) { this.experience.set(experience); }
    public void setGold(int gold) { this.gold.set(gold); }

    // Metodi di gioco
    public void gainExperience(int amount) {
        int newExp = getExperience() + amount;

        System.out.println("DEBUG: XP guadagnati=" + amount + ", XP prima=" + (newExp - amount));

        // Controlla quanti livelli sali
        int levelsGained = 0;
        while (newExp >= getRequiredExpForLevel()) {
            int required = getRequiredExpForLevel();
            newExp -= required;
            levelsGained++;
            levelUpNoExp();  // Nuovo metodo che non usa l'XP
        }

        // Imposta l'XP residuo
        setExperience(newExp);

        // Se hai guadagnato livelli, mostra messaggio
        if (levelsGained > 0) {
            System.out.println("🎉 Guadagnati " + levelsGained + " livello/i! XP residui: " + newExp);
        }
    }

    private void levelUpNoExp() {
        int newLevel = getLevel() + 1;
        setLevel(newLevel);

        // Calcola nuovo max HP
        int oldMaxHp = getMaxHp();
        int newMaxHp = oldMaxHp + 20;
        setMaxHp(newMaxHp);

        // Cura 1/4 del nuovo max HP
        int healAmount = newMaxHp / 4;
        int newHp = getHp() + healAmount;
        setHp(Math.min(newHp, newMaxHp));

        // Aumenta attacco e difesa
        setAttack(getAttack() + 5);
        setDefense(getDefense() + 3);

        System.out.println("🎉 " + getName() + " raggiunge il livello " + newLevel + "! 🎉");
    }

    private int getRequiredExpForLevel() {
        // Formula più lenta: 150 XP per livello 1, +100 a ogni livello
        // Livello 1: 150 XP
        // Livello 2: 250 XP
        // Livello 3: 350 XP
        // Livello 4: 450 XP
        // ecc.
        return 150 + (getLevel() - 1) * 100;
    }

    private void levelUp() {
        levelUpNoExp();
    }
    public void addGold(int amount) {
        setGold(getGold() + amount);
    }

    public void addItem(Item item) {
        // Se è una pozione, verifica il limite
        if (item instanceof HealthPotion) {
            int currentPotionCount = getPotionCount();
            int potionToAdd = ((HealthPotion) item).getQuantity();

            if (currentPotionCount + potionToAdd > MAX_POTIONS) {
                // Aggiungi solo le pozioni che entrano
                int allowedToAdd = MAX_POTIONS - currentPotionCount;
                if (allowedToAdd > 0) {
                    ((HealthPotion) item).setQuantity(allowedToAdd);
                    inventory.add(item);
                    System.out.println("⚠️ Inventario pieno! Aggiunte solo " + allowedToAdd + " pozioni su " + potionToAdd);
                } else {
                    System.out.println("❌ Inventario pieno! Non puoi aggiungere altre pozioni!");
                    return;
                }
            } else {
                inventory.add(item);
            }
        } else {
            inventory.add(item);
        }
    }

    public boolean canAddPotions(int quantity) {
        int currentCount = getPotionCount();
        boolean canAdd = currentCount + quantity <= MAX_POTIONS;
        System.out.println("DEBUG canAddPotions: current=" + currentCount +
                ", quantity=" + quantity +
                ", max=" + MAX_POTIONS +
                ", risultato=" + canAdd);
        return canAdd;
    }

    public void removeItem(Item item) {
        inventory.remove(item);
    }

    public boolean hasItem(Item item) {
        return inventory.contains(item);
    }

    public int getPotionCount() {
        int count = 0;
        for (Item item : inventory) {
            if (item instanceof HealthPotion) {
                count += ((HealthPotion) item).getQuantity();
            }
        }
        return count;
    }

    public int getMaxPotions() {
        return MAX_POTIONS;
    }

    public void debugPotionCount() {
        System.out.println("=== DEBUG POZIONI ===");
        System.out.println("Pozioni attuali: " + getPotionCount());
        System.out.println("Max pozioni: " + MAX_POTIONS);
        for (Item item : inventory) {
            if (item instanceof HealthPotion) {
                System.out.println("Pozione trovata: quantità=" + ((HealthPotion) item).getQuantity());
            }
        }
    }

    @Override
    public String toString() {
        return getName() + " (Lv." + getLevel() + " " + characterClass + ") - HP: " + getHp() + "/" + getMaxHp();
    }
}