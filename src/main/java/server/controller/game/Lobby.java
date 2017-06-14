package server.controller.game;

/*
 * Created by alberto on 10/05/17.
 */

import exception.NoSuchHanlderException;
import exception.NoSuchPlayerException;
import logger.Level;
import logger.Logger;
import netobject.notification.LobbyNotification;
import netobject.notification.LobbyNotificationType;
import server.controller.network.ClientHandler;
import server.model.board.Player;
import server.utility.UnicodeChars;
import singleton.GameConfig;

import java.util.*;

/**
 * The Lobby represents a virtual room where handlers wait for other handlers to join and for the match to begin.
 */


public class Lobby {

    //This array list holds the clients in the lobby
    private ArrayList<ClientHandler> handlers;

    //Match controller
    private MatchController matchController;

    //Match controller runner thread
    private Thread matchControllerRunner;

    //Timer to start the match
    private Timer timeout;

    //The name of the lobby, given by the first player
    private String name;

    //Status variable that tells if the match has started
    private boolean matchDidStart = false;

    //Status variable that tells if the timeout has started
    private boolean timeoutDidStart = false;

    //Constants
    private static final int MAXIMUM_PLAYERS = 4;
    private static final int MINIMUM_PLAYERS = 2;
    private static final int START_DELAY = GameConfig.getInstance().getMatchTimeout() * 1000;

    /**
     * Creates a new Lobby.
     * Every lobby is created with a player at first
     * @param firstHandler the client handler that created the lobby
     */
    public Lobby(ClientHandler firstHandler) {


        //Create the handlers map
        this.handlers = new ArrayList<ClientHandler>();

        //Assign the lobby name
        this.name = firstHandler.getUsername();

        this.join(firstHandler);

        Logger.log(Level.FINE, this.toString(), firstHandler.getUsername() + " created the lobby.");

    }

    /**
     * Tells if the lobby has the specified client
     * @param handler the client handler
     * @return
     */
    public boolean hasClientHandler(ClientHandler handler) {

        return this.handlers.contains(handler);

    }

    /**
     * Tells if the lobby has the specified player
     * @param username the player username
     * @return
     */
    public boolean hasPlayer(String username) {

        if (this.matchController == null) {

            return false;

        }

        for (Player p : this.matchController.getMatch().getPlayers()) {

            if (p.getUsername().equals(username)) {

                return true;

            }

        }

        return false;

    }


    /**
     * If the timeout has not expired yet the lobby is in a joinable state.
     * @return true or false
     */
    public boolean isJoinable() {

        return (!this.matchDidStart && this.handlers.size() < MAXIMUM_PLAYERS);

    }

    /**
     * Method called to joint tha lobby.
     * It constantly checks whether or not there are enough handlers to start the timeout
     * When the minimum number requirement is fulfilled a task is scheduled with the specified amount of time
     * @param handler the client handler who wants to join the lobby
     * @return true if the join was successful
     */
    public synchronized boolean join(ClientHandler handler) {

        //If the lobby is full
        if (this.handlers.size() >= MAXIMUM_PLAYERS || this.matchDidStart) {

            return false;

        }

        //Add the new handler
        this.handlers.add(handler);

        Logger.log(Level.FINEST, this.toString(), "Client " + handler.getUsername() + " has joined!");

        this.welcomeClient(handler);

        this.notifyAllExcept(handler, new LobbyNotification(LobbyNotificationType.ClientJoin, "Client " + handler.getUsername() + " has joined!"));

        this.showConnectedClients();


        //Check whether or not to start the timeout
        if (this.handlers.size() == MINIMUM_PLAYERS) {

            /* Create always a new timer.
             * This operation must always be performed.
             * This because once a timer scheduled task get cancelled, nothing else can be scheduled.
             */
            this.timeout = new Timer();

            //Set the timeout status variable
            this.timeoutDidStart = true;

            //Schedule the task with the callback
            this.timeout.schedule(new TimerTask() {

                public void run() {

                    //This is called when the timeout expires
                    Lobby.this.startMatch();


                }

            }, START_DELAY);

            Logger.log(Level.FINEST, this.toString(), "Timeout started: " + GameConfig.getInstance().getMatchTimeout() + "s");


            this.notifyAll(new LobbyNotification(LobbyNotificationType.TimeoutStarted, "The match will start in " + GameConfig.getInstance().getMatchTimeout() + "s" ));


        }

        else if (this.handlers.size() > MINIMUM_PLAYERS && this.handlers.size() < MAXIMUM_PLAYERS) {

            this.notifyAll(new LobbyNotification(LobbyNotificationType.TimeoutStarted, "The match will soon.." ));

        }

        else if (this.handlers.size() == MAXIMUM_PLAYERS) {

            //When the fourth player joins, start the match and clear the timeout
            this.stopTimeout();
            this.startMatch();

        }

        return true;

    }

    /**
     * Remaps a client handler to a player that is already playing.
     * Note that event though getPlayerForUsername may return null
     * when this method is triggered a check has already been made
     * to be sure that this lobby's match instance has that player.
     * @param handler
     * @return
     */
    public synchronized void joinAfterDisconnection(ClientHandler handler) {


        try {

            //Re add the client to the handlers list
            this.handlers.add(handler);

            Logger.log(Level.FINEST, this.toString(), "Client " + handler.getUsername() + " has rejoined!");

            handler.sendLobbyNotification(new LobbyNotification(LobbyNotificationType.ResumeGame, "Welcome back to game " + handler.getUsername()));

            //Inform the match controller after sending the game resume notification
            this.matchController.addPlayer(handler);


        } catch (NoSuchPlayerException e) {

            Logger.log(Level.SEVERE, this.toString(), "Cant find the player corresponding player after rejoin!", e);

        }

    }

    /**
     * Method to handle a player disconnection
     * When a client disconnects two things may happen:
     * If the match had already started, it must be stopped.
     * If the match hadn't started yet:
     * If there were only two handlers the timeout gets cleared
     * Otherwise just a notification to other clients
     * @param handler the handler of the client that has left
     * @return int the number of clients in the lobby after the leaving
     */
    public synchronized int leave(ClientHandler handler) throws NoSuchHanlderException {

        if (!this.handlers.contains(handler)) {

            throw new NoSuchHanlderException("The player did not belong to this lobby");

        }
        else {

            //Remove any reference to the handler (If the match has started the player is still accessible in the model).
            this.handlers.remove(handler);

        }

        if (this.matchDidStart) {

            Logger.log(Level.WARNING, this.toString(), "Client " + handler.getUsername() + " disconnected while playing, disabling player..");

            try {

                //Inform the match controller
                this.matchController.removePlayer(handler);

            } catch (NoSuchPlayerException e) {

                Logger.log(Level.SEVERE, this.toString(), "Unable to disable player after client leave!", e);

            }

        }
        else {

            Logger.log(Level.FINEST, this.toString(), "Client " + handler.getUsername() + " left");

            this.notifyAllExcept(handler, new LobbyNotification(LobbyNotificationType.ClientLeave, "Client " + handler.getUsername() + " has left the lobby."));

            this.showConnectedClients();

            if (this.handlers.size() < MINIMUM_PLAYERS) {

                if (this.timeoutDidStart) {

                    this.stopTimeout();

                    this.notifyAllExcept(handler, new LobbyNotification(LobbyNotificationType.TimeoutStopped, "Timeout stopped, not enough players!"));

                }


            }

        }

        return this.handlers.size();

    }

    private synchronized void stopTimeout() {

        //Cancel the timeout if less than 2 handlers are in the room
        this.timeout.cancel();

        //Let the old timer object get garbage collected
        this.timeout = null;

        //Change the status variable
        this.timeoutDidStart = false;

        Logger.log(Level.FINEST, this.toString(), "Timeout stopped");

    }


    public boolean isEmpty() {

        return (this.handlers.size() == 0);

    }

    /**
     * Starts the match
     * It loads the match controller with the handlers
     */
    private void startMatch() {

        if (this.handlers.size() < MINIMUM_PLAYERS) {

            Logger.log(Level.WARNING, this.toString(), "The timeout might have expired while a client was disconnecting, not enough players to start");

            return;

        }

        this.matchDidStart = true;

        Logger.log(Level.FINE, this.toString(), "Match started");

        this.notifyAll(new LobbyNotification(LobbyNotificationType.MatchStart, "The match has started"));

        this.matchController = new MatchController(this.handlers, this);

        this.matchControllerRunner = new Thread(matchController);

        this.matchControllerRunner.start();

    }

    /**
     * Stops the match controller thread if need to
     */
    public void destroy() {

        if (this.matchControllerRunner != null && this.matchControllerRunner.isAlive()) {

            this.matchControllerRunner.interrupt();

        }

    }

    private void welcomeClient(ClientHandler handler) {

        handler.sendLobbyNotification(new LobbyNotification(LobbyNotificationType.LobbyInfo, "Hi " + handler.getUsername() + ", welcome to " + this.toString()));
        handler.sendLobbyNotification(new LobbyNotification(LobbyNotificationType.LobbyInfo, "Action timeout set to " + GameConfig.getInstance().getPlayerTimeout() + "s"));

    }

    private void showConnectedClients() {

        StringBuilder b = new StringBuilder();

        b.append("Players waiting in the lobby: ");

        for (ClientHandler h : this.handlers) {

            if (h != this.handlers.get(this.handlers.size() - 1)) {

                b.append(UnicodeChars.Man);
                b.append(" ");
                b.append(h.getUsername());
                b.append(", ");

            }
            else {

                b.append(UnicodeChars.Man);
                b.append(" ");
                b.append(h.getUsername());

            }

        }

        this.notifyAll(new LobbyNotification(LobbyNotificationType.LobbyInfo, b.toString()));

    }

    private synchronized void notifyAllExcept(ClientHandler handler, LobbyNotification not) {

        for (ClientHandler c : this.handlers) {

            if (c != handler) {

                c.sendLobbyNotification(not);

            }

        }

    }

    private synchronized void notifyAll(LobbyNotification not) {

        for (ClientHandler c : this.handlers) {

            c.sendLobbyNotification(not);
            
        }

    }

    public ArrayList<ClientHandler> getHandlers() {

        return this.handlers;

    }


    @Override
    public String toString() {

        //Returns the name of the client handler that created the lobby
        return  this.name + "'s Lobby";

    }

    public MatchController getMatchController() {
        return matchController;
    }

    public boolean hasStarted() {
        return this.matchDidStart;
    }
}
