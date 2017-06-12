package server.model.board;

import exception.SixCardsLimitReachedException;
import logger.AnsiColors;
import server.model.GameSingleton;
import server.model.card.developement.*;
import server.utility.BoardConfigParser;
import server.utility.UnicodeChars;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Federico on 11/05/2017.
 */
public class PersonalBoard implements Serializable {
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

        if(card != null) {
            if (card.getType() == DvptCardType.territory)
                addTerritoryCard((TerritoryDvptCard) card);

            if (card.getType() == DvptCardType.building)
                addBuildingCard((BuildingDvptCard) card);

            if (card.getType() == DvptCardType.character)
                addCharacterCard((CharacterDvptCard) card);

            if (card.getType() == DvptCardType.venture)
                addVentureCard(((VentureDvptCard) card));
        }

    }

    @Override
    public String toString(){
        String pBoard = new String();
        pBoard += ("________________________________________________\n");

        pBoard += ("|                                              |   \n");

        pBoard += ("| " + AnsiColors.ANSI_YELLOW + "BUILDING CARDS" + "                           " + AnsiColors.ANSI_RESET);
        pBoard += "    |   "+ AnsiColors.ANSI_PURPLE + "VENTURE CARDS" + AnsiColors.ANSI_RESET + "\n";

        pBoard += ("|          _______________________________     |   ");

        for(VentureDvptCard card : ventureCards)
            pBoard += ("_____");

        pBoard += "\n";

        pBoard += ("|          |    |    |    |    |    |    |     |   ");

        for(VentureDvptCard card : ventureCards)
            pBoard += ("|    ");

        if(!ventureCards.isEmpty())
            pBoard += "|";

        pBoard += "\n";

        pBoard += ("|          |    |    |    |    |    |    |     |   ");

        for(VentureDvptCard card : ventureCards)
            pBoard += ("|    ");

        if(!ventureCards.isEmpty())
            pBoard += "|";

        pBoard += "\n";

        pBoard += ("|          ");

        for(int i=0; i<6; i++) {
            pBoard += "| ";

            if (i< buildingCards.size())
                pBoard += Board.printCardId(getBuildingCards().get(i));

            else
                pBoard += "  ";

            pBoard += " ";
        }

        pBoard += "|     |   ";

        for(VentureDvptCard card : ventureCards)
            pBoard += ("| " + Board.printCardId(card)+ " ");

        if(!ventureCards.isEmpty())
            pBoard += "|";
        pBoard += "\n";

        pBoard += ("|          |    |    |    |    |    |    |     |   ");

        for(VentureDvptCard card : ventureCards)
            pBoard += ("|    ");

        if(!ventureCards.isEmpty())
            pBoard += "|";

        pBoard += "\n";

        pBoard += ("|          |____|____|____|____|____|____|     |   ");

        for(VentureDvptCard card : ventureCards)
            pBoard += ("|____");

        if(!ventureCards.isEmpty())
            pBoard += "|";
        pBoard += "\n";

        pBoard += ("|                                              |   \n");

        pBoard += ("| " + AnsiColors.ANSI_GREEN + "TERRITORY CARDS" + "                          " + AnsiColors.ANSI_RESET);
        pBoard += "    |   "+ AnsiColors.ANSI_BLUE + "CHARACTER CARDS" + AnsiColors.ANSI_RESET + "\n";

        pBoard += ("|                     " + UnicodeChars.MilitaryPoints + " " + BoardConfigParser.getMinimumMilitaryPoints(3) + "  " + UnicodeChars.MilitaryPoints + " " + BoardConfigParser.getMinimumMilitaryPoints(4) + " " + UnicodeChars.MilitaryPoints + " " + BoardConfigParser.getMinimumMilitaryPoints(5) + " " + UnicodeChars.MilitaryPoints + " " + BoardConfigParser.getMinimumMilitaryPoints(6)+ "      |\n" );

        pBoard += ("|          _______________________________     |   ");

        for(CharacterDvptCard card : characterCards)
            pBoard += ("_____");

        pBoard += "\n";

        pBoard += ("|          |    |    |    |    |    |    |     |   ");

        for(CharacterDvptCard card : characterCards)
            pBoard += ("|    ");

        if(!characterCards.isEmpty())
            pBoard += "|";

        pBoard += "\n";

        pBoard += ("|          |    |    |    |    |    |    |     |   ");

        for(CharacterDvptCard card : characterCards)
            pBoard += ("|    ");

        if(!characterCards.isEmpty())
            pBoard += "|";

        pBoard += "\n";

        pBoard += ("|          ");

        for(int i=0; i<6; i++) {
            pBoard += "| ";

            if (i< territoryCards.size())
                pBoard += Board.printCardId(getTerritoryCards().get(i));

            else
                pBoard += "  ";

            pBoard += " ";
        }

        pBoard += "|     |   ";

        for(CharacterDvptCard card : characterCards)
            pBoard += ("| " + Board.printCardId(card)+ " ");

        if(!characterCards.isEmpty())
            pBoard += "|";

        pBoard += "\n";

        pBoard += ("|          |    |    |    |    |    |    |     |   ");

        for(CharacterDvptCard card : characterCards)
            pBoard += ("|    ");

        if(!characterCards.isEmpty())
            pBoard += "|";
        pBoard += "\n";

        pBoard += ("|          |____|____|____|____|____|____|     |   ");

        for(CharacterDvptCard card : characterCards)
            pBoard += ("|____");

        if(!characterCards.isEmpty())
            pBoard += "|";

        pBoard += "\n";
        pBoard += ("|______________________________________________|   \n");

        return pBoard;
    }

    public BonusTile getBonusTile() {
        return bonusTile;}

    public void setBonusTile(BonusTile bonusTile) {
        this.bonusTile = bonusTile;
    }
}
