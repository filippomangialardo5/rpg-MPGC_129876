package it.unicam.cs.mpgc.rpg129876.model.characters;

import it.unicam.cs.mpgc.rpg129876.model.items.HealthPotion;
import it.unicam.cs.mpgc.rpg129876.model.items.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Merchant {

    private String name;
    private ObservableList<Item> itemsForSale;

    public Merchant(String name) {
        this.name = name;
        this.itemsForSale = FXCollections.observableArrayList();
        setupShop();
    }

    private void setupShop() {
        // Pozione normale: 30 gold
        itemsForSale.add(new HealthPotion(1, 30));
        // Pozione potente: 60 gold
        itemsForSale.add(new HealthPotion(1, 50));
    }

    public String getName() { return name; }
    public ObservableList<Item> getItemsForSale() { return itemsForSale; }

    public boolean buyItem(Item item, Player player) {
        int price = getItemPrice(item);
        if (player.getGold() >= price) {
            player.addGold(-price);
            player.addItem(item);
            return true;
        }
        return false;
    }

    private int getItemPrice(Item item) {
        if (item instanceof HealthPotion) {
            int healAmount = ((HealthPotion) item).getHealAmount();
            return healAmount == 30 ? 30 : 60;
        }
        return 0;
    }
}