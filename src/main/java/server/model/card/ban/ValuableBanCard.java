package server.model.card.ban;

import server.model.valuable.Point;
import server.model.valuable.Resource;
import server.model.valuable.Valuable;

import java.util.ArrayList;

/**
 * Created by LBARCELLA on 20/05/2017.
 */
public class ValuableBanCard extends BanCard {

    ArrayList<Resource> resources;
    ArrayList<Point> points;

    public ValuableBanCard(Integer id, Integer period, ArrayList<Resource> resources, ArrayList<Point> points) {
        super(id, period, BanType.valuableMalus);
        this.resources = resources;
        this.points = points;
    }

    public ArrayList<Resource> getResources() {
        return resources;
    }

    public ArrayList<Point> getPoints() { return points; }
}
