package it.unicam.cs.mpgc.rpg129876.model.combat;

import it.unicam.cs.mpgc.rpg129876.model.characters.Player;
import it.unicam.cs.mpgc.rpg129876.model.characters.Enemy;
import it.unicam.cs.mpgc.rpg129876.model.items.Item;
import java.util.Random;

public class CombatSystem {

    private Player player;
    private Enemy enemy;
    private boolean inCombat;
    private Random random;

    public CombatSystem(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
        this.inCombat = true;
        this.random = new Random();
    }

    // Getters
    public boolean isInCombat() { return inCombat; }
    public Player getPlayer() { return player; }
    public Enemy getEnemy() { return enemy; }

    // Metodo principale per l'attacco del giocatore
    public CombatResult playerAttack() {
        if (!inCombat) return new CombatResult(false, "Combat is over!", false);

        int damage = calculateDamage(player.getAttack(), enemy.getDefense());
        enemy.takeDamage(damage);

        CombatResult result = new CombatResult();
        result.setMessage(player.getName() + " deals " + damage + " damage to " + enemy.getName() + "!");
        result.setDamageDealt(damage);
        result.setPlayerAction(true);

        System.out.println(result.getMessage());

        if (!enemy.isAlive()) {
            result.setCombatEnded(true);
            result.setPlayerWon(true);
            result.setMessage(result.getMessage() + "\n⚔ " + enemy.getName() + " is defeated! ⚔");
            inCombat = false;
        }

        return result;
    }

    // Metodo per l'attacco del nemico
    public CombatResult enemyAttack() {
        if (!inCombat) return new CombatResult(false, "Combat is over!", false);

        int damage = calculateDamage(enemy.getAttack(), player.getDefense());
        player.takeDamage(damage);

        CombatResult result = new CombatResult();
        result.setMessage(enemy.getName() + " deals " + damage + " damage to " + player.getName() + "!");
        result.setDamageDealt(damage);
        result.setPlayerAction(false);

        System.out.println(result.getMessage());

        if (!player.isAlive()) {
            result.setCombatEnded(true);
            result.setPlayerWon(false);
            result.setMessage(result.getMessage() + "\n💀 " + player.getName() + " has been defeated! 💀");
            inCombat = false;
        }

        return result;
    }

    // Turno completo del nemico (dopo l'attacco del giocatore)
    public CombatResult enemyTurn() {
        if (!inCombat) return new CombatResult(false, "Combat is over!", false);
        return enemyAttack();
    }

    // Usare un oggetto in combattimento
    public CombatResult useItem(Item item) {
        if (!inCombat) return new CombatResult(false, "Not in combat!", false);

        CombatResult result = new CombatResult();
        item.use(player);
        result.setMessage("Used " + item.getIcon() + " " + item.getName() + "!");
        result.setItemUsed(true);
        result.setPlayerAction(true);

        System.out.println(result.getMessage());

        // Rimuovi l'oggetto se la quantità è zero
        if (item instanceof it.unicam.cs.mpgc.rpg129876.model.items.HealthPotion) {
            it.unicam.cs.mpgc.rpg129876.model.items.HealthPotion potion =
                    (it.unicam.cs.mpgc.rpg129876.model.items.HealthPotion) item;
            if (potion.isEmpty()) {
                player.removeItem(item);
            }
        }

        return result;
    }

    // Tentativo di fuga
    public CombatResult flee() {
        if (!inCombat) return new CombatResult(false, "Not in combat!", false);

        int chance = random.nextInt(100);
        if (chance < 50) {
            inCombat = false;
            CombatResult result = new CombatResult(true, "🏃 You successfully fled from combat! 🏃", false);
            result.setFled(true);
            System.out.println(result.getMessage());
            return result;
        } else {
            System.out.println("Failed to flee!");
            return enemyAttack();
        }
    }

    // Calcola danno basato su attacco e difesa
    private int calculateDamage(int attack, int defense) {
        // Danno base + variazione casuale
        int baseDamage = Math.max(1, attack - defense / 2);
        int variation = random.nextInt(11) - 5;  // da -5 a +5
        int damage = baseDamage + variation;
        return Math.max(1, damage);
    }

    // Assegna ricompense al giocatore (XP e oro) dopo la vittoria
    public void awardRewards() {
        if (!enemy.isAlive()) {
            int expGained = enemy.getExperienceReward();
            int goldGained = enemy.getGoldReward();

            player.gainExperience(expGained);
            player.addGold(goldGained);

            System.out.println("✨ Gained " + expGained + " XP and " + goldGained + " gold! ✨");
        }
    }

    // Restituisce le statistiche attuali del combattimento
    public String getCombatStatus() {
        return String.format(
                "⚔ %s: %d/%d HP ⚔\n" +
                        "🛡 %s: %d/%d HP 🛡",
                player.getName(), player.getHp(), player.getMaxHp(),
                enemy.getName(), enemy.getHp(), enemy.getMaxHp()
        );
    }

    // Classe interna per i risultati del combattimento
    public static class CombatResult {
        private boolean combatEnded;
        private boolean playerWon;
        private boolean itemUsed;
        private boolean fled;
        private boolean playerAction;
        private String message;
        private int damageDealt;

        public CombatResult() {}

        public CombatResult(boolean combatEnded, String message, boolean playerWon) {
            this.combatEnded = combatEnded;
            this.message = message;
            this.playerWon = playerWon;
        }

        // Getters e Setters
        public boolean isCombatEnded() { return combatEnded; }
        public void setCombatEnded(boolean combatEnded) { this.combatEnded = combatEnded; }

        public boolean isPlayerWon() { return playerWon; }
        public void setPlayerWon(boolean playerWon) { this.playerWon = playerWon; }

        public boolean isItemUsed() { return itemUsed; }
        public void setItemUsed(boolean itemUsed) { this.itemUsed = itemUsed; }

        public boolean isFled() { return fled; }
        public void setFled(boolean fled) { this.fled = fled; }

        public boolean isPlayerAction() { return playerAction; }
        public void setPlayerAction(boolean playerAction) { this.playerAction = playerAction; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public int getDamageDealt() { return damageDealt; }
        public void setDamageDealt(int damageDealt) { this.damageDealt = damageDealt; }

        @Override
        public String toString() {
            return message;
        }
    }
}