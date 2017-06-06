package server.model.effect;

import server.model.valuable.Point;
import server.model.valuable.Resource;
import server.utility.UnicodeChars;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by LBARCELLA on 17/05/2017.
 */
public class OnceARoundEffect implements Serializable {

    private ArrayList<Resource> resources;
    private ArrayList<Point> points;
    private Integer council;
    private HashMap<ActionType,Integer> action;
    private Boolean sixEffect;

    public OnceARoundEffect(ArrayList<Resource> resources, ArrayList<Point> points,Integer council, HashMap<ActionType,Integer> action, Boolean sixEffect){
        this.resources = resources;
        this.points = points;
        this.action = action;
        this.council = council;
        this.sixEffect = sixEffect;
    }

    public ArrayList<Resource> getResources() {
        return resources;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public Boolean getSixEffect() {
        return sixEffect;
    }

    public HashMap<ActionType, Integer> getAction() {
        return action;
    }

    @Override
    public String toString() {

        String onceARound = "Once a Round, ";

        for (Resource resource: this.resources) {

            onceARound += resource.toString()+", ";
        }

        for (Point point: this.points) {

            onceARound += point.toString()+", ";
        }

        if(council >=1)
            onceARound += "Council "+UnicodeChars.Council+" "+this.council;

        if(!action.isEmpty()){

            Set types = action.keySet();

            for ( Object type: types) {

                if(type == ActionType.card)
                    onceARound += type+" " + UnicodeChars.Card +" "+action.get(type);

                if(type == ActionType.production)
                    onceARound += type+" " + UnicodeChars.Production +" "+action.get(type);

                if(type == ActionType.harvest)
                    onceARound += type+" " + UnicodeChars.Harvest +" "+action.get(type);

            }
        }

        if(sixEffect)
            onceARound += "One of your colored Family Members has a value of 6 , regardless of its related die";

        return onceARound;
    }
}
