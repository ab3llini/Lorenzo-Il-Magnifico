package server.controller.game;

import server.model.GameSingleton;
import server.model.board.Board;
import server.model.card.Deck;
import server.model.card.developement.DvptCard;

import java.util.ArrayList;


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
     * There are 3 total periods
     */
    private static final int TOTAL_PERIODS = 3;

    /**
     * There are 4 total towers
     */
    private static  final int TOTAL_TOWERS = 4;

    /**
     * There are 4 total slots for each tower
     */
    private static final int SLOTS_FOR_TOWER = 4;

    /**
     * Constructor. The board controller takes care of every update relative to the board
     */
    public BoardController(Board board) {

        //TODO: Load into the board model the proper values! Even throughout a constructor chain!

    }

    /**
     * A method that creates the card decks
     * Was originally implemented by Federico but has been revisited to fit changes.
     * @return the array list
     */
    public ArrayList<Deck<DvptCard>> createDecks() {

        GameSingleton singleton = GameSingleton.getInstance();

        ArrayList<Deck<DvptCard>> deckArray = new ArrayList<Deck<DvptCard>>();

        for (int deckIndex = 0; deckIndex < TOTAL_PERIODS * TOTAL_TOWERS; deckIndex++) {

            Deck<DvptCard> deck = new Deck<DvptCard>();

            for (int cardIndex = deckIndex * DVPT_CARD_OFFSET; cardIndex < deckIndex * DVPT_CARD_OFFSET + DVPT_CARD_OFFSET; cardIndex++) {

                deck.addCard(singleton.getDvptCard(cardIndex));

            }

            deckArray.add(deck.shuffle());

        }
        return deckArray;

    }

}
