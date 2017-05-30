package server.model.effect;

import server.model.valuable.Point;
import server.model.valuable.Resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by LBARCELLA on 17/05/2017.
 */
public class OnceARoundEffect implements Serializable {

    private ArrayList<Resource> resources;
    private ArrayList<Point> points;
    private HashMap<ActionType,Integer> action;
    private Boolean sixEffect;

    public OnceARoundEffect(ArrayList<Resource> resources, ArrayList<Point> points, HashMap<ActionType,Integer> action, Boolean sixEffect){
        this.resources=resources;
        this.points=points;
        this.action=action;
        this.sixEffect=sixEffect;
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
}
