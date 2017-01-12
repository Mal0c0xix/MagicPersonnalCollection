package fr.polar_dev.magicpersonnalcollection.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pascal on 03/10/2016.
 */

public class Deck implements Serializable{

    private String deckName;
    private List<String> deckColors;
    private List<String> cardsList;

    public Deck(String deckName) {
        this.deckName = deckName;
        deckColors = new ArrayList<>();
        cardsList = new ArrayList<>();
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public List<String> getDeckColors() {
        return deckColors;
    }

    public void setDeckColors(List<String> deckColors) {
        this.deckColors = deckColors;
    }

    public List<String> getCardsList() {
        return cardsList;
    }

    public void setCardsList(List<String> cardsList) {
        this.cardsList = cardsList;
    }

}
