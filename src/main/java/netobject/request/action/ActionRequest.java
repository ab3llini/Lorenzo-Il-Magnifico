package netobject.request.action;

/*
 * Created by alberto on 10/05/17.
 */

import netobject.NetObjectType;
import netobject.request.Request;
import netobject.request.RequestType;

public abstract class ActionRequest extends Request {

    protected final ActionType actionType;

    public ActionRequest(ActionType actionType) {

        super(RequestType.Action);

        this.actionType = actionType;

    }

}
