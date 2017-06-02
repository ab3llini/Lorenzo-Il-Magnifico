package netobject.action.immediate;

import netobject.action.Action;
import netobject.action.ActionType;
import netobject.action.SelectionType;

/*
 * @author  ab3llini
 * @since   01/06/17.
 */
public class ImmediateChoiceAction extends Action {

    private final ImmediateActionType immediateActionType;

    private final SelectionType selection;

    public ImmediateChoiceAction(ImmediateActionType immediateActionType, SelectionType selection) {

        super(ActionType.Immediate);

        this.immediateActionType = immediateActionType;
        this.selection = selection;
    }
}
