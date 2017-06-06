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

            if(resourceRequired.indexOf(resource) == resourceRequired.size()-1 && pointsRequired.size()==0) {

                requirement += resource.toString();
            }

            else
            {
                requirement += resource.toString()+", ";
            }
        }

        for (Point point: this.pointsRequired) {

            if(pointsRequired.indexOf(point) == pointsRequired.size()-1) {

                requirement += point.toString();
            }
            else {
                requirement += point.toString()+", ";
            }
        }

        if(!cardsRequired.isEmpty()){

            Set types = cardsRequired.keySet();
            int size = types.size();
            int i=0;

            for (Object type: types) {

                if(size>i+1)
                    requirement += type+" cards " + UnicodeChars.Card +" "+cardsRequired.get(type)+", ";
                else
                    requirement += type+" cards " + UnicodeChars.Card +" "+cardsRequired.get(type);
                i++;
            }
        }

        if(sixIdentical)
            requirement += "sei carte dello stesso tipo!";

        return requirement;
    }
}
