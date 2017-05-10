package Server.Model.Card.Ban;

import Server.Model.valuable.PointType;

/**
 * Created by Federico on 10/05/2017.
 */
public class PointMalus {
    private PointType what;
    private Integer malus;

    public PointMalus (PointType what, Integer malus){
        this.what = what;
        this.malus = malus;
    }
}
