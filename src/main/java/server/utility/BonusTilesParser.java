package server.utility;

import com.google.gson.JsonObject;
import server.model.board.BonusTile;
import server.model.effect.EffectSurplus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static server.utility.DvptCardParser.getEffectSurplus;


/**
 * Created by LBARCELLA on 17/05/2017.
 */
public class BonusTilesParser {

    private BonusTilesParser(){
    }

    /**
     * this method parse bonus tiles. take a json file and returns an arrayList with all bonus tiles
     *
     * @return
     */

    public static ArrayList<BonusTile> parse() {

        //two EffectSurplus object to save harvest and production bonus. Null only at the moment because all bonus tiles contains the keys harvest,production
        EffectSurplus productionSurplus = null;
        EffectSurplus harvestSurplus = null;

        Integer productionMinForce = 0;
        Integer harvestMinForce = 0;

        //arrayList to save all bonus tiles
        ArrayList<BonusTile> allBonusTiles = new ArrayList<BonusTile>();

        //get a JsonObject from the file stored in resource that contains all the bonusTiles in json
        JsonObject bonusTilesSet = null;
        try {

            bonusTilesSet = Loader.getJsonObjectFromFile("json/bonusTiles.json");

        } catch (IOException e) {

            e.printStackTrace();
        } catch (URISyntaxException e) {

            e.printStackTrace();
        }

        //extract one by one all the card from cardsSet and create a bonus tiles object from every single bunus tile in json file
        for (String bonusTilesId : Json.getObjectKeys(bonusTilesSet)) {

            //extract one single bonus tile
            JsonObject tile = bonusTilesSet.getAsJsonObject(bonusTilesId);

            //get bonus tile keys in json representation
            ArrayList<String> keys = Json.getObjectKeys(tile);

            //foreach key extract his value
            for (String key : keys) {

                if (key.equals("production")) {

                    //extract JsonObject productionObject from tile
                    JsonObject productionObject = tile.getAsJsonObject("production");
                    productionMinForce = getMinForce(productionObject);
                    productionSurplus = getEffectSurplus(productionObject);

                }

                if (key.equals("harvest")) {

                    //extract JsonObject harvestObject from tile
                    JsonObject harvestObject = tile.getAsJsonObject("harvest");
                    harvestMinForce = getMinForce(harvestObject);
                    harvestSurplus = getEffectSurplus(harvestObject);

                }

            }
            //add the new bonus tile to the arrayList
            allBonusTiles.add(new BonusTile(Integer.parseInt(bonusTilesId), productionMinForce, productionSurplus, harvestMinForce, harvestSurplus));
        }

        return allBonusTiles;
    }

    private static Integer getMinForce (JsonObject object){
        return object.get("minForce").getAsInt();
    }


}
