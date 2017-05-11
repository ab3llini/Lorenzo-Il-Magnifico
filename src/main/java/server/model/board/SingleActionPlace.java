package server.model.board;

import server.model.effect.ImmediateEffect;

/**
 * Created by Federico on 11/05/2017.
 */
public class SingleActionPlace extends ActionPlace {
    private FamilyMember place;
    private boolean occupied;

    public SingleActionPlace(ImmediateEffect immediateEffect, Integer entryForce, Integer minPlayers) {
        super(immediateEffect, entryForce, minPlayers);
        this.place = place;
        this.occupied = occupied;
    }
}
