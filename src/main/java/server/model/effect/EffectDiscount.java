package server.model.effect;

import server.model.card.developement.DvptCardType;
import server.model.valuable.Valuable;

/**
 * Created by Federico on 10/05/2017.
 */
public class EffectDiscount {
    private DvptCardType type;
    private Valuable what;


    public EffectDiscount(DvptCardType type, Valuable what) {
        this.type = type;
        this.what = what;
    }
}
