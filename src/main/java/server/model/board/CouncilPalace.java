package server.model.board;

import server.model.Player;
import server.model.effect.ImmediateEffect;

import java.util.ArrayList;

/**
 * Created by Federico on 11/05/2017.
 */
public class CouncilPalace extends CompositeActionPlace {


    public CouncilPalace(ImmediateEffect immediateEffect, Integer entryForce, Integer minPlayers, ArrayList<FamilyMember> places) {
        super(immediateEffect, entryForce, minPlayers, places);
    }

    public ImmediateEffect getImmediateEffect(){
        return immediateEffect;
    }

    public Integer getEntryForce(){
        return entryForce;
    }

    public Integer getMinPlayers(){
        return minPlayers;
    }

    //returns an ordered arrayList of players that has their familiar in the CouncilPalace area
    public ArrayList<Player> getCouncilPalaceOrder(){
        ArrayList<Player> playersOrder=new ArrayList<Player>();
        for (FamilyMember place:places) {
            if(!playersOrder.contains(place.getPlayer())){
                playersOrder.add(place.getPlayer());
            }
        }
        return playersOrder;
    }
}
