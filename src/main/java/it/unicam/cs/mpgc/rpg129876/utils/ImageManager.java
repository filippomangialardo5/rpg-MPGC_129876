package it.unicam.cs.mpgc.rpg129876.utils;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class ImageManager {
    private static final Map<String, Image> cache = new HashMap<>();

    // Carica un'immagine con caching
    public static Image getImage(String path) {
        if (cache.containsKey(path)) {
            return cache.get(path);
        }

        try {
            var inputStream = ImageManager.class.getResourceAsStream(path);
            if (inputStream != null) {
                Image img = new Image(inputStream, 60, 60, true, true);
                cache.put(path, img);
                return img;
            }
        } catch (Exception e) {
            System.err.println("Immagine non trovata: " + path);
        }
        return null;
    }

    // Immagini personaggi
    public static Image getPlayerImage(String playerClass) {
        return getImage("/images/characters/" + playerClass.toLowerCase() + ".png");
    }

    public static Image getEnemyImage(String enemyName) {
        return getImage("/images/characters/" + enemyName.toLowerCase().replace(" ", "") + ".png");
    }

    // Immagini oggetti
    public static Image getPotionImage() {
        return getImage("/images/items/potion.png");
    }

    public static Image getSwordImage() {
        return getImage("/images/items/sword.png");
    }

    public static Image getGoldImage() {
        return getImage("/images/items/gold.png");
    }

    // Immagini UI
    public static Image getHealthBarImage() {
        return getImage("/images/ui/healthbar.png");
    }
}