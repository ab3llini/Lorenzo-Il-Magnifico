package netobject.action.standard;

import netobject.action.Action;
import netobject.action.ActionType;
import server.model.card.Deck;
import server.model.card.leader.LeaderCard;

/*
 * @author  ab3llini
 * @since   06/06/17.
 */
public class ShuffleLeaderCardStandardAction extends Action {

    private final int selection;

    private final Deck<LeaderCard> deck;

    public ShuffleLeaderCardStandardAction(int selection, Deck<LeaderCard> deck, String sender) {

        super(ActionType.Standard, sender);

        this.selection = selection;

        this.deck = deck;
    }

    public Deck<LeaderCard> getDeck() {
        return deck;
    }

    public int getSelection() {
        return selection;
    }
}
