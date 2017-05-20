package server.model;

import server.model.card.developement.DvptCard;
import server.model.card.developement.TerritoryDvptCard;
import server.utility.DvptCardParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Federico on 15/05/2017.
 */

public class GameSingleton {
    private static GameSingleton instance = null;
    private ArrayList<DvptCard> dvptCards = new ArrayList<DvptCard>();


    private GameSingleton() {} //costructor

    public static GameSingleton getInstance() {
        if(instance ==null) {

            instance = new GameSingleton();

            try {
                instance.dvptCards = DvptCardParser.parse();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }

        }
        return instance;
    }


   public DvptCard getSpecificDvptCard(Integer CardId) {

        return this.dvptCards.get(CardId);
    }

    public ArrayList<DvptCard> getDvptCards() {

        return this.dvptCards;
    }
}
