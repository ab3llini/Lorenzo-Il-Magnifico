package server.utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import logger.Level;
import logger.Logger;
import server.model.board.*;
import server.model.card.developement.DvptCard;
import server.model.card.developement.DvptCardType;
import server.model.effect.EffectSurplus;
import server.model.valuable.Point;
import server.model.valuable.Resource;

import java.util.ArrayList;

import static server.utility.DvptCardParser.getEffectSurplus;
import static server.utility.DvptCardParser.getSurplus;

/**
 * Created by LBARCELLA on 18/05/2017.
 */
public class BoardConfigParser {

    //boardConfig object contains all the configuration for the board
    public static JsonObject boardConfig;


    public static JsonObject getBoardConfig() {

        //Do not parse again if already.
        if (boardConfig != null) { return boardConfig; }


        try {

            //Fetch the config object
            boardConfig = Loader.getJsonObjectFromFile("/json/board-config.json");

        }
        catch (Exception e) {

            Logger.log(Level.SEVERE, "GameConfigParser", "Unable to parse the config file! Any access to config will fail", e);


        }

        return boardConfig;

    }

    /**
     * this method parse cathedral attributes
     * @return Cathedral that will be used in board model
     */
    public static Cathedral getCathedral(){

        //get JsonObject cathedral from boardConfig
        JsonObject cathedralObject = BoardConfigParser.getBoardConfig().getAsJsonObject("cathedral");

        //we have to fetch from file the minimum number of faith points to avoid excommunication for each period
        Integer firstMinFaith = cathedralObject.get("firstMinFaith").getAsInt();

        Integer secondMinFaith = cathedralObject.get("secondMinFaith").getAsInt();

        Integer thirdtMinFaith = cathedralObject.get("thirdMinFaith").getAsInt();

        //create cathedral object and return it
        return new Cathedral(firstMinFaith, secondMinFaith , thirdtMinFaith);
    }


    /**
     * this method parse productionArea attributes; production area is composed by two parts : single production place, composite production place.
     * this method uses getSingleActionPlace and getCompositeActionPlace to obtain his attributes.
     * @return ActionRequest Area (production) that will be used in board model
     */
    public static ActionArea getProductionActionArea(){

        //get JsonObject productionArea from boardConfig
        JsonObject productionAreaObject = BoardConfigParser.getBoardConfig().getAsJsonObject("productionArea");

        SingleActionPlace productionSinglePlace = getSingleActionPlace(productionAreaObject);

        PHCompositeActionPlace productionCompositePlace = getCompositeActionPlace(productionAreaObject);

        //return actionArea object with the specification of his authenticationType
        return new ActionArea(ActionType.Production, productionSinglePlace, productionCompositePlace);

    }

    /**
     * this method parse harvestArea attributes; harvest area is composed by two parts : single production place, composite production place.
     * this method uses getSingleActionPlace and getCompositeActionPlace to obtain his attributes.
     * @return ActionRequest Area (harvest) that will be used in board model
     */
    public static ActionArea getHarvestActionArea(){

        //get JsonObject productionArea from boardConfig
        JsonObject productionAreaObject = BoardConfigParser.getBoardConfig().getAsJsonObject("harvestArea");

        SingleActionPlace productionSinglePlace = getSingleActionPlace(productionAreaObject);

        PHCompositeActionPlace productionCompositePlace = getCompositeActionPlace(productionAreaObject);

        //return actionArea object with the specification of his authenticationType
        return new ActionArea(ActionType.Harvest, productionSinglePlace, productionCompositePlace);

    }

    /**
     * this method parse market attributes and create the Market that is composed by an arrayList of action places
     * @return Market that will be used in board model
     */
    public static Market getMarket(){

        ArrayList<SingleActionPlace> marketPlaces = new ArrayList<SingleActionPlace>();

        //get JsonObject market from boardConfig
        JsonObject marketObject = BoardConfigParser.getBoardConfig().getAsJsonObject("market");

        //get keys from marketObject ( 1 || 2 || 3 || 4)
        ArrayList<String> marketKeys = Json.getObjectKeys(marketObject);

        //foreach market place extract his values
        for (String marketKey: marketKeys) {

            JsonObject marketPlace = marketObject.getAsJsonObject(marketKey);

            //get from json file all the attributes of the single market place
            Integer minForce = marketPlace.get("minForce").getAsInt();
            Integer minPlayers = marketPlace.get("minPlayers").getAsInt();
            EffectSurplus surplus = getEffectSurplus(marketPlace);

            marketPlaces.add(new SingleActionPlace(surplus, minForce, minPlayers));
        }
        return new Market(marketPlaces);
    }

    /**
     * this method parse council palace attributes and create the Council Palace
     * @return Council Palace that will be used in board model
     */
    public static CouncilPalace getCouncilPalace(){

        //get JsonObject council from boardConfig
        JsonObject councilObject = BoardConfigParser.getBoardConfig().getAsJsonObject("councilPalace");

        //get council palace attributes
        EffectSurplus surplus = getEffectSurplus(councilObject);
        Integer entryForce = councilObject.get("minForce").getAsInt();
        Integer minPlayers = councilObject.get("minPlayers").getAsInt();

        return new CouncilPalace(surplus,entryForce,minPlayers);
    }

    /**
     * this method parse a tower depending on her authenticationType
     * @return a tower that will be used in board model
     */
    public static ArrayList<TowerSlot> getTower(DvptCardType type){

        ArrayList<TowerSlot> tower = new ArrayList<TowerSlot>();

        //get correct JsonObject ventureTower from boardConfig (authenticationType+Tower because in json territoryTower is the key for JsonObject territoryTower)
        JsonObject towerObject = BoardConfigParser.getBoardConfig().getAsJsonObject(type+"Tower");

        //get keys from towerObject ( 1 || 2 || 3 || 4)
        ArrayList<String> towerKeys = Json.getObjectKeys(towerObject);

        //foreach key extract his values ( for each towerSlot[1,2,3,4] extract his value)
        for (String towerKey: towerKeys) {

            //every towerSlot has minForce(or entryForce) and surplus
            Integer minForce=0;
            EffectSurplus surplus = new EffectSurplus(new ArrayList<Resource>(),new ArrayList<Point>(),0);
            JsonObject towerSlot = towerObject.getAsJsonObject(towerKey);

            for(String towerSlotKey : Json.getObjectKeys(towerSlot)){
                if(towerSlotKey.equals("minForce")){
                    minForce = towerSlot.get("minForce").getAsInt();}
                if(towerSlotKey.equals("surplus")){
                    surplus = getEffectSurplus(towerSlot);
                }
            }

            tower.add(new TowerSlot(surplus,minForce,0));
        }

        return tower;
    }

    private static SingleActionPlace getSingleActionPlace(JsonObject area){

        //extract JsonObject singleActionPlace from area
        JsonObject singleActionPlaceObject = area.getAsJsonObject("singleActionPlace");

        Integer minForce = singleActionPlaceObject.get("minForce").getAsInt();

        Integer minPlayers = singleActionPlaceObject.get("minPlayers").getAsInt();

        //TODO load values from file
        EffectSurplus surplus = new EffectSurplus(new ArrayList<Resource>(),new ArrayList<Point>(),0);

        return new SingleActionPlace(surplus, minForce,minPlayers);
    }

    private static PHCompositeActionPlace getCompositeActionPlace(JsonObject area){

        //extract JsonObject compositeActionPlace from area
        JsonObject compositeActionPlaceObject = area.getAsJsonObject("phCompositeActionPlace");

        Integer minForce = compositeActionPlaceObject.get("minForce").getAsInt();

        Integer minPlayers = compositeActionPlaceObject.get("minPlayers").getAsInt();

        Integer forceMalus = compositeActionPlaceObject.get("malus").getAsInt();

        //TODO load values from file
        EffectSurplus surplus = new EffectSurplus(new ArrayList<Resource>(),new ArrayList<Point>(),0);

        return new PHCompositeActionPlace(surplus, minForce, forceMalus, minPlayers);
    }

    public static ArrayList<EffectSurplus> getCouncilPrivilegeOptions(){
        ArrayList<EffectSurplus> councilPrivilegeOptions = new ArrayList<EffectSurplus>();

        JsonArray optionArray = BoardConfigParser.getBoardConfig().getAsJsonArray("councilPrivilegeOptions");

        for(JsonElement option : optionArray){
            EffectSurplus surplus = getSurplus((JsonObject) option);
            councilPrivilegeOptions.add(surplus);
        }

        return councilPrivilegeOptions;
    }

    public static Integer getVictoryBonus(DvptCardType dvptCardType, Integer numberOfCards){

        Integer victoryBonus= 0;

        //get JsonObject productionArea from boardConfig
        JsonObject cardsBonusObject = BoardConfigParser.getBoardConfig().getAsJsonObject("cardsToVictoryPoints");

        if(dvptCardType == DvptCardType.territory){
            JsonObject territoryBonusObject = cardsBonusObject.getAsJsonObject("territoryCardToVictory");
            victoryBonus = territoryBonusObject.get(""+numberOfCards).getAsInt();
        }

        if(dvptCardType == DvptCardType.character){
            JsonObject characterBonusObject = cardsBonusObject.getAsJsonObject("characterCardToVictory");
            victoryBonus = characterBonusObject.get(""+numberOfCards).getAsInt();
        }

        return  victoryBonus;
    }
}
