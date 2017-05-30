package server.model.effect;

import server.model.card.developement.DvptCardType;
import server.model.valuable.Valuable;

import java.io.Serializable;

/**
 * Created by Federico on 10/05/2017.
 */
public class EffectDiscount implements Serializable {
    private DvptCardType type;
    private Valuable what;


    public EffectDiscount(DvptCardType type, Valuable what) {
        this.type = type;
        this.what = what;
    }

    public DvptCardType getType() {
        return type;
    }

    public Valuable getWhat() {
        return what;
    }
}
