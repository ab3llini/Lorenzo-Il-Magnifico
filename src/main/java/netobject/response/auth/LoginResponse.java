package netobject.response.auth;

import netobject.response.Response;
import netobject.response.ResponseType;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */
public class LoginResponse extends Response {

    private final boolean loginSucceeded;

    private final String message;

    public LoginResponse(boolean loginSucceeded, String message) {

        super(ResponseType.Login);

        this.loginSucceeded = loginSucceeded;
        this.message = message;

    }

    public boolean loginHasSucceeded() {
        return this.loginSucceeded;
    }

    public String getMessage() {
        return this.message;
    }
}
