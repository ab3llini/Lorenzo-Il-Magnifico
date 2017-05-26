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

        //create the board configured from file
        this.territoryTower = BoardConfigParser.getTower(DvptCardType.territory);
        this.characterTower = BoardConfigParser.getTower(DvptCardType.character);
        this.buildingTower = BoardConfigParser.getTower(DvptCardType.building);
        this.ventureTower = BoardConfigParser.getTower(DvptCardType.venture);
        this.cathedral = BoardConfigParser.getCathedral();
        this.councilPalace = BoardConfigParser.getCouncilPalace();
        this.productionArea = BoardConfigParser.getProductionActionArea();
        this.harvestArea = BoardConfigParser.getHarvestActionArea();
        this.market = BoardConfigParser.getMarket();

        //create the three dices
        this.dices = new ArrayList<Dice>();
        dices.add(new Dice(ColorType.Black));
        dices.add(new Dice(ColorType.White));
        dices.add(new Dice(ColorType.Orange));
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

    public ArrayList<TowerSlot> getTower(DvptCardType towerType){

        if(towerType == DvptCardType.building){
            return this.buildingTower;}

        if(towerType == DvptCardType.character){
            return this.characterTower;}

        if(towerType == DvptCardType.territory){
            return this.territoryTower;}

        else{
            return this.ventureTower;}

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

    /**
     * return dice force from its color
     * @param colorType
     * @return
     */
    public Integer getDiceForce(ColorType colorType){

        for (Dice dice:this.dices) {
            if(dice.getColor() == colorType)
                return  dice.getValue();
        }
        return 0;
    }

}
