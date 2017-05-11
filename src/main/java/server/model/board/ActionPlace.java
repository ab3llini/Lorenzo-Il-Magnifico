package server.model.board;

import server.model.effect.ImmediateEffect;

/**
 * Created by Federico on 11/05/2017.
 */
public class ActionPlace {
    private ImmediateEffect immediateEffect;
    private Integer entryForce;
    private Integer minPlayers;


    public ActionPlace(ImmediateEffect immediateEffect, Integer entryForce, Integer minPlayers){
        this.immediateEffect = immediateEffect;
        this.entryForce = entryForce;
        this.minPlayers = minPlayers;
    }
}
