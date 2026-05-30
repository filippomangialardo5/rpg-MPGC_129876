package it.unicam.cs.mpgc.rpg129876.model.world;

import it.unicam.cs.mpgc.rpg129876.model.characters.Enemy;
import it.unicam.cs.mpgc.rpg129876.model.characters.Merchant;
import it.unicam.cs.mpgc.rpg129876.model.characters.Player;
import it.unicam.cs.mpgc.rpg129876.model.items.HealthPotion;
import it.unicam.cs.mpgc.rpg129876.model.items.Item;
import java.util.*;

public class Dungeon {

    private final int width;
    private final int height;
    private final Room[][] map;
    private Room currentRoom;
    private final Random random;
    private int currentLevel;

    public Dungeon(int width, int height) {
        this.width = width;
        this.height = height;
        this.map = new Room[height][width];
        this.random = new Random();
        this.currentLevel = 1;
        generateDungeon();
        setupBossArea();

        // FORZA l'esplorazione della stanza iniziale e delle aree circostanti per test
        map[0][0].setExplored(true);

        Room door = map[height-1][width-1];
        System.out.println("Porta creata in posizione: [" + (width-1) + "," + (height-1) + "]");
        System.out.println("isDoorRoom: " + door.isDoorRoom());
    }

    private void generateDungeon() {
        // Crea tutte le stanze
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = new Room(x, y);
            }
        }

        // Collega le stanze adiacenti (griglia completa)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Room room = map[y][x];

                if (y > 0) room.setExit(Direction.NORTH, map[y-1][x]);
                if (y < height - 1) room.setExit(Direction.SOUTH, map[y+1][x]);
                if (x > 0) room.setExit(Direction.WEST, map[y][x-1]);
                if (x < width - 1) room.setExit(Direction.EAST, map[y][x+1]);
            }
        }

        // Imposta nomi e descrizioni
        setupRooms();

        // Popola con nemici e tesori
        populateDungeon();

        currentRoom = map[0][0];
        currentRoom.enter();
        currentRoom.setName("🏛 Punto di partenza");
        currentRoom.setDescription("Qui inizia la tua avventura!");
        currentRoom.setEnemy(null);  // Nessun nemico all'inizio

        Room bossRoom = map[height-1][width-1];
        bossRoom.setName("🐉 Trono dei Draghi 🐉");
        bossRoom.setDescription("Tre draghi custodiscono il tesoro! Sconfiggili tutti per vincere!");
        bossRoom.setEnemy(Enemy.createDragon());
        bossRoom.setDragonCount(3); // Aggiungi questo campo
    }

    private void setupRooms() {
        String[] nomi = {
                "🌿 Stanza della Foresta", "⚰ Cripta Antica", "💧 Caverna Umida",
                "🔥 Sala del Fuoco", "❄ Tempio di Ghiaccio", "⚡ Torre del Tuono",
                "🌑 Abisso Oscuro", "✨ Camera dei Cristalli", "🍄 Fungaia Tossica",
                "🏺 Magazzino Abbandonato", "📚 Biblioteca Arcana", "🔮 Sala delle Profezie"
        };

        String[] descrizioni = {
                "Muschio e radici ricoprono le pareti.", "L'aria è fredda e inquietante.",
                "L'acqua gocciola dal soffitto.", "Il calore è quasi insopportabile.",
                "Brividi di freddo ti percorrono la schiena.", "L'elettricità statica nell'aria fa rizzare i capelli.",
                "L'oscurità sembra viva e pulsante.", "Cristalli luminosi illuminano la stanza.",
                "Funghi giganti crescono ovunque.", "Polvere e ragnatele coprono ogni cosa.",
                "Antichi tomi sono allineati sugli scaffali.", "Una sfera di cristallo brilla al centro."
        };

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Room room = map[y][x];
                int idx = (x + y) % nomi.length;
                room.setName(nomi[idx]);
                room.setDescription(descrizioni[idx]);
            }
        }
    }

    private void populateDungeon() {
        int merchantsAdded = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Room room = map[y][x];
                if ((x == 0 && y == 0) || (x == width-1 && y == height-1)) continue;

                // Mercanti (2 max) - probabilità 5%
                if (merchantsAdded < 2 && random.nextDouble() < 0.05) {
                    room.setMerchant(new Merchant("🏪 Mercante"));
                    room.setName("🏪 Negozio");
                    room.setDescription("Un mercante vende pozioni curative!");
                    merchantsAdded++;
                    continue;
                }

                // Nemici - probabilità 35%
                if (random.nextDouble() < 0.35) {
                    room.setEnemy(Enemy.randomEnemy());
                }

                // TESORI - probabilità 15%
                if (random.nextDouble() < 0.15) {
                    room.addTreasure(generateTreasure());
                }
            }
        }
    }

    private Item generateTreasure() {
        double randomValue = this.random.nextDouble();
        // 40% oro, 60% pozione
        if (randomValue < 0.4) {
            // Oro: quantità variabile da 15 a 60
            int goldAmount = 15 + this.random.nextInt(46);
            return new Item() {
                @Override
                public String getName() { return goldAmount + " monete d'oro"; }
                @Override
                public String getDescription() { return "Contiene " + goldAmount + " monete d'oro"; }
                @Override
                public void use(Player player) { player.addGold(goldAmount); }
                @Override
                public String getIcon() { return "💰"; }
            };
        } else {
            return new HealthPotion(1);
        }
    }


    private void setupBossArea() {
        int bossX = width - 1;
        int bossY = height - 1;

        System.out.println("=== SETUP BOSS AREA ===");
        System.out.println("Porta posizione: [" + bossX + "," + bossY + "]");

        // PORTA
        Room doorRoom = map[bossY][bossX];
        doorRoom.setName("🚪 PORTA DEL TESORO 🚪");
        doorRoom.setDescription("Una porta antica brilla di luce dorata. Oltre si intravede un tesoro leggendario!");
        doorRoom.setEnemy(null);
        doorRoom.setDoorRoom(true);
        doorRoom.setExplored(true);  // Rende visibile la porta
        doorRoom.setVisited(false);

        // Drago a SINISTRA (x-1, y)
        if (bossX - 1 >= 0) {
            Room leftRoom = map[bossY][bossX - 1];
            leftRoom.setName("🐉 Tana del Drago Orientale 🐉");
            leftRoom.setDescription("Un drago rosso fiammeggiante ti blocca la strada!");
            leftRoom.setEnemy(Enemy.createDragon());
            leftRoom.setHasDragon(true);
            leftRoom.setExplored(true);
            System.out.println("Drago sinistra in: [" + (bossX-1) + "," + bossY + "]");
        }

        // Drago SOPRA (x, y-1)
        if (bossY - 1 >= 0) {
            Room upRoom = map[bossY - 1][bossX];
            upRoom.setName("🐉 Tana del Drago Settentrionale 🐉");
            upRoom.setDescription("Un drago blu elettrico ti fissa con occhi di ghiaccio!");
            upRoom.setEnemy(Enemy.createDragon());
            upRoom.setHasDragon(true);
            upRoom.setExplored(true);
            System.out.println("Drago sopra in: [" + bossX + "," + (bossY-1) + "]");
        }

        // Drago in DIAGONALE (x-1, y-1)
        if (bossX - 1 >= 0 && bossY - 1 >= 0) {
            Room diagRoom = map[bossY - 1][bossX - 1];
            diagRoom.setName("🐉 Tana del Drago Occidentale 🐉");
            diagRoom.setDescription("Un drago nero emerge dalle ombre, pronto a combattere!");
            diagRoom.setEnemy(Enemy.createDragon());
            diagRoom.setHasDragon(true);
            diagRoom.setExplored(true);
            System.out.println("Drago diagonale in: [" + (bossX-1) + "," + (bossY-1) + "]");
        }
    }

    // Controlla se tutti i draghi intorno alla porta sono stati sconfitti
    public boolean areAllDragonsDefeated() {
        int bossX = width - 1;
        int bossY = height - 1;

        System.out.println("=== CHECK DRAGONS ===");

        // Controlla drago a sinistra
        if (bossX - 1 >= 0) {
            Room leftRoom = map[bossY][bossX - 1];
            if (leftRoom.hasDragon() && leftRoom.hasEnemy()) {
                System.out.println("Drago sinistra ancora vivo");
                return false;
            }
        }

        // Controlla drago sopra
        if (bossY - 1 >= 0) {
            Room upRoom = map[bossY - 1][bossX];
            if (upRoom.hasDragon() && upRoom.hasEnemy()) {
                System.out.println("Drago sopra ancora vivo");
                return false;
            }
        }

        // Controlla drago in diagonale
        if (bossX - 1 >= 0 && bossY - 1 >= 0) {
            Room diagRoom = map[bossY - 1][bossX - 1];
            if (diagRoom.hasDragon() && diagRoom.hasEnemy()) {
                System.out.println("Drago diagonale ancora vivo");
                return false;
            }
        }

        System.out.println("TUTTI I DRAGHI SONO STATI SCONFITTI!");
        return true;
    }

    public boolean isBossRoom() {
        Room current = getCurrentRoom();
        if (current.isDoorRoom()) {
            // Se è la porta, controlla se i draghi sono stati sconfitti
            if (areAllDragonsDefeated()) {
                current.setDragonsDefeated(true);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    // Movimento
    public boolean move(Direction direction) {
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom != null) {
            currentRoom = nextRoom;
            if (!currentRoom.isVisited()) {
                currentRoom.enter();
            }
            return true;
        }
        return false;
    }

    public Room getCurrentRoom() { return currentRoom; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Room getRoomAt(int x, int y) { return map[y][x]; }
}