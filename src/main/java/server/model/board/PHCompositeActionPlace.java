package server.model.board;

import server.model.effect.EffectSurplus;
import server.model.effect.ImmediateEffect;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Federico on 11/05/2017.
 * Method implemented by LBARCELLA on 18/05/2017.
 */
public class PHCompositeActionPlace extends CompositeActionPlace implements Serializable {
    private Integer forceMalus;


    public PHCompositeActionPlace(EffectSurplus effectSurplus, Integer entryForce, Integer forceMalus, Integer minPlayers) {
        super(effectSurplus, entryForce, minPlayers);
        this.forceMalus = forceMalus;
    }


    public Integer getForceMalus() {
        return forceMalus;
    }


    public void setForceMalus(Integer forceMalus) {
        this.forceMalus = forceMalus;
    }
}
