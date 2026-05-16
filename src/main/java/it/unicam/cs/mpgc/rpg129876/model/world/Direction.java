package it.unicam.cs.mpgc.rpg129876.model.world;

public enum Direction {
    NORTH("⬆ Nord", 0, -1),
    SOUTH("⬇ Sud", 0, 1),
    EAST("➡ Est", 1, 0),
    WEST("⬅ Ovest", -1, 0);

    private final String displayName;
    private final int dx;
    private final int dy;

    Direction(String displayName, int dx, int dy) {
        this.displayName = displayName;
        this.dx = dx;
        this.dy = dy;
    }

    public String getDisplayName() { return displayName; }
    public int getDx() { return dx; }
    public int getDy() { return dy; }

    public Direction getOpposite() {
        switch(this) {
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case EAST: return WEST;
            case WEST: return EAST;
            default: return this;
        }
    }

    public static Direction fromString(String str) {
        for (Direction d : values()) {
            if (d.name().equalsIgnoreCase(str)) return d;
        }
        return null;
    }
}