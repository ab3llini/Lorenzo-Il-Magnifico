package server.controller.game;

/*
 * Created by alberto on 10/05/17.
 */

import exception.NoSuchHanlderException;
import logger.Level;
import logger.Logger;
import netobject.Notification;
import netobject.NotificationType;
import server.controller.network.ClientHandler;
import server.model.board.Player;
import singleton.GameConfig;

import java.util.*;

/**
 * The Lobby represents a virtual room where players wait for other players to join and for the match to begin.
 */


public class Lobby {

    //This table maps each clientHandler to it relative player object in the model
    private LinkedHashMap<ClientHandler, Player> players;

    //Match controller
    private MatchController matchController;

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

    /**
     * Creates a new Lobby.
     * Every lobby is created with a player at first
     * @param firstHandler the client handler that created the lobby
     */
    public Lobby(ClientHandler firstHandler) {


        //Create the players map
        this.players = new LinkedHashMap<ClientHandler, Player>();

        //Add the first player
        this.players.put(firstHandler, new Player(firstHandler.getUsername()));

        //Assign the lobby name
        this.name = firstHandler.getUsername();

        Logger.log(Level.FINE, this.toString(), firstHandler.getUsername() + " created the lobby.");

    }

    /**
     * Tells if the lobby has the specified client
     * @param handler the client handler
     * @return
     */
    public boolean hasClientHandler(ClientHandler handler) {

        return this.players.containsKey(handler);

    }

    /**
     * Tells if the lobby has the specified player
     * @param username the player username
     * @return
     */
    public boolean hasPlayer(String username) {

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

        return (!this.matchDidStart && this.players.size() < MAXIMUM_PLAYERS);

    }

    /**
     * Method called to joint tha lobby.
     * It constantly checks whether or not there are enough players to start the timeout
     * When the minimum number requirement is fulfilled a task is scheduled with the specified amount of time
     * @param handler the client handler who wants to join the lobby
     * @return true if the join was successful
     */
    public synchronized boolean join(ClientHandler handler) {

        //If the lobby is full
        if (this.players.size() >= MAXIMUM_PLAYERS || this.matchDidStart) {

            return false;

        }

        //Add the new player
        this.players.put(handler, new Player(handler.getUsername()));

        Logger.log(Level.FINEST, this.toString(), "Client " + handler.getUsername() + " has joined!");


        //Check whether or not to start the timeout
        if (this.players.size() == MINIMUM_PLAYERS) {

            /* Create always a new timer.
             * This operation must always be performed.
             * This because once a timer scheduled task get cancelled, nothing else can be scheduled.
             */
            this.timeout = new Timer();

            //Prepare the expiry date
            Calendar expiry = Calendar.getInstance();

            //Sum the timeout value
            expiry.add(Calendar.SECOND, GameConfig.getInstance().getMatchTimeout());

            //Set the timeout status variable
            this.timeoutDidStart = true;

            //Schedule the task with the callback
            this.timeout.schedule(new TimerTask() {

                public void run() {

                    //This is called when the timeout expires
                    Lobby.this.startMatch();


                }

            }, expiry.getTime());

            Logger.log(Level.FINEST, this.toString(), "Timeout started: " + GameConfig.getInstance().getMatchTimeout() + "s");

        }

        else if (this.players.size() == MAXIMUM_PLAYERS) {

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

        //Get the player in the model
        Player belongingPlayer = this.matchController.getMatch().getPlayerFromUsername(handler.getUsername());

        //Update his state! It was set to disabled
        belongingPlayer.setDisabled(false);

        //Remap the player with the new handler
        this.players.put(handler, belongingPlayer);

        Logger.log(Level.FINEST, this.toString(), "Client " + handler.getUsername() + " has rejoined!");

    }

    /**
     * Method to handle a player disconnection
     * When a client disconnects two things may happen:
     * If the match had already started, it must be stopped.
     * If the match hadn't started yet:
     * If there were only two players the timeout gets cleared
     * Otherwise just a notification to other clients
     * @param handler the handler of the client that has left
     * @return int the number of clients in the lobby after the leaving
     */
    public synchronized int leave(ClientHandler handler) throws NoSuchHanlderException {

        if (this.players.get(handler) == null) {

            throw new NoSuchHanlderException("The player did not belong to this lobby");

        }

        if (this.matchDidStart) {

            Logger.log(Level.WARNING, this.toString(), "Client " + handler.getUsername() + " disconnected while playing, disabling player..");

            //Permanently disable the player
            this.players.get(handler).setDisabled(true);

        }
        else {

            Logger.log(Level.FINEST, this.toString(), "Client " + handler.getUsername() + " left");

            if (this.players.size() > MINIMUM_PLAYERS) {

                //TODO: Notify players of client disconnection, but let the timeout keep on going

            }
            else {

                if (this.timeoutDidStart) {

                    this.stopTimeout();

                }


            }

        }

        //Remove any reference to the handler (If the match has started the player is still accessible in the model).
        this.players.remove(handler);

        return this.players.size();

    }

    private void stopTimeout() {

        //Cancel the timeout if less than 2 players are in the room
        this.timeout.cancel();

        //Let the old timer object get garbage collected
        this.timeout = null;

        //Change the status variable
        this.timeoutDidStart = false;

        Logger.log(Level.FINEST, this.toString(), "Timeout stopped");

    }

    private void notifyMatchAborted() {

        while(this.players.keySet().iterator().hasNext()) {

            this.players.keySet().iterator().next().sendObject(new Notification(NotificationType.MatchAborted));

        }

    }

    public boolean isEmpty() {

        return (this.players.size() == 0);

    }

    /**
     * Starts the match
     * It loads the match controller with the players
     */
    public void startMatch() {

        this.matchDidStart = true;

        Logger.log(Level.FINE, this.toString(), "Match started");

        this.matchController = new MatchController(this.getPlayers());

    }

    public ArrayList<Player> getPlayers() {

        return new ArrayList<Player>(this.players.values());

    }

    public ArrayList<ClientHandler> getClients() {

        return new ArrayList<ClientHandler>(this.players.keySet());

    }

    public LinkedHashMap<ClientHandler, Player> getMapping() {
        return players;
    }

    @Override
    public String toString() {

        //Returns the name of the client handler that created the lobby
        return  this.name + "'s Lobby";

    }

    public boolean hasStarted() {
        return this.matchDidStart;
    }
}
