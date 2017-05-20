package server.model.card.ban;

import server.model.card.developement.DvptCardType;
import server.model.effect.ActionType;

/**
 * Created by Federico on 10/05/2017.
 */
public class EffectDiceMalus {
    private ActionType target;
    private DvptCardType type;
    private boolean RoundDiceMalus;
    private Integer malus;

    public EffectDiceMalus(ActionType target, DvptCardType type, boolean RoundDiceMalus, Integer malus ){
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

    public boolean isRoundDiceMalus() {
        return RoundDiceMalus;
    }

    public Integer getMalus() {
        return malus;
    }
    
}
