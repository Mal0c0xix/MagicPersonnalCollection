package fr.polar_dev.magicpersonnalcollection.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.polar_dev.magicpersonnalcollection.models.Card;
import fr.polar_dev.magicpersonnalcollection.models.ManaCost;
import fr.polar_dev.magicpersonnalcollection.models.cards_type.Artifact;
import fr.polar_dev.magicpersonnalcollection.models.cards_type.CardType;
import fr.polar_dev.magicpersonnalcollection.models.cards_type.Conspiracy;
import fr.polar_dev.magicpersonnalcollection.models.cards_type.Creature;
import fr.polar_dev.magicpersonnalcollection.models.cards_type.Enchantment;
import fr.polar_dev.magicpersonnalcollection.models.cards_type.Instant;
import fr.polar_dev.magicpersonnalcollection.models.cards_type.Land;
import fr.polar_dev.magicpersonnalcollection.models.cards_type.Phenomenon;
import fr.polar_dev.magicpersonnalcollection.models.cards_type.Plane;
import fr.polar_dev.magicpersonnalcollection.models.cards_type.Planeswalker;
import fr.polar_dev.magicpersonnalcollection.models.cards_type.Scheme;
import fr.polar_dev.magicpersonnalcollection.models.cards_type.Sorcery;
import fr.polar_dev.magicpersonnalcollection.models.cards_type.Tribal;
import fr.polar_dev.magicpersonnalcollection.models.cards_type.Vanguard;

/**
 * Created by Pascal on 16/11/2016.
 */

public class ParseJSON {


    private static Card jsonToCard(String json)
    {
        Card result = null;

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray extensionArray = jsonObject.getJSONArray("editions");

            String set = extensionArray.getJSONObject(0).getString("set_id");
            List<String> colorslist = new ArrayList<>();

            if(jsonObject.has("colors"))
            {
                JSONArray colorsArray = jsonObject.getJSONArray("colors");
                for(int i=0; i< colorsArray.length(); i++)
                    colorslist.add(colorsArray.getString(i));
            }

            ManaCost cost = new ManaCost(colorslist, jsonObject.getString("cost"));
            JSONArray cardtypes = jsonObject.getJSONArray("types");

            List<CardType> types = new ArrayList<>();
            for (int i=0; i<cardtypes.length(); i++)
            {
                String row = cardtypes.getString(i);

                CardType temp = parseCardType(row, jsonObject);

                types.add(temp);
            }

            result = new Card(jsonObject.getString("name"), cost);
            result.setId(jsonObject.getString("id"));
            result.setImage_url(extensionArray.getJSONObject(0).getString("image_url"));
            result.setCardTypes(types);
            result.setCardSet(set);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static CardType parseCardType(String row, JSONObject jsonObject)
    {
        CardType temp = null;

        switch (row)
        {
            case "instant":
                temp = new Instant();
                break;
            case "enchantment":
                temp = new Enchantment();
                break;
            case "artifact":
                temp = new Artifact();
                break;
            case "creature":
                JSONArray subtypes;
                try {
                    subtypes = jsonObject.getJSONArray("subtypes");
                    String power = jsonObject.getString("power");
                    String toughness = jsonObject.getString("toughness");
                    temp = new Creature(subtypes.getString(0), power, toughness);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "sorcery":
                temp = new Sorcery();
                break;
            case "conspiracy":
                temp = new Conspiracy();
                break;
            case "land":
                temp = new Land();
                break;
            case "phenomenon":
                temp = new Phenomenon();
                break;
            case "plane":
                temp = new Plane();
                break;
            case "planeswalker":
                temp = new Planeswalker();
                break;
            case "scheme":
                temp = new Scheme();
                break;
            case "tribal":
                temp = new Tribal();
                break;
            case "vanguard":
                temp = new Vanguard();
                break;

        }

        return temp;
    }

    public static List<Card> jsonToCardList(String json)
    {
        List<Card> results = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(json);
            for(int i = 0; i< array.length(); i++)
            {
                String row = array.getString(i);
                results.add(jsonToCard(row));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



        return results;
    }

}
