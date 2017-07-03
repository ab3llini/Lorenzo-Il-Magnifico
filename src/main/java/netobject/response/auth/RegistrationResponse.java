package netobject.response.auth;

import netobject.response.Response;
import netobject.response.ResponseType;

/**
 * Created by LBARCELLA on 03/07/2017.
 */
public class RegistrationResponse extends Response {

    private final boolean registerSucceeded;
    private final String username;
    private final String message;

    public RegistrationResponse(boolean registerSucceeded, String username, String message) {

        super(ResponseType.Registration);

        this.registerSucceeded = registerSucceeded;
        this.username = username;
        this.message = message;

    }

    public boolean registerHasSucceeded() {
        return this.registerSucceeded;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return this.message;
    }
}
