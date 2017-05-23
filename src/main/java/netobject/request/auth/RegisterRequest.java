package netobject.request.auth;

import netobject.request.Request;
import netobject.request.RequestType;

public abstract class RegisterRequest extends Request {

    private final String username;
    private final String password;
    private final String name;
    private final String surname;
    private final String gender;

    protected RegisterRequest(String username, String password, String name, String surname, String gender) {

        super(RequestType.Registration);

        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.gender = gender;

    }
}
