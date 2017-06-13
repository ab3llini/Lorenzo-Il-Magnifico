package server.model.effect;/*
 * Created by alberto on 09/05/17.
 */

import server.model.valuable.Multiplier;
import server.utility.UnicodeChars;

import java.io.Serializable;
import java.util.ArrayList;

public class PermanentEffect implements Serializable {
    private ActionType actionType;
    private Integer minForce;
    private Integer vPoints;
    private EffectSurplus surplus;
    private ArrayList<EffectConversion> conversion;
    private Multiplier multiplier;
    private EffectPermanentAction action;
    private boolean penality;


    public PermanentEffect (ActionType actionType, Integer minForce,Integer vPoints, EffectSurplus surplus, ArrayList<EffectConversion> conversion, Multiplier multiplier, EffectPermanentAction action, boolean penality){
        this.actionType = actionType;
        this.minForce = minForce;
        this.vPoints=vPoints;
        this.surplus = surplus;
        this.conversion = conversion;
        this.multiplier=multiplier;
        this.action = action;
        this.penality = penality;
    }

    public Integer getMinForce() {
        return minForce;
    }

    public Integer getvPoints() {
        return vPoints;
    }

    public EffectSurplus getSurplus() {
        return surplus;
    }

    public ArrayList<EffectConversion> getConversion() {
        return conversion;
    }

    public Multiplier getMultiplier() {
        return multiplier;
    }

    public EffectPermanentAction getAction() {
        return action;
    }

    public boolean isPenality() {
        return penality;
    }

    @Override
    public String toString() {

        String permanentEffect="";

        if (actionType != ActionType.unknown){

            if(actionType == ActionType.harvest)
                permanentEffect += " - Harvest "+UnicodeChars.Harvest+"\t";

            if(actionType == ActionType.production)
                permanentEffect += " - Production "+UnicodeChars.Production+"\t";

            permanentEffect += "Minforce : "+this.minForce+"\n";

            permanentEffect += " - Surplus : "+this.surplus.toString();

        }

        if(vPoints > 0)
            permanentEffect +=" - Victory Points "+ UnicodeChars.VictoryPoints+" : "+vPoints;

        return permanentEffect;
    }
}
