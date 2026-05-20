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

    // Factory methods per creare nemici predefiniti
    public static Enemy createGoblin() {
        return new Enemy("Goblin", 40, 12, 5, 50, 20);
    }

    public static Enemy createOrc() {
        return new Enemy("Orc", 70, 18, 8, 100, 50);
    }

    public static Enemy createSkeleton() {
        return new Enemy("Skeleton", 50, 15, 6, 75, 30);
    }

    public static Enemy createWolf() {
        return new Enemy("Wolf", 35, 14, 4, 45, 15);
    }

    public static Enemy createDarkKnight() {
        return new Enemy("Dark Knight", 100, 25, 12, 150, 80);
    }

    public static Enemy createDragon() {
        return new Enemy("Dragon", 200, 35, 20, 500, 300);
    }

    public boolean isDragon() {return this.getName().equalsIgnoreCase("Dragon");
    }

    // Metodo per generare nemico casuale (utile per esplorazione)
    public static Enemy randomEnemy() {
        double random = Math.random();
        if (random < 0.4) {
            return createGoblin();
        } else if (random < 0.7) {
            return createWolf();
        } else if (random < 0.9) {
            return createSkeleton();
        } else {
            return createOrc();
        }
    }

    @Override
    public String toString() {
        return getName() + " [HP: " + getHp() + "/" + getMaxHp() +
                "] - Rewards: " + experienceReward + " XP, " + goldReward + " gold";
    }
}