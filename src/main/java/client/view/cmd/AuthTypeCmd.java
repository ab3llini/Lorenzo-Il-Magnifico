package client.view.cmd;

public enum AuthTypeCmd implements Command {

    Login("Login", "1"),
    Registration("Registration", "2");

    private final String name;
    private final String value;

    AuthTypeCmd(String name, String value) {

        this.name = name;
        this.value = value;

    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
