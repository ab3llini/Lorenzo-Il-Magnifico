package netobject.response.auth;

import netobject.response.Response;
import netobject.response.ResponseType;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */
public class LoginResponse extends Response {

    private final boolean loginSucceeded;
    private final String username;
    private final String message;

    public LoginResponse(boolean loginSucceeded, String username, String message) {

        super(ResponseType.Login);

        this.loginSucceeded = loginSucceeded;
        this.username = username;
        this.message = message;

    }

    public boolean loginHasSucceeded() {
        return this.loginSucceeded;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return this.message;
    }
}
