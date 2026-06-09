package it.unicam.cs.mpgc.rpg129876.model.world;

/**
 * Enumerazione che rappresenta le quattro direzioni cardinali di movimento nel dungeon.
 *
 * Ogni direzione è associata a:
 * - Un nome visuale per l'interfaccia utente
 * - Uno spostamento orizzontale (dx) e verticale (dy) sulla griglia della mappa
 *
 * La mappa è organizzata con coordinate (x, y) dove:
 * - x aumenta verso EST
 * - y aumenta verso SUD
 *
 * @author Filippo Mangialardo
 * @version 1.0
 */
public enum Direction {
    NORTH("⬆ Nord", 0, -1),
    SOUTH("⬇ Sud", 0, 1),
    EAST("➡ Est", 1, 0),
    WEST("⬅ Ovest", -1, 0);

    private final String displayName;

    /**
     * Costruttore privato dell'enum.
     *
     * @param displayName nome visuale della direzione
     * @param dx variazione orizzontale sulla griglia
     * @param dy variazione verticale sulla griglia
     */
    Direction(String displayName, int dx, int dy) {
        this.displayName = displayName;
    }

    /**
     * Restituisce il nome visuale della direzione.
     *
     * @return nome formattato per l'interfaccia utente (es. "⬆ Nord")
     */
    public String getDisplayName() { return displayName; }

    /**
     * Restituisce la direzione opposta a quella corrente.
     *
     * Utile per collegare le stanze in modo bidirezionale:
     * se da stanza A si va a NORD verso stanza B,
     * allora da stanza B si va a SUD verso stanza A.
     *
     * @return la direzione opposta
     */
    public Direction getOpposite() {
        switch(this) {
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case EAST: return WEST;
            case WEST: return EAST;
            default: return this;
        }
    }
}