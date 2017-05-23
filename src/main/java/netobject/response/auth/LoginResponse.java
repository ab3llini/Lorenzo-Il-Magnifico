package netobject.response.auth;

import netobject.response.Response;
import netobject.response.ResponseType;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */
public class LoginResponse extends Response {

    private final boolean status;

    private final String message;

    public LoginResponse(boolean status, String message) {

        super(ResponseType.Login);

        this.status = status;
        this.message = message;

    }

    public boolean getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }
}
