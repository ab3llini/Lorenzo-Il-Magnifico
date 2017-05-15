package server.model.board;

import server.model.effect.ImmediateEffect;

import java.util.ArrayList;

/**
 * Created by Federico on 11/05/2017.
 */
public class PHCompositeActionPlace extends CompositeActionPlace {
    private Integer forceMalus;


    public PHCompositeActionPlace(ImmediateEffect immediateEffect, Integer entryForce, Integer minPlayers, ArrayList<FamilyMember> places) {
        super(immediateEffect, entryForce, minPlayers, places);
        this.forceMalus = forceMalus;
    }

    public Integer getForceMalus() {
        return forceMalus;
    }


    public void setForceMalus(Integer forceMalus) {
        this.forceMalus = forceMalus;
    }
}
