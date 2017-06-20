package netobject.action.standard;

import netobject.action.Action;
import netobject.action.ActionType;

/**
 * Created by Federico on 22/05/2017.
 * Edited by ab3llini on 23/05/2017
 */
public class LeaderCardActivationAction extends Action {

    private final StandardActionType standardActionType;

    private final int leaderCardIndex; //index of the leader card which is gonna to be activated

    public LeaderCardActivationAction(int leaderCardIndex, String sender) {

        super(ActionType.Standard, sender);
        this.standardActionType = StandardActionType.LeaderCardActivation;
        this.leaderCardIndex = leaderCardIndex;

    }

    public int getLeaderCardIndex() {
        return leaderCardIndex;
    }

}
