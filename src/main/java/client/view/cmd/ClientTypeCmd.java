package client.view.cmd;


/*
 * @author  ab3llini
 * @since   23/05/17.
 */


/**
 * Client connectivity method command
 */
public enum ClientTypeCmd implements Command {

    Socket("Socket", "1"),
    RMI("RMI", "2");

    private final String name;
    private final String value;

    ClientTypeCmd(String name, String value) {

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

