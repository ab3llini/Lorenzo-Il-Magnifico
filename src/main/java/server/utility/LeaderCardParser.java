package server.utility;

import com.google.gson.JsonObject;
import server.model.card.leader.LeaderCard;
import server.model.card.leader.LeaderEffect;
import server.model.card.leader.Requirement;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by LBARCELLA on 16/05/2017.
 */
public class LeaderCardParser {

    private LeaderCardParser(){

    }

    public static ArrayList<LeaderCard> parse() throws IOException, URISyntaxException {
        ArrayList<LeaderCard> allCards = new ArrayList<LeaderCard>();    //this arrayList contains all the leaderCard already parsed
        String name = null;
        Requirement requirement=null;
        LeaderEffect leaderEffect=null;

        //get a JsonObject from the file stored in resource that contains all the cards in json
        JsonObject cardsSet = DvptCardParser.getJsonObjectFromFile("/json/leaderCards.json");

        //extract one by one all the card from cardsSet and create a Card object from every single card in json file
        for (String cardId : DvptCardParser.getKeys(cardsSet)) {

            //extract one single card
            JsonObject card = cardsSet.getAsJsonObject(cardId);

            //get card keys in json representation
            ArrayList<String> keys = DvptCardParser.getKeys(card);

            //foreach key extract his value
            for (String key : keys) {
                if (key.equals("name")) {
                    name = getName(card);
                }

                if (key.equals("requirement")) {
                    requirement = getRequirement(card);
                }

                if (key.equals("effects")) {
                    leaderEffect = getLeaderEffect(card);
                }
            }
            //add the new card to the arrayList
            allCards.add(new LeaderCard(Integer.parseInt(cardId),name,requirement,leaderEffect));
        }
        return allCards;
    }

    private static String getName(JsonObject card) {
        return card.get("name").getAsString();
    }

    private static Requirement getRequirement(JsonObject card){
        return null;
    }

    private static LeaderEffect getLeaderEffect(JsonObject card){
        return null;
    }


}
