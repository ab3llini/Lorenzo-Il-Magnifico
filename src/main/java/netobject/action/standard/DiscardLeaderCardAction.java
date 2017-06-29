package netobject.action.standard;

import netobject.action.Action;
import netobject.action.ActionType;

/**
 * Created by Federico on 29/06/2017.
 */
public class DiscardLeaderCardAction extends Action {

    private final StandardActionType standardActionType;

    private final int leaderCardIndex; //index of the leader card which is gonna to be activated

    public DiscardLeaderCardAction(int leaderCardIndex, String sender) {

        super(ActionType.Standard, sender);
        this.standardActionType = StandardActionType.LeaderCardActivation;
        this.leaderCardIndex = leaderCardIndex;

    }

    public int getLeaderCardIndex() {
        return leaderCardIndex;
    }
}
