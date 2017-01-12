package fr.polar_dev.magicpersonnalcollection.database;

import android.content.Context;

import fr.polar_dev.magicpersonnalcollection.database.sqlite.cards.CardDao;
import fr.polar_dev.magicpersonnalcollection.database.sqlite.cards.CardDaoImpl;
import fr.polar_dev.magicpersonnalcollection.database.sqlite.decks.DeckDao;
import fr.polar_dev.magicpersonnalcollection.database.sqlite.decks.DeckDaoImpl;

/**
 * Created by Pascal on 10/10/2016.
 */

public class DaoFactory {

    public static final int SQLite = 1;

    public static DeckDao getDeckDAO(Context ctx, int activeDB) {
        DeckDao deckDao;
        switch(activeDB) {
            case SQLite:
                deckDao = new DeckDaoImpl(ctx);
                return deckDao;
            default:
                return null;
        }

    }

    public static CardDao getCardDAO(Context ctx, int activeDB) {
        CardDao cardDao;
        switch(activeDB) {
            case SQLite:
                cardDao = new CardDaoImpl(ctx);
                return cardDao;
            default:
                return null;
        }

    }

}
