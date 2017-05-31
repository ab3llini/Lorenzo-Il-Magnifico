package netobject.request.action;

/**
 * Created by Federico on 22/05/2017.
 * Edited by ab3llini on 23/05/2017
 */
public class LeaderCardActivationStandardActionRequest extends StandardActionRequest {

    private final int leaderCardIndex; //index of the leader card which is gonna to be activated

    public LeaderCardActivationStandardActionRequest(int leaderCardIndex) {

        super(StandardActionType.LeaderCardActivation);
        this.leaderCardIndex = leaderCardIndex;

    }

    public int getLeaderCardIndex() {
        return leaderCardIndex;
    }

}
