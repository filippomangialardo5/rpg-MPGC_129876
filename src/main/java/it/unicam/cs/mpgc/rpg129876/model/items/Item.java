package it.unicam.cs.mpgc.rpg129876.model.items;

import it.unicam.cs.mpgc.rpg129876.model.characters.Player;

public interface Item {

    String getName();

    String getDescription();

    void use(Player player);

    default boolean isStackable() {
        return true;
    }

    default String getIcon() {
        return "📦";
    }
}