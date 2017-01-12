package fr.polar_dev.magicpersonnalcollection.database.sqlite.cards;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.polar_dev.magicpersonnalcollection.database.DatabaseContract;
import fr.polar_dev.magicpersonnalcollection.database.sqlite.DatabaseHelper;
import fr.polar_dev.magicpersonnalcollection.models.Card;
import fr.polar_dev.magicpersonnalcollection.models.ManaCost;

/**
 * Created by Pascal on 12/10/2016.
 *
 */
public class CardDaoImpl implements CardDao {

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private static final String[] cardQueryColumns =
            {
                    DatabaseContract.CardEntry.COLUMN_NAME,
                    DatabaseContract.CardEntry.COLUMN_URL,
                    DatabaseContract.CardEntry.COLUMN_COLORS,
                    DatabaseContract.CardEntry.COLUMN_COST,
                    DatabaseContract.CardEntry.COLUMN_TYPE
            };

    public CardDaoImpl(Context ctx) {
        dbHelper = new DatabaseHelper(ctx);
    }

    @Override
    public void insertCard(Card object) {
        database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.CardEntry.COLUMN_NAME, object.getName());
        values.put(DatabaseContract.CardEntry.COLUMN_URL, object.getImage_url());
        values.put(DatabaseContract.CardEntry.COLUMN_COST, object.getManaCost().getCost());
        values.put(DatabaseContract.CardEntry.COLUMN_TYPE, object.getCardTypes().get(0).toString());

        String colorsValues = "";
        for (String color : object.getManaCost().getColors())
        {
            colorsValues += color + ",";
        }
        if(!colorsValues.isEmpty())
            colorsValues = colorsValues.substring(0, colorsValues.length() - 1);

        values.put(DatabaseContract.CardEntry.COLUMN_COLORS, colorsValues);

        database.beginTransaction();
        try
        {
            //database.insertOrThrow(DatabaseContract.CardEntry.TABLE_NAME, null, values);
            database.insertWithOnConflict(DatabaseContract.CardEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            database.setTransactionSuccessful();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally {
            database.endTransaction();
        }
    }

    @Override
    public Card getCardById(int id) {
        database = dbHelper.getReadableDatabase();
        Card result = null;

        String[] args = {String.valueOf(id)};

        Cursor cursor = database.query(DatabaseContract.CardEntry.TABLE_NAME,
                cardQueryColumns,
                DatabaseContract.CardEntry._ID + " = ? ", args,
                null, null, null);

        if (cursor.moveToNext())
        {
            List<String> tempscolors = new ArrayList<>();
            String colorsString = cursor.getString(2);

            if(!colorsString.isEmpty())
            {
                String[] colors = colorsString.split(",");
                tempscolors.addAll(Arrays.asList(colors));
            }

            ManaCost cost = new ManaCost(tempscolors, cursor.getString(3));

            result = new Card(cursor.getString(0), cost);
        }

        cursor.close();

        return result;
    }

    @Override
    public List<Card> getCardByDeck(int deck_id)
    {
        database = dbHelper.getReadableDatabase();
        List<Card> results = new ArrayList<>();

        String [] args = {String.valueOf(deck_id)};

        String request = "SELECT * FROM " + DatabaseContract.CardEntry.TABLE_NAME
                + " WHERE "+ DatabaseContract.CardEntry._ID +" IN "
                + "(SELECT " + DatabaseContract.DeckCardEntry.COLUMN_CARD_NAME
                + " FROM " + DatabaseContract.DeckCardEntry.TABLE_NAME
                + " WHERE "+ DatabaseContract.DeckCardEntry.COLUMN_DECK_NAME
                +"= ?)";

        Cursor cursor = database.rawQuery(request, args);

        cursor.moveToFirst();
        while(cursor.moveToNext())
        {
            List<String> tempscolors = new ArrayList<>();
            String colorsString = cursor.getString(2);

            if(!colorsString.isEmpty())
            {
                String[] colors = colorsString.split(",");
                tempscolors.addAll(Arrays.asList(colors));
            }

            ManaCost cost = new ManaCost(tempscolors, cursor.getString(3));

            results.add(new Card(cursor.getString(0), cost));
        }

        cursor.close();

        return results;
    }

    @Override
    public List<Card> getAllCards() {
        database = dbHelper.getReadableDatabase();
        List<Card> results = new ArrayList<>();

        Cursor cursor = database.query(DatabaseContract.CardEntry.TABLE_NAME,
                null,
                null, null,
                null, null, null);

        cursor.moveToFirst();
        while(cursor.moveToNext())
        {
            List<String> tempscolors = new ArrayList<>();
            String colorsString = cursor.getString(2);

            if(!colorsString.isEmpty())
            {
                String[] colors = colorsString.split(",");
                tempscolors.addAll(Arrays.asList(colors));
            }

            ManaCost cost = new ManaCost(tempscolors, cursor.getString(3));

            results.add(new Card(cursor.getString(0), cost));
        }

        cursor.close();

        return results;
    }

    @Override
    public void insertCardsInDeck(List<Card> cards, String deckName) {

        database = dbHelper.getWritableDatabase();

        String request = "INSERT INTO " + DatabaseContract.DeckCardEntry.TABLE_NAME
                + " (" + DatabaseContract.DeckCardEntry.COLUMN_DECK_NAME
                + ", " + DatabaseContract.DeckCardEntry.COLUMN_CARD_NAME
                + ", " + DatabaseContract.DeckCardEntry.COLUMN_QUANTITY
                + ") VALUES (?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(request);

        try
        {
            database.beginTransaction();
            for (Card c : cards)
            {
                insertCard(c);
                statement.clearBindings();
                statement.bindString(1, deckName);
                statement.bindString(2, c.getName());
                statement.bindLong(3, c.getQuantity());
                statement.executeInsert();
            }
            database.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.w("Exception:", e);
        } finally {
            database.endTransaction();
        }

    }

    @Override
    public void updateCard(Card object) {

        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.CardEntry.COLUMN_NAME, object.getName());
        values.put(DatabaseContract.CardEntry.COLUMN_URL, object.getImage_url());
        values.put(DatabaseContract.CardEntry.COLUMN_COST, object.getManaCost().getCost());

        String colorsValues = "";
        for (String color : object.getManaCost().getColors())
        {
            colorsValues += color + ",";
        }
        if(!colorsValues.isEmpty())
            colorsValues = colorsValues.substring(0, colorsValues.length() - 1);

        values.put(DatabaseContract.CardEntry.COLUMN_COLORS, colorsValues);

        String[] args = {object.getName()};

        database.update(DatabaseContract.CardEntry.TABLE_NAME,
                values, DatabaseContract.CardEntry.COLUMN_NAME + " = ?",
                args);
    }

    @Override
    public void deleteCard(Card object) {

        database = dbHelper.getWritableDatabase();

        String[] args = {object.getName()};

        database.delete(DatabaseContract.CardEntry.TABLE_NAME,
                DatabaseContract.CardEntry.COLUMN_NAME + " = ?",
                args);
    }
}
