import org.junit.Assert;
import org.junit.Test;
import server.model.board.Board;
import server.model.board.Period;
import server.model.board.Player;
import server.model.board.TowerSlot;
import server.model.card.ban.BanCard;
import server.model.card.developement.DvptCard;
import server.utility.BanCardParser;
import server.utility.DvptCardParser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by LBARCELLA on 09/06/2017.
 */
public class TestBoard {

    ArrayList<Player> players;

    Board board = new Board(players);

    @Test
    public void initBoardTest(){

        Board board = new Board(players);
    }

    @Test
    public void cleanTowerTest(){

        ArrayList<TowerSlot> ventureTower = board.getVentureTower();

        for (TowerSlot ventureSlot:ventureTower) {

            ventureSlot.setOccupied(true);

        }

        ArrayList<TowerSlot> characterTower = board.getCharacterTower();

        for (TowerSlot characterSlot:characterTower) {

            characterSlot.setOccupied(true);

        }

        board.cleanTowers();

        for (TowerSlot characterSlot:characterTower) {

            assertEquals(false,characterSlot.isOccupied());

        }

    }

    @Test
    public void setBanCardOnCathedralTest() throws IOException, URISyntaxException {

        ArrayList<BanCard> banCards = BanCardParser.parse();


        for(int i=0; i<1000 ; i++){

            ArrayList<BanCard> banCardsToAdd = new ArrayList<>();

            int primo = (int) (Math.random()*100) % 7;
            int secondo = (int) (Math.random()*100) % 7+7;
            int terzo = (int) (Math.random()*100) % 7+14;

            int firstId = banCards.get(primo).getId();
            int secondId = banCards.get(secondo).getId();
            int thirdId = banCards.get(terzo).getId();

            banCardsToAdd.add(banCards.get(primo));
            banCardsToAdd.add(banCards.get(secondo));
            banCardsToAdd.add(banCards.get(terzo));

            board.setBanCardOnCathedral(banCardsToAdd);

            int firstCardId = board.getCathedral().getBanCard(Period.first).getId();
            int secondCardId = board.getCathedral().getBanCard(Period.second).getId();
            int thirdCardId = board.getCathedral().getBanCard(Period.third).getId();

            assertEquals(firstId,firstCardId);
            assertEquals(secondId,secondCardId);
            assertEquals(thirdId,thirdCardId);

        }
    }

    @Test
    public void setDvptCardOnTowerTest() throws IOException, URISyntaxException {

        ArrayList<DvptCard> mazzo = DvptCardParser.parse();

        ArrayList<DvptCard> mazzettoBuilding = new ArrayList<DvptCard>();
        for(int i=0;i<4;i++){
            mazzettoBuilding.add(mazzo.get(i+24));
        }

        ArrayList<DvptCard> mazzettoCharacter = new ArrayList<DvptCard>();
        for(int i=0;i<4;i++){
            mazzettoCharacter.add(mazzo.get(i+48));
        }

        ArrayList<DvptCard> mazzettoVenture = new ArrayList<DvptCard>();
        for(int i=0;i<4;i++){
            mazzettoVenture.add(mazzo.get(i+72));
        }

        board.setDvptCardOnTerritoryTower(mazzo);
        board.setDvptCardOnBuildingTower(mazzettoBuilding);
        board.setDvptCardOnCharacterTower(mazzettoCharacter);
        board.setDvptCardOnVentureTower(mazzettoVenture);

        assertEquals(1,(int)board.getTerritoryTower().get(0).getDvptCard().getId());
        assertEquals(2,(int)board.getTerritoryTower().get(1).getDvptCard().getId());
        assertEquals(3,(int)board.getTerritoryTower().get(2).getDvptCard().getId());
        assertEquals(4,(int)board.getTerritoryTower().get(3).getDvptCard().getId());

        assertEquals(73,(int)board.getVentureTower().get(0).getDvptCard().getId());
        assertEquals(74,(int)board.getVentureTower().get(1).getDvptCard().getId());
        assertEquals(75,(int)board.getVentureTower().get(2).getDvptCard().getId());
        assertEquals(76,(int)board.getVentureTower().get(3).getDvptCard().getId());

        assertEquals(25,(int)board.getBuildingTower().get(0).getDvptCard().getId());
        assertEquals(26,(int)board.getBuildingTower().get(1).getDvptCard().getId());
        assertEquals(27,(int)board.getBuildingTower().get(2).getDvptCard().getId());
        assertEquals(28,(int)board.getBuildingTower().get(3).getDvptCard().getId());

        assertEquals(49,(int)board.getCharacterTower().get(0).getDvptCard().getId());
        assertEquals(50,(int)board.getCharacterTower().get(1).getDvptCard().getId());
        assertEquals(51,(int)board.getCharacterTower().get(2).getDvptCard().getId());
        assertEquals(52,(int)board.getCharacterTower().get(3).getDvptCard().getId());
    }
}



