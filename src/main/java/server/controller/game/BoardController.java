package server.controller.game;

import exception.NotEnoughPlayersException;
import exception.NotStrongEnoughException;
import javafx.scene.effect.Effect;
import server.model.GameSingleton;
import server.model.board.Board;
import server.model.board.FamilyMember;
import server.model.board.Period;
import server.model.card.Deck;
import server.model.card.ban.BanCard;
import server.model.card.developement.DvptCard;
import server.model.effect.EffectSurplus;
import server.utility.DvptCardParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static server.model.board.Period.first;


/*
 * @author  ab3llini
 * @since   19/05/17.
 */

public class BoardController {

    /**
     * Each development card has an offset between periods of 8
     */
    private static final int DVPT_CARD_OFFSET = 8;

    /**
     * Each ban card has an offset between periods of 7
     */

    private static final int BAN_CARD_OFFSET = 7;

    /**
     * There are 3 total periods
     */
    private static final int TOTAL_PERIODS = 3;

    /**
     * There are 4 total towers
     */
    private static final int TOTAL_TOWERS = 4;

    /**
     * There are 4 total slots for each tower
     */
    private static final int SLOTS_FOR_TOWER = 4;

    /**
     * Territory, building, character and venture tower are placed in a specific order on board; decks' numeration follows this order
     */
    private static final int TERRITORY_TOWER_INDEX = 0;
    private static final int BUILDING_TOWER_INDEX = 1;
    private static final int CHARACTER_TOWER_INDEX = 2;
    private static final int VENTURE_TOWER_INDEX = 3;

    /**
     * Constructor. The board controller takes care of every update relative to the board
     */

    private Board board;

    public BoardController(Board board) {

        //TODO: Load into the board model the proper values! Even throughout a constructor chain!
        this.board = board;

    }

    /**
     * A method that creates the card decks
     *
     * @return the array list
     */
    public ArrayList<Deck<DvptCard>> createDecks() {

        GameSingleton singleton = GameSingleton.getInstance();

        ArrayList<Deck<DvptCard>> deckArray = new ArrayList<Deck<DvptCard>>();

        for (int deckIndex = 0; deckIndex < TOTAL_PERIODS * TOTAL_TOWERS; deckIndex++) {

            Deck<DvptCard> deck = new Deck<DvptCard>();

            for (int cardIndex = deckIndex * DVPT_CARD_OFFSET; cardIndex < deckIndex * DVPT_CARD_OFFSET + DVPT_CARD_OFFSET; cardIndex++) {

                deck.addCard(singleton.getSpecificDvptCard(cardIndex));

            }

            deckArray.add(deck.shuffle());

        }
        return deckArray;

    }

    public ArrayList<Deck<BanCard>> createBanDecks() {

        GameSingleton singleton = GameSingleton.getInstance();

        ArrayList<Deck<BanCard>> banDeckArray = new ArrayList<Deck<BanCard>>();

        for (int deckIndex = 0; deckIndex < 3; deckIndex++) {

            Deck<BanCard> deck = new Deck<BanCard>();

            for (int cardIndex = deckIndex * BAN_CARD_OFFSET; cardIndex < deckIndex * BAN_CARD_OFFSET + BAN_CARD_OFFSET; cardIndex++) {

                deck.addCard(singleton.getSpecificBanCard(cardIndex));

            }

            banDeckArray.add(deck.shuffle());

        }
        return banDeckArray;

    }

    /**
     * A method that inserts the cards into board towers
     */

    public void prepareTowers(Board board, Integer round, ArrayList<Deck<DvptCard>> deckArray) {

        Integer period;

        ArrayList<DvptCard> temporaryTerritory = new ArrayList<DvptCard>();

        ArrayList<DvptCard> temporaryBuilding = new ArrayList<DvptCard>();

        ArrayList<DvptCard> temporaryCharacter = new ArrayList<DvptCard>();

        ArrayList<DvptCard> temporaryVenture = new ArrayList<DvptCard>();

        period = round / 2;

        // If it is the first turn of a period, every tower will contain the first half of his specific deck, according to his authenticationType and period

        if (round % 2 == 1) {

            for (int i = 0; i < 4; i++) {
                temporaryTerritory.add(deckArray.get(TERRITORY_TOWER_INDEX * TOTAL_PERIODS + (period - 1)).getCards().get(i));
                System.out.println("Sto caricando la carta" + deckArray.get(TERRITORY_TOWER_INDEX * TOTAL_PERIODS).getCards().get(i).getId());
            }

            for (int i = 0; i < 4; i++) {
                temporaryBuilding.add(deckArray.get(BUILDING_TOWER_INDEX * TOTAL_PERIODS + (period - 1)).getCards().get(i));
            }


            for (int i = 0; i < 4; i++) {
                temporaryCharacter.add(deckArray.get(CHARACTER_TOWER_INDEX * TOTAL_PERIODS + (period - 1)).getCards().get(i));
            }


            for (int i = 0; i < 4; i++) {
                temporaryVenture.add(deckArray.get(VENTURE_TOWER_INDEX * TOTAL_PERIODS + (period - 1)).getCards().get(i));
            }

        }

        //On the contrary, if it is the second round of that period, every tower will contain the second half of his specific deck, according to his authenticationType and period

        else {

            for (int i = 4; i < 8; i++) {

                temporaryTerritory.add(deckArray.get(TERRITORY_TOWER_INDEX * TOTAL_PERIODS + (period - 1)).getCards().get(i));

            }

            for (int i = 4; i < 8; i++) {

                temporaryBuilding.add(deckArray.get(BUILDING_TOWER_INDEX * TOTAL_PERIODS + (period - 1)).getCards().get(i));

            }


            for (int i = 4; i < 8; i++) {

                temporaryCharacter.add(deckArray.get(CHARACTER_TOWER_INDEX * TOTAL_PERIODS + (period - 1)).getCards().get(i));

            }


            for (int i = 4; i < 8; i++) {

                temporaryVenture.add(deckArray.get(VENTURE_TOWER_INDEX * TOTAL_PERIODS + (period - 1)).getCards().get(i));

            }

        }

        board.setDvptCardOnTerritoryTower(temporaryTerritory);

        board.setDvptCardOnBuildingTower(temporaryBuilding);

        board.setDvptCardOnCharacterTower(temporaryCharacter);

        board.setDvptCardOnVentureTower(temporaryVenture);

    }

    /**
     * A method that inserts the ban cards into cathedral
     */

    public void prepareCathedral(ArrayList<Deck<BanCard>> bancards, Integer round, Board board) {

        Integer period = round / 2;

        ArrayList<BanCard> temporaryBanCardArray = new ArrayList<BanCard>();

        for (int banCardDeckIndex = 0; banCardDeckIndex < 3; banCardDeckIndex++) {

            temporaryBanCardArray.add(bancards.get(banCardDeckIndex).getCards().get(0));

        }

        board.setBanCardOnCathedral(temporaryBanCardArray);

    }

    /**
     * this method place a familyMember in the CouncilPalace (if it's possible )
     * @param familyMember
     * @param additionalServants that a player can use to increment his family member force
     * @return an effect surplus for the player that has performed the action
     * @throws NotStrongEnoughException
     * @throws NotEnoughPlayersException
     */
    public EffectSurplus placeOnCouncilPalace(FamilyMember familyMember, Integer additionalServants, Integer numberOfPlayers) throws NotStrongEnoughException, NotEnoughPlayersException {

        //check if the match has enough player to use council palace
        if(!(numberOfPlayers>=this.board.getCouncilPalace().getMinPlayers()))
            throw new NotEnoughPlayersException("Not enough players to use this place");

        //check if the family member has enough force to set on council palace
        if(familyMember.getForce() + additionalServants >= this.board.getCouncilPalace().getEntryForce()){

            //set the family member on the council palace
            this.board.getCouncilPalace().placeFamilyMember(familyMember);
            return this.board.getCouncilPalace().getEffectSurplus();
        }

        else
            throw new NotStrongEnoughException("Not strong enough to do this action");

    }

    /**
     *this method place a familyMember in the one specific place of the market place (if it's possible )
     * @param familyMember
     * @param additionalServants that a player can use to increment his family member force
     * @param placementIndex
     * @return an effect surplus for the player that has performed the action
     * @throws NotStrongEnoughException
     */
    public EffectSurplus placeOnMarket(FamilyMember familyMember, Integer additionalServants, Integer placementIndex, Integer numberOfPlayers) throws NotStrongEnoughException, NotEnoughPlayersException {

        //check if the match has enough player to use council palace
        if(!(numberOfPlayers >= this.board.getMarket().getMarketPlaces().get(placementIndex).getMinPlayers()))
            throw new NotEnoughPlayersException("Not enough players to use this place");

        //check if the family member has enough force to set on council palace
        if(familyMember.getForce() + additionalServants >= this.board.getMarket().getMarketPlaces().get(placementIndex).getEntryForce()) {

            //set the family member on the market place
            this.board.getMarket().getMarketPlaces().get(placementIndex).setFamilyMember(familyMember);
            return this.board.getMarket().getMarketPlaces().get(placementIndex).getEffectSurplus();
        }
        else
            throw new NotStrongEnoughException("Not strong enough to do this action");
    }
}

