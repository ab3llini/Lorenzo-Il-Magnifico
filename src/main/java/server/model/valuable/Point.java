package server.model.valuable;

/**
 * Created by Federico on 10/05/2017.
 */
public class Point extends Valuable {

    private PointType type;
    private Multiplier multiplier;

    public Point(Integer amount, PointType type) {
        super(amount);
        this.type = type;
    }
}
