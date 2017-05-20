package server.model.card.ban;

import server.model.valuable.Point;
import server.model.valuable.ResourceType;

import java.util.ArrayList;

/**
 * Created by Federico on 10/05/2017.
 */
public class EffectVictoryMalus {
    private Point causedByPoints;
    private ArrayList<ResourceType> causedbyResources;
    private Integer malus;
    private boolean isRelatedToBuilding;

    public EffectVictoryMalus(Point causedbyPoints, ArrayList<ResourceType> causedbyResources, Integer malus, boolean isRelatedToBuilding) {
        this.causedByPoints = causedbyPoints;
        this.causedbyResources = causedbyResources;
        this.malus = malus;
        this.isRelatedToBuilding = isRelatedToBuilding;
    }

    public Integer getMalus() {
        return malus;
    }

    public ArrayList<ResourceType> getCausedbyResources() {
        return causedbyResources;
    }

    public boolean isRelatedToBuilding() {
        return isRelatedToBuilding;
    }

    public Point getCausedByPoints() {
        return causedByPoints;
    }

}
