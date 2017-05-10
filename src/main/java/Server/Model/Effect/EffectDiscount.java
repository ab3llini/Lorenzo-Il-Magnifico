package Server.Model.Effect;

import Server.Model.Card.Developement.DvptCardType;
import Server.Model.valuable.Valuable;

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
