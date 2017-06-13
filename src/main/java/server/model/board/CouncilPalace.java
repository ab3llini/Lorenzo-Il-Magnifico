package server.model.board;

import server.model.effect.EffectSurplus;
import server.model.effect.ImmediateEffect;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by LBARCELLA on 14/05/2017.
 */
public class CouncilPalace extends CompositeActionPlace implements Serializable {


    public CouncilPalace(EffectSurplus effectSurplus, Integer entryForce, Integer minPlayers) {
        super(effectSurplus, entryForce, minPlayers);
    }

    public EffectSurplus getEffectSurplus() { return this.effectSurplus;}

    public Integer getEntryForce(){
        return this.entryForce;
    }

    public Integer getMinPlayers(){
        return this.minPlayers;
    }

    /**
     * returns an ordered arrayList of players that has their familiar in the CouncilPalace area
     * @return
     */
    public ArrayList<Player> getCouncilPalaceOrder(){
        ArrayList<Player> playersOrder=new ArrayList<Player>();

        for (FamilyMember place: this.places) {
            if(!playersOrder.contains(place.getPlayer())){
                playersOrder.add(place.getPlayer());
            }
        }
        return playersOrder;
    }

    public void clean(){

        this.places.removeAll(places);
    }

}
