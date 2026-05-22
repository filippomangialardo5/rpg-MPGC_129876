package it.unicam.cs.mpgc.rpg129876.utils;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader {
    private static final Map<String, Image> cache = new HashMap<>();

    public static Image loadImage(String path) {
        if (cache.containsKey(path)) {
            return cache.get(path);
        }

        try {
            var inputStream = ImageLoader.class.getResourceAsStream(path);
            if (inputStream != null) {
                Image img = new Image(inputStream);
                cache.put(path, img);
                return img;
            } else {
                System.err.println("Immagine non trovata: " + path);
            }
        } catch (Exception e) {
            System.err.println("Errore caricamento: " + path);
        }
        return null;
    }

    // Immagini personaggi
    public static Image getPlayerImage(String playerClass) {
        String path = "/images/" + playerClass.toLowerCase() + ".png";
        return loadImage(path);
    }

    // Immagini nemici
    public static Image getEnemyImage(String enemyName) {
        String path = "/images/" + enemyName.toLowerCase().replace(" ", "") + ".png";
        return loadImage(path);
    }

    public static Image getPotionImage() {
        return loadImage("/images/potion.png");
    }

    public static Image getMerchantImage() {
        return loadImage("/images/merchant.png");
    }

    public static Image getGoldImage() {return loadImage("/images/gold.png");}

    public static Image getChestImage() {return loadImage("/images/chest.png");}
}