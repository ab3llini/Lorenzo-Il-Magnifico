package server.controller.game;
import netobject.Action;
import server.model.*;
import server.model.board.Board;
import server.model.board.Player;
import server.model.board.TowerSlot;
import server.model.card.developement.DvptCard;
import server.utility.DvptCardParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import static java.util.Collections.shuffle;

/**
 * Created by Federico on 15/05/2017.
 */


public class MatchController {

    private Match match;
    private final Integer dvptCardOffset = 8; //every deck is a subarray composed by 8 card, according to different combination of type and era
    private final Integer numberOfEra = 3;
    private final Integer numberOfTower = 4;
    private final Integer numberOfSlot = 4;

    public void onPlayerAction(Player player, Action action) {
    }

    private ArrayList<Player> players;
    private ArrayList<Player> playersOrder;
    private Integer era;
    private Integer round;
    private MatchSettings matchSettings;

    public ArrayList<ArrayList<DvptCard>> createDecks(GameSingleton singleton) {
        ArrayList<ArrayList<DvptCard>> deckArray = new ArrayList<ArrayList<DvptCard>>();
        for (int deckIndex = 0; deckIndex < numberOfEra * numberOfTower; deckIndex++) {
            ArrayList<DvptCard> deck = new ArrayList<DvptCard>();
            for (int cardIndex = deckIndex * dvptCardOffset; cardIndex < deckIndex * dvptCardOffset + dvptCardOffset; cardIndex++) {
                deck.add(singleton.getDvptCard(cardIndex));
            }
            deckArray.add(deck);
        }
        return deckArray;
    }

    public void shuffleDecks(ArrayList<ArrayList<DvptCard>> deckArray) {
        for (ArrayList<DvptCard> deck : deckArray) {
            shuffle(deck);
        }
    }
}