package netobject.action;

import netobject.NetObject;
import netobject.NetObjectType;

/*
 * @author  ab3llini
 * @since   01/06/17.
 */
public class Action extends NetObject {

    private ActionType actionType;

    public Action(ActionType actionType) {

        super(NetObjectType.Action);

        this.actionType = actionType;
    }

    public Action() {

        super(NetObjectType.Poison);

    }

    public ActionType getActionType() {
        return actionType;
    }
}
