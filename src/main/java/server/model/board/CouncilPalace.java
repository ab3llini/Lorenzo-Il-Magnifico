package server.model.board;

import server.model.effect.ImmediateEffect;

import java.util.ArrayList;

/**
 * Created by Federico on 11/05/2017.
 */
public class CouncilPalace extends CompositeActionPlace {


    public CouncilPalace(ImmediateEffect immediateEffect, Integer entryForce, Integer minPlayers, ArrayList<FamilyMember> places) {
        super(immediateEffect, entryForce, minPlayers, places);
    }
}
