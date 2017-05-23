package client.view;

import logger.Level;
import logger.Logger;

import java.util.ArrayList;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */
public enum ClientTypeCommand {

    Socket("Socket"),
    RMI("RMI");

    private final String literal;

    private ClientTypeCommand(String literal) {

        this.literal = literal;

    }

    public String toString() {

        return this.literal;

    }

    public static ArrayList<String> getAllCommands() {

        ClientTypeCommand[] all = ClientTypeCommand.values();

        ArrayList<String> list = new ArrayList<String>();

        for (ClientTypeCommand c : all) {

            list.add(c.toString());

        }

        return list;

    }

    public static void printCommands() {

        for (String cmd : ClientTypeCommand.getAllCommands()) {

            Logger.log(Level.FINE, "Bootstrap", "Select which connection method you would like to use:");


        }
    }


}
