package server.model.board;

import server.model.card.developement.BuildingDvptCard;
import server.model.card.developement.CharacterDvptCard;
import server.model.card.developement.TerritoryDvptCard;
import server.model.card.developement.VentureDvptCard;

import java.util.ArrayList;

/**
 * Created by Federico on 11/05/2017.
 */
public class PersonalBoard {
    private BonusTile bonusTile;
    private ArrayList<TerritoryDvptCard> territoryCards;
    private ArrayList<BuildingDvptCard> buildingCards;
    private ArrayList<CharacterDvptCard>  characterCards;
    private ArrayList<VentureDvptCard>  ventureCards;

    public PersonalBoard (BonusTile bonusTile, ArrayList<TerritoryDvptCard> territoryCards, ArrayList<BuildingDvptCard> buildingCards, ArrayList<CharacterDvptCard>  characterCards, ArrayList<VentureDvptCard>  ventureCards){
        this.bonusTile = bonusTile;
        this.territoryCards = territoryCards;
        this.buildingCards = buildingCards;
        this.characterCards = characterCards;
        this.ventureCards = ventureCards;
    }

    public ArrayList<BuildingDvptCard> getBuildingCards() {
        return buildingCards;
    }

    public ArrayList<CharacterDvptCard> getCharacterCards() {
        return characterCards;
    }

    public ArrayList<TerritoryDvptCard> getTerritoryCards() {
        return territoryCards;
    }

    public ArrayList<VentureDvptCard> getVentureCards() {
        return ventureCards;
    }

    public void setBuildingCards(ArrayList<BuildingDvptCard> buildingCards) {
        this.buildingCards = buildingCards;
    }

    public void setCharacterCards(ArrayList<CharacterDvptCard> characterCards) {
        this.characterCards = characterCards;
    }

    public void setTerritoryCards(ArrayList<TerritoryDvptCard> territoryCards) {
        this.territoryCards = territoryCards;
    }

    public void setVentureCards(ArrayList<VentureDvptCard> ventureCards) {
        this.ventureCards = ventureCards;
    }
}
