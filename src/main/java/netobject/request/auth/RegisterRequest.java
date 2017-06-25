package netobject.request.auth;

import netobject.request.Request;
import netobject.request.RequestType;

public class RegisterRequest extends Request {

    private final String username;
    private final String password;


    public RegisterRequest(String username, String password) {

        super(RequestType.Registration);

        this.username = username;
        this.password = password;

    }
}
