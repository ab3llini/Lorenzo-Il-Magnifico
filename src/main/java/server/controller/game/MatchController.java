package server.controller.game;
        import netobject.Action;
        import server.model.*;
        import server.model.board.TowerSlot;
        import server.model.card.developement.DvptCard;
        import java.util.ArrayList;
        import static java.util.Collections.shuffle;

/**
 * Created by Federico on 15/05/2017.
 */


public class MatchController {

    private Match match;
    private final Integer dvptCardOffset = 8;

    public void onPlayerAction(Player player, Action action) {
    }

    private ArrayList<Player> players;
    private ArrayList<Player> playersOrder;
    private Integer era;
    private Integer round;
    private MatchSettings matchSettings;

    public ArrayList<ArrayList<DvptCard>> createDecks(GameSingleton singleton) {
        ArrayList<ArrayList<DvptCard>> deckArray = new ArrayList<ArrayList<DvptCard>>();
        for (int i = 0; i < 12; i++) {
            ArrayList<DvptCard> deck = new ArrayList<DvptCard>();
            for (int j = i * 8; j < i * 8 + 8; j++) {
                deck.add(singleton.getDvptCard(j));
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
        for (int i = 0; i < 4; i++) {
            ArrayList<TowerSlot> tower = new ArrayList<TowerSlot>();
            if (round == era * 2) {
                for (int j = 0; j < 4; j++) {
                    temporaryCard = decks.get(i * 3 + era).get(j);
                    temporarySlot.setDvptCard(temporaryCard);
                    tower.add(temporarySlot);
                }
                towers.add(tower);
            }
            if (round == era * 2 - 1) {
                for (int j = 0; j < 4; j++) {
                    temporaryCard = decks.get(i * 3 + era).get(j + 4);
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