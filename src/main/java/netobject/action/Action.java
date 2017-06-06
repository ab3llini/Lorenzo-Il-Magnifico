package netobject.action;

import netobject.NetObject;
import netobject.NetObjectType;

/*
 * @author  ab3llini
 * @since   01/06/17.
 */
public class Action extends NetObject {

    private ActionType actionType;

    private String sender;

    public Action(ActionType actionType, String sender) {

        super(NetObjectType.Action);

        this.actionType = actionType;
        this.sender = sender;

    }

    public Action() {

        super(NetObjectType.Poison);

    }

    public String getSender() {
        return sender;
    }

    public ActionType getActionType() {
        return actionType;
    }
}
