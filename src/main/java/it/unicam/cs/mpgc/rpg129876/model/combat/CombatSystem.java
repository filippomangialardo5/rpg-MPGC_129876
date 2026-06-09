package it.unicam.cs.mpgc.rpg129876.model.combat;

import it.unicam.cs.mpgc.rpg129876.model.characters.Player;
import it.unicam.cs.mpgc.rpg129876.model.characters.Enemy;
import it.unicam.cs.mpgc.rpg129876.model.items.Item;
import java.util.Random;

/**
 * Gestisce il sistema di combattimento a turni.
 * Calcola i danni, gestisce gli attacchi del giocatore e del nemico, e la fuga.
 *
 * @author Filippo Mangialardo
 * @version 1.0
 */
public class CombatSystem {

    private final Player player;
    private final Enemy enemy;
    private boolean inCombat;
    private final Random random;

    public CombatSystem(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
        this.inCombat = true;
        this.random = new Random();
    }

    // Getters
    public boolean isInCombat() { return inCombat; }

    public Enemy getEnemy() { return enemy; }

    /**
     * Esegue l'attacco del giocatore contro il nemico.
     * Calcola il danno in base ad attacco del giocatore e difesa del nemico.
     *
     * @return risultato del combattimento con messaggio e danno inflitto
     */
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

    /**
     * Esegue l'attacco del nemico contro il giocatore.
     * Calcola il danno in base ad attacco del nemico e difesa del giocatore.
     *
     * @return risultato del combattimento con messaggio e danno inflitto
     */
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

    /**
     * Esegue il turno del nemico durante il combattimento.
     * Calcola il danno inflitto dal nemico al giocatore e aggiorna gli HP.
     * Se il giocatore muore, termina il combattimento.
     *
     * @return un oggetto CombatResult contenente l'esito del turno
     */
    public CombatResult enemyTurn() {
        if (!inCombat) return new CombatResult(false, "Combat is over!", false);
        return enemyAttack();
    }

    /**
     * Utilizza un oggetto (es. pozione) durante il combattimento.
     * Applica l'effetto dell'oggetto al giocatore (es. cura HP).
     *
     * @param item l'oggetto da utilizzare (deve implementare l'interfaccia Item)
     * @return un oggetto CombatResult con il messaggio dell'effetto
     */
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

    /**
     * Tenta la fuga dal combattimento.
     *
     * Meccanica:
     * - 50% di probabilità di successo → il combattimento termina
     * - 50% di probabilità di fallimento → il giocatore subisce un attacco gratuito
     *
     * @return CombatResult con esito della fuga (riuscita/fallita) e eventuale danno subito
     */
    public CombatResult flee() {
        int chance = random.nextInt(100);
        if (chance < 50) {  // 50% di successo
            inCombat = false;
            CombatResult result = new CombatResult(true, "🏃 SEI RIUSCITO A FUGGIRE! 🏃", false);
            result.setFled(true);
            return result;
        } else {
            // Fuga fallita: subisci un attacco
            int damage = calculateDamage(enemy.getAttack(), player.getDefense());
            player.takeDamage(damage);

            System.out.println("Fuga fallita! Danno subito: " + damage + ", HP rimasti: " + player.getHp());

            // Se il giocatore è morto, il combattimento termina
            if (!player.isAlive()) {
                inCombat = false;
                CombatResult result = new CombatResult(true, "🏃 FUGA FALLITA! " + enemy.getName() + " ti colpisce con " + damage + " danni e ti ha sconfitto! 💀", false);
                result.setFled(false);
                result.setDamageDealt(damage);
                result.setCombatEnded(true);
                result.setPlayerWon(false);
                return result;
            }

            // Se il giocatore è ancora vivo, il combattimento continua
            CombatResult result = new CombatResult(false, "🏃 FUGA FALLITA! " + enemy.getName() + " ti colpisce con " + damage + " danni!", false);
            result.setFled(false);
            result.setDamageDealt(damage);
            result.setCombatEnded(false);  // IMPORTANTE: false = combattimento continua
            return result;
        }
    }

    /**
     * Calcola il danno inflitto in un attacco.
     *
     * Formula:
     *
     * dannoBase = attacco - (difesa / 2) → minimo 3
     * variazione = casuale tra -2 e +2
     * danno finale = max(1, dannoBase + variazione)
     *
     *
     * @param attack il valore di attacco dell'attaccante
     * @param defense il valore di difesa del difensore
     * @return il danno calcolato (minimo 1)
     */
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

    /**
     * Assegna le ricompense al giocatore dopo la vittoria.
     * Il giocatore guadagna:
     * - Esperienza (XP) per salire di livello
     * - Oro per acquistare oggetti dai mercanti
     */
    public void awardRewards() {
        if (!enemy.isAlive()) {
            int expGained = enemy.getExperienceReward();
            int goldGained = enemy.getGoldReward();

            player.gainExperience(expGained);
            player.addGold(goldGained);

            System.out.println("✨ Gained " + expGained + " XP and " + goldGained + " gold! ✨");
        }
    }

    /**
     * Restituisce una stringa con lo stato attuale del combattimento.
     * Mostra gli HP di giocatore e nemico in un formato leggibile.
     *
     * Formato:
     *
     * Giocatore: X/Y HP
     * Nemico: X/Y HP
     *
     * @return stringa con lo stato del combattimento
     */
    public String getCombatStatus() {
        return String.format(
                "⚔ %s: %d/%d HP ⚔\n" +
                        "🛡 %s: %d/%d HP 🛡",
                player.getName(), player.getHp(), player.getMaxHp(),
                enemy.getName(), enemy.getHp(), enemy.getMaxHp()
        );
    }

    /**
     * Classe interna che rappresenta il risultato di un'azione di combattimento.
     * Contiene tutte le informazioni necessarie per aggiornare l'interfaccia utente.
     *
     * Campi principali:
     * - combatEnded: true se il combattimento è terminato
     * - playerWon: true se il giocatore ha vinto (solo se combatEnded = true)
     * - itemUsed: true se è stato usato un oggetto
     * - fled: true se il giocatore è riuscito a fuggire
     * - playerAction: true se l'azione è del giocatore (false se del nemico)
     * - message: messaggio descrittivo da mostrare all'utente
     * - damageDealt: quantità di danno inflitto nell'attacco
     */
    public static class CombatResult {
        private boolean combatEnded;
        private boolean playerWon;
        private boolean itemUsed;
        private boolean fled;
        private boolean playerAction;
        private String message;
        private int damageDealt;

        /**
         * Costruttore vuoto per risultato predefinito.
         */
        public CombatResult() {}

        /**
         * Costruttore per risultato con esito immediato (fine combattimento).
         *
         * @param combatEnded true se il combattimento è terminato
         * @param message messaggio descrittivo
         * @param playerWon true se il giocatore ha vinto
         */
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

        public void setItemUsed(boolean itemUsed) { this.itemUsed = itemUsed; }

        public void setFled(boolean fled) { this.fled = fled; }

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