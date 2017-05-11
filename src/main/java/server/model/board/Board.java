package server.model.board;

import server.model.card.ban.BanCard;
import server.model.card.developement.BuildingDvptCard;
import server.model.card.developement.CharacterDvptCard;
import server.model.card.developement.TerritoryDvptCard;
import server.model.card.developement.VentureDvptCard;

import java.util.ArrayList;

/**
 * Created by Federico on 11/05/2017.
 */
public class Board {
    private ArrayList<TerritoryDvptCard> territoryCards;
    private ArrayList<CharacterDvptCard> characterCards;
    private ArrayList<BuildingDvptCard> buildingCards;
    private ArrayList<VentureDvptCard> ventureCards;
    private ArrayList<TowerSlot> territoryTower;
    private ArrayList<TowerSlot> characterTower;
    private ArrayList<TowerSlot> buildingTower;
    private ArrayList<TowerSlot> ventureTower;
    private ArrayList<BanCard> cathedral;
    private CouncilPalace councilPalace;
    private ArrayList<Dice> dices;
    private ActionArea productionArea;
    private ActionArea harvestArea;
    private ArrayList<SingleActionPlace> market;


    public Board (ArrayList<TerritoryDvptCard> territoryCards, ArrayList<CharacterDvptCard> characterCards, ArrayList<BuildingDvptCard> buildingCards, ArrayList<VentureDvptCard> ventureCards, ArrayList<TowerSlot> territoryTower, ArrayList<TowerSlot> characterTower, ArrayList<TowerSlot> buildingTower, ArrayList<TowerSlot> ventureTower, ArrayList<BanCard> cathedral, CouncilPalace councilPalace, ArrayList<Dice> dices, ActionArea productionArea, ActionArea harvestArea, ArrayList<SingleActionPlace> market){
        this.territoryCards = territoryCards;
        this.ventureCards = ventureCards;
        this.buildingTower = buildingTower;
        this.cathedral = cathedral;
        this.councilPalace = councilPalace;
        this.dices = dices;
        this.productionArea = productionArea;
        this.harvestArea = harvestArea;
        this.market = market;
    }

}
