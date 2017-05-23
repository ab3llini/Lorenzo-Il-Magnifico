package netobject.response;

import netobject.NetObject;
import netobject.NetObjectType;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */
public abstract class Response extends NetObject {

    protected final ResponseType responseType;

    public Response(ResponseType responseType) {

        super(NetObjectType.Response);

        this.responseType = responseType;

    }

    public ResponseType getResponseType() {
        return responseType;
    }
}
