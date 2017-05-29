package netobject.request.action;

/*
 * Created by alberto on 10/05/17.
 */
import netobject.request.Request;
import netobject.request.RequestType;

public class ActionRequest extends Request {

    private final ActionType actionType;

    public ActionRequest(ActionType actionType) {

        super(RequestType.Action);

        this.actionType = actionType;

    }

    public ActionType getActionType() {
        return actionType;
    }
}
