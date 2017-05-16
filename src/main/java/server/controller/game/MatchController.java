package server.controller.game;
import netobject.Action;
import server.model.*;
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
        for (int deckIndex = 0; deckIndex < numberOfEra*numberOfTower; deckIndex++) {
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

    public void createTowers(Integer round, Integer era, ArrayList<ArrayList<DvptCard>> decks) {
        DvptCard temporaryCard;
        TowerSlot temporarySlot = new TowerSlot(null, 0, 0, null);
        ArrayList<ArrayList<TowerSlot>> towers = new ArrayList<ArrayList<TowerSlot>>();
        for (int towerIndex = 0; towerIndex < numberOfTower; towerIndex++) {
            ArrayList<TowerSlot> tower = new ArrayList<TowerSlot>();
            if (round == era * 2 - 1) { //an era is composed by two round, the first round of an era is equal to era * 2 - 1
                for (int towerSlotIndex = 0; towerSlotIndex < numberOfSlot; towerSlotIndex++) {
                    temporaryCard = decks.get(towerIndex * numberOfEra + era - 1 ).get(towerSlotIndex); //extract card from the first deck
                    temporarySlot.setDvptCard(temporaryCard);
                    System.out.println("Sto caricando la carta " + temporaryCard.getId() + " nello slot " + towerSlotIndex + " della torre " + towerIndex);
                    tower.add(temporarySlot);
                }
                towers.add(tower);
                }
            if (round == era * 2) {
                for (int towerSlotIndex = 0; towerSlotIndex < numberOfSlot; towerSlotIndex++) { //second round
                    temporaryCard = decks.get(towerIndex * numberOfEra + era - 1).get(towerSlotIndex + numberOfSlot); //extract card from second deck
                    temporarySlot.setDvptCard(temporaryCard);
                    tower.add(temporarySlot);
                }
                towers.add(tower);
            }
        }
    }

    public void prepareTowers(Integer round, Integer era, GameSingleton singleton) {
        ArrayList<ArrayList<DvptCard>> decks = createDecks(singleton);
        shuffleDecks(decks);
        createTowers(round, era, decks);
    }
}

class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        ArrayList<DvptCard> mazzo = null;
        MatchController controller = new MatchController();
        GameSingleton singleton = GameSingleton.getInstance();
        try {
            mazzo = DvptCardParser.parse();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        controller.prepareTowers(1,1,singleton);
    }
}