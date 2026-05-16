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

        // Stanza iniziale (centro o angolo in alto a sinistra)
        int startX = width / 2;
        int startY = height / 2;
        currentRoom = map[startY][startX];
        currentRoom.enter();
        currentRoom.setName("🏛 Punto di partenza");
        currentRoom.setDescription("Qui inizia la tua avventura...");
        currentRoom.setEnemy(null);  // Nessun nemico all'inizio

        // Stanza del boss nell'angolo opposto
        int bossX = width - 1;
        int bossY = height - 1;
        Room bossRoom = map[bossY][bossX];
        bossRoom.setName("👑 Trono del Drago");
        bossRoom.setDescription("Un'enorme creatura ti fissa con occhi di fuoco!");
        bossRoom.setEnemy(Enemy.createDragon());
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

    public boolean isBossRoom() {
        return currentRoom == map[height-1][width-1];
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