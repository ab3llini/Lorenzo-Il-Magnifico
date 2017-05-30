package server.model.card.ban;

import server.model.card.developement.DvptCardType;
import server.model.effect.ActionType;

import java.io.Serializable;

/**
 * Created by Federico on 10/05/2017.
 */
public class EffectDiceMalus implements Serializable {
    private ActionType target;
    private DvptCardType type;
    private Integer RoundDiceMalus;
    private Integer malus;

    public EffectDiceMalus(ActionType target, DvptCardType type, Integer malus, Integer RoundDiceMalus ){
        this.target = target;
        this.type = type;
        this.RoundDiceMalus = RoundDiceMalus;
        this.malus = malus;
    }

    public ActionType getTarget() {
        return target;
    }

    public DvptCardType getType() {
        return type;
    }

    public Integer getRoundDiceMalus() {
        return RoundDiceMalus;
    }

    public Integer getMalus() {
        return malus;
    }
    
}
