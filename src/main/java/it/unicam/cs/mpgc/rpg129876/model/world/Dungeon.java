package it.unicam.cs.mpgc.rpg129876.model.world;

import it.unicam.cs.mpgc.rpg129876.model.characters.Enemy;
import it.unicam.cs.mpgc.rpg129876.model.items.HealthPotion;
import it.unicam.cs.mpgc.rpg129876.model.items.Item;
import java.util.*;

public class Dungeon {

    private final int width;
    private final int height;
    private final Room[][] map;
    private Room currentRoom;
    private int currentLevel;
    private final Random random;

    public Dungeon(int width, int height) {
        this.width = width;
        this.height = height;
        this.map = new Room[height][width];
        this.random = new Random();
        this.currentLevel = 1;
        generateDungeon();
        setupBossArea();
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
                "🌿 Stanza della Foresta", "🪦 Cripta Antica", "💧 Caverna Umida",
                "🔥 Sala del Fuoco", "❄️ Tempio di Ghiaccio", "⚡ Torre del Tuono",
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
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Room room = map[y][x];

                // Salta la stanza iniziale e la stanza del boss
                if ((x == width/2 && y == height/2) ||
                        (x == width-1 && y == height-1)) {
                    continue;
                }

                // 40% probabilità di avere un nemico
                if (random.nextDouble() < 0.4) {
                    room.setEnemy(generateEnemyByLevel());
                }

                // 20% probabilità di avere un tesoro
                if (random.nextDouble() < 0.2) {
                    room.addTreasure(generateTreasure());
                }
            }
        }
    }

    private Enemy generateEnemyByLevel() {
        double r = random.nextDouble();
        if (currentLevel == 1) {
            if (r < 0.6) return Enemy.createGoblin();
            if (r < 0.9) return Enemy.createWolf();
            return Enemy.createSkeleton();
        } else if (currentLevel == 2) {
            if (r < 0.5) return Enemy.createSkeleton();
            if (r < 0.8) return Enemy.createOrc();
            return Enemy.createGoblin();
        } else {
            if (r < 0.4) return Enemy.createOrc();
            if (r < 0.7) return Enemy.createDarkKnight();
            return Enemy.createGoblin();
        }
    }

    private Item generateTreasure() {
        int goldAmount = 20 + random.nextInt(60);
        // Per ora solo pozioni, poi si possono aggiungere armi/armature
        return new HealthPotion(1 + random.nextInt(3));
    }

    private void setupBossArea() {
        /// La stanza finale (basso a destra) è una PORTA
        int bossX = width - 1;  // se width=8, bossX=7
        int bossY = height - 1; // se height=8, bossY=7
        Room doorRoom = map[bossY][bossX];
        doorRoom.setName("🚪 PORTA DEL TESORO 🚪");
        doorRoom.setDescription("Una porta antica brilla di luce dorata.");
        doorRoom.setEnemy(null);
        doorRoom.setDoorRoom(true);  // IMPORTANTE!
        doorRoom.setExplored(true);   // Per vederla subito

        // Aggiungi i 3 draghi nelle caselle adiacenti alla porta

        // Drago a SINISTRA della porta (x-1, y)
        if (bossX - 1 >= 0) {
            Room leftRoom = map[bossY][bossX - 1];
            leftRoom.setName("🐉 Tana del Drago Orientale 🐉");
            leftRoom.setDescription("Un drago rosso fiammeggiante ti blocca la strada!");
            leftRoom.setEnemy(Enemy.createDragon());
            leftRoom.setHasDragon(true);
        }

        // Drago SOPRA la porta (x, y-1)
        if (bossY - 1 >= 0) {
            Room upRoom = map[bossY - 1][bossX];
            upRoom.setName("🐉 Tana del Drago Settentrionale 🐉");
            upRoom.setDescription("Un drago blu elettrico ti fissa con occhi di ghiaccio!");
            upRoom.setEnemy(Enemy.createDragon());
            upRoom.setHasDragon(true);
        }

        // Drago in DIAGONALE (x-1, y-1) - sopra a sinistra
        if (bossX - 1 >= 0 && bossY - 1 >= 0) {
            Room diagonalRoom = map[bossY - 1][bossX - 1];
            diagonalRoom.setName("🐉 Tana del Drago Occidentale 🐉");
            diagonalRoom.setDescription("Un drago nero emerge dalle ombre, pronto a combattere!");
            diagonalRoom.setEnemy(Enemy.createDragon());
            diagonalRoom.setHasDragon(true);
        }
    }

    // Controlla se tutti i draghi intorno alla porta sono stati sconfitti
    public boolean areAllDragonsDefeated() {
        int bossX = width - 1;
        int bossY = height - 1;

        // Controlla drago a sinistra
        if (bossX - 1 >= 0) {
            Room leftRoom = map[bossY][bossX - 1];
            if (leftRoom.hasDragon() && leftRoom.hasEnemy()) {
                return false;  // Drago ancora vivo
            }
        }

        // Controlla drago sopra
        if (bossY - 1 >= 0) {
            Room upRoom = map[bossY - 1][bossX];
            if (upRoom.hasDragon() && upRoom.hasEnemy()) {
                return false;
            }
        }

        // Controlla drago in diagonale
        if (bossX - 1 >= 0 && bossY - 1 >= 0) {
            Room diagRoom = map[bossY - 1][bossX - 1];
            if (diagRoom.hasDragon() && diagRoom.hasEnemy()) {
                return false;
            }
        }

        return true;  // Tutti i draghi sono stati sconfitti
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

    public void nextLevel() {
        currentLevel++;
        // Rigenera i nemici per il nuovo livello (opzionale)
    }


    public String getMapAsString(boolean showAll) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Room room = map[y][x];
                if (room == currentRoom) {
                    sb.append("[X]");
                } else if (showAll || room.isExplored()) {
                    if (room.hasEnemy()) sb.append("[E]");
                    else if (room.hasTreasures()) sb.append("[T]");
                    else sb.append("[ ]");
                } else {
                    sb.append("[?]");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}