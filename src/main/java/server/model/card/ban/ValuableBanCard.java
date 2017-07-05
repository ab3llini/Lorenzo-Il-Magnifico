package server.model.card.ban;

import server.model.valuable.Point;
import server.model.valuable.Resource;
import server.model.valuable.Valuable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by LBARCELLA on 20/05/2017.
 */
public class ValuableBanCard extends BanCard implements Serializable {

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

    @Override
    public String toString() {

        String banString = "";

        banString += super.toString();

        banString += "Valuable malus ...";

        if(resources.size()> 0)
            banString +=  " "+resources;

        if(points.size() > 0)
            banString += " "+points;

        return banString;

    }
}
