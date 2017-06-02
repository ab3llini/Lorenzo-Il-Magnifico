import exception.NotEnoughCoinsException;
import exception.NotEnoughMilitaryPointsException;
import exception.NotEnoughResourcesException;
import exception.NotEnoughVictoryPointsException;
import org.junit.Test;
import static org.junit.Assert.*;
import server.model.board.Player;
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
    public void familyMembersSuitable() {

        assertEquals(player.getFamilyMembers(),player.getFamilyMembersSuitable(0));
        assertNotEquals(player.getFamilyMembers(),player.getFamilyMembersSuitable(2));
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
    public void addGenericTest() {

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
    public void subtractGenericTest() {

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


}
