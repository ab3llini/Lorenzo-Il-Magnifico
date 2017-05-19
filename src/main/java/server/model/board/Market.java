package server.model.board;

import java.util.ArrayList;

/**
 * Created by LBARCELLA on 19/05/2017.
 */
public class Market {

    private ArrayList<ActionPlace> marketPlaces;

    public Market(ArrayList<ActionPlace> marketPlaces){
        this.marketPlaces=marketPlaces;
    }

    public ArrayList<ActionPlace> getMarketPlaces() {
        return marketPlaces;
    }
}
