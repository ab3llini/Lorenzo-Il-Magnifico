package server.model.effect;

import server.model.card.developement.DvptCardType;
import server.model.valuable.Valuable;

import java.util.ArrayList;

/**
 * Created by LBARCELLA on 12/05/2017.
 */
public class EffectAction {
    private ActionType target;
    private DvptCardType type;
    private Integer force;
    private ArrayList<Valuable> discount;

    public EffectAction(ActionType target, DvptCardType type, Integer force, ArrayList<Valuable> discount){
        this.target=target;
        this.type=type;
        this.force=force;
        this.discount=discount;
    }
}
