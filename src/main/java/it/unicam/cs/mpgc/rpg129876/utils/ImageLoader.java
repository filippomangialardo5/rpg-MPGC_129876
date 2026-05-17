package it.unicam.cs.mpgc.rpg129876.utils;

import javafx.scene.image.Image;

public class ImageLoader {

    // Metodo per caricare un'immagine dalle risorse
    public static Image loadImage(String path) {
        try {
            var inputStream = ImageLoader.class.getResourceAsStream(path);
            if (inputStream == null) {
                System.err.println("Immagine non trovata: " + path);
                return null;
            }
            return new Image(inputStream);
        } catch (Exception e) {
            System.err.println("Errore caricamento immagine: " + path);
            return null;
        }
    }

    // Immagini del giocatore per classe
    public static Image getWarriorImage() {
        return loadImage("/images/warrior.png");
    }

    public static Image getMageImage() {
        return loadImage("/images/mage.png");
    }

    public static Image getRogueImage() {
        return loadImage("/images/rogue.png");
    }

    // Immagini dei nemici
    public static Image getGoblinImage() {
        return loadImage("/images/goblin.png");
    }

    public static Image getOrcImage() {
        return loadImage("/images/orc.png");
    }

    public static Image getSkeletonImage() {
        return loadImage("/images/skeleton.png");
    }

    public static Image getDragonImage() {
        return loadImage("/images/dragon.png");
    }

    public static Image getWolfImage() {
        return loadImage("/images/wolf.png");
    }

    public static Image getDarkKnightImage() {
        return loadImage("/images/darkknight.png");
    }

    // Immagini di oggetti e UI
    public static Image getPotionImage() {
        return loadImage("/images/potion.png");
    }

    public static Image getSwordImage() {
        return loadImage("/images/sword.png");
    }

    public static Image getShieldImage() {
        return loadImage("/images/shield.png");
    }

    public static Image getGoldImage() {
        return loadImage("/images/gold.png");
    }

    public static Image getChestImage() {
        return loadImage("/images/chest.png");
    }

    // Immagine della mappa (background 2D)
    public static Image getMapBackground() {
        return loadImage("/images/map_background.png");
    }

    // Ottieni l'immagine in base al nome del nemico
    public static Image getEnemyImage(String enemyName) {
        switch(enemyName.toLowerCase()) {
            case "goblin": return getGoblinImage();
            case "orc": return getOrcImage();
            case "skeleton": return getSkeletonImage();
            case "dragon": return getDragonImage();
            case "wolf": return getWolfImage();
            case "dark knight": return getDarkKnightImage();
            default: return null;
        }
    }

    // Ottieni l'immagine in base alla classe del giocatore
    public static Image getPlayerImage(String playerClass) {
        switch(playerClass.toLowerCase()) {
            case "warrior": return getWarriorImage();
            case "mage": return getMageImage();
            case "rogue": return getRogueImage();
            default: return null;
        }
    }
}