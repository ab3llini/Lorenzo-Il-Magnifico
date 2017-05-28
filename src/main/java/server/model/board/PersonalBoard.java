package server.model.board;

import exception.SixCardsLimitReachedException;
import server.model.card.developement.*;

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

    public PersonalBoard (){

        //initialize player card container
        this.territoryCards = new ArrayList<TerritoryDvptCard>();
        this.buildingCards = new ArrayList<BuildingDvptCard>();
        this.characterCards = new ArrayList<CharacterDvptCard>();
        this.ventureCards = new ArrayList<VentureDvptCard>();

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

    public void addVentureCard(VentureDvptCard card) throws SixCardsLimitReachedException {

        if(ventureCards.size()< 6){
            ventureCards.add(card);}

        else
            throw new SixCardsLimitReachedException("The player has already reached the maximum limit of six cards");
    }

    public void addBuildingCard(BuildingDvptCard card) throws SixCardsLimitReachedException {

        if(buildingCards.size()< 6){
            buildingCards.add(card);}

        else
            throw new SixCardsLimitReachedException("The player has already reached the maximum limit of six cards");

    }

    public void addCharacterCard(CharacterDvptCard card) throws SixCardsLimitReachedException {

        if(characterCards.size()< 6){
            characterCards.add(card);}

        else
            throw new SixCardsLimitReachedException("The player has already reached the maximum limit of six cards");


    }

    public void addTerritoryCard(TerritoryDvptCard card) throws SixCardsLimitReachedException {

        if(territoryCards.size()< 6){
            territoryCards.add(card);}

        else
            throw new SixCardsLimitReachedException("The player has already reached the maximum limit of six cards");
    }

    public void addCard(DvptCard card) throws SixCardsLimitReachedException {

        if(card.getType() == DvptCardType.territory)
            addTerritoryCard((TerritoryDvptCard) card);

        if(card.getType() == DvptCardType.building)
            addBuildingCard((BuildingDvptCard) card);

        if(card.getType() == DvptCardType.character)
            addCharacterCard((CharacterDvptCard) card);

        if(card.getType() == DvptCardType.venture)
            addVentureCard(((VentureDvptCard) card));


    }

    public BonusTile getBonusTile() {
        return bonusTile;}

    public void setBonusTile(BonusTile bonusTile) {
        this.bonusTile = bonusTile;
    }
}
