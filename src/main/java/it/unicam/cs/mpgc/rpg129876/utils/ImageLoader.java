package it.unicam.cs.mpgc.rpg129876.utils;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility per il caricamento e la gestione delle immagini del gioco.
 *
 * Questa classe fornisce metodi statici per caricare immagini dalle cartelle
 * delle risorse. Implementa un sistema di caching per evitare di caricare
 * più volte la stessa immagine, migliorando le prestazioni.
 *
 * Le immagini devono essere posizionate nella cartella:
 *
 * src/main/resources/images/
 *
 *
 * @author Filippo Mangialardo
 * @version 1.0
 */
public class ImageLoader {
    private static final Map<String, Image> cache = new HashMap<>();

    /**
     * Carica un'immagine dal percorso specificato.
     *
     * Se l'immagine è già presente nella cache, viene restituita immediatamente.
     * Altrimenti viene caricata dal file system e aggiunta alla cache.
     *
     * @param path percorso relativo dell'immagine nella cartella resources
     *             (es. "/images/warrior.png")
     * @return l'immagine caricata, o null se il file non viene trovato
     */
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

    /**
     * Carica l'immagine del personaggio in base alla classe.
     *
     * @param playerClass la classe del personaggio (Warrior, Mage, Rogue)
     * @return l'immagine del personaggio, o null se non trovata
     */
    public static Image getPlayerImage(String playerClass) {
        String path = "/images/" + playerClass.toLowerCase() + ".png";
        return loadImage(path);
    }

    /**
     * Carica l'immagine del nemico in base al nome.
     *
     * @param enemyName il nome del nemico (es. "Goblin", "Drago")
     * @return l'immagine del nemico, o null se non trovata
     */
    public static Image getEnemyImage(String enemyName) {
        String path = "/images/" + enemyName.toLowerCase().replace(" ", "") + ".png";
        return loadImage(path);
    }

    public static Image getPotionImage() {
        return loadImage("/images/pozione.png");
    }

    public static Image getMerchantImage() {
        return loadImage("/images/mercante.png");
    }

    public static Image getGoldImage() {return loadImage("/images/gold.png");}
}