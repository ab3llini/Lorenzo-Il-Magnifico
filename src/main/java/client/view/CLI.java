package client.view;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */

import client.controller.network.Client;
import client.controller.network.ClientObserver;
import client.controller.network.RMI.RMIClient;
import client.controller.network.Socket.SocketClient;
import client.view.cmd.AuthTypeCmd;
import client.view.cmd.ClientTypeCmd;
import client.view.utility.AsyncInputStream;
import client.view.utility.AsyncInputStreamObserver;
import client.view.cmd.Cmd;
import logger.AnsiColors;
import logger.Level;
import logger.Logger;
import netobject.NetObject;
import netobject.request.auth.LoginRequest;
import singleton.GameConfig;

/**
 * The command line interface for the game :/
 */
public class CLI implements AsyncInputStreamObserver, ClientObserver {

    //The client that will be used
    private Client client;

    //The keyboard stream
    private AsyncInputStream keyboard;

    private final Object keyboardMutex = new Object();

    private String keyboardInput = "";

    private boolean keyboardEnabled = true;

    private final Object connectionMutex = new Object();

    private CliContext ctx;

    /**
     * The command line interface constructor
     */
    private CLI() {

        this.ctx = CliContext.Bootstrap;

        //Initialization procedure
        this.keyboard = new AsyncInputStream(System.in);

        //Register us as observer
        this.keyboard.addObserver(this);

        //Launch the keyboard listener and wait for events
        this.keyboard.start();

        //Log only important messages
        Logger.setMinLevel(Level.WARNING);

    }

    /**
     * Pauses the main thread until a notify is performed on the provided mutex object
     * @param mutex the object on which the lock is acquired
     */
    private void waitForInputOnMutex(Object mutex) {

        synchronized (mutex) {

            try {

                mutex.wait();

            } catch (InterruptedException e) {

                e.printStackTrace();

            }
        }

    }

    /**
     * Interface implementation for AsyncInputStreamObserver
     * @param stream the stream that raised the event
     * @param value the value associated with the stream, supposed to be always a string in this case
     */
    public void onInput(AsyncInputStream stream, String value) {

        if (stream == this.keyboard && this.keyboardEnabled) {

            synchronized (keyboardMutex) {

                //Assign the last input
                this.keyboardInput = value;

                //Resume the CLI thread
                keyboardMutex.notify();

            }

        }

    }

    /**
     * Starts the command line interface
     * Requests the server ip address and the connection type
     */
    private void bootstrap() {

        String hostIP;
        String connection;

        if (this.ctx != CliContext.Bootstrap) return;
        
        //Request the IP
        Cmd.askFor("Please enter the IP address of the host");

        //Suspend
        this.waitForInputOnMutex(this.keyboardMutex);

        //Read the IP
        hostIP = this.keyboardInput;

        //Check it
        while (hostIP.equals("")) {

            Cmd.askFor("Invalid IP address");

            this.waitForInputOnMutex(this.keyboardMutex);

            hostIP = this.keyboardInput;


        }

        //Request the connection type
        Cmd.askFor("Select which connection method you would like to use");

        //Print the available choices
        Cmd.printChoices(ClientTypeCmd.values());

        //Suspend
        this.waitForInputOnMutex(this.keyboardMutex);

        //Read the selection
        connection = this.keyboardInput;

        //Check it
        while (!Cmd.isValid(ClientTypeCmd.values(), connection)) {

            this.waitForInputOnMutex(this.keyboardMutex);

            connection = this.keyboardInput;

        }

        //Select the proper client interface
        if (connection.equals(ClientTypeCmd.Socket.getValue())) {

            this.client = new SocketClient(hostIP, GameConfig.getInstance().getSocketPort());

        }
        else if (connection.equals(ClientTypeCmd.RMI.getValue())) {

            this.client = new RMIClient(hostIP, GameConfig.getInstance().getRmiPort(), "server");

        }
        else {

            Logger.log(Level.SEVERE, "Bootstrap", "Bad selection");

            return;

        }

        //Connect the client
        this.client.connect();

        //Register us as observer
        this.client.addObserver(this);

        this.authenticate();

    }

    private void authenticate() {

        //Switch context
        this.ctx = CliContext.Authentication;

        //Request the IP
        Cmd.askFor("Please select how to authenticate");

        //Print the available choices
        Cmd.printChoices(AuthTypeCmd.values());

        //Suspend
        this.waitForInputOnMutex(this.keyboardMutex);

        //Check it
        while (!Cmd.isValid(AuthTypeCmd.values(), this.keyboardInput)) {

            this.waitForInputOnMutex(this.keyboardMutex);

        }

        if (this.keyboardInput.equals(AuthTypeCmd.Login.getValue())) {

            this.login();

        }
        else if (this.keyboardInput.equals(AuthTypeCmd.Registration.getValue())) {

            this.register();

        }
        else {

            Logger.log(Level.SEVERE, "Authentication", "Bad selection");;

        }

    }

    private void login() {

        //Switch context
        this.ctx = CliContext.Login;

        String username, password;

        //Request the IP
        Cmd.askFor("Please enter your username");

        //Suspend
        this.waitForInputOnMutex(this.keyboardMutex);

        username = this.keyboardInput;

        //Request the IP
        Cmd.askFor("Please enter your password");

        //Suspend
        this.waitForInputOnMutex(this.keyboardMutex);

        password = this.keyboardInput;

        //Perform login request
        this.client.login(new LoginRequest(username, password));

        //On RMI clients the login method, after has returned, has already called the notify.
        //We need to wait only for socket logins
        if (this.client instanceof SocketClient) {

            //Suspend and wait for a response
            this.keyboardEnabled = false;

            //Wait for the server response
            this.waitForInputOnMutex(this.connectionMutex);

            this.keyboardEnabled = true;

        }


        if (this.client.hasAuthenticated()) {

            Cmd.notify("Login successful, welcome back " + client.getUsername());

            this.interactWithLobby();

        }
        else {

            Cmd.notify("Login failed");

            this.authenticate();

        }

    }

    private void register() {

        //Switch context
        this.ctx = CliContext.Registration;

    }

    private void interactWithLobby() {

        this.ctx = CliContext.Lobby;

    }

    public void onObjectReceived(Client client, NetObject object) {

    }

    public void onDisconnection(Client client) {

    }

    public void onLoginFailed(Client client, String reason) {

        synchronized (this.connectionMutex) {

            //Resume the CLI thread
            connectionMutex.notify();

        }

    }

    public void onLoginSuccess(Client client) {

        synchronized (this.connectionMutex) {

            //Resume the CLI thread
            connectionMutex.notify();

        }

    }

    public static void main(String[] args) {

        (new CLI()).bootstrap();

    }

}
