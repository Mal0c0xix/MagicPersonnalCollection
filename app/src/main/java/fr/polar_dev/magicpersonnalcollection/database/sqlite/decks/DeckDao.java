package fr.polar_dev.magicpersonnalcollection.database.sqlite.decks;

import java.util.List;

import fr.polar_dev.magicpersonnalcollection.models.Deck;

/**
 * Created by Pascal on 07/10/2016.
 */

public interface DeckDao {

    void insertDeck(Deck object);

    Deck getDeckById(int id);
    List<Deck> getAllDecks();

    int getDeckIdFromName(String name);
    Deck getDeckFromName(String name);

    void updateDeck(Deck object);

    void deleteDeck(Deck object);
}
