package fr.polar_dev.magicpersonnalcollection.database.sqlite.decks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.polar_dev.magicpersonnalcollection.database.DatabaseContract;
import fr.polar_dev.magicpersonnalcollection.database.sqlite.DatabaseHelper;
import fr.polar_dev.magicpersonnalcollection.models.Deck;

/**
 * Created by Pascal on 07/10/2016.
 */

public class DeckDaoImpl implements DeckDao {

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private static final String[] deckQueryColumns =
            { DatabaseContract.DeckEntry.COLUMN_NAME,
                    DatabaseContract.DeckEntry.COLUMN_COLORS};

    public DeckDaoImpl(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public void insertDeck(Deck object) {
        database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", object.getDeckName());

        String colors = "";
        if(!object.getDeckColors().isEmpty()) {
            for (String c : object.getDeckColors()) {
                colors += c + ",";
            }

            colors = colors.substring(0, colors.length() - 1);

            values.put("colors", colors);
        }

        database.beginTransaction();
        try
        {
            //database.insertOrThrow(DatabaseContract.DeckEntry.TABLE_NAME, null, values);
            database.insertWithOnConflict(DatabaseContract.DeckEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            database.setTransactionSuccessful();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally {
            database.endTransaction();
            database.close();
        }
    }

    @Override
    public Deck getDeckById(int id) {
        database = dbHelper.getReadableDatabase();
        Deck result = null;

        String[] args = {String.valueOf(id)};

        Cursor cursor = database.query(DatabaseContract.DeckEntry.TABLE_NAME,
                deckQueryColumns,
                DatabaseContract.DeckEntry._ID + " = ? ", args,
                null, null, null);

        if (cursor.moveToFirst())
        {

            result = new Deck(cursor.getString(0));
        }

        cursor.close();

        return result;
    }

    public int getDeckIdFromName(String name)
    {
        database = dbHelper.getReadableDatabase();
        int result = 0;

        String request = "SELECT " + DatabaseContract.DeckEntry._ID + " FROM " + DatabaseContract.DeckEntry.TABLE_NAME
                + " WHERE " + DatabaseContract.DeckEntry.COLUMN_NAME
                + " = ?";

        String [] args = {name};

        Cursor cursor = database.rawQuery(request, args);

        if (cursor.moveToFirst())
        {

            result = cursor.getInt(0);
        }

        cursor.close();

        return result;
    }

    public Deck getDeckFromName(String name)
    {
        database = dbHelper.getReadableDatabase();
        Deck result = null;

        String[] args = {name};

        Cursor cursor = database.query(DatabaseContract.DeckEntry.TABLE_NAME,
                deckQueryColumns,
                DatabaseContract.DeckEntry.COLUMN_NAME + " = ? ", args,
                null, null, null);

        if (cursor.moveToFirst())
        {

            result = new Deck(cursor.getString(0));
        }

        cursor.close();

        return result;
    }

    @Override
    public List<Deck> getAllDecks() {
        database = dbHelper.getReadableDatabase();
        List<Deck> results = new ArrayList<>();

        Cursor cursor = database.query(DatabaseContract.DeckEntry.TABLE_NAME,
                null,
                null, null,
                null, null, null);

        cursor.moveToFirst();
        while(cursor.moveToNext())
        {
            Deck currentDeck = new Deck(cursor.getString(1));
            currentDeck.setDeckColors(getDeckColors(cursor));

            results.add(currentDeck);
        }

        cursor.close();
        database.close();

        return results;
    }

    private List<String> getDeckColors(Cursor cursor)
    {
        List<String> deckColors = new ArrayList<>();
        String deckString = cursor.getString(2);
        if(deckString != null)
        {
            String[] colors = deckString.split(",");
            deckColors.addAll(Arrays.asList(colors));
        }

        return deckColors;
    }

    @Override
    public void updateDeck(Deck object) {
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        String[] args = {object.getDeckName()};

        database.update(DatabaseContract.DeckEntry.TABLE_NAME,
                values, DatabaseContract.DeckEntry.COLUMN_NAME + " = ?",
                args);

        database.close();
    }

    @Override
    public void deleteDeck(Deck object) {
        database = dbHelper.getWritableDatabase();

        String[] args = {object.getDeckName()};

        database.delete(DatabaseContract.DeckEntry.TABLE_NAME,
                DatabaseContract.DeckEntry.COLUMN_NAME + " = ?",
                args);
        database.close();
    }
}
