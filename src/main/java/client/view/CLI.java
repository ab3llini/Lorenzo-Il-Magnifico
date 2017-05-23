package client.view;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */

import client.controller.network.Client;
import client.controller.network.ClientObserver;
import client.controller.network.RMI.RMIClient;
import client.controller.network.Socket.SocketClient;
import client.view.utility.AsyncInputStream;
import client.view.utility.AsyncInputStreamObserver;
import logger.Level;
import logger.Logger;
import netobject.NetObject;
import netobject.request.auth.LoginRequest;

/**
 * The command line interface for the game :/
 */
public class CLI implements AsyncInputStreamObserver, ClientObserver {

    //The client that will be used
    private Client client;

    private AsyncInputStream keyboard;

    /**
     * The command line interface constructor
     */
    public CLI() {

        //Initialization procedure
        this.keyboard = new AsyncInputStream(System.in);

        //Register us as observer
        this.keyboard.addObserver(this);

        //Launch the keyboard listener and wait for events
        this.keyboard.start();

    }

    private void bootstrap() {

        Logger.log(Level.FINE, "Bootstrap", "Select which connection method you would like to use:");

        for (String cmd : ClientTypeCommand.getAllCommands()) {

            Logger.log(Level.INFO, "Bootstrap", cmd);


        }

    }

    private void parseKeyboardInout(String value) {

        if (value.equals("1")) {

            this.client = new SocketClient("127.0.0.1", 4545);

        }
        else {

            this.client = new RMIClient("127.0.0.1", 1099, "server");

        }

        this.client.addObserver(this);

        this.client.connect();

        this.client.login(new LoginRequest("alberto", "unix"));

    }


    public static void main(String[] args) {

        (new CLI()).bootstrap();

    }

    public void onInput(AsyncInputStream stream, String value) {

        if (stream == this.keyboard) {

            this.parseKeyboardInout(value);

        }

    }

    public void onObjectReceived(Client client, NetObject object) {

    }

    public void onDisconnection(Client client) {

    }

    public void onLoginFailed(Client client, String reason) {

    }

    public void onLoginSuccess(Client client) {

    }
}
