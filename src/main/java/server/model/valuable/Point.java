package server.model.valuable;

import java.io.Serializable;

/**
 * Created by Federico on 10/05/2017.
 */
public class Point extends Valuable implements Serializable {

    private PointType type;
    private Multiplier multiplier;

    public Point(PointType type,Integer amount, Multiplier multiplier) {
        super(amount);
        this.type = type;
        this.multiplier=multiplier;
    }


    public Multiplier getMultiplier() {
        return multiplier;
    }

    public PointType getType() {
        return type;
    }
}
