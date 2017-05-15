package server.model.board;

import server.model.effect.ImmediateEffect;

/**
 * Created by Federico on 11/05/2017.
 */
public class ActionPlace {
    protected ImmediateEffect immediateEffect;
    protected Integer entryForce;
    protected Integer minPlayers;


    public ActionPlace(ImmediateEffect immediateEffect, Integer entryForce, Integer minPlayers){
        this.immediateEffect = immediateEffect;
        this.entryForce = entryForce;
        this.minPlayers = minPlayers;
    }
}
