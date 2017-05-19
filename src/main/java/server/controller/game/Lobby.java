package server.controller.game;

/*
 * Created by alberto on 10/05/17.
 */

import exception.NoSuchHanlderException;
import logger.Level;
import logger.Logger;
import netobject.Action;
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
    private Timer timeout = new Timer();

    //Status variable
    private boolean hasStarted = false;

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

        Logger.log(Level.FINE, this.toString(), firstHandler.getUsername() + " created the lobby.");

    }

    /**
     * Tells if the lobby has the specified client playing
     * @param handler the client handler
     * @return
     */
    public boolean hasClientHandler(ClientHandler handler) {

        return this.players.containsKey(handler);

    }

    public boolean onClientAction(ClientHandler handler, Action action) {

        //TODO: Map the handler to the player and forward the action to the match controller

        return true;

    }

    public boolean onClientDisconnection(ClientHandler handler) {

        //TODO: Inform the controller, take right measures.

        return true;

    }

    /**
     * If the timeout has not expired yet the lobby is in a joinable state.
     * @return true or false
     */
    public boolean isJoinable() {

        return (!this.hasStarted && this.players.size() < MAXIMUM_PLAYERS);

    }

    /**
     * Method called to joint tha lobby.
     * It constantly checks whether or not there are enough players to start the timeout
     * When the minimum number requirement is fulfilled a task is scheduled with the specified amount of time
     * @param handler the client handler who wants to join the lobby
     * @return true if the join was successful
     */
    public boolean join(ClientHandler handler) {

        //If the lobby is full
        if (this.players.size() >= MAXIMUM_PLAYERS || this.hasStarted) {

            return false;

        }

        //Add the new player
        this.players.put(handler, new Player(handler.getUsername()));

        Logger.log(Level.FINEST, this.toString(), "Client " + handler.getUsername() + " has joined!");



        //Check whether or not to start the timeout
        if (this.players.size() == MINIMUM_PLAYERS) {

            //Prepare the expiry date
            Calendar expiry = Calendar.getInstance();

            //Sum the timeout value
            expiry.add(Calendar.SECOND, GameConfig.getInstance().getMatchTimeout());


            //Schedule the task with the callback
            this.timeout.schedule(new TimerTask() {

                public void run() {

                    //This is called when the timeout expires
                    Lobby.this.startMatch();

                }

            }, expiry.getTime());

            Logger.log(Level.FINEST, this.toString(), "Timeout started: " + GameConfig.getInstance().getMatchTimeout() + "s");

        }

        return true;

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
    public int leave(ClientHandler handler) throws NoSuchHanlderException {

        if (this.players.get(handler) == null) {

            throw new NoSuchHanlderException("The player did not belong to this lobby");

        }

        if (this.hasStarted) {

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

                this.timeout.cancel();

                Logger.log(Level.FINEST, this.toString(), "Timeout stopped");


            }

        }

        //Remove any reference to the handler (If the match has started the player is still accessible in the model).
        this.players.remove(handler);

        return this.players.size();

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

        this.hasStarted = true;

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
        return  this.players.keySet().iterator().next().getUsername() + "'s Lobby";
    }


}
