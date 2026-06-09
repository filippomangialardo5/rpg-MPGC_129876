package it.unicam.cs.mpgc.rpg129876.model.characters;

/**
 * Rappresenta un nemico del gioco.
 * Contiene statistiche e ricompense (XP e oro) per la sconfitta.
 *
 * @author Filippo Mangialardo
 * @version 1.0
 */
public class Enemy extends GameCharacter {

    private final int experienceReward;
    private final int goldReward;

    public Enemy(String name, int maxHp, int attack, int defense, int experienceReward, int goldReward) {
        super(name, maxHp, attack, defense);
        this.experienceReward = experienceReward;
        this.goldReward = goldReward;
    }

    // Getters
    public int getExperienceReward() { return experienceReward; }

    public int getGoldReward() { return goldReward; }

    /**
     * Crea un nuovo nemico di tipo Goblin.
     *
     * @return istanza di Goblin
     */
    public static Enemy createGoblin() {
        return new Enemy("Goblin", 30, 15, 6, 40, 20);
    }

    /**
     * Crea un nuovo nemico di tipo Wolf
     *
     * @return istanza di Wolf
     */
    public static Enemy createWolf() {
        return new Enemy("Lupo", 40, 18, 8, 40, 25);
    }

    /**
     * Crea un nuovo nemico di tipo Skeleton
     *
     * @return istanza di Skeleton
     */
    public static Enemy createSkeleton() {
        return new Enemy("Scheletro", 50, 20, 9, 50, 35);
    }

    /**
     * Crea un nuovo nemico di tipo Orc
     *
     * @return istanza di Orc
     */
    public static Enemy createOrc() {
        return new Enemy("Orco", 60, 23, 13, 60, 50);
    }

    /**
     * Crea un nuovo nemico di tipo DarkKnight
     *
     * @return istanza di DarkKnight
     */
    public static Enemy createDarkKnight() {
        return new Enemy("Cavaliere oscuro", 100, 30, 11, 100, 60);
    }

    /**
     * Crea un nuovo nemico di tipo Drago (Boss finale).
     *
     * @return istanza di Drago
     */
    public static Enemy createDragon() {
        return new Enemy("Drago", 200, 25, 15, 0, 100);
    }

    /**
     * Genera un nemico casuale per l'esplorazione del dungeon.
     * La probabilità di spawn è distribuita come segue:
     *
     * | Nemico | Probabilità |
     * |--------|-------------|
     * | Goblin | 25% |
     * | Lupo | 22% |
     * | Scheletro | 20% |
     * | Orco | 15% |
     * | Cavaliere Oscuro | 18% |
     *
     * I nemici più deboli (Goblin, Lupo) sono più comuni,
     * mentre quelli più forti (Orco, Cavaliere Oscuro) sono più rari.
     *
     * @return un nemico casuale tra quelli disponibili
     */
    public static Enemy randomEnemy() {
        double random = Math.random();
        if (random < 0.25) {          // 25% Goblin
            return createGoblin();
        } else if (random < 0.47) {   // 22% Lupo
            return createWolf();
        } else if (random < 0.67) {   // 20% Scheletro
            return createSkeleton();
        } else if (random < 0.82) {   // 15% Orco
            return createOrc();
        } else {                       // 18% Cavaliere Oscuro
            return createDarkKnight();
        }
    }

    /**
     * Restituisce una rappresentazione testuale del nemico.
     * Formato: "NomeNemico [HP: X/Y] - Ricompense: XP Z, Oro W"
     *
     * Esempio: "Goblin [HP: 40/40] - Ricompense: 50 XP, 20 oro"
     *
     * @return stringa descrittiva del nemico con statistiche e ricompense
     */
    @Override
    public String toString() {
        return getName() + " [HP: " + getHp() + "/" + getMaxHp() +
                "] - Rewards: " + experienceReward + " XP, " + goldReward + " gold";
    }
}