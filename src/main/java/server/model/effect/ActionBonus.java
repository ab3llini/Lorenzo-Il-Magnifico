package server.model.effect;

import server.model.card.developement.Discount;

import java.util.ArrayList;

/**
 * Created by LBARCELLA on 21/06/2017.
 */
public class ActionBonus {

    ArrayList<Discount> discounts;
    Integer forceBonus;

    public ActionBonus(){

        discounts = new ArrayList<>();
        forceBonus = 0;

    }

    public Integer getForceBonus() {
        return forceBonus;
    }

    public void setDiscounts(ArrayList<Discount> discounts) {
        this.discounts = discounts;
    }

    public ArrayList<Discount> getDiscounts() {
        return discounts;
    }

    public void setForceBonus(Integer forceBonus) {
        this.forceBonus = forceBonus;
    }

    public void increaseForceBonus(Integer increase) {
        this.forceBonus += increase;
    }
}
