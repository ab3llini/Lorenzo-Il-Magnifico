package server.model.board;

import server.model.card.ban.BanCard;
import server.model.card.developement.BuildingDvptCard;
import server.model.card.developement.CharacterDvptCard;
import server.model.card.developement.TerritoryDvptCard;
import server.model.card.developement.VentureDvptCard;

import java.util.ArrayList;

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
    private ArrayList<SingleActionPlace> market;


    public Board (ArrayList<TerritoryDvptCard> territoryCards, ArrayList<CharacterDvptCard> characterCards, ArrayList<BuildingDvptCard> buildingCards, ArrayList<VentureDvptCard> ventureCards, ArrayList<TowerSlot> territoryTower, ArrayList<TowerSlot> characterTower, ArrayList<TowerSlot> buildingTower, ArrayList<TowerSlot> ventureTower, ArrayList<BanCard> cathedral, CouncilPalace councilPalace, ArrayList<Dice> dices, ActionArea productionArea, ActionArea harvestArea, ArrayList<SingleActionPlace> market){
        this.buildingTower = buildingTower;
        this.territoryTower = territoryTower;
        this.characterTower = characterTower;
        this.ventureTower = ventureTower;
        this.councilPalace = councilPalace;
        this.dices = dices;
        this.productionArea = productionArea;
        this.harvestArea = harvestArea;
        this.market = market;
    }

    public ArrayList<TowerSlot> getTerritoryTower() {
        return territoryTower;
    }

    public void setTerritoryTower(ArrayList<TowerSlot> territoryTower){
        this.territoryTower = territoryTower;
    }

    public ArrayList<TowerSlot> getBuildingTower() {
        return buildingTower;
    }

    public void setBuildingTower(ArrayList<TowerSlot> buildingTower){
        this.buildingTower = buildingTower;
    }

    public ArrayList<TowerSlot> getCharacterTower() {
        return characterTower;
    }

    public void setCharacterTower(ArrayList<TowerSlot> characterTower){
        this.characterTower = characterTower;
    }

    public ArrayList<TowerSlot> getVentureTower() {
        return ventureTower;
    }

    public void setVentureTower(ArrayList<TowerSlot> ventureTower){
        this.ventureTower = ventureTower;
    }

}
