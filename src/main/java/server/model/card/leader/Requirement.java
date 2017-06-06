package server.model.card.leader;

import server.model.card.developement.DvptCard;
import server.model.card.developement.DvptCardType;
import server.model.valuable.Point;
import server.model.valuable.Resource;
import server.utility.UnicodeChars;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Federico on 10/05/2017.
 */
public class Requirement implements Serializable {
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

    @Override
    public String toString() {

        String requirement= "";

        for (Resource resource: this.resourceRequired) {

            requirement += resource.toString()+", ";
        }

        for (Point point: this.pointsRequired) {

            requirement += point.toString()+", ";
        }

        if(!cardsRequired.isEmpty()){
            Set types = cardsRequired.keySet();

            for (Object type: types) {

                requirement += type+" cards " + UnicodeChars.Card +" "+cardsRequired.get(type)+", ";
            }
        }

        if(sixIdentical)
            requirement += "sei carte dello stesso tipo!";

        return requirement;
    }
}
