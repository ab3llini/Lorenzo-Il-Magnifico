package server.model.effect;

import java.io.Serializable;

/**
 * Created by LBARCELLA on 13/05/2017.
 */
public class EffectConversion implements Serializable {

    private EffectSurplus from;
    private EffectSurplus to;

    public EffectConversion(EffectSurplus from, EffectSurplus to){
        this.from=from;
        this.to=to;
    }

    public EffectSurplus getFrom() {
        return from;
    }

    public EffectSurplus getTo() {
        return to;
    }
}
