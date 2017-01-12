package fr.polar_dev.magicpersonnalcollection.database.sqlite.cards;

import java.util.List;

import fr.polar_dev.magicpersonnalcollection.models.Card;

/**
 * Created by Pascal on 12/10/2016.
 */

public interface CardDao {

    void insertCard(Card object);

    Card getCardById(int id);
    List<Card> getCardByDeck(int deck_id);
    List<Card> getAllCards();

    void insertCardsInDeck(List<Card> cards, String deckName);

    void updateCard(Card object);

    void deleteCard(Card object);
}
