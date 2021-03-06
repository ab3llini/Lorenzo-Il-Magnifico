import exception.ActionException;
import exception.NoActionPerformedException;
import exception.SixCardsLimitReachedException;
import netobject.action.BoardSectorType;
import netobject.action.SelectionType;
import netobject.action.standard.LeaderCardActivationAction;
import netobject.action.standard.LeaderOnceARoundActivationAction;
import netobject.action.standard.StandardActionType;
import netobject.action.standard.StandardPlacementAction;
import org.junit.Test;
import server.controller.game.MatchController;
import server.model.FinalStanding;
import server.model.GameSingleton;
import server.model.board.*;
import server.model.card.developement.*;
import server.model.card.leader.LeaderCard;
import server.model.effect.ActionType;
import server.model.effect.EffectSurplus;
import server.model.valuable.*;
import server.utility.BonusTilesParser;
import server.utility.DvptCardParser;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by LBARCELLA on 02/06/2017.
 */
public class TestMachController {

    MatchController mc = new MatchController(new ArrayList<Player>(),0);
    Player player1 = new Player("test");

    @Test
    public void applyEffectSurplusTest() throws NoActionPerformedException {

        ArrayList<Resource> resources = new ArrayList<Resource>();
        resources.add(new Resource(ResourceType.Coins,3));
        resources.add(new Resource(ResourceType.Servants,4));
        resources.add(new Resource(ResourceType.Wood,1));
        resources.add(new Resource(ResourceType.Stones,5));

        ArrayList<Point> points = new ArrayList<Point>();
        points.add(new Point(PointType.Military,5,new Multiplier(null,null,(float)0)));
        points.add(new Point(PointType.Faith,6,null));
        points.add(new Point(PointType.Victory,10,null));

        int council = 0;



        mc.applyEffectSurplus(player1,new EffectSurplus(resources,points,0));
        assertEquals(3,(int)player1.getCoins());
        assertEquals(4,(int)player1.getServants());
        assertEquals(1,(int)player1.getWood());
        assertEquals(5,(int)player1.getStones());
        assertEquals(5,(int)player1.getMilitaryPoints());
        assertEquals(6,(int)player1.getFaithPoints());

    }

    @Test
    public void applyImmediateEffectTest() throws IOException, URISyntaxException, ActionException, NoActionPerformedException, InterruptedException {

        ArrayList<DvptCard> mazzo = DvptCardParser.parse();

        mc.applyImmediateEffect(player1,mazzo.get(1));

        assertEquals(1,(int)player1.getWood());

        player1.getPersonalBoard().addTerritoryCard((TerritoryDvptCard) mazzo.get(1));
        mc.applyImmediateEffect(player1,mazzo.get(64));

        assertEquals(2,(int)player1.getVictoryPoints());

        mc.applyImmediateEffect(player1,mazzo.get(86));

        assertEquals(5,(int)player1.getCoins());
        assertEquals(1,(int)player1.getFaithPoints());
    }

    @Test
    public void applyAllCardAndDiscountCostTest() throws IOException, URISyntaxException {


        ArrayList<DvptCard> mazzo = DvptCardParser.parse();

        ArrayList<DvptCard> mazzoCost = new ArrayList<DvptCard>();

        for (DvptCard card : mazzo) {

            if(card.getType()!= DvptCardType.territory)
                mazzoCost.add(card);
        }

        for (DvptCard card : mazzoCost) {

            player1.setCoins(5);
            player1.setServants(5);
            player1.setStones(5);
            player1.setWood(5);
            player1.setMilitaryPoints(5);

            try {
                ArrayList<Resource> discount = new ArrayList<>();
                discount.add(new Resource(ResourceType.Servants,1));
                discount.add(new Resource(ResourceType.Coins,1));
                discount.add(new Resource(ResourceType.Stones,1));
                discount.add(new Resource(ResourceType.Wood,1));

                ArrayList<Resource> test = new ArrayList<>();
                test.add(new Resource(ResourceType.Servants,1));

                ArrayList<Discount> discounts = new ArrayList<>();
                discounts.add(new Discount(discount));
                discounts.add(new Discount(test));
                mc.applyDvptCardCost(player1,card,discounts);
                Cost cost = card.getCost().get(0);

                int coinsMalus = 0;
                int servantsMalus = 0;
                int stoneMalus = 0;
                int woodMalus = 0;

                for (Resource resource : cost.getResources()) {

                    if(resource.getType() == ResourceType.Coins)
                        coinsMalus += resource.getAmount()-1;

                    if(resource.getType() == ResourceType.Wood)
                        woodMalus += resource.getAmount()-1;

                    if(resource.getType() == ResourceType.Servants)
                        servantsMalus += (resource.getAmount()-1);

                    if(resource.getType() == ResourceType.Stones)
                        stoneMalus += resource.getAmount()-1;

                }

                int militaryMalus = cost.getMilitary().getMalus();

                assertEquals(5 - coinsMalus,(int)player1.getCoins());
                assertEquals(5 - stoneMalus,(int)player1.getStones());
                assertEquals(5 - servantsMalus,(int)player1.getServants());
                assertEquals(5 - woodMalus,(int)player1.getWood());
                assertEquals(5 - militaryMalus,(int)player1.getMilitaryPoints());

            }

            catch (ActionException e){
                assertEquals(5,(int)player1.getCoins());
                assertEquals(5,(int)player1.getStones());
                assertEquals(5,(int)player1.getServants());
                assertEquals(5,(int)player1.getWood());
                assertEquals(5,(int)player1.getMilitaryPoints());

            } catch (InterruptedException e) {
                e.printStackTrace();

            } catch (NoActionPerformedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void applyHarvestChainTest() throws IOException, URISyntaxException, SixCardsLimitReachedException, NoActionPerformedException {

        ArrayList<DvptCard> cards = DvptCardParser.parse();

        ArrayList<BonusTile> tiles = BonusTilesParser.parse();

        ArrayList<TerritoryDvptCard> territoryCards = new ArrayList<TerritoryDvptCard>();

        for (DvptCard card: cards) {

            if(card.getType() == DvptCardType.territory)
                territoryCards.add((TerritoryDvptCard)card);
        }

        for(int i=0; i<10000;i++){

            Player testPlayer = new Player("testPlayer");
            Player testPlayer2 = new Player("testPlayer2");

            int random = (int)(Math.random()*100)%5;

            testPlayer2.getPersonalBoard().setBonusTile(tiles.get(random));

            int firstCard = (int)(Math.random()*100)%24;
            int secondCard = (int)(Math.random()*100)%24;
            int thirdCard = (int)(Math.random()*100)%24;
            int fourthCard = (int)(Math.random()*100)%24;
            int fifthCard = (int)(Math.random()*100)%24;
            int sixthCard = (int)(Math.random()*100)%24;


            mc.applyEffectSurplus(testPlayer,territoryCards.get(firstCard).getPermanentEffect().getSurplus());
            mc.applyEffectSurplus(testPlayer,territoryCards.get(secondCard).getPermanentEffect().getSurplus());
            mc.applyEffectSurplus(testPlayer,territoryCards.get(thirdCard).getPermanentEffect().getSurplus());
            mc.applyEffectSurplus(testPlayer,territoryCards.get(fourthCard).getPermanentEffect().getSurplus());
            mc.applyEffectSurplus(testPlayer,territoryCards.get(fifthCard).getPermanentEffect().getSurplus());
            mc.applyEffectSurplus(testPlayer,territoryCards.get(sixthCard).getPermanentEffect().getSurplus());
            mc.applyEffectSurplus(testPlayer,tiles.get(random).getHarvestSurplus());

            testPlayer2.getPersonalBoard().addTerritoryCard(territoryCards.get(firstCard));
            testPlayer2.getPersonalBoard().addTerritoryCard(territoryCards.get(secondCard));
            testPlayer2.getPersonalBoard().addTerritoryCard(territoryCards.get(thirdCard));
            testPlayer2.getPersonalBoard().addTerritoryCard(territoryCards.get(fourthCard));
            testPlayer2.getPersonalBoard().addTerritoryCard(territoryCards.get(fifthCard));
            testPlayer2.getPersonalBoard().addTerritoryCard(territoryCards.get(sixthCard));

            mc.applyHarvestChain(testPlayer2,6);

            assertEquals(testPlayer.getWood(),testPlayer2.getWood());
            assertEquals(testPlayer.getCoins(),testPlayer2.getCoins());
            assertEquals(testPlayer.getServants(),testPlayer2.getServants());
            assertEquals(testPlayer.getStones(),testPlayer2.getStones());

        }
    }

    @Test
    public void placeFamilyMemberTest() throws IOException, URISyntaxException, NoActionPerformedException, InterruptedException {

        ArrayList<DvptCard> mazzo = DvptCardParser.parse();
        ArrayList<BonusTile> tiles = BonusTilesParser.parse();

        for (FamilyMember member: player1.getFamilyMembers()) {

                member.setForce(10);
        }

        player1.setCoins(10);
        player1.setServants(10);
        player1.setStones(10);
        player1.setMilitaryPoints(10);
        player1.setWood(10);
        player1.setMilitaryPoints(10);
        player1.setVictoryPoints(10);
        player1.setFaithPoints(10);

        ArrayList<DvptCard> mazzettoBuilding = new ArrayList<DvptCard>();
        for(int i=0;i<4;i++){
            mazzettoBuilding.add(mazzo.get(i+24));
        }

        ArrayList<DvptCard> mazzettoCharacter = new ArrayList<DvptCard>();
        for(int i=0;i<4;i++){
            mazzettoCharacter.add(mazzo.get(i+52));
        }

        ArrayList<DvptCard> mazzettoVenture = new ArrayList<DvptCard>();
        for(int i=0;i<4;i++){
            mazzettoVenture.add(mazzo.get(i+86));
        }


        player1.getPersonalBoard().setBonusTile(tiles.get(0));

        mc.getMatch().getBoard().setDvptCardOnTerritoryTower(mazzo);
        mc.getMatch().getBoard().setDvptCardOnBuildingTower(mazzettoBuilding);
        mc.getMatch().getBoard().setDvptCardOnCharacterTower(mazzettoCharacter);
        mc.getMatch().getBoard().setDvptCardOnVentureTower(mazzettoVenture);

        StandardPlacementAction standardPlacementAction1 = new StandardPlacementAction(BoardSectorType.TerritoryTower,3, ColorType.Black,1, "");
        try {
            mc.placeFamilyMember(standardPlacementAction1,player1);
            assertEquals(12,(int)player1.getStones());
            assertEquals(12,(int)player1.getWood());
            assertEquals(10,(int)player1.getCoins());
            assertEquals(true,mc.getMatch().getBoard().getTerritoryTower().get(3).isOccupied());
            assertEquals(true,player1.getFamilyMembers().get(0).isBusy());
        }
        catch (ActionException e){
            assertEquals(10,(int)player1.getCoins());
            assertEquals(10,(int)player1.getStones());
            assertEquals(10,(int)player1.getServants());
            assertEquals(10,(int)player1.getWood());
            assertEquals(10,(int)player1.getMilitaryPoints());
            assertEquals(10,(int)player1.getVictoryPoints());
            assertEquals(10,(int)player1.getFaithPoints());
        }

        StandardPlacementAction standardPlacementAction2 = new StandardPlacementAction(BoardSectorType.BuildingTower,3, ColorType.Orange,1, "");
        try {
            DvptCard cartaPresa = mc.getMatch().getBoard().getBuildingTower().get(3).getDvptCard();
            mc.placeFamilyMember(standardPlacementAction2,player1);

            assertEquals(12,(int)player1.getStones());
            assertEquals(8,(int)player1.getCoins());
            assertEquals(10,(int)player1.getWood());
            assertEquals(12,(int)player1.getMilitaryPoints());
            assertEquals(16,(int)player1.getVictoryPoints());
            assertEquals(true,mc.getMatch().getBoard().getBuildingTower().get(3).isOccupied());
            assertEquals(true,player1.getFamilyMembers().get(1).isBusy());
            assertEquals(cartaPresa,player1.getPersonalBoard().getBuildingCards().get(0));
            assertEquals(null,mc.getMatch().getBoard().getBuildingTower().get(3).getDvptCard());
        }
        catch (ActionException e){
            assertEquals(10,(int)player1.getCoins());
            assertEquals(12,(int)player1.getStones());
            assertEquals(10,(int)player1.getServants());
            assertEquals(12,(int)player1.getWood());
            assertEquals(10,(int)player1.getMilitaryPoints());
            assertEquals(10,(int)player1.getVictoryPoints());
            assertEquals(10,(int)player1.getFaithPoints());
        }

    }

    @Test
    public void applyProductionChainTest() throws IOException, URISyntaxException, SixCardsLimitReachedException,NoActionPerformedException {

       player1.getPersonalBoard().setBonusTile(GameSingleton.getInstance().getBonusTiles().get(4));
       player1.getPersonalBoard().addTerritoryCard((TerritoryDvptCard)GameSingleton.getInstance().getSpecificDvptCard(0));
       player1.getPersonalBoard().addCharacterCard((CharacterDvptCard)GameSingleton.getInstance().getSpecificDvptCard(48));
       player1.getPersonalBoard().addVentureCard((VentureDvptCard)GameSingleton.getInstance().getSpecificDvptCard(72));

        for(int i=24; i<48; i++) {

            player1.setCoins(10);
            player1.setServants(10);
            player1.setStones(10);
            player1.setMilitaryPoints(10);
            player1.setWood(10);
            player1.setVictoryPoints(10);
            player1.setFaithPoints(10);

            int coinsMalus = 0;
            int servantsMalus = 0;
            int stonesMalus = 0;
            int militaryMalus = 0;
            int woodMalus = 0;
            int victoryMalus = 0;
            int faithMalus = 0;
            int coinsBonus = 0;
            int servantsBonus = 0;
            int stonesBonus = 0;
            int militaryBonus = 0;
            int woodBonus = 0;
            int victoryBonus = 0;
            int faithBonus = 0;
            int councilBonus = 0;

            DvptCard card = GameSingleton.getInstance().getSpecificDvptCard(i);
            player1.getPersonalBoard().addBuildingCard((BuildingDvptCard) card);

            if (card.getPermanentEffect().getMultiplier() != null) {

                if (GameSingleton.getInstance().getSpecificDvptCard(i).getPermanentEffect().getMultiplier().getResult() == ResultType.coins)
                    coinsBonus = (int) (card.getPermanentEffect().getMultiplier().getCoefficient()) * player1.getSizeMultipliedType(card.getPermanentEffect().getMultiplier().getWhat());

                else
                    victoryBonus = (int) (card.getPermanentEffect().getMultiplier().getCoefficient()) * player1.getSizeMultipliedType(card.getPermanentEffect().getMultiplier().getWhat());

            }

            if (card.getPermanentEffect().getConversion() != null) {

                if (card.getPermanentEffect().getConversion().get(0).getFrom().getResources() != null) {

                    for (Resource resource : card.getPermanentEffect().getConversion().get(0).getFrom().getResources()) {

                        if (resource.getType() == ResourceType.Coins)
                                coinsMalus = resource.getAmount();

                        if (resource.getType() == ResourceType.Servants)
                                servantsMalus = resource.getAmount();

                        if (resource.getType() == ResourceType.Wood)
                                woodMalus = resource.getAmount();

                        if (resource.getType() == ResourceType.Stones)
                                stonesMalus = resource.getAmount();

                    }
                }

                if (card.getPermanentEffect().getConversion().get(0).getFrom().getPoints() != null) {

                     for (Point point : card.getPermanentEffect().getConversion().get(0).getFrom().getPoints()) {

                         if (point.getType() == PointType.Military)
                             militaryMalus = point.getAmount();

                         if (point.getType() == PointType.Faith)
                             faithMalus = point.getAmount();

                         if (point.getType() == PointType.Victory)
                             victoryMalus = point.getAmount();

                        }

                    }

                if (card.getPermanentEffect().getConversion().get(0).getTo().getResources() != null) {

                    for (Resource resource : card.getPermanentEffect().getConversion().get(0).getTo().getResources()) {

                        if (resource.getType() == ResourceType.Coins)
                            coinsBonus = resource.getAmount();

                        if (resource.getType() == ResourceType.Servants)
                            servantsBonus = resource.getAmount();

                        if (resource.getType() == ResourceType.Wood)
                            woodBonus = resource.getAmount();

                        if (resource.getType() == ResourceType.Stones)
                            stonesBonus = resource.getAmount();

                    }

                }

                if (card.getPermanentEffect().getConversion().get(0).getTo().getPoints() != null) {

                    for (Point point : card.getPermanentEffect().getConversion().get(0).getTo().getPoints()) {

                        if (point.getType() == PointType.Military)
                            militaryBonus = point.getAmount();

                        if (point.getType() == PointType.Faith)
                            faithBonus = point.getAmount();

                        if (point.getType() == PointType.Victory)
                            victoryBonus = point.getAmount();
                     }

                }

            }

            if(card.getPermanentEffect().getSurplus() != null) {

                if (card.getPermanentEffect().getSurplus().getResources() != null) {

                    for (Resource resource : card.getPermanentEffect().getSurplus().getResources()) {

                        if (resource.getType() == ResourceType.Coins)
                            coinsBonus = resource.getAmount();

                        if (resource.getType() == ResourceType.Servants)
                            servantsBonus = resource.getAmount();

                        if (resource.getType() == ResourceType.Wood)
                            woodBonus = resource.getAmount();

                        if (resource.getType() == ResourceType.Stones)
                            stonesBonus = resource.getAmount();

                    }

                }

                if (card.getPermanentEffect().getSurplus().getPoints() != null) {

                    for (Point point : card.getPermanentEffect().getSurplus().getPoints()) {

                        if (point.getType() == PointType.Military)
                            militaryBonus = point.getAmount();

                        if (point.getType() == PointType.Faith)
                            faithBonus = point.getAmount();

                        if (point.getType() == PointType.Victory)
                            victoryBonus = point.getAmount();

                    }

                }

                councilBonus = card.getPermanentEffect().getSurplus().getCouncil();

            }


        try {

            mc.applyProductionChain(player1, 6);
            assertEquals(10 + victoryBonus - victoryMalus, (int) player1.getVictoryPoints());
            assertEquals(10 + woodBonus - woodMalus, (int) player1.getWood());
            assertEquals(11 + militaryBonus - militaryMalus, (int) player1.getMilitaryPoints());
            assertEquals(12 + coinsBonus - coinsMalus, (int) player1.getCoins());
            assertEquals(10 + servantsBonus - servantsMalus, (int) player1.getServants());
            assertEquals(10 + stonesBonus - stonesMalus, (int) player1.getStones());
            assertEquals(10 + faithBonus - faithMalus, (int) player1.getFaithPoints());

            }

        catch (ActionException e) {

            assertEquals(12,(int)player1.getCoins());
            assertEquals(10,(int)player1.getStones());
            assertEquals(10,(int)player1.getServants());
            assertEquals(10,(int)player1.getWood());
            assertEquals(11,(int)player1.getMilitaryPoints());
            assertEquals(10,(int)player1.getVictoryPoints());
            assertEquals(10,(int)player1.getFaithPoints());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

            player1.getPersonalBoard().getBuildingCards().remove(0);

        }

    }

    @Test
    public void calculateFinalScoreTest() throws SixCardsLimitReachedException, IOException, URISyntaxException {

        ArrayList<Player> players = new ArrayList<>();
        ArrayList<DvptCard> cards = DvptCardParser.parse();
        players.add(new Player("testA"));
        players.add(new Player("testB"));

        MatchController mc = new MatchController(players,0);

        players.get(0).setMilitaryPoints(10);
        players.get(1).setMilitaryPoints(11);

        players.get(0).getPersonalBoard().addVentureCard((VentureDvptCard) cards.get(85));

        HashMap<Player,Integer> finalScore =mc.calculatesFinalScore();

        assertEquals(2 + cards.get(85).getPermanentEffect().getvPoints(),(int)finalScore.get(players.get(0)));
        assertEquals(5,(int)finalScore.get(players.get(1)));

    }

    @Test
    public void createFinalStandingTest() throws IOException, URISyntaxException, SixCardsLimitReachedException {

        ArrayList<Player> players = new ArrayList<>();
        ArrayList<DvptCard> cards = DvptCardParser.parse();
        players.add(new Player("testA"));
        players.add(new Player("testB"));

        MatchController mc = new MatchController(players,0);

        players.get(0).setMilitaryPoints(10);
        players.get(1).setMilitaryPoints(11);

        players.get(0).getPersonalBoard().addVentureCard((VentureDvptCard) cards.get(85));

        FinalStanding finalStanding = mc.createFinalStanding();

        finalStanding.toNotification();
        System.out.println(finalStanding.toString());

        assertEquals(2 + cards.get(85).getPermanentEffect().getvPoints(),finalStanding.getScore(players.get(0)));

    }



    @Test
    public void DicesWith5PlayersTest() throws IOException, URISyntaxException {

        ArrayList<Player> players = new ArrayList<>();

        players.add(new Player("testA"));
        players.add(new Player("testB"));
        players.add(new Player("testC"));
        players.add(new Player("testD"));
        players.add(new Player("testE"));

        MatchController mc = new MatchController(players,0);

        for(int i=0;i<10000;i++){

            int tot=0;

            mc.rollDices();

            for (Dice dice : mc.getBoardController().getBoard().getDices() ) {

                    tot+=dice.getValue();

            }

            assertTrue(tot >= 14);

        }

    }


}
