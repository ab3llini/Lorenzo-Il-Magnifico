package server.controller.game;

import logger.Level;
import logger.Logger;
import netobject.notification.LobbyNotification;
import netobject.notification.LobbyNotificationType;
import server.controller.network.ClientHandler;
import server.model.Match;
import server.utility.UnicodeChars;
import singleton.Database;
import java.util.ArrayList;


/*
 * @author  ab3llini
 * @since   07/07/17.
 */
public class PersistenceLobby extends Lobby {


    private ArrayList<String> previousPlayers = new ArrayList<>();;

    private int privateMatchId = -1;

    private Match previousMatch;


    /**
     * Creates a new Lobby.
     * Every lobby is created with a player at first
     *
     * @param firstHandler the client handler that created the lobby
     */
    public PersistenceLobby(ClientHandler firstHandler, int privateMatchId) {

        super(firstHandler);

        this.privateMatchId = privateMatchId;

        //Add this player to the list of previous players
        this.previousPlayers.add(firstHandler.getUsername());

        //FIll the list with all the previous players

        this.previousPlayers.addAll(Database.getInstance().wasInMatchWithHim(firstHandler.getUsername()));

        this.previousMatch = Database.getInstance().getMatchFromID(privateMatchId);

        this.MINIMUM_PLAYERS = this.previousPlayers.size();

        this.MAXIMUM_PLAYERS = this.MINIMUM_PLAYERS;

    }

    /**
     * Method called to joint tha lobby.
     * It constantly checks whether or not there are enough handlers to start the timeout
     * When the minimum number requirement is fulfilled a task is scheduled with the specified amount of time
     * @param handler the client handler who wants to join the lobby
     * @return true if the join was successful
     */
    @Override
    public synchronized boolean join(ClientHandler handler) {

        //If the lobby is full
        if (this.handlers.size() >= MAXIMUM_PLAYERS || this.matchDidStart) {

            return false;

        }

        //If the player was not playing in the match of this persistence lobby
        if (this.handlers.size() > 1 && !this.previousPlayers.contains(handler.getUsername())) {

            return false;

        }

        //Add the new handler
        this.handlers.add(handler);

        Logger.log(Level.FINEST, this.toString(), "Client " + handler.getUsername() + " has joined!");


        this.notifyAllExcept(handler, new LobbyNotification(LobbyNotificationType.ClientJoin, "Client " + handler.getUsername() + " has joined!"));


        if (this.previousPlayers != null && this.handlers.size() == this.previousPlayers.size()) {

            //When the all the previous players have joined, start the match and clear the timeout
            this.startMatch();

        }

        return true;

    }

    @Override
    protected void startMatch() {


        if (this.handlers.size() < MINIMUM_PLAYERS) {

            Logger.log(Level.WARNING, this.toString(), "The timeout might have expired while a client was disconnecting, not enough players to start");

            return;

        }

        this.matchDidStart = true;

        Logger.log(Level.FINE, this.toString(), "Resuming the match that was suspended...");

        this.matchController = new MatchController(this.handlers, this, this.previousMatch);

        this.matchControllerDaemon = new Thread(matchController);

        this.matchController.setDaemon(this.matchControllerDaemon);

        this.matchController.addObserver(this);

        this.matchControllerDaemon.start();


    }

    @Override
    public String toString() {
        //Returns the name of the client handler that created the lobby
        return  this.name + "'s Persistent Lobby";
    }

    @Override
    public void welcomeClient(ClientHandler handler) {
        super.welcomeClient(handler);

        handler.sendLobbyNotification(new LobbyNotification(LobbyNotificationType.LobbyInfo, "Waiting for this players to reconnect:"));

        handler.sendLobbyNotification(new LobbyNotification(LobbyNotificationType.LobbyInfo, this.getRequestedPlayers()));


    }

    protected String getRequestedPlayers() {

        StringBuilder b = new StringBuilder();

        for (String name : this.previousPlayers) {

            if (!name.equals(this.previousPlayers.get(this.previousPlayers.size() - 1))) {

                b.append(UnicodeChars.Man);
                b.append(" ");
                b.append(name);
                b.append(", ");

            }
            else {

                b.append(UnicodeChars.Man);
                b.append(" ");
                b.append(name);

            }

        }

        return b.toString();

    }

}
