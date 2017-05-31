package netobject.request.action;

/*
 * Created by alberto on 10/05/17.
 */
import netobject.request.Request;
import netobject.request.RequestType;

public class StandardActionRequest extends Request {

    private StandardActionType standardActionType;

    public StandardActionRequest(StandardActionType standardActionType) {

        super(RequestType.StandardAction);

        this.standardActionType = standardActionType;

    }

    public StandardActionRequest() {

        super(RequestType.Invalid);

    }

    public StandardActionType getStandardActionType() {
        return standardActionType;
    }
}
