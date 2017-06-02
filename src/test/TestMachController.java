import exception.ActionException;
import netobject.action.BoardSectorType;
import netobject.action.SelectionType;
import netobject.action.standard.StandardActionType;
import netobject.action.standard.StandardPlacementAction;
import org.junit.Test;
import server.controller.game.MatchController;
import server.model.board.BonusTile;
import server.model.board.ColorType;
import server.model.board.FamilyMember;
import server.model.board.Player;
import server.model.card.developement.Cost;
import server.model.card.developement.DvptCard;
import server.model.card.developement.DvptCardType;
import server.model.card.developement.TerritoryDvptCard;
import server.model.effect.EffectSurplus;
import server.model.valuable.*;
import server.utility.BonusTilesParser;
import server.utility.DvptCardParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by LBARCELLA on 02/06/2017.
 */
public class TestMachController {

    MatchController mc = new MatchController(new ArrayList<Player>(),0);
    Player player1 = new Player("test");

    @Test
    public void applyEffectSurplusTest() {

        ArrayList<Resource> resources = new ArrayList<Resource>();
        resources.add(new Resource(ResourceType.Coins,3));
        resources.add(new Resource(ResourceType.Servants,4));
        resources.add(new Resource(ResourceType.Wood,1));
        resources.add(new Resource(ResourceType.Stones,5));

        ArrayList<Point> points = new ArrayList<Point>();
        points.add(new Point(PointType.Military,5,new Multiplier(null,null,null)));
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
    public void applyImmediateEffectTest() throws IOException, URISyntaxException, ActionException {

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
    public void applyAllCardCostTest() throws IOException, URISyntaxException {


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
                mc.applyDvptCardCost(player1,card, SelectionType.First);
                Cost cost = card.getCost().get(0);

                int coinsMalus = 0;
                int servantsMalus = 0;
                int stoneMalus = 0;
                int woodMalus = 0;

                for (Resource resource : cost.getResources()) {

                    if(resource.getType() == ResourceType.Coins)
                        coinsMalus += resource.getAmount();

                    if(resource.getType() == ResourceType.Wood)
                        woodMalus += resource.getAmount();

                    if(resource.getType() == ResourceType.Servants)
                        servantsMalus += resource.getAmount();

                    if(resource.getType() == ResourceType.Stones)
                        stoneMalus += resource.getAmount();

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

            }
        }
    }


    @Test
    public void placeFamilyMemberTest() throws IOException, URISyntaxException {

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

        StandardPlacementAction standardPlacementAction = new StandardPlacementAction(StandardActionType.FamilyMemberPlacement, BoardSectorType.TerritoryTower,3, ColorType.Black,1,SelectionType.First);
        try {
            mc.placeFamilyMember(standardPlacementAction,player1);
            assertEquals(12,(int)player1.getStones());
            assertEquals(12,(int)player1.getWood());
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

    }
}
