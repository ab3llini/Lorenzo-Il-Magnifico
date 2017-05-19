package server.model.board;

import server.model.card.ban.BanCard;
import server.model.card.developement.*;
import server.utility.BoardConfigParser;

import java.util.ArrayList;

import static server.model.board.Period.next;

/**
 * Created by LBARCELLA on 19/05/2017.
 */
public class Board {
    private ArrayList<TowerSlot> territoryTower;
    private ArrayList<TowerSlot> characterTower;
    private ArrayList<TowerSlot> buildingTower;
    private ArrayList<TowerSlot> ventureTower;
    private Cathedral cathedral;
    private CouncilPalace councilPalace;
    private ArrayList<Dice> dices;
    private ActionArea productionArea;
    private ActionArea harvestArea;
    private Market market;

    public Board() {
        this.territoryTower = BoardConfigParser.getTower(DvptCardType.venture);
        this.characterTower = BoardConfigParser.getTower(DvptCardType.character);
        this.buildingTower = BoardConfigParser.getTower(DvptCardType.building);
        this.ventureTower = BoardConfigParser.getTower(DvptCardType.venture);
        this.cathedral = BoardConfigParser.getCathedral();
        this.councilPalace = BoardConfigParser.getCouncilPalace();
        this.productionArea = BoardConfigParser.getProductionActionArea();
        this.harvestArea = BoardConfigParser.getHarvestActionArea();
        this.market = BoardConfigParser.getMarket();
        this.dices = new ArrayList<Dice>();
    }


    public ArrayList<TowerSlot> getTerritoryTower() {
        return territoryTower;
    }

    public ArrayList<TowerSlot> getBuildingTower() {
        return buildingTower;
    }

    public ArrayList<TowerSlot> getCharacterTower() {
        return characterTower;
    }

    public ArrayList<TowerSlot> getVentureTower() {
        return ventureTower;
    }

    /**
     * this method insert territory dvptCard in the territory tower.
     * it enters an array of 4 cards and fills the tower 'from botton to top'
     * @param cards
     */
    public void setDvptCardOnTerritoryTower(ArrayList<DvptCard> cards){
        Integer i=0;
        for (TowerSlot towerSlot: this.territoryTower) {
            towerSlot.setDvptCard(cards.get(i));
            i++;
        }

    }

    /**
     * this method insert building dvptCard in the building tower.
     * it enters an array of 4 cards and fills the tower 'from botton to top'
     * @param cards
     */

    public void setDvptCardOnBuildingTower(ArrayList<DvptCard> cards){
        Integer i=0;
        for (TowerSlot towerSlot: this.buildingTower) {
            towerSlot.setDvptCard(cards.get(i));
            i++;
        }

    }

    /**
     * this method insert character dvptCard in the character tower.
     * it enters an array of 4 cards and fills the tower 'from botton to top'
     * @param cards
     */
    public void setDvptCardOnCharacterTower(ArrayList<DvptCard> cards){
        Integer i=0;
        for (TowerSlot towerSlot: this.characterTower) {
            towerSlot.setDvptCard(cards.get(i));
            i++;
        }

    }

    /**
     * this method insert venture dvptCard in the venture tower.
     * it enters an array of 4 cards and fills the tower 'from botton to top'
     * @param cards
     */
    public void setDvptCardOnVentureTower(ArrayList<DvptCard> cards){
        Integer i=0;
        for (TowerSlot towerSlot: this.ventureTower) {
            towerSlot.setDvptCard(cards.get(i));
            i++;
        }

    }

    /**
     * this method insert ban card in the cathedral.
     * it enters an array of 3 ban cards and put them in order from first period to third
     * @param banCards
     */
    public void setBanCardOnCathedral(ArrayList<BanCard> banCards){
        Period period = Period.first;
        for (BanCard banCard: banCards) {
            this.cathedral.setBanCard(period,banCard);
            System.out.println(period);
            period = next(period);
        }
    }

    public Cathedral getCathedral() {
        return this.cathedral;
    }

    public ActionArea getHarvestArea() {
        return this.harvestArea;
    }

    public ActionArea getProductionArea() {
        return this.productionArea;
    }

    public ArrayList<Dice> getDices() {
        return this.dices;
    }

    public void setDices(ArrayList<Dice> dices) {
        this.dices = dices;
    }

    public CouncilPalace getCouncilPalace() {
        return this.councilPalace;
    }

    public Market getMarket() {
        return this.market;
    }
}
