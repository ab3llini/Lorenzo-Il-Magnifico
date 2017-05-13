package server.model.effect;

import server.model.card.developement.DvptCardType;
import server.model.valuable.Resource;
import server.model.valuable.Valuable;

import java.util.ArrayList;

/**
 * Created by LBARCELLA on 12/05/2017.
 */
public class EffectAction {
    private ActionType target;
    private DvptCardType type;
    private Integer force;
    private ArrayList<Resource> discount;

    public EffectAction(ActionType target, DvptCardType type, Integer force, ArrayList<Resource> discount){
        this.target=target;
        this.type=type;
        this.force=force;
        this.discount=discount;
    }
}
