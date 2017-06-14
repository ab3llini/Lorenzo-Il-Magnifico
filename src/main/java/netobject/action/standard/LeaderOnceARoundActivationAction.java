package netobject.action.standard;

import netobject.action.Action;
import netobject.action.ActionType;

/**
 * Created by Federico on 11/06/2017.
 */
public class LeaderOnceARoundActivationAction extends Action {
    private final StandardActionType standardActionType;

    private final int leaderCardIndex;

    private final int choice;

    public LeaderOnceARoundActivationAction(StandardActionType standardActionType, int leaderCardIndex, String sender, int choice) {

        super(ActionType.Standard, sender);
        this.standardActionType = standardActionType;
        this.leaderCardIndex = leaderCardIndex;
        this.choice = choice;
    }

    public int getLeaderCardIndex() {
        return leaderCardIndex;
    }

    public int getChoice() {
        return choice;
    }

    public StandardActionType getStandardActionType() {
        return standardActionType;
    }
}
