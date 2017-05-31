package client.view.cmd;

/**
 * Authentication commands
 */
public enum AuthType implements CliPrintable {

    Login("Login"),
    Registration("Registration");

    private final String name;

    AuthType(String name) {

        this.name = name;

    }

    public String toString() {
        return name;
    }

}
