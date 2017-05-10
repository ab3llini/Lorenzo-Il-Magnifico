package server.model.effect;/*
 * Created by alberto on 09/05/17.
 */

import javafx.scene.effect.Effect;

public class ImmediateEffect {
    private EffectSurplus surplus;
    private EffectAction effectAction;


    public ImmediateEffect (EffectSurplus surplus, EffectAction effectAction){
        this.surplus = surplus;
        this.effectAction = effectAction;
    }
}
