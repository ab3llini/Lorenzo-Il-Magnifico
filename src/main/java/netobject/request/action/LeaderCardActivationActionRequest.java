package netobject.request.action;

/**
 * Created by Federico on 22/05/2017.
 * Edited by ab3llini on 23/05/2017
 */
public class LeaderCardActivationActionRequest extends ActionRequest {

    private final int leaderCardIndex; //index of the leader card which is gonna to be activated

    public LeaderCardActivationActionRequest(int leaderCardIndex) {

        super(ActionType.LeaderCardActivation);
        this.leaderCardIndex = leaderCardIndex;

    }
}
