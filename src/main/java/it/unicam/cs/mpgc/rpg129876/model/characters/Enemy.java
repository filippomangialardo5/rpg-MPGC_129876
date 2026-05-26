package it.unicam.cs.mpgc.rpg129876.model.characters;

public class Enemy extends GameCharacter {

    private int experienceReward;
    private int goldReward;

    public Enemy(String name, int maxHp, int attack, int defense, int experienceReward, int goldReward) {
        super(name, maxHp, attack, defense);
        this.experienceReward = experienceReward;
        this.goldReward = goldReward;
    }

    // Getters
    public int getExperienceReward() { return experienceReward; }
    public int getGoldReward() { return goldReward; }

    // Setters
    public void setExperienceReward(int experienceReward) { this.experienceReward = experienceReward; }
    public void setGoldReward(int goldReward) { this.goldReward = goldReward; }

    public static Enemy createGoblin() {
        return new Enemy("Goblin", 30, 14, 5, 40, 20);
    }

    public static Enemy createWolf() {
        return new Enemy("Lupo", 40, 17, 7, 40, 25);
    }

    public static Enemy createSkeleton() {
        return new Enemy("Scheletro", 50, 20, 8, 60, 35);
    }

    public static Enemy createOrc() {
        return new Enemy("Orco", 60, 22, 12, 80, 50);
    }

    public static Enemy createDarkKnight() {
        return new Enemy("Cavaliere oscuro", 100, 30, 10, 120, 80);
    }

    public static Enemy createDragon() {
        return new Enemy("Drago", 200, 25, 15, 250, 250);
    }

    public boolean isDragon() {return this.getName().equalsIgnoreCase("Dragon");
    }

    // Metodo per generare nemico casuale (utile per esplorazione)
    public static Enemy randomEnemy() {
        double random = Math.random();
        if (random < 0.27) {          // 27% Goblin
            return createGoblin();
        } else if (random < 0.47) {   // 20% Lupo
            return createWolf();
        } else if (random < 0.67) {   // 20% Scheletro
            return createSkeleton();
        } else if (random < 0.82) {   // 15% Orco
            return createOrc();
        } else {                       // 18% Cavaliere Oscuro
            return createDarkKnight();
        }
    }

    @Override
    public String toString() {
        return getName() + " [HP: " + getHp() + "/" + getMaxHp() +
                "] - Rewards: " + experienceReward + " XP, " + goldReward + " gold";
    }
}