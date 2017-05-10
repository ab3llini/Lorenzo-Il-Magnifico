package Server.Model.Effect;
import java.util.ArrayList;
import Server.Model.valuable.*;

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
}
