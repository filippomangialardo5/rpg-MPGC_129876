package it.unicam.cs.mpgc.rpg129876.model.combat;

import it.unicam.cs.mpgc.rpg129876.model.characters.Player;
import it.unicam.cs.mpgc.rpg129876.model.characters.Enemy;
import it.unicam.cs.mpgc.rpg129876.model.items.HealthPotion;
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

    public CombatResult useItem(Item item) {
        if (!inCombat) return new CombatResult(false, "Not in combat!", false);

        int oldHp = player.getHp();
        int maxHp = player.getMaxHp();

        // Verifica se ha bisogno di cura
        if (oldHp >= maxHp) {
            CombatResult result = new CombatResult();
            result.setMessage("❌ Sei già a piena vita! Pozione non usata.");
            result.setItemUsed(false);
            result.setPlayerAction(true);
            return result;
        }

        // Salva la quantità prima di usare
        int oldQuantity = 1;
        if (item instanceof HealthPotion) {
            oldQuantity = ((HealthPotion) item).getQuantity();
        }

        item.use(player);

        int newHp = player.getHp();
        int healed = newHp - oldHp;

        System.out.println("DEBUG: oldHp=" + oldHp + ", newHp=" + newHp + ", healed=" + healed);

        CombatResult result = new CombatResult();
        if (healed > 0) {
            result.setMessage("🧪 Usata pozione! +" + healed + " HP! (da " + oldHp + " a " + newHp + ")");
        } else {
            result.setMessage("🧪 Usata pozione! Nessun effetto (vita piena)");
        }
        result.setItemUsed(true);
        result.setPlayerAction(true);

        return result;
    }

    // Tentativo di fuga
    public CombatResult flee() {
        int chance = random.nextInt(100);
        if (chance < 50) {  // 50% di successo
            inCombat = false;
            CombatResult result = new CombatResult(true, "🏃 SEI RIUSCITO A FUGGIRE! 🏃", false);
            result.setFled(true);
            System.out.println("Fuga riuscita!");
            return result;
        } else {
            // Fuga fallita: subisci un attacco
            int damage = calculateDamage(enemy.getAttack(), player.getDefense());
            player.takeDamage(damage);
            CombatResult result = new CombatResult(false, "🏃 FUGA FALLITA! " + enemy.getName() + " ti colpisce con " + damage + " danni!", false);
            result.setFled(false);
            result.setDamageDealt(damage);
            System.out.println("Fuga fallita! Danno subito: " + damage);
            return result;
        }
    }

    private int calculateDamage(int attack, int defense) {
        // Danno base = attacco - difesa/2 (minimo 3)
        int baseDamage = Math.max(3, attack - defense / 2);
        // Variazione casuale tra -2 e +2 (più prevedibile)
        int variation = random.nextInt(5) - 2;  // -2, -1, 0, 1, 2
        int damage = baseDamage + variation;
        int finalDamage = Math.max(1, damage);

        System.out.println("DEBUG: attack=" + attack + ", defense=" + defense +
                ", base=" + baseDamage + ", var=" + variation +
                ", final=" + finalDamage);
        return finalDamage;
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