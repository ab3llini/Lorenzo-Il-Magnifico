import exception.*;
import org.junit.Test;
import static org.junit.Assert.*;

import server.model.board.ColorType;
import server.model.board.FamilyMember;
import server.model.board.Player;
import server.model.valuable.Point;
import server.model.valuable.PointType;
import server.model.valuable.ResourceType;
import server.model.valuable.RollbackClass;

/**
 * Created by LBARCELLA on 02/06/2017.
 */
public class TestPlayer {

    Player player = new Player("testPlayer");

    @Test
    public void addvPointsTest(){

        for(int i=0;i<1000;i++){

            player.setVictoryPoints(0);
            int random = (int)Math.random()*100;
            player.addVictoryPoints(random);
            assertEquals(random, (int)player.getVictoryPoints());

            try {
                player.subtractVictoryPoints(20);
                assertEquals(random-20,(int)player.getVictoryPoints());
            }
            catch (NotEnoughVictoryPointsException e){
                assertEquals(random,(int)player.getVictoryPoints());
            }

        }

    }

    @Test
    public void addMilitaryPointsTest(){

        for(int i=0;i<1000;i++){

            player.setMilitaryPoints(0);
            int random = (int)Math.random()*100;
            player.addMilitaryPoints(random);
            assertEquals(random, (int)player.getMilitaryPoints());

            try {
                player.subtractMilitaryPoints(20);
                assertEquals(random-20,(int)player.getMilitaryPoints());
            }
            catch (NotEnoughMilitaryPointsException e){
                assertEquals(random,(int)player.getMilitaryPoints());
            }

        }

    }

    @Test
    public void subtractCoinsTest() {

        for(int i=0;i<1000;i++){

            int random = (int)Math.random()*100;
            int random2 = (int)Math.random()*100;
            player.setCoins(random);

            try{
                player.subtractCoins(random2);
                assertEquals(random - random2, (int)player.getCoins());
            }
            catch (NotEnoughCoinsException e){

                assertEquals(random,(int)player.getCoins());
            }

        }
    }

    @Test
    public void familyMembersSuitableTest() {

        assertEquals(player.getFamilyMembers(),player.getFamilyMembersSuitable(0));
        assertNotEquals(player.getFamilyMembers(),player.getFamilyMembersSuitable(2));
    }

    @Test
    public void getFamilyMemberTest()  {

        for(int i=0; i<100 ; i++){

            if((int)(Math.random()*100)%2 == 0){
                player.getFamilyMembers().get(0).setBusy(false);
            }
            else{
                player.getFamilyMembers().get(0).setBusy(true);
            }

           try{
               player.getFamilyMember(player.getFamilyMembers().get(0).getColor());
               assertEquals(false,player.getFamilyMembers().get(0).isBusy());
           }
           catch (FamilyMemberAlreadyInUseException e){
                assertEquals(true,player.getFamilyMembers().get(0).isBusy());
           }
        }
    }

    @Test
    public void isDisabledTest() {

        boolean bool;

        for(int i=0;i<1000;i++){

            if((Math.random()*100)%2 == 0)
                bool = true;
            else
                bool = false;

            player.setDisabled(bool);

            assertEquals(player.isDisabled(),bool);

        }
    }

    @Test
    public void addGenericResourceTest() {

        ResourceType resourceType = ResourceType.Wood;

        for(int i=0;i<2000;i++){

            if(i%3 == 0)
                resourceType = ResourceType.Stones;

            if(i%4 == 0)
                resourceType = ResourceType.Servants;

            if(i%5 == 0)
                resourceType = ResourceType.Coins;

            int random = (int)Math.random()*100;

            player.addGenericResource(resourceType,random);

            assertEquals(random,(int)player.getResource(resourceType));
        }
    }

    @Test
    public void subtractGenericResourceTest() {

        ResourceType resourceType = ResourceType.Wood;
        player.setWood(20);
        for(int i=0;i<2000;i++){

            if(i%3 == 0){
                resourceType = ResourceType.Stones;
                player.setStones(20);}

            if(i%4 == 0){
                resourceType = ResourceType.Servants;
                player.setServants(20);}

            if(i%5 == 0){
                resourceType = ResourceType.Coins;
                player.setCoins(20);}

            int random = (int)Math.random()*100;

            try{
                player.subtractGenericResource(resourceType,random);

                assertEquals(20 - random,(int)player.getResource(resourceType));
            }
            catch (NotEnoughResourcesException e){

                assertEquals(20,(int)player.getResource(resourceType));
            }
        }
    }

    @Test
    public void addGenericPointTest() {

        PointType pointType = PointType.Victory;

        for(int i=0;i<2000;i++){

            if(i%3 == 0)
                pointType = PointType.Faith;

            if(i%4 == 0)
                pointType = PointType.Military;

            int random = (int)Math.random()*100;

            player.addGenericPoint(pointType,random);

            assertEquals(random,(int)player.getPoints(pointType));
        }
    }

    @Test
    public void subtractGenericPointTest() {

        PointType pointType = PointType.Victory;
        player.setVictoryPoints(20);

        for(int i=0;i<2000;i++){

            if(i%3 == 0){
                pointType = PointType.Faith;
                player.setFaithPoints(20);}

            if(i%4 == 0){
                pointType = PointType.Military;
                player.setMilitaryPoints(20);}


            int random = (int)Math.random()*100;

            try{
                player.subtractGenericPoint(pointType,random);

                assertEquals(20 - random,(int)player.getPoints(pointType));
            }
            catch (NotEnoughPointsException e){

                assertEquals(20,(int)player.getPoints(pointType));
            }
        }
    }

    @Test
    public void freeFamilyMemberTest(){

        Player player = new Player("test");

        for (FamilyMember familyMember: player.getFamilyMembers()) {

            familyMember.setBusy(true);

        }

        player.freeFamilyMembers();

        for (FamilyMember familyMember: player.getFamilyMembers()) {

            assertEquals(false,familyMember.isBusy());

        }
    }

    @Test
    public void getFamilyMemberByColorTest() throws FamilyMemberAlreadyInUseException {

        for(int i=0; i<100 ; i++){

            int j=(int)(Math.random()*100)%2;

            FamilyMember familyMember;

            if(j%2 == 0){

                familyMember = player.getFamilyMember(ColorType.Black);

            }
            else{

                familyMember = player.getFamilyMember(ColorType.Neutral);

            }

            if(j%2 == 0){

                assertEquals(ColorType.Black,familyMember.getColor());

            }
            else {

                assertEquals(ColorType.Neutral,familyMember.getColor());

            }
        }
    }

    @Test
    public void rollbackTest() {

        Player player = new Player("test");

        int j=(int)(Math.random()*100)%2;
        int a=(int)(Math.random()*100)%2;
        int v=(int)(Math.random()*100)%2;
        int s=(int)(Math.random()*100)%2;
        int w=(int)(Math.random()*100)%2;

        player.setWood(j);
        player.setCoins(a);
        player.setServants(v);
        player.setFaithPoints(s);
        player.setMilitaryPoints(w);

        RollbackClass rollbackClass = new RollbackClass(player);

        for(int i=0; i<1000; i++){

            ResourceType resourceType;

            if(i%2 == 0)
                resourceType = ResourceType.Wood;

            else
                resourceType = ResourceType.Coins;

            player.addGenericResource(resourceType,i);

            PointType pointType;

            if(i%7 == 0)
                pointType = PointType.Military;
            else if(i%3 == 0)
                pointType = PointType.Faith;
            else
                pointType = PointType.Victory;

            player.addGenericPoint(pointType,i);

        }

        player.rollback(rollbackClass);

        assertEquals(j,(int)player.getWood());
        assertEquals(a,(int)player.getCoins());
        assertEquals(v,(int)player.getServants());
        assertEquals(s,(int)player.getFaithPoints());
        assertEquals(w,(int)player.getMilitaryPoints());

    }

    @Test
    public void hasEnoughPointAndResourcesTest() {

        Player player = new Player("test");

        for(int i=0; i<1000; i++){

            player.setMilitaryPoints(i);
            player.setFaithPoints(i);
            player.setVictoryPoints(i);


            assertEquals(true,player.hasEnoughPoints(PointType.Victory,i-1));
            assertEquals(false,player.hasEnoughPoints(PointType.Faith,i+1));
            assertEquals(true,player.hasEnoughPoints(PointType.Military,i-1));

            player.setCoins(i);
            player.setWood(i);
            player.setServants(i);
            player.setStones(i);

            assertEquals(false,player.hasEnoughResources(ResourceType.Coins,i+1));
            assertEquals(true,player.hasEnoughResources(ResourceType.Wood,i-1));
            assertEquals(true,player.hasEnoughResources(ResourceType.Servants,i-1));
            assertEquals(false,player.hasEnoughResources(ResourceType.Stones,i+1));

        }

    }
}
