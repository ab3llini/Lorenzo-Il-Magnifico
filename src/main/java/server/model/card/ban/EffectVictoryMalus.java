package server.model.card.ban;

import server.model.valuable.Point;
import server.model.valuable.Resource;
import server.model.valuable.ResourceType;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Federico on 10/05/2017.
 */
public class EffectVictoryMalus implements Serializable {
    private ArrayList<Point> causedByPoints;
    private ArrayList<Resource> causedByResources;
    private Integer malus;
    private boolean isRelatedToBuilding;

    public EffectVictoryMalus(ArrayList<Point> causedbyPoints, ArrayList<Resource> causedByResources, Integer malus, boolean isRelatedToBuilding) {
        this.causedByPoints = causedbyPoints;
        this.causedByResources = causedByResources;
        this.malus = malus;
        this.isRelatedToBuilding = isRelatedToBuilding;
    }

    public Integer getMalus() {
        return malus;
    }

    public ArrayList<Resource> getCausedbyResources() {
        return causedByResources;
    }

    public boolean isRelatedToBuilding() {
        return isRelatedToBuilding;
    }

    public ArrayList<Point> getCausedByPoints() {
        return causedByPoints;
    }

    @Override
    public String toString() {

        String banString = "";

        banString += "At the end of the game, you lose 1 Victory Point for every :";

        if(causedByPoints.size() > 0)

            banString += causedByPoints.toString();

        if(causedByResources.size() > 0)

            banString += causedByResources.toString();

        if(isRelatedToBuilding)

            banString +=" you lose 1 Victory Point for every resource on your Building Cards’ costs";

        return banString;

    }
}
