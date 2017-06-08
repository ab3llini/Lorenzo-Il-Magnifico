package server.model.effect;/*
 * Created by alberto on 09/05/17.
 */

import server.utility.UnicodeChars;

import java.io.Serializable;

public class ImmediateEffect implements Serializable {

    private EffectSurplus surplus;
    private EffectAction effectAction;


    public ImmediateEffect (EffectSurplus surplus, EffectAction effectAction){
        this.surplus = surplus;
        this.effectAction = effectAction;
    }

    public EffectAction getEffectAction() {
        return effectAction;
    }

    public EffectSurplus getSurplus() {
        return surplus;
    }

    @Override
    public String toString() {

        String immediateEffect = "";

        if(this.surplus.getResources().size() >0 || this.surplus.getPoints().size()>0 ||this.surplus.getCouncil()>0){

            immediateEffect += " - Surplus ->" + this.surplus.toString()+"\n";
        }

        if(this.getEffectAction().getTarget()!= ActionType.unknown){

            immediateEffect += " - Effect Action: "+this.getEffectAction().toString()+"\n";

        }

        return immediateEffect;
    }
}
