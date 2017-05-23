package server.model.card.leader;

import server.model.card.developement.DvptCardType;
import server.model.valuable.Point;
import server.model.valuable.Resource;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Federico on 10/05/2017.
 */
public class Requirement {
    private ArrayList<Resource> resourceRequired;
    private ArrayList<Point> pointsRequired;
    private HashMap<DvptCardType,Integer> cardsRequired;
    private Boolean sixIdentical;

    public Requirement(ArrayList<Resource> resourceRequired, ArrayList<Point> pointsRequired, HashMap<DvptCardType,Integer> cardsRequired,Boolean sixIdentical){
        this.cardsRequired=cardsRequired;
        this.pointsRequired=pointsRequired;
        this.resourceRequired=resourceRequired;
        this.sixIdentical=sixIdentical;
    }

    public ArrayList<Resource> getResourceRequired() {
        return resourceRequired;
    }

    public HashMap<DvptCardType, Integer> getCardsRequired() {
        return cardsRequired;
    }

    public ArrayList<Point> getPointsRequired() {
        return pointsRequired;
    }

    public Boolean getSixIdentical() {
        return sixIdentical;
    }
}
