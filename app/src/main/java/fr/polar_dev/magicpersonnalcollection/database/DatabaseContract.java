package fr.polar_dev.magicpersonnalcollection.database;

import android.provider.BaseColumns;

/**
 * Created by Pascal on 07/10/2016.
 */

public class DatabaseContract {

    public static final class DeckEntry implements BaseColumns {

        public static final String TABLE_NAME = "deck";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_COLORS = "colors";

    }

    public static final class CardEntry implements BaseColumns {

        public static final String TABLE_NAME = "card";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_URL = "url";

        public static final String COLUMN_COST = "cost";

        public static final String COLUMN_COLORS = "colors";

        public static final String COLUMN_TYPE = "type";

    }

    public static final class DeckCardEntry implements BaseColumns {

        public static final String TABLE_NAME = "deckcard";

        public static final String COLUMN_DECK_NAME = "deck_name";

        public static final String COLUMN_CARD_NAME = "card_name";

        public static final String COLUMN_QUANTITY = "quantity";
    }
}
