package server.utility; /**
 * Created by LBARCELLA on 10/05/2017.
 */


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import server.model.card.developement.Cost;
import server.model.card.developement.DvptCard;
import server.model.card.developement.MilitaryCost;
import server.model.card.developement.TerritoryDvptCard;
import server.model.valuable.Resource;
import server.model.valuable.ResourceType;


import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class DvptCardParser {

    /**
     * this method parse dvpmCards. receive a json file and returns an arrayList with all dvpmCards
     *
     * @return
     */

    public static ArrayList<DvptCard> parse() throws IOException, URISyntaxException {

        ArrayList<DvptCard> allCards = new ArrayList<DvptCard>();    //this arrayList contains all the dvpmCard already parsed
        String name = null;
        Integer period = 0;
        String type = null;
        Cost cost = null;

        //get a JsonObject from the file stored in resource that contains all the cards in json
        JsonObject cardsSet = DvptCardParser.getJsonObjectFromFile("/json/cards.json");

        //extract one by one all the card from cardsSet and create a Card object from every single card in json file
        for (Integer i = 1; i < 97; i++) {

            //extract one single card
            JsonObject card = cardsSet.getAsJsonObject(i.toString());

            //get card keys in json representation
            ArrayList<String> keys = DvptCardParser.getKeys(card);

            //foreach key extract his value
            for (String key : keys) {
                if (key.equals("name"))
                    name = getName(card);

                if (key.equals("period"))
                    period = getPeriod(card);

                if (key.equals("type"))
                    type = getType(card);

                if (key.equals("cost"))
                    cost = getCost(card);

            }
            //add the new card at the arrayList
            if(type.equals("territory")){
                    allCards.add(new TerritoryDvptCard(i,name, period,null,null));

            }
        }


        return allCards;
    }

    private static String getName(JsonObject card) {
        return card.get("name").getAsString();
    }

    private static Integer getPeriod(JsonObject card) {
        return card.get("period").getAsInt();
    }

    private static String getType(JsonObject card) {
        return card.get("type").getAsString();
    }

    private static Cost getCost(JsonObject card) {
        //arrayList to save the cost in resources
        //militaryCost to save the cost in military points(required and removed)
        ArrayList<Resource> resourceCost = new ArrayList<Resource>();
        MilitaryCost militaryCost=null;

        //extract arrayCost from JsonObject
        JsonArray arrayCost = card.getAsJsonArray("cost");

        for (int j = 0; j < arrayCost.size(); j++) {
            //extract JsonObject cost from card
            JsonObject costo = arrayCost.get(j).getAsJsonObject();

            //get keys from cost (resource || military)
            ArrayList<String> costoKeys = DvptCardParser.getKeys(costo);

            //get resourceCost and militaryCost..so i have both 'items' to create the cost
            for (String costoKey : costoKeys) {
                if (costoKey.equals("resources"))
                    resourceCost = getResourceCost(costo);
                if (costoKey.equals("military"))
                    militaryCost = getMilitaryCost(costo);
            }
        }
        //create the cost
        Cost cost=new Cost(resourceCost,militaryCost);

        return cost;
    }

    private static ArrayList<Resource> getResourceCost(JsonObject costo){
        ArrayList<Resource> resourceCost=new ArrayList<Resource>();   //this arrayList contains all the resource required for the card

        //extract JsonObject resources from costo
        JsonObject resources = costo.getAsJsonObject("resources");

        //get keys from resources(coins || wood || stones || servants)
        ArrayList<String> resourceKeys = DvptCardParser.getKeys(resources);

        for (String resourceKey:resourceKeys) {
            if(resourceKey.equals("coins"))
                resourceCost.add(new Resource(ResourceType.Coins,resources.get("coins").getAsInt()));
            if(resourceKey.equals("wood"))
                resourceCost.add(new Resource(ResourceType.Wood,resources.get("wood").getAsInt()));
            if(resourceKey.equals("stones"))
                resourceCost.add(new Resource(ResourceType.Stones,resources.get("stones").getAsInt()));
            if(resourceKey.equals("servants"))
                resourceCost.add(new Resource(ResourceType.Servants,resources.get("servants").getAsInt()));
        }
        return resourceCost;
    }

    private static MilitaryCost getMilitaryCost(JsonObject costo){
        Integer militaryRequired=null;            //integer to save military points required to take the card
        Integer militaryMalus=null;               //integer to save military points lost to take the card

        //extract JsonObject military from costo
        JsonObject military= costo.getAsJsonObject("military");

        //get keys from military(required || malus)
        ArrayList<String> militaryKeys = DvptCardParser.getKeys(military);

        for (String resourceKey:militaryKeys) {
            if(resourceKey.equals("required"))
                militaryRequired=military.get("required").getAsInt();
            if(resourceKey.equals("malus"))
                militaryMalus=military.get("malus").getAsInt();
        }
        return new MilitaryCost(militaryRequired,militaryMalus);
    }

    /**
     * all file pars with gson
     * @return
     * @throws IOException
     */

    private static JsonObject getJsonObjectFromFile(String filename) throws IOException, URISyntaxException {
        BufferedReader br = null;
        FileReader fr = null;

        File file = new File(DvptCardParser.class.getResource("/json/cards.json").toURI());
        br = new BufferedReader(new FileReader(file));

        //parse all file
        JsonParser parser = new JsonParser();
        //get json object
        JsonObject jobject = parser.parse(br).getAsJsonObject();

        return jobject;
    }

    /**
     * return an ArrayList of string of all the keys of JsonObject
     *
     * @param o
     * @return
     */

    private static ArrayList<String> getKeys(JsonObject o) {

        ArrayList<String> keys = new ArrayList<String>();

        Set<Map.Entry<String, JsonElement>> entrySet = o.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {

            //Append key
            keys.add(entry.getKey());

        }
        return keys;
    }

}