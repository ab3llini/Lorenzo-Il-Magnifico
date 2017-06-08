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

    private DvptCardParser(){
    }


    /**
     * this method parse dvptCards. take a json file and returns an arrayList with all dvptCards
     *
     * @return
     */

    public static ArrayList<DvptCard> parse() throws IOException, URISyntaxException {

        ArrayList<DvptCard> allCards = new ArrayList<DvptCard>();    //this arrayList contains all the dvpmCard already parsed
        String name = null;
        Integer period = 0;
        DvptCardType type = null;
        ArrayList<Cost> cost = new ArrayList<Cost>();


        //get a JsonObject from the file stored in resource that contains all the cards in json
        JsonObject cardsSet = Loader.getJsonObjectFromFile("json/cards.json");

        //extract one by one all the card from cardsSet and create a Card object from every single card in json file
        for (String cardId : Json.getObjectKeys(cardsSet)) {

            //initialize immediateEffect to default value in order to avoid null pointers
            ImmediateEffect immediateEffect = new ImmediateEffect(new EffectSurplus(new ArrayList<>(),new ArrayList<>(),0),new EffectAction(ActionType.unknown,null,0,new ArrayList<>()));
            PermanentEffect permanentEffect = new PermanentEffect(0,0,new EffectSurplus(new ArrayList<>(),new ArrayList<>(),0),null,null,new EffectPermanentAction(ActionType.unknown,null,0,new ArrayList<>()),false);

            //extract one single card
            JsonObject card = cardsSet.getAsJsonObject(cardId);

            //get card keys in json representation
            ArrayList<String> keys = Json.getObjectKeys(card);

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
                    for (String effectKey : Json.getObjectKeys(effect)) {
                        if (effectKey.equals("immediate")){
                            immediateEffect = getImmediateEffect(effect);}

                        if (effectKey.equals("permanent")) {
                            permanentEffect = getPermanentEffect(effect);
                        }
                    }
                }

            }
            //add the new card to the arrayList, choose the correct constructor depending on the card type
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

    public static Integer getPeriod(JsonObject card) {
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
            ArrayList<String> costoKeys = Json.getObjectKeys(costo);

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
            }
            //create the cost and added to the arrayList
            cost.add(new Cost(resourceCost, militaryCost));
        }

        return cost;
    }

    private static ImmediateEffect getImmediateEffect(JsonObject effect) {

        //initialize effect surplus and effect Action in order to avoid null pointers
        EffectSurplus effectSurplus = new EffectSurplus(new ArrayList<Resource>(),new ArrayList<Point>(), 0);
        EffectAction effectAction = new EffectAction(ActionType.unknown,null,0,new ArrayList<Resource>());

        //extract immediate from JsonObject
        JsonObject immediate = effect.getAsJsonObject("immediate");

        //get keys from immediate (surplus || Action)
        ArrayList<String> effectKeys = Json.getObjectKeys(immediate);

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

        Integer minForce=0;
        Integer vpoints= 0;
        EffectSurplus surplus=new EffectSurplus(new ArrayList<Resource>(), new ArrayList<Point>(), 0 );
        Multiplier multiplier=null;
        ArrayList<EffectConversion> conversion=null;
        EffectPermanentAction action=new EffectPermanentAction(ActionType.unknown,null,0,null);
        Boolean penality=false;

        //extract permanent from JsonObject effect
        JsonObject permanent = effect.getAsJsonObject("permanent");

        //get keys from permanent (minforce || type || surplus || conversion || Action || discount || penality)
        ArrayList<String> effectKeys = Json.getObjectKeys(permanent);

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
        ArrayList<String> resourceKeys = Json.getObjectKeys(resources);

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
        ArrayList<String> militaryKeys = Json.getObjectKeys(military);

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

    public static EffectSurplus getSurplus(JsonObject surplus){

        ArrayList<Resource> resources=new ArrayList<Resource>();      //ArrayList to save surplus in resources
        ArrayList<Point> points=new ArrayList<Point>();               //ArrayList to save surplus in points
        Integer council=0;

        //get keys from surplus(resources || points || council) that identify all the different kind of surplus
        ArrayList<String> surplusKeys = Json.getObjectKeys(surplus);

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

        ActionType target= ActionType.unknown;
        DvptCardType type=null;
        Integer force=0;
        ArrayList<Resource> discount = new ArrayList<Resource>();

        //get JsonObject surplus from immediate
        JsonObject actions=immediate.getAsJsonObject("action");

        //get keys from actions(target || type || force || discount) that identify all the different keys of Action
        ArrayList<String> actionKeys = Json.getObjectKeys(actions);

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

        //get keys from permanent (minForce || type || surplus || conversion || Action || discount || penality)
        ArrayList<String> permanentKeys = Json.getObjectKeys(permanent);

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

            //add new conversion to the conversion array
            conversions.add(new EffectConversion(from,to));
        }
        return conversions;
    }

    public static ArrayList<Resource> getResourceSurplus(JsonObject surplus){
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
            for (String discountKey:Json.getObjectKeys(ObjectDiscount)) {
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
        ArrayList<String> pointsKeys = Json.getObjectKeys(pointsObject);

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
        ArrayList<String> multiplierKeys = Json.getObjectKeys(multiplier);

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