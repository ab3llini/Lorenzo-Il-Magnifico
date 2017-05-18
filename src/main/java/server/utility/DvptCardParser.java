package server.utility;

/**
 * Created by LBARCELLA on 10/05/2017.
 */


import com.google.gson.*;
import server.model.card.developement.*;
import server.model.effect.*;
import server.model.valuable.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class DvptCardParser {

    /**
     * this method parse dvptCards. receive a json file and returns an arrayList with all dvptCards
     *
     * @return
     */

    private DvptCardParser(){
    }

    public static ArrayList<DvptCard> parse() throws IOException, URISyntaxException {

        ArrayList<DvptCard> allCards = new ArrayList<DvptCard>();    //this arrayList contains all the dvpmCard already parsed
        String name = null;
        Integer period = 0;
        DvptCardType type = null;
        ArrayList<Cost> cost = new ArrayList<Cost>();
        ImmediateEffect immediateEffect = null;
        PermanentEffect permanentEffect = null;

        //get a JsonObject from the file stored in resource that contains all the cards in json
        JsonObject cardsSet = DvptCardParser.getJsonObjectFromFile("/json/cards.json");

        //extract one by one all the card from cardsSet and create a Card object from every single card in json file
        for (String cardId : DvptCardParser.getKeys(cardsSet)) {

            //extract one single card
            JsonObject card = cardsSet.getAsJsonObject(cardId);

            //get card keys in json representation
            ArrayList<String> keys = DvptCardParser.getKeys(card);

            //foreach key extract his value
            for (String key : keys) {
                if (key.equals("name")){
                    name = getName(card);}

                if (key.equals("period")){
                    period = getPeriod(card);}

                if (key.equals("type")){
                    type = getType(card);}

                if (key.equals("cost")){
                    cost = getCost(card);}

                if (key.equals("effects")) {

                    JsonObject effect = card.getAsJsonObject("effects");

                    //effects can be immediate and permanent
                    for (String effectKey : DvptCardParser.getKeys(effect)) {
                        if (effectKey.equals("immediate")){
                            immediateEffect = getImmediateEffect(effect);}

                        if (effectKey.equals("permanent")) {
                            permanentEffect = getPermanentEffect(effect);
                        }
                    }
                }

            }
            //add the new card to the arrayList
            if (type==DvptCardType.territory) {
                allCards.add(new TerritoryDvptCard(Integer.parseInt(cardId), name, period, immediateEffect, permanentEffect));
            }

            if (type==DvptCardType.building) {
                allCards.add(new BuildingDvptCard(Integer.parseInt(cardId), name, period, cost, immediateEffect, permanentEffect));
            }

            if (type==DvptCardType.character) {
                allCards.add(new CharacterDvptCard(Integer.parseInt(cardId), name, period, cost, immediateEffect, permanentEffect));
            }

            if (type==DvptCardType.venture) {
                allCards.add(new VentureDvptCard(Integer.parseInt(cardId), name, period, cost, immediateEffect, permanentEffect));
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

    private static DvptCardType getType(JsonObject card) {
        return DvptCardType.valueOf(card.get("type").getAsString());
    }

    private static ArrayList<Cost> getCost(JsonObject card) {
        //arrayList to all the possible cost
        ArrayList<Cost> cost = new ArrayList<Cost>();

        //extract arrayCost from JsonObject
        JsonArray arrayCost = card.getAsJsonArray("cost");

        for (int j = 0; j < arrayCost.size(); j++) {
            //extract JsonObject cost from card
            JsonObject costo = arrayCost.get(j).getAsJsonObject();

            //get keys from cost (resource || military)
            ArrayList<String> costoKeys = DvptCardParser.getKeys(costo);

            //initialize two variables in order not to use null pointers
            ArrayList<Resource> resourceCost = new ArrayList<Resource>();
            MilitaryCost militaryCost = new MilitaryCost(0,0);

            //get resourceCost and militaryCost..so i have both 'items' to create the cost
            for (String costoKey : costoKeys) {

                if (costoKey.equals("resources")) {
                    resourceCost = getResourceCost(costo);
                }

                if (costoKey.equals("military")) {
                    militaryCost = getMilitaryCost(costo);
                }
                //create the cost and added to the arrayList
                cost.add(new Cost(resourceCost, militaryCost));
            }
        }

        return cost;
    }

    private static ImmediateEffect getImmediateEffect(JsonObject effect) {
        EffectSurplus effectSurplus = null;
        EffectAction effectAction = null;

        //extract immediate from JsonObject
        JsonObject immediate = effect.getAsJsonObject("immediate");

        //get keys from immediate (surplus || action)
        ArrayList<String> effectKeys = DvptCardParser.getKeys(immediate);

        //get effectSurplus and effectAction..so i have both 'items' to create the cost
        for (String effectKey : effectKeys) {

            if (effectKey.equals("surplus")) {
                effectSurplus = getEffectSurplus(immediate);
            }

            if (effectKey.equals("action")) {
                effectAction = getEffectAction(immediate);
            }
        }

        return new ImmediateEffect(effectSurplus,effectAction);
    }

    private static PermanentEffect getPermanentEffect(JsonObject effect){
        Integer minForce=null;
        Integer vpoints=null;
        EffectSurplus surplus=null;
        Multiplier multiplier=null;
        ArrayList<EffectConversion> conversion=null;
        EffectPermanentAction action=null;
        Boolean penality=false;

        //extract permanent from JsonObject effect
        JsonObject permanent = effect.getAsJsonObject("permanent");

        //get keys from permanent (minforce || type || surplus || conversion || action || discount || penality)
        ArrayList<String> effectKeys = DvptCardParser.getKeys(permanent);

        //get effectSurplus and effectAction..so i have both 'items' to create the cost
        for (String effectKey : effectKeys) {

            if (effectKey.equals("vpoints")) {
                vpoints = permanent.get("vpoints").getAsInt();
            }

            if (effectKey.equals("minForce")) {
                minForce = permanent.get("minForce").getAsInt();
            }

            if (effectKey.equals("surplus")) {
                surplus = getEffectSurplus(permanent);
            }

            if(effectKey.equals("forceBonus")){
                action=getEffectPermanentAction(permanent);
            }

            if(effectKey.equals("conversion")){
                conversion=getEffectConversion(permanent);
            }

            if(effectKey.equals("multiplier")){
                multiplier=getMultiplier(permanent);
            }

            if(effectKey.equals("penality")){
                penality=true;
            }

        }



        return new PermanentEffect(minForce,vpoints,surplus,conversion,multiplier,action,penality);
    }

    public static ArrayList<Resource> getResourceCost(JsonObject costo){
        ArrayList<Resource> resourceCost=new ArrayList<Resource>();   //this arrayList contains all the resource required for the card

        //extract JsonObject resources from costo
        JsonObject resources = costo.getAsJsonObject("resources");

        //get keys from resources(coins || wood || stones || servants)
        ArrayList<String> resourceKeys = DvptCardParser.getKeys(resources);

        for (String resourceKey:resourceKeys) {
            if(resourceKey.equals("coins")){
                resourceCost.add(new Resource(ResourceType.Coins,resources.get("coins").getAsInt()));}
            if(resourceKey.equals("wood")){
                resourceCost.add(new Resource(ResourceType.Wood,resources.get("wood").getAsInt()));}
            if(resourceKey.equals("stones")){
                resourceCost.add(new Resource(ResourceType.Stones,resources.get("stones").getAsInt()));}
            if(resourceKey.equals("servants")){
                resourceCost.add(new Resource(ResourceType.Servants,resources.get("servants").getAsInt()));}
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

    public static EffectSurplus getEffectSurplus (JsonObject immediate){
        ArrayList<Resource> resources=new ArrayList<Resource>();      //ArrayList to save surplus in resources
        ArrayList<Point> points=new ArrayList<Point>();               //ArrayList to save surplus in points
        Integer council=0;                                            //Integer to save surplus in council privilege

        //get JsonObject surplus from immediate
        JsonObject surplus=immediate.getAsJsonObject("surplus");

        return getSurplus(surplus);
    }

    private static EffectSurplus getSurplus(JsonObject surplus){
        ArrayList<Resource> resources=new ArrayList<Resource>();      //ArrayList to save surplus in resources
        ArrayList<Point> points=new ArrayList<Point>();               //ArrayList to save surplus in points
        Integer council=0;
        //get keys from surplus(resources || points || council) that identify all the different kind of surplus
        ArrayList<String> surplusKeys = DvptCardParser.getKeys(surplus);

        for (String surplusKey:surplusKeys) {
            if(surplusKey.equals("resources")){
                resources=getResourceSurplus(surplus);}
            if(surplusKey.equals("points")){
                points=getPoints(surplus);}
            if(surplusKey.equals("council")){
                council=getCouncil(surplus);}
        }

        return new EffectSurplus(resources,points,council);
    }

    private static EffectAction getEffectAction (JsonObject immediate){
        ActionType target=null;
        DvptCardType type=null;
        Integer force=null;
        ArrayList<Resource> discount=new ArrayList<Resource>();

        //get JsonObject surplus from immediate
        JsonObject actions=immediate.getAsJsonObject("action");

        //get keys from actions(target || type || force || discount) that identify all the different keys of action
        ArrayList<String> actionKeys = DvptCardParser.getKeys(actions);

        for (String actionKey: actionKeys) {
            if(actionKey.equals("target")){
                target= ActionType.valueOf(actions.get("target").getAsString());}
            if(actionKey.equals("type")){
                type=DvptCardType.valueOf(actions.get("type").getAsString());}
            if(actionKey.equals("force")){
                force=actions.get("force").getAsInt();}
            if(actionKey.equals("discount")){
                discount=getDiscount(actions);}

        }
    return new EffectAction(target,type,force,discount);
    }

    private static EffectPermanentAction getEffectPermanentAction(JsonObject permanent){

        ActionType target=null;
        DvptCardType type=null;
        Integer forceBonus=0;
        ArrayList<Resource> discount=new ArrayList<Resource>();

        //get keys from permanent (minForce || type || surplus || conversion || action || discount || penality)
        ArrayList<String> permanentKeys = DvptCardParser.getKeys(permanent);

        //get effectSurplus and effectAction..so i have both 'items' to create the cost
        for (String permanentKey : permanentKeys) {

            if (permanentKey.equals("target")) {
                target = ActionType.valueOf(permanent.get("target").getAsString());
            }

            if (permanentKey.equals("type")) {
                type = DvptCardType.valueOf(permanent.get("type").getAsString());
            }

            if (permanentKey.equals("forceBonus")) {
                forceBonus = permanent.get("forceBonus").getAsInt();
            }

            if (permanentKey.equals("discount")) {
                discount = getDiscount(permanent);
            }
        }

        return new EffectPermanentAction(target,type,forceBonus,discount);

    }

    private static ArrayList<EffectConversion> getEffectConversion(JsonObject permanent){
        ArrayList<EffectConversion> conversions =new ArrayList<EffectConversion>();

        //extract conversion array from JSon Object permanent
        JsonArray conversion=permanent.getAsJsonArray("conversion");

        //consider all the possible conversion
        for(int i=0;i<conversion.size();i++){
            JsonObject ObjectConversion = conversion.get(i).getAsJsonObject();
            EffectSurplus from=getSurplus(ObjectConversion.get("from").getAsJsonObject());
            EffectSurplus to=getSurplus(ObjectConversion.get("to").getAsJsonObject());
            conversions.add(new EffectConversion(from,to));
        }
        return conversions;
    }

    private static ArrayList<Resource> getResourceSurplus(JsonObject surplus){
        return getResourceCost(surplus);
    }

    private static ArrayList<Resource> getDiscount(JsonObject actions){

        ArrayList<Resource> discount=new ArrayList<Resource>();
        //extract arrayDiscount from JsonObject
        JsonArray arrayDiscount = actions.getAsJsonArray("discount");

        for (int j = 0; j < arrayDiscount.size(); j++) {
            //extract JsonObject ObjectDiscount from actions
            JsonObject ObjectDiscount = arrayDiscount.get(j).getAsJsonObject();

            //foreach key of ObjectDiscount get his value
            for (String discountKey:getKeys(ObjectDiscount)) {
                if(discountKey.equals("coins")){
                    discount.add(new Resource(ResourceType.Coins,ObjectDiscount.get("coins").getAsInt()));
                }
                if(discountKey.equals("wood")){
                    discount.add(new Resource(ResourceType.Wood,ObjectDiscount.get("wood").getAsInt()));
                }
                if(discountKey.equals("stones")){
                    discount.add(new Resource(ResourceType.Stones,ObjectDiscount.get("stones").getAsInt()));
                }
                if(discountKey.equals("servants")){
                    discount.add(new Resource(ResourceType.Servants,ObjectDiscount.get("servants").getAsInt()));
                }

            }

        }
        return discount;
    }

    public static ArrayList<Point> getPoints (JsonObject surplus){
        ArrayList<Point> points=new ArrayList<Point>();   //this arrayList contains all the points gained for the card

        //extract JsonObject points from surplus
        JsonObject pointsObject = surplus.getAsJsonObject("points");

        //get keys from points(military || victory || faith || multiplier)
        ArrayList<String> pointsKeys = DvptCardParser.getKeys(pointsObject);

        for (String pointsKey:pointsKeys) {

            if(pointsKey.equals("military"))
                points.add(new Point(PointType.Military,pointsObject.get("military").getAsInt(),null));

            if(pointsKey.equals("victory"))
                points.add(new Point(PointType.Victory,pointsObject.get("victory").getAsInt(),null));

            if(pointsKey.equals("faith"))
                points.add(new Point(PointType.Faith,pointsObject.get("faith").getAsInt(),null));

            if(pointsKey.equals("multiplier"))
                points.add(new Point(null,null,DvptCardParser.getMultiplier(pointsObject)));
        }

        return points;
    }

    private static Multiplier getMultiplier(JsonObject pointsObject){
        MultipliedType what=null;
        ResultType result=null;
        Float coefficient=null;

        //extract JsonObject points from pointsObject
        JsonObject multiplier = pointsObject.getAsJsonObject("multiplier");

        //get keys from multiplier(what || result || coefficient)
        ArrayList<String> multiplierKeys = DvptCardParser.getKeys(multiplier);

        for (String multiplierKey:multiplierKeys) {

            if(multiplierKey.equals("what")){
                what = MultipliedType.valueOf(multiplier.get("what").getAsString());
            }

            if(multiplierKey.equals("result")){
                result = ResultType.valueOf(multiplier.get("result").getAsString());}

            if(multiplierKey.equals("coefficient")){
                coefficient=multiplier.get("coefficient").getAsFloat();}

        }


        return new Multiplier(what,result,coefficient);
    }

    private static Integer getCouncil (JsonObject surplus){
        return surplus.get("council").getAsInt();
    }

    /**
     * all file pars with gson
     * @return
     * @throws IOException
     */

    public static JsonObject getJsonObjectFromFile(String filename) throws IOException, URISyntaxException {
        BufferedReader br = null;
        FileReader fr = null;

        File file = new File(DvptCardParser.class.getResource(filename).toURI());
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

    public static ArrayList<String> getKeys(JsonObject o) {

        ArrayList<String> keys = new ArrayList<String>();

        Set<Map.Entry<String, JsonElement>> entrySet = o.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {

            //Append key
            keys.add(entry.getKey());

        }
        return keys;
    }

}