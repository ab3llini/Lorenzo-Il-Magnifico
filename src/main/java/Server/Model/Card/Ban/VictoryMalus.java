package Server.Model.Card.Ban;

import Server.Model.Card.Developement.DvptCard;
import Server.Model.valuable.Point;
import Server.Model.valuable.ResourceType;

import java.util.ArrayList;

/**
 * Created by Federico on 10/05/2017.
 */
public class VictoryMalus {
    private Point causedbyPoints;
    private ArrayList<ResourceType> causedbyResources;
    private Integer malus;
    private boolean isRelatedToBuilding;

    public VictoryMalus(Point causedbyPoints, ArrayList<ResourceType> causedbyResources, Integer malus, boolean isRelatedToBuilding) {
        this.causedbyPoints = causedbyPoints;
        this.causedbyResources = causedbyResources;
        this.malus = malus;
        this.isRelatedToBuilding = isRelatedToBuilding;
    }
}
