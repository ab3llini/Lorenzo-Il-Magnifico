import exception.*;
import org.junit.Test;
import static org.junit.Assert.*;

import server.model.Match;
import server.model.board.ColorType;
import server.model.board.FamilyMember;
import server.model.board.Player;
import server.model.valuable.PointType;
import server.model.valuable.ResourceType;

/**
 * Created by LBARCELLA on 02/06/2017.
 */
public class TestPlayer {

    Player player = new Player("testPlayer");

    @Test
    public void vPointsTest(){

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
    public void militaryPointsTest(){

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
                familyMember = player.getFamilyMember(ColorType.Nautral);
            }

            if(j%2 == 0){
                assertEquals(ColorType.Black,familyMember.getColor());
            }
            else {
                assertEquals(ColorType.Nautral,familyMember.getColor());
            }
        }
    }
}
