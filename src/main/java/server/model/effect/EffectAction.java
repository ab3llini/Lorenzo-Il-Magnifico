package server.model.effect;

import server.model.card.developement.Discount;
import server.model.card.developement.DvptCardType;
import server.model.valuable.Resource;
import server.model.valuable.Valuable;
import server.utility.UnicodeChars;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by LBARCELLA on 12/05/2017.
 */
public class EffectAction implements Serializable {
    private ActionType target;
    private DvptCardType type;
    private Integer force;
    private ArrayList<Discount> discounts;

    public EffectAction(ActionType target, DvptCardType type, Integer force, ArrayList<Discount> discounts){
        this.target=target;
        this.type=type;
        this.force=force;
        this.discounts=discounts;
    }

    public DvptCardType getType() {
        return type;
    }

    public ActionType getTarget() {
        return target;
    }

    public Integer getForce() {
        return force;
    }

    public ArrayList<Discount> getDiscounts() {
        return discounts;
    }

    @Override
    public String toString() {

        String actionString="";

        if(this.getTarget() == ActionType.harvest)
            actionString += "harvest "+ UnicodeChars.Harvest+" --> "+"Force "+UnicodeChars.Force+"\n";

        if(this.getTarget() == ActionType.production)
            actionString += "production "+ UnicodeChars.Production+" --> "+"Force "+UnicodeChars.Force+" "+this.force+"\n";

        return actionString;
    }
}
