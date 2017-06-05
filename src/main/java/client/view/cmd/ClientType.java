package client.view.cmd;


/*
 * @author  ab3llini
 * @since   23/05/17.
 */


/**
 * Client connectivity method command
 */


public enum ClientType implements CliPrintable {

    Socket("Socket"),
    RMI("RMI");

    private final String name;

    ClientType(String name) {

        this.name = name;

    }

    @Override
    public String toString() {
        return this.name;
    }
}

