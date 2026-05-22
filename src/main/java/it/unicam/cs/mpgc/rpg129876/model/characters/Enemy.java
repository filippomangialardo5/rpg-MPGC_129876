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
        return new Enemy("Goblin", 35, 14, 10, 40, 20);   // XP 50→40
    }

    public static Enemy createOrc() {
        return new Enemy("Orc", 70, 23, 12, 80, 50);     // XP 100→80
    }

    public static Enemy createSkeleton() {
        return new Enemy("Skeleton", 60, 22, 10, 60, 35); // XP 75→60
    }

    public static Enemy createWolf() {
        return new Enemy("Wolf", 40, 24, 5, 40, 25);     // XP 45→40
    }

    public static Enemy createDarkKnight() {
        return new Enemy("Dark Knight", 100, 30, 10, 120, 80); // XP 150→120
    }

    public static Enemy createDragon() {
        return new Enemy("Dragon", 250, 50, 25, 250, 250); // XP 500→250
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
        } else if (random < 0.70) {   // 23% Scheletro
            return createSkeleton();
        } else if (random < 0.87) {   // 17% Orco
            return createOrc();
        } else {                       // 13% Cavaliere Oscuro
            return createDarkKnight();
        }
    }

    @Override
    public String toString() {
        return getName() + " [HP: " + getHp() + "/" + getMaxHp() +
                "] - Rewards: " + experienceReward + " XP, " + goldReward + " gold";
    }
}