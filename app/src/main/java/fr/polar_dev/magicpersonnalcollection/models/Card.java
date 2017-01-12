package fr.polar_dev.magicpersonnalcollection.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.polar_dev.magicpersonnalcollection.models.cards_type.CardType;
import fr.polar_dev.magicpersonnalcollection.models.cards_type.Creature;

/**
 * Created by Pascal on 07/10/2016.
 */

public class Card implements Serializable{

    private String name;
    private ManaCost manaCost;
    private String image_url;
    private String cardSet;
    private List<CardType> cardTypes;
    private String id;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    private int quantity;


    public Card(String name, ManaCost manaCost) {
        this.name = name;
        this.manaCost = manaCost;
        image_url = "";
        cardTypes = new ArrayList<>();
        cardTypes.add(new Creature("beast", "1" , "1"));
        quantity = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ManaCost getManaCost() {
        return manaCost;
    }

    public void setManaCost(ManaCost manaCost) {
        this.manaCost = manaCost;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public List<CardType> getCardTypes() {
        return cardTypes;
    }

    public String showCardTypes()
    {
        String result = "";

        for(CardType c : cardTypes)
        {
            result += c.toString() + " ";
        }

        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCardTypes(List<CardType> types)
    {
        this.cardTypes = types;
    }

    public String getCardSet() {
        return cardSet;
    }

    public void setCardSet(String cardSet) {
        this.cardSet = cardSet;
    }
}
