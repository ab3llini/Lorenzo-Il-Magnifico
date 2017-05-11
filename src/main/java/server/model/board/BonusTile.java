package server.model.board;

import server.model.effect.EffectSurplus;

/**
 * Created by Federico on 11/05/2017.
 */
public class BonusTile {
    private EffectSurplus harvestSurplus;
    private EffectSurplus productionSurplus;


    public BonusTile(EffectSurplus harvestSurplus, EffectSurplus productionSurplus){
        this.harvestSurplus = harvestSurplus;
        this.productionSurplus = productionSurplus;
    }
}
