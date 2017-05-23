package netobject.request;

import netobject.NetObject;
import netobject.NetObjectType;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */
public abstract class Request extends NetObject {

    protected final RequestType requestType;

    protected Request(RequestType requestType) {

        super(NetObjectType.Request);

        this.requestType = requestType;
    }

    public RequestType getRequestType() {
        return requestType;
    }
}
