package server.model.board;

import server.model.effect.EffectSurplus;
import server.model.effect.ImmediateEffect;

import java.util.ArrayList;

/**
 * Created by Federico on 11/05/2017.
 */
public class CompositeActionPlace extends ActionPlace {
    protected ArrayList<FamilyMember> places;

    public CompositeActionPlace(EffectSurplus effectSurplus, Integer entryForce, Integer minPlayers) {
        super(effectSurplus, entryForce, minPlayers);
    }

    public ArrayList<FamilyMember> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<FamilyMember> places) {
        this.places = places;
    }
}
