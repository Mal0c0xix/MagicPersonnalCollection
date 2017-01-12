package fr.polar_dev.magicpersonnalcollection.database.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import fr.polar_dev.magicpersonnalcollection.database.DatabaseContract;

/**
 * Created by Pascal on 07/10/2016.
 *
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "magicpc.db";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_DECK_TABLE = "CREATE TABLE "
                + DatabaseContract.DeckEntry.TABLE_NAME + " ("
                + DatabaseContract.DeckEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseContract.DeckEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + DatabaseContract.DeckEntry.COLUMN_COLORS + " TEXT, "
                + "UNIQUE (" + DatabaseContract.DeckEntry.COLUMN_NAME
                + ") ON CONFLICT IGNORE);";

        final String CREATE_CARD_TABLE = "CREATE TABLE "
                + DatabaseContract.CardEntry.TABLE_NAME + " ("
                + DatabaseContract.CardEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseContract.CardEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + DatabaseContract.CardEntry.COLUMN_URL + " TEXT, "
                + DatabaseContract.CardEntry.COLUMN_COST + " TEXT, "
                + DatabaseContract.CardEntry.COLUMN_COLORS + " TEXT, "
                + DatabaseContract.CardEntry.COLUMN_TYPE + " TEXT, "
                + "UNIQUE (" + DatabaseContract.CardEntry.COLUMN_NAME + ") ON CONFLICT IGNORE);";

        final String CREATE_DECK_CARD_TABLE = "CREATE TABLE "
                + DatabaseContract.DeckCardEntry.TABLE_NAME + " ("
                + DatabaseContract.DeckCardEntry.COLUMN_DECK_NAME + " INTEGER NOT NULL, "
                + DatabaseContract.DeckCardEntry.COLUMN_CARD_NAME + " INTEGER NOT NULL, "
                + DatabaseContract.DeckCardEntry.COLUMN_QUANTITY+ " INTEGER NOT NULL, "
                + "FOREIGN KEY (" + DatabaseContract.DeckCardEntry.COLUMN_DECK_NAME
                + ") REFERENCES " + DatabaseContract.DeckEntry.TABLE_NAME + " ("
                + DatabaseContract.DeckEntry._ID + "), "
                + "FOREIGN KEY (" + DatabaseContract.DeckCardEntry.COLUMN_CARD_NAME
                + ") REFERENCES " + DatabaseContract.CardEntry.TABLE_NAME + " ("
                + DatabaseContract.CardEntry._ID + "), "
                + "PRIMARY KEY (" + DatabaseContract.DeckCardEntry.COLUMN_DECK_NAME
                + ", " + DatabaseContract.DeckCardEntry.COLUMN_CARD_NAME
                + "));";


        db.execSQL(CREATE_DECK_TABLE);
        db.execSQL(CREATE_CARD_TABLE);
        db.execSQL(CREATE_DECK_CARD_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.DeckCardEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.CardEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.DeckEntry.TABLE_NAME);
        onCreate(db);
    }
}
