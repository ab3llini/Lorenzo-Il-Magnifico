package server.model.effect;/*
 * Created by alberto on 09/05/17.
 */

import server.model.valuable.Multiplier;

import java.util.ArrayList;

public class PermanentEffect {
    private Integer minForce;
    private Integer vPoints;
    private EffectSurplus surplus;
    private ArrayList<EffectConversion> conversion;
    private Multiplier multiplier;
    private EffectPermanentAction action;
    private boolean penality;


    public PermanentEffect (Integer minForce,Integer vPoints, EffectSurplus surplus, ArrayList<EffectConversion> conversion, Multiplier multiplier, EffectPermanentAction action, boolean penality){
        this.minForce = minForce;
        this.vPoints=vPoints;
        this.surplus = surplus;
        this.conversion = conversion;
        this.multiplier=multiplier;
        this.action = action;
        this.penality = penality;
    }

}
