package server.model.board;

import server.model.effect.EffectSurplus;
import server.model.effect.ImmediateEffect;

/**
 * Created by LBARCELLA on 18/05/2017.
 */
public class ActionPlace {
    protected EffectSurplus effectSurplus;
    protected Integer entryForce;
    protected Integer minPlayers;


    public ActionPlace(EffectSurplus effectSurplus, Integer entryForce, Integer minPlayers){
        this.effectSurplus = effectSurplus;
        this.entryForce = entryForce;
        this.minPlayers = minPlayers;
    }

    public EffectSurplus getEffectSurplus() {
        return effectSurplus;
    }

    public Integer getEntryForce() {
        return entryForce;
    }

    public Integer getMinPlayers() {
        return minPlayers;
    }
}
