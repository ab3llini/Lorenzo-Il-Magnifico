package netobject;

/*
 * @author  ab3llini
 * @since   20/05/17.
 */
public class LoginAuthentication extends NetObject {

    private final String username;
    private final String password;

    public LoginAuthentication(String username, String password) {
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

