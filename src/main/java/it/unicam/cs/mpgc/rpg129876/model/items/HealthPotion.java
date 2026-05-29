package it.unicam.cs.mpgc.rpg129876.model.items;

import it.unicam.cs.mpgc.rpg129876.model.characters.Player;

public class HealthPotion implements Item {

    private int healAmount;
    private int quantity;

    public HealthPotion(int quantity) {
        this(quantity, 20);
    }

    public HealthPotion(int quantity, int healAmount) {
        this.quantity = quantity;
        this.healAmount = healAmount;
    }

    @Override
    public String getName() {
        return "Health Potion (+" + healAmount + " HP)";
    }

    @Override
    public String getDescription() {
        return "Restores " + healAmount + " HP. Quantity: " + quantity;
    }

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

    @Override
    public String getIcon() { return "🧪"; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void addQuantity(int amount) { this.quantity += amount; }
    public boolean isEmpty() { return quantity <= 0; }
}