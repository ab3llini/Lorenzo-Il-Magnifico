package netobject.action.standard;

import netobject.action.Action;
import netobject.action.ActionType;

/*
 * @author  ab3llini
 * @since   03/06/17.
 */
public class TerminateRoundStandardAction extends Action {

    private final StandardActionType actionType;

    public TerminateRoundStandardAction(String sender) {

        super(ActionType.Standard, sender);

        this.actionType = StandardActionType.TerminateRound;
    }

}
