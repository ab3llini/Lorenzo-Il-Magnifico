import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.junit.Test;
import server.model.board.Board;
import server.model.board.BonusTile;
import server.model.card.ban.BanCard;
import server.model.card.developement.DvptCard;
import server.model.card.developement.DvptCardType;
import server.model.card.leader.LeaderCard;
import server.utility.BanCardParser;
import server.utility.BonusTilesParser;
import server.utility.DvptCardParser;
import server.utility.LeaderCardParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
/**
 * Created by LBARCELLA on 09/06/2017.
 */
public class ParsersTest {

    @Test
    public void DvptCardParserTest(){

        ArrayList<DvptCard> cards = new ArrayList<>();

        try {

            cards = DvptCardParser.parse();

            assertEquals(96,cards.size());

            int ventureNumber = 0;
            int characterNumber = 0;
            int buildingNumber = 0;
            int territoryNumber = 0;

            for (DvptCard card: cards) {

                if(card.getType() == DvptCardType.venture)
                    ventureNumber++;
                if(card.getType() == DvptCardType.building)
                    buildingNumber++;
                if(card.getType() == DvptCardType.character)
                    characterNumber++;
                if(card.getType() == DvptCardType.territory)
                    territoryNumber++;

                assert (card.getPeriod()>=1 && card.getPeriod()<=3);

                assert (card.getType() == DvptCardType.venture || card.getType() == DvptCardType.building ||
                        card.getType() == DvptCardType.character || card.getType() == DvptCardType.territory);


            }

            assertEquals(24,territoryNumber);
            assertEquals(24,characterNumber);
            assertEquals(24,buildingNumber);
            assertEquals(24,ventureNumber);

            int i=1;

            for (DvptCard card: cards) {

                assertEquals(i,(int)card.getId());

                i++;

            }

        }
        catch (URISyntaxException e1){

            assertEquals(0,cards.size());

        }
        catch (IOException e2){

            assertEquals(0,cards.size());

        }
    }

    @Test
    public void LeaderCardParserTest() {

        ArrayList<LeaderCard> leaderCards = new ArrayList<>();

        try {

            leaderCards = LeaderCardParser.parse();

            assertEquals(20, leaderCards.size());

            int i=1;

            for (LeaderCard card: leaderCards) {

                assertEquals(i,(int)card.getId());

                i++;

            }

        } catch (URISyntaxException e1) {

            assertEquals(0, leaderCards.size());

        } catch (IOException e2) {

            assertEquals(0, leaderCards.size());

        }
    }

    @Test
    public void BanCardParserTest(){

        ArrayList<BanCard> banCards = new ArrayList<>();

        try {

            banCards = BanCardParser.parse();

            assertEquals(21, banCards.size());

            int i=1;

            for (BanCard card: banCards) {

                assertEquals(i,(int)card.getId());

                i++;

                if(card.getId()<= 7)
                    assertEquals(1,(int)card.getPeriod());

                if(card.getId()>7 && card.getId()<= 14)
                    assertEquals(2,(int)card.getPeriod());

                if(card.getId()> 14)
                    assertEquals(3,(int)card.getPeriod());

            }

        } catch (URISyntaxException e1) {

            assertEquals(0, banCards.size());

        } catch (IOException e2) {

            assertEquals(0, banCards.size());

        }
    }

    @Test
    public void BonusTilesParserTest(){


        ArrayList<BonusTile> bonusTiles = new ArrayList<>();

        try {

            bonusTiles = BonusTilesParser.parse();

            assertEquals(5, bonusTiles.size());

            int i=1;

            for (BonusTile card: bonusTiles) {

                assertEquals(i,(int)card.getId());

                i++;

                assert (card.getHarvestMinForce() >= 0);

                assert (card.getProductionMinForce() >= 0);

            }

        } catch (URISyntaxException e1) {

            assertEquals(0, bonusTiles.size());

        } catch (IOException e2) {

            assertEquals(0, bonusTiles.size());

        }


    }
    
}
