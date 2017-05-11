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
    private TerritoryDvptCard[] territoryCards;
    private BuildingDvptCard[] buildingCards;
    private ArrayList<CharacterDvptCard>  characterCards;
    private ArrayList<VentureDvptCard>  ventureCards;

    public PersonalBoard (BonusTile bonusTile, TerritoryDvptCard[] territoryCards, BuildingDvptCard[] buildingCards, ArrayList<CharacterDvptCard>  characterCards, ArrayList<VentureDvptCard>  ventureCards){
        this.bonusTile = bonusTile;
        this.territoryCards = territoryCards;
        this.buildingCards = buildingCards;
        this.characterCards = characterCards;
        this.ventureCards = ventureCards;
    }
}
