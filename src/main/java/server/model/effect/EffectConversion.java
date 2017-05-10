package server.model.effect;

import server.model.valuable.Valuable;

/**
 * Created by Federico on 10/05/2017.
 */
public class EffectConversion {
    private Valuable from;
    private Valuable to;


    private EffectConversion (Valuable from, Valuable to){
        this.from = from;
        this.to = to;
    }
}
