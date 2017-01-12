package fr.polar_dev.magicpersonnalcollection.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Pascal on 07/10/2016.
 */

public class ManaCost implements Serializable{
    
    private List<String> colors;
    private String cost;

    public ManaCost(List<String> colors, String cost) {
        this.colors = colors;
        this.cost = cost;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
