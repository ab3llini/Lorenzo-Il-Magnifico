package server.model.effect;/*
 * Created by alberto on 09/05/17.
 */

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
}
