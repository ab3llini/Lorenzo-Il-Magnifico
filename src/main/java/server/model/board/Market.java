package server.model.board;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by LBARCELLA on 19/05/2017.
 */
public class Market implements Serializable {

    private ArrayList<SingleActionPlace> marketPlaces;

    public Market(ArrayList<SingleActionPlace> marketPlaces){
        this.marketPlaces=marketPlaces;
    }

    public ArrayList<SingleActionPlace> getMarketPlaces() {
        return marketPlaces;
    }

    public void placeFamilyMember(FamilyMember familyMember,Integer placementIndex){

        this.getMarketPlaces().get(placementIndex).setOccupied(true);
        this.getMarketPlaces().get(placementIndex).setFamilyMember(familyMember);
    }
}
