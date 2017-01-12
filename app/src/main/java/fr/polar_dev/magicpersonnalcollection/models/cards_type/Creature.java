package fr.polar_dev.magicpersonnalcollection.models.cards_type;

/**
 * Created by Pascal on 21/11/2016.
 */

public class Creature implements CardType {

    private String subtype;
    private String power;
    private String toughness;

    public Creature(String subtype, String power, String toughness) {
        this.subtype = subtype;
        this.power = power;
        this.toughness = toughness;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getToughness() {
        return toughness;
    }

    public void setToughness(String toughness) {
        this.toughness = toughness;
    }

    @Override
    public String toString() {
        return subtype + " (" + power + "/" + toughness + ")";
    }
}
