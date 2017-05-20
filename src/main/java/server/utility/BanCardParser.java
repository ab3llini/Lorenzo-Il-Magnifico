package server.utility;

import com.google.gson.JsonObject;
import server.model.card.ban.*;
import server.model.card.developement.DvptCardType;
import server.model.valuable.Point;
import server.model.valuable.Resource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static server.utility.DvptCardParser.getPeriod;
import static server.utility.DvptCardParser.getPoints;
import static server.utility.DvptCardParser.getResourceCost;

/**
 * Created by LBARCELLA on 20/05/2017.
 */
public class BanCardParser {

    /**
     * this method parse banCards and return an arrayList with all of them
     */

    public static ArrayList<BanCard> parse() throws IOException, URISyntaxException {

        ArrayList<BanCard> allBanCards = new ArrayList<BanCard>();
        Integer period = 0;
        BanType banType = null;
        DvptCardType cardType = null;
        ArrayList<Resource> resources = null;
        ArrayList<Point> points = null;
        String specialEffect = "";
        EffectVictoryMalus effectVictoryMalus = null;
        EffectDiceMalus effectDiceMalus = null;

        //get a JsonObject from the file stored in resource that contains all the banCards in json
        JsonObject cardsSet = Loader.getJsonObjectFromFile("/json/banCards.json");

        //extract one by one all the ban cards from cardsSet and create a banCard object from every single banCard in json file
        for (String banCardId : Json.getObjectKeys(cardsSet)) {

            //extract one single banCard
            JsonObject banCard = cardsSet.getAsJsonObject(banCardId);

            //get card keys in json representation
            ArrayList<String> keys = Json.getObjectKeys(banCard);

            //foreach key extract his value
            for (String key : keys) {

                if (key.equals("period")) {
                    period = getPeriod(banCard);
                }

                if (key.equals("type")) {
                    banType = BanType.valueOf(banCard.get("type").getAsString());
                }

                if (key.equals("effect")) {
                    JsonObject effect = banCard.getAsJsonObject("effect");

                    for (String effectKey: Json.getObjectKeys(effect)) {

                        if(effectKey.equals("malus")){
                            JsonObject malus = effect.getAsJsonObject("malus");

                            for (String malusKey: Json.getObjectKeys(malus)) {
                                if(malusKey.equals("resources"))
                                    resources = getResourceCost(malus);
                                if(malusKey.equals("points"))
                                    points = getPoints(malus);
                            }
                        }

                        if(effectKey.equals("specialEffect"))
                            specialEffect = effect.get("specialEffect").getAsString();

                        if(effectKey.equals("noVictoryPointsMalus")){
                            JsonObject malusType = effect.getAsJsonObject("noVictoryPointsMalus");
                            cardType = DvptCardType.valueOf(malusType.get("target").getAsString());
                            }

                        if(effectKey.equals("victoryMalus")){
                            effectVictoryMalus = getEffectVictoryMalus(effect);
                        }

                        if(effectKey.equals("diceMalus")) {
                            effectDiceMalus = getEffectDiceMalus(effect);
                        }
                    }
                }

            }
            //add the new card to the arrayList, choose the correct constructor depending on the banCard type
            if (banType == BanType.dice) {
                allBanCards.add(new DiceBanCard(Integer.parseInt(banCardId), period, effectDiceMalus));
            }

            if (banType == BanType.noVictoryPoints) {
                allBanCards.add(new NoVictoryBanCard(Integer.parseInt(banCardId), period, cardType));
            }

            if (banType == BanType.special) {
                allBanCards.add(new SpecialBanCard(Integer.parseInt(banCardId), period, specialEffect));
            }

            if (banType == BanType.valuableMalus) {
                allBanCards.add(new ValuableBanCard(Integer.parseInt(banCardId), period, resources, points));
            }

            if (banType == BanType.victoryMalus) {
                allBanCards.add(new VictoryMalusBanCard(Integer.parseInt(banCardId), period, effectVictoryMalus));
            }

        }

        return allBanCards;
    }

    private static EffectDiceMalus getEffectDiceMalus (JsonObject effect){
        //TODO
        return null;
    }

    private static EffectVictoryMalus getEffectVictoryMalus (JsonObject effect){
        //TODO
        return null;
    }

}
