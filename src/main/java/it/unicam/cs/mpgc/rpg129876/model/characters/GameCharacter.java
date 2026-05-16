package it.unicam.cs.mpgc.rpg129876.model.characters;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public abstract class GameCharacter {

    // Observable properties per JavaFX binding
    protected final StringProperty name = new SimpleStringProperty();
    protected final IntegerProperty hp = new SimpleIntegerProperty();
    protected final IntegerProperty maxHp = new SimpleIntegerProperty();
    protected final IntegerProperty attack = new SimpleIntegerProperty();
    protected final IntegerProperty defense = new SimpleIntegerProperty();

    public GameCharacter(String name, int maxHp, int attack, int defense) {
        this.name.set(name);
        this.maxHp.set(maxHp);
        this.hp.set(maxHp);
        this.attack.set(attack);
        this.defense.set(defense);
    }

    // Property getters per binding (JavaFX)
    public StringProperty nameProperty() { return name; }
    public IntegerProperty hpProperty() { return hp; }
    public IntegerProperty maxHpProperty() { return maxHp; }
    public IntegerProperty attackProperty() { return attack; }
    public IntegerProperty defenseProperty() { return defense; }

    // Getters
    public String getName() { return name.get(); }
    public int getHp() { return hp.get(); }
    public int getMaxHp() { return maxHp.get(); }
    public int getAttack() { return attack.get(); }
    public int getDefense() { return defense.get(); }

    // Setters with bounds
    public void setName(String name) { this.name.set(name); }
    public void setHp(int hp) { this.hp.set(Math.max(0, Math.min(hp, maxHp.get()))); }
    public void setMaxHp(int maxHp) { this.maxHp.set(maxHp); }
    public void setAttack(int attack) { this.attack.set(attack); }
    public void setDefense(int defense) { this.defense.set(defense); }

    // Action methods
    public void takeDamage(int damage) {
        int actualDamage = Math.max(1, damage - getDefense());
        setHp(getHp() - actualDamage);
    }

    public void heal(int amount) {
        setHp(getHp() + amount);
    }

    public boolean isAlive() {
        return getHp() > 0;
    }

    @Override
    public String toString() {
        return getName() + " [HP: " + getHp() + "/" + getMaxHp() + "]";
    }
}