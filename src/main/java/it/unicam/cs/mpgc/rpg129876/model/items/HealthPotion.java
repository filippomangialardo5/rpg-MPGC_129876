package it.unicam.cs.mpgc.rpg129876.model.items;

import it.unicam.cs.mpgc.rpg129876.model.characters.Player;

public class HealthPotion implements Item {

    private static final int DEFAULT_HEAL_AMOUNT = 30;
    private int healAmount;
    private int quantity;

    public HealthPotion() {
        this(1, DEFAULT_HEAL_AMOUNT);
    }

    public HealthPotion(int quantity) {
        this(quantity, DEFAULT_HEAL_AMOUNT);
    }

    public HealthPotion(int quantity, int healAmount) {
        this.quantity = quantity;
        this.healAmount = healAmount;
    }

    @Override
    public String getName() {
        return "Health Potion";
    }

    @Override
    public String getDescription() {
        return "Restores " + healAmount + " HP. Quantity: " + quantity;
    }

    @Override
    public void use(Player player) {
        if (quantity > 0 && player.isAlive()) {
            int oldHp = player.getHp();
            player.heal(healAmount);
            int healed = player.getHp() - oldHp;
            quantity--;
            System.out.println(player.getName() + " used a Health Potion and recovered " + healed + " HP!");
        } else if (quantity <= 0) {
            System.out.println("No Health Potions left!");
        }
    }

    @Override
    public String getIcon() {
        return "🧪";
    }

    public int getHealAmount() { return healAmount; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void addQuantity(int amount) { this.quantity += amount; }
    public boolean isEmpty() { return quantity <= 0; }
}

