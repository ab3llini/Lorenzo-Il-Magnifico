package server.model.effect;

import server.model.card.developement.Discount;
import server.model.card.developement.DvptCardType;
import server.model.valuable.Resource;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by LBARCELLA on 13/05/2017.
 */
public class EffectPermanentAction implements Serializable {

    private ActionType target;
    private DvptCardType type;
    private Integer forceBonus;
    private ArrayList<Discount> discounts;

    public EffectPermanentAction(ActionType target, DvptCardType type , Integer forceBonus , ArrayList<Discount> discounts){
        this.discounts=discounts;
        this.target=target;
        this.type=type;
        this.forceBonus=forceBonus;
    }

    public DvptCardType getType() {
        return type;
    }

    public ActionType getTarget() {
        return target;
    }

    public ArrayList<Discount> getDiscounts() {
        return discounts;
    }

    public Integer getForceBonus() {
        return forceBonus;
    }
}
