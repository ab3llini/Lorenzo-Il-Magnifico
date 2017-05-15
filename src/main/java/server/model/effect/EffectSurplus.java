package server.model.effect;
import java.util.ArrayList;
import server.model.valuable.*;

/**
 * Created by Federico on 10/05/2017.
 */
public class EffectSurplus {
    private ArrayList<Resource> resources;
    private ArrayList<Point> points;
    private Integer council;


    public EffectSurplus(ArrayList<Resource> resources, ArrayList<Point> points, Integer council){
        this.resources = resources;
        this.points = points;
        this.council = council;
    }

    public ArrayList<Resource> getResources() {
        return resources;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public Integer getCouncil() {
        return council;
    }


}
