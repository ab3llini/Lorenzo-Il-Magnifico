package netobject.request.auth;

import netobject.request.Request;
import netobject.request.RequestType;

/*
 * @author  ab3llini
 * @since   20/05/17.
 */
public class LoginRequest extends Request {

    private final String username;
    private final String password;

    public LoginRequest(String username, String password) {

        super(RequestType.Login);

        this.username = username;
        this.password = password;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

