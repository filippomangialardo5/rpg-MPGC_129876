package it.unicam.cs.mpgc.rpg129876.model.items;

import it.unicam.cs.mpgc.rpg129876.model.characters.Player;

/**
 * Interfaccia che rappresenta un oggetto utilizzabile all'interno del gioco.
 *
 * Tutti gli oggetti che il giocatore può raccogliere e utilizzare (pozioni, equipaggiamento,
 * oggetti speciali) devono implementare questa interfaccia.
 *
 * L'interfaccia fornisce i metodi essenziali per:
 * - Identificare l'oggetto (nome, descrizione, icona)
 * - Utilizzare l'oggetto sul giocatore
 *
 * @author Filippo Mangialardo
 * @version 1.0
 */
public interface Item {

    String getName();

    String getDescription();

    void use(Player player);

    default String getIcon() {
        return "📦";
    }
}