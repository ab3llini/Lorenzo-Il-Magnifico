package server.model.card.ban;

import server.model.valuable.PointType;

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

    public Integer getMalus() {
        return malus;
    }

    public PointType getWhat() {
        return what;
    }
}
