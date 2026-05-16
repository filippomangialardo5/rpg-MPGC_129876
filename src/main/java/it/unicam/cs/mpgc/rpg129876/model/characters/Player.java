package it.unicam.cs.mpgc.rpg129876.model.characters;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import it.unicam.cs.mpgc.rpg129876.model.items.Item;

public class Player extends GameCharacter {

    // Proprietà aggiuntive del giocatore
    private final IntegerProperty level = new SimpleIntegerProperty(1);
    private final IntegerProperty experience = new SimpleIntegerProperty(0);
    private final IntegerProperty gold = new SimpleIntegerProperty(100);
    private final ObservableList<Item> inventory = FXCollections.observableArrayList();
    private String characterClass;

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
        setExperience(newExp);

        int requiredExp = getRequiredExpForLevel();
        if (getExperience() >= requiredExp) {
            levelUp();
        }
    }

    private int getRequiredExpForLevel() {
        return 100 + (getLevel() - 1) * 50;
    }

    private void levelUp() {
        int newLevel = getLevel() + 1;
        int remainingExp = getExperience() - getRequiredExpForLevel();

        setLevel(newLevel);
        setExperience(remainingExp);

        // Aumenta le statistiche
        setMaxHp(getMaxHp() + 20);
        setHp(getMaxHp());  // Cura completa
        setAttack(getAttack() + 5);
        setDefense(getDefense() + 3);

        System.out.println(getName() + " reached level " + newLevel + "!");
    }

    public void addGold(int amount) {
        setGold(getGold() + amount);
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void removeItem(Item item) {
        inventory.remove(item);
    }

    public boolean hasItem(Item item) {
        return inventory.contains(item);
    }

    @Override
    public String toString() {
        return getName() + " (Lv." + getLevel() + " " + characterClass + ") - HP: " + getHp() + "/" + getMaxHp();
    }
}