package server.model.effect;
import java.io.Serializable;
import java.util.ArrayList;
import server.model.valuable.*;
import server.utility.UnicodeChars;

/**
 * Created by Federico on 10/05/2017.
 */
public class EffectSurplus implements Serializable {
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

    @Override
    public String toString() {

        String surplus = "";

        for (Resource resource: this.resources) {

            if(resources.indexOf(resource) == resources.size()-1 && points.size()==0 && council==0) {

                surplus += resource.toString();
            }

            else
            {
                surplus += resource.toString()+", ";
            }
        }

        for (Point point : this.points) {

            if(points.indexOf(point) == points.size()-1 && council==0) {

                surplus += point.toString();

            }

            else {

                surplus += point.toString()+", ";

            }
        }

        if(council >= 1)
            surplus += "Council "+ UnicodeChars.Council+" : "+council;

        return surplus;
    }
}
