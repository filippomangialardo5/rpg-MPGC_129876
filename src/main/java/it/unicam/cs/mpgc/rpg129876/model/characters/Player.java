package it.unicam.cs.mpgc.rpg129876.model.characters;

import it.unicam.cs.mpgc.rpg129876.model.items.HealthPotion;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import it.unicam.cs.mpgc.rpg129876.model.items.Item;

/**
 * Rappresenta il personaggio giocatore.
 * Gestisce statistiche, livello, esperienza, oro e inventario.
 *
 * @author Filippo Mangialardo
 * @version 1.0
 */
public class Player extends GameCharacter {

    // Proprietà aggiuntive del giocatore
    private final IntegerProperty level = new SimpleIntegerProperty(1);
    private final IntegerProperty experience = new SimpleIntegerProperty(0);
    private final IntegerProperty gold = new SimpleIntegerProperty(80);
    private final ObservableList<Item> inventory = FXCollections.observableArrayList();
    private final String characterClass;
    private static final int MAX_POTIONS = 10;

    public Player(String name, String characterClass) {
        super(name, 100, 15, 10);  // Valori base, verranno sovrascritti
        this.characterClass = characterClass;
        initializeClassStats();
    }

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

    /**
     * Inizializza le statistiche del personaggio in base alla classe scelta.
     *
     * **Guerriero (Warrior):** HP 120, Attacco 20, Difesa 15
     * **Mago (Mage):** HP 80, Attacco 25, Difesa 8
     * **Ladro (Rogue):** HP 100, Attacco 22, Difesa 12
     */
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

    /**
     * Aggiunge esperienza al giocatore.
     * Se l'esperienza supera la soglia, il giocatore sale di livello.
     *
     * @param amount quantità di esperienza da aggiungere
     */
    public void gainExperience(int amount) {
        int newExp = getExperience() + amount;

        System.out.println("DEBUG: XP guadagnati=" + amount + ", XP prima=" + (newExp - amount));

        int levelsGained = 0;
        while (newExp >= getRequiredExpForLevel()) {
            int required = getRequiredExpForLevel();
            newExp -= required;
            levelsGained++;
            levelUpNoExp();
        }

        // Imposta l'XP residuo
        setExperience(newExp);

        // Se hai guadagnato livelli, mostra messaggio
        if (levelsGained > 0) {
            System.out.println("🎉 Guadagnati " + levelsGained + " livello/i! XP residui: " + newExp);
        }
    }

    /**
     * Aumenta il livello del giocatore.
     * Incrementa HP massimi, attacco, difesa e cura parzialmente il personaggio.
     */
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

    /**
     * Calcola l'esperienza necessaria per salire al livello successivo.
     * La formula è: 150 + (livello - 1) * 100
     *
     * Esempi:
     * - Livello 1 → 150 XP
     * - Livello 2 → 250 XP
     * - Livello 3 → 350 XP
     *
     * @return l'esperienza richiesta per il prossimo livello
     */
    private int getRequiredExpForLevel(){
        return 150 + (getLevel() - 1) * 100;
    }

    /**
     * Aggiunge una quantità di oro al giocatore.
     * L'oro viene utilizzato per acquistare pozioni dai mercanti.
     *
     * @param amount la quantità di oro da aggiungere (può essere negativa per spendere)
     */
    public void addGold(int amount) {
        setGold(getGold() + amount);
    }

    /**
     * Aggiunge un oggetto all'inventario del giocatore.
     * Se l'oggetto è una pozione, controlla che non venga superato il limite massimo.
     *
     * @param item l'oggetto da aggiungere all'inventario
     */
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
                }
            } else {
                inventory.add(item);
            }
        } else {
            inventory.add(item);
        }
    }

    /**
     * Verifica se è possibile aggiungere una certa quantità di pozioni all'inventario.
     * Il limite massimo di pozioni è 10.
     *
     * @param quantity la quantità di pozioni che si vuole aggiungere
     * @return true se l'aggiunta è possibile, false se supera il limite
     */
    public boolean canAddPotions(int quantity) {
        int currentCount = getPotionCount();
        boolean canAdd = currentCount + quantity <= MAX_POTIONS;
        System.out.println("DEBUG canAddPotions: current=" + currentCount +
                ", quantity=" + quantity +
                ", max=" + MAX_POTIONS +
                ", risultato=" + canAdd);
        return canAdd;
    }

    /**
     * Rimuove un oggetto dall'inventario del giocatore.
     *
     * @param item l'oggetto da rimuovere
     */
    public void removeItem(Item item) {
        inventory.remove(item);
    }

    /**
     * Calcola il numero totale di pozioni presenti nell'inventario.
     * Somma le quantità di tutte le istanze di HealthPotion.
     *
     * @return il numero totale di pozioni nell'inventario
     */
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

    /**
     * Restituisce una rappresentazione testuale del giocatore.
     * Formato: "Nome (Lv.X Classe) - HP: Y/Z"
     *
     * @return stringa descrittiva del giocatore
     */
     @Override
    public String toString() {
        return getName() + " (Lv." + getLevel() + " " + characterClass + ") - HP: " + getHp() + "/" + getMaxHp();
    }
}