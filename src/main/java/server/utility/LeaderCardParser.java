package server.utility;

import com.google.gson.JsonObject;
import server.model.card.developement.DvptCardType;
import server.model.card.leader.LeaderCard;
import server.model.card.leader.LeaderEffect;
import server.model.card.leader.Requirement;
import server.model.effect.ActionType;
import server.model.effect.OnceARoundEffect;
import server.model.effect.PermanentLeaderEffectType;
import server.model.valuable.Point;
import server.model.valuable.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import static server.utility.DvptCardParser.getPoints;
import static server.utility.DvptCardParser.getResourceCost;

/**
 * Created by LBARCELLA on 16/05/2017.
 */
public class LeaderCardParser {

    private LeaderCardParser(){

    }

    /**
     * this method parse leaderCards. take a json file and returns an arrayList with all leaderCards
     *
     * @return
     */

    public static ArrayList<LeaderCard> parse() throws IOException, URISyntaxException {

        // name,requirement,leaderEffect only at the moment null because all leader cards contains the keys name,requirement,effects
        String name = null;
        Requirement requirement=null;
        LeaderEffect leaderEffect=null;

        ArrayList<LeaderCard> allLeaderCards = new ArrayList<LeaderCard>();    //this arrayList contains all the leaderCard already parsed

        //get a JsonObject from the file stored in resource that contains all the cards in json
        JsonObject cardsSet = Loader.getJsonObjectFromFile("/json/leaderCards.json");

        //extract one by one all the card from cardsSet and create a Card object from every single card in json file
        for (String cardId : Json.getObjectKeys(cardsSet)) {

            //extract one single leaderCard
            JsonObject card = cardsSet.getAsJsonObject(cardId);

            //get leaderCard keys in json representation
            ArrayList<String> keys = Json.getObjectKeys(card);

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
            allLeaderCards.add(new LeaderCard(Integer.parseInt(cardId),name,requirement,leaderEffect));
        }
        return allLeaderCards;
    }

    private static String getName(JsonObject card) {
        return card.get("name").getAsString();
    }

    private static Requirement getRequirement(JsonObject card){
        //requirement can be composed by resources or points or also by the number of card of one specific type
        ArrayList<Resource> resourceRequired=new ArrayList<Resource>();
        ArrayList<Point> pointsRequired=new ArrayList<Point>();
        HashMap<DvptCardType,Integer> cardsRequired=new HashMap<DvptCardType, Integer>();
        Boolean sixIdentical=false;

        //extract JsonObject requirement from card
        JsonObject requirement= card.getAsJsonObject("requirement");

        //get keys from requirement(resources || points || cards || sixIdentical)
        ArrayList<String> requirementKeys = Json.getObjectKeys(requirement);

        for (String requirementKey:requirementKeys) {
            if (requirementKey.equals("resources")) {
                resourceRequired = getResourceRequired(requirement);
            }
            if (requirementKey.equals("points")) {
                pointsRequired = getPointsRequired(requirement);
            }

            if(requirementKey.equals("cards")) {
                cardsRequired=getCardsRequired(requirement);
            }

            if (requirementKey.equals("sixIdentical")){
                sixIdentical=true;}
        }
        return new Requirement(resourceRequired,pointsRequired,cardsRequired,sixIdentical);
    }

    private static LeaderEffect getLeaderEffect(JsonObject card){

        //LeaderEffect can be of two types : once a round or permanent
        OnceARoundEffect onceARound=null;
        PermanentLeaderEffectType permanentLeaderEffect=null;

        //extract JsonObject effects from card
        JsonObject effects= card.getAsJsonObject("effects");

        //get keys from effects(onceARound || permanent)
        ArrayList<String> effectKeys = Json.getObjectKeys(effects);

        for (String effectKey:effectKeys) {
            if (effectKey.equals("onceARound")) {
                onceARound = getOnceARoundEffect(effects);
            }
            if (effectKey.equals("permanents")) {
                //permanent effect are very particular and characterized, so it is much better to develop them in the code
                permanentLeaderEffect=PermanentLeaderEffectType.valueOf(effects.get("permanents").getAsString());
            }

        }
        return new LeaderEffect(onceARound,permanentLeaderEffect);
    }

    private static ArrayList<Resource> getResourceRequired(JsonObject requirement){
        return getResourceCost(requirement);
    }

    private static ArrayList<Point> getPointsRequired(JsonObject requirement){
        return getPoints(requirement);
    }

    private static HashMap<DvptCardType,Integer> getCardsRequired(JsonObject requirement){

        //HashMap very convenient to save key-value pairs
        HashMap<DvptCardType,Integer> cardsReq=new HashMap<DvptCardType, Integer>();

        //extract JsonObject cardsRequired from requirement
        JsonObject cardsRequired=requirement.getAsJsonObject("cards");

        //for each type of card get his value required
        for(String cardRequiredKey : Json.getObjectKeys(cardsRequired)){

            if(cardRequiredKey.equals("territory")){
                cardsReq.put(DvptCardType.territory,cardsRequired.get("territory").getAsInt());}

            if(cardRequiredKey.equals("building")){
                cardsReq.put(DvptCardType.building,cardsRequired.get("building").getAsInt());}

            if(cardRequiredKey.equals("venture")){
                cardsReq.put(DvptCardType.venture,cardsRequired.get("venture").getAsInt());}

            if(cardRequiredKey.equals("character")){
                cardsReq.put(DvptCardType.character,cardsRequired.get("character").getAsInt());}
        }
        return cardsReq;
    }

    private static OnceARoundEffect getOnceARoundEffect(JsonObject effects){

        //onceARound effect can be composed by resources,points,action. if sixEffect is true you can do one particular card action one a round
        ArrayList<Resource> resources= new ArrayList<Resource>();
        ArrayList<Point> points = new ArrayList<Point>();
        HashMap<ActionType,Integer> action= new HashMap<ActionType, Integer>();
        Boolean sixEffect = false;

        //extract JsonObject onceAroundEffect from effects
        JsonObject onceARoundEffect= effects.getAsJsonObject("onceARound");

        //get keys from onceARoundEffect(resources || points || actions || sixEffect)
        ArrayList<String> effectKeys = Json.getObjectKeys(onceARoundEffect);

        for (String effectKey:effectKeys) {
            if (effectKey.equals("resources")) {
                resources = getResources(onceARoundEffect);
            }
            if (effectKey.equals("points")) {
                points = getPoints(onceARoundEffect);
            }

            if (effectKey.equals("action")){
                action=getAction(onceARoundEffect);
            }

            if (effectKey.equals("sixEffect")){
                sixEffect=true;
            }
        }


        return new OnceARoundEffect(resources,points,action,sixEffect);
    }

    private static ArrayList<Resource> getResources(JsonObject onceARoundEffect){
        return getResourceCost(onceARoundEffect);
    }

    private static HashMap<ActionType,Integer> getAction (JsonObject onceARoundEffect){

        HashMap<ActionType, Integer> action= new HashMap<ActionType, Integer>();

        //get Json object actionObject from onceARoundEffect
        JsonObject actionObject = onceARoundEffect.getAsJsonObject("action");

        //get keys from onceARoundEffect(harvest|| production)
        ArrayList<String> effectKeys = Json.getObjectKeys(actionObject);

        for (String effectKey:effectKeys) {

            ActionType target=null;
            Integer force=0;

            if(effectKey.equals("target")){
                target = ActionType.valueOf(actionObject.get("target").getAsString());}

            if(effectKey.equals("force")){
                force=actionObject.get("force").getAsInt();}

            action.put(target,force);
        }

        return action;
    }

}
