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
                Image img = new Image(inputStream, 80, 80, true, true);
                cache.put(path, img);
                return img;
            } else {
                System.err.println("Immagine non trovata: " + path);
            }
        } catch (Exception e) {
            System.err.println("Errore caricamento immagine: " + path + " - " + e.getMessage());
        }
        return null;
    }

    // Immagini personaggi giocatore
    public static Image getWarriorImage() { return loadImage("/images/warrior.png"); }
    public static Image getMageImage() { return loadImage("/images/mage.png"); }
    public static Image getRogueImage() { return loadImage("/images/rogue.png"); }

    // Immagini nemici
    public static Image getGoblinImage() { return loadImage("/images/goblin.png"); }
    public static Image getOrcImage() { return loadImage("/images/orc.png"); }
    public static Image getSkeletonImage() { return loadImage("/images/skeleton.png"); }
    public static Image getDragonImage() { return loadImage("/images/dragon.png"); }
    public static Image getWolfImage() { return loadImage("/images/wolf.png"); }

    // Immagini item
    public static Image getPotionImage() { return loadImage("/images/potion.png"); }
    public static Image getGoldImage() { return loadImage("/images/gold.png"); }

    // Metodo generico per nemico
    public static Image getEnemyImage(String enemyName) {
        switch(enemyName.toLowerCase()) {
            case "goblin": return getGoblinImage();
            case "orc": return getOrcImage();
            case "skeleton": return getSkeletonImage();
            case "dragon": return getDragonImage();
            case "wolf": return getWolfImage();
            default: return null;
        }
    }

    // Metodo generico per giocatore
    public static Image getPlayerImage(String playerClass) {
        switch(playerClass.toLowerCase()) {
            case "warrior": return getWarriorImage();
            case "mage": return getMageImage();
            case "rogue": return getRogueImage();
            default: return null;
        }
    }
}