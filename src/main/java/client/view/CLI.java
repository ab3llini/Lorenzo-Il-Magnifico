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
import logger.Level;
import logger.Logger;
import netobject.notification.LobbyNotification;
import netobject.notification.LobbyNotificationType;
import netobject.notification.Notification;
import netobject.request.auth.LoginRequest;
import server.model.Match;
import singleton.GameConfig;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * The command line interface for the game :/
 */
public class CLI implements AsyncInputStreamObserver, ClientObserver {

    /**
     * The client handler that will be dynamically bounded and used
     */
    private Client client;

    /**
     * An asynchronous stream that listens for user input. It is event based with observers.
     */
    private final AsyncInputStream keyboard;

    /**
     * This is the blocking queue that will be filled with new user input
     */
    private final BlockingQueue<String> inputQueue;

    /**
     * This is the blocking queue that will be filled with notifications
     */
    private final BlockingQueue<Notification> notificationQueue;

    /**
     * This mutex is used to wait until a connection-driven event occurs
     */
    private final Object connectionMutex;

    private final Object moveMutex;

    /**
     * Event though the CLI thread might be suspended, the Asynchronous stream will never be
     * Therefore it is a priority to prevent the user from entering any input
     */
    private boolean keyboardEnabled = true;

    /**
     * Defines the context on which the CLI is operating
     * Please see the relative enum for more details
     */
    private CliContext ctx;

    /**
     * The copy of the match model
     */
    private Match match;

    /**
     * The command line interface constructor
     * Initializes the required objects and proceeds with a bootstrap phase
     */
    private CLI() {

        //Init blocking queue for user input
        this.inputQueue = new ArrayBlockingQueue<String>(10);

        //Init blocking queue for server data
        this.notificationQueue = new ArrayBlockingQueue<Notification>(10);

        //Init the mutex
        this.connectionMutex = new Object();

        //Init the mutex
        this.moveMutex = new Object();

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
     * Pauses the main thread until a onLobbyNotification is performed on the provided mutex object
     * @param mutex the object on which the lock is acquired
     */
    private void waitOnMutex(Object mutex) {

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

            this.inputQueue.add(value);

        }

    }

    /**
     * Starts the command line interface
     * Requests the server ip address and the connection type
     */
    private void bootstrap() throws InterruptedException {

        //Set the context
        this.ctx = CliContext.Bootstrap;

        String hostIP;
        String connection;

        //Request the IP
        Cmd.askFor("Please enter the IP address of the host");

        //Read the IP
        hostIP = this.inputQueue.take();

        //Check it
        while (hostIP.equals("")) {

            Cmd.askFor("Invalid IP address");

            hostIP = this.inputQueue.take();

        }

        //Request the connection type
        Cmd.askFor("Select which connection method you would like to use");

        //Print the available choices
        Cmd.printChoices(ClientTypeCmd.values());

        //Read the selection
        connection = this.inputQueue.take();

        //Check it
        while (!Cmd.isValid(ClientTypeCmd.values(), connection)) {

            connection = this.inputQueue.take();

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

    /**
     * Authenticates the user
     * @throws InterruptedException blockingQueue take
     */
    private void authenticate() throws InterruptedException {

        //Switch context
        this.ctx = CliContext.Authentication;

        //Request the IP
        Cmd.askFor("Please select how to authenticate");

        //Print the available choices
        Cmd.printChoices(AuthTypeCmd.values());

        String choice = this.inputQueue.take();

        //Check it
        while (!Cmd.isValid(AuthTypeCmd.values(), choice)) {

            choice = this.inputQueue.take();

        }

        if (choice.equals(AuthTypeCmd.Login.getValue())) {

            this.login();

        }
        else if (choice.equals(AuthTypeCmd.Registration.getValue())) {

            this.register();

        }
        else {

            Logger.log(Level.SEVERE, "Authentication", "Bad selection");;

        }

    }

    /**
     * Logs in the user
     * @throws InterruptedException blockingQueue take
     */
    private void login() throws InterruptedException {

        //Switch context
        this.ctx = CliContext.Login;

        String username, password;

        //Request the IP
        Cmd.askFor("Please enter your username");

        username = this.inputQueue.take();

        //Request the IP
        Cmd.askFor("Please enter your password");

        password = this.inputQueue.take();

        //Perform login request
        this.client.login(new LoginRequest(username, password));

        //On RMI clients the login method, after has returned, has already called the onLobbyNotification.
        //We need to wait only for socket logins
        if (this.client instanceof SocketClient) {

            //Suspend and wait for a response
            this.keyboardEnabled = false;

            //Wait for the server response
            this.waitOnMutex(this.connectionMutex);

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

    /**
     * Notify the user about the events that happen while waiting for the match to start
     * @throws InterruptedException blockingQueue take
     */
    private void interactWithLobby() throws InterruptedException {

        this.ctx = CliContext.Lobby;

        this.keyboardEnabled = false;

        //Assuming that before the match start the client will receive just Lobby notifications
        //Read the first notification
        LobbyNotification o = (LobbyNotification)this.notificationQueue.take();

        //Keep posting notifications until the match starts
        do {

            Cmd.notify(o.getMessage());

            o = (LobbyNotification)this.notificationQueue.take();


        }
        while (o.getLobbyNotificationType() != LobbyNotificationType.MatchStart);

        Cmd.notify(o.getMessage());

        this.keyboardEnabled = true;

        this.play();

    }

    private void play() {

        this.ctx = CliContext.Match;

        synchronized (this.moveMutex) {

            //TODO : Make the move!

        }

    }

    public void onDisconnection(Client client) {

        Cmd.notify("Connection lost.");

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

    public void onLobbyNotification(Client client, LobbyNotification not) {

        this.notificationQueue.add(not);

    }

    public static void main(String[] args) throws InterruptedException {

        (new CLI()).bootstrap();

    }



    public void onModelUpdate(Client sender, Match model) {

        this.match = model;

        this.match.getBoard().printBoard();

    }

    public void onMoveEnabled(Client sender, String message) {

        Cmd.notify(message);

    }

    public void onMoveDisabled(Client sender, String message) {

        Cmd.notify(message);

    }

    public void onTimeoutExpired(Client sender, String message) {

        Cmd.notify(message);

    }

    public void onActionRefused(Client sender, String message) {

        Cmd.notify(message);

    }
}
