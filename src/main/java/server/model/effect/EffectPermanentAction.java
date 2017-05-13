package server.model.effect;

import server.model.card.developement.DvptCardType;
import server.model.valuable.Resource;

import java.util.ArrayList;

/**
 * Created by LBARCELLA on 13/05/2017.
 */
public class EffectPermanentAction {

    private ActionType target;
    private DvptCardType type;
    private Integer forceBonus;
    private ArrayList<Resource> discount;

    public EffectPermanentAction(ActionType target, DvptCardType type , Integer forceBonus , ArrayList<Resource> discount){
        this.discount=discount;
        this.target=target;
        this.type=type;
        this.forceBonus=forceBonus;
    }

}
