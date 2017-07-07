package server.controller.game;

/*
 * Created by alberto on 09/05/17.
 */

import client.controller.network.ObserverType;
import exception.NoSuchHanlderException;
import exception.NoSuchLobbyException;
import exception.PlayerNeverDisconnectedException;
import logger.Level;
import logger.Logger;
import netobject.action.Action;
import server.controller.network.*;
import server.controller.network.RMI.RMIServer;
import server.controller.network.Socket.SocketServer;
import singleton.Database;
import singleton.GameConfig;

import java.util.ArrayList;


/**
 * Represents the game engine.
 */

public class GameEngine implements ServerObserver, LobbyObserver {

    //Socket server listening for clients to connect
    private SocketServer socketServer;

    //RMI Server listening for RMI invocations
    private RMIServer rmiServer;

    //Reference to the lobbies
    private ArrayList<Lobby> lobbies;

    /**
     * Game engine constructor
     * Initializes the lobbies
     * Launches the servers
     */
    public GameEngine() {

        //Initialize the lobbies
        this.lobbies = new ArrayList<Lobby>();

        //Initialize the servers
        this.socketServer = new SocketServer(GameConfig.getInstance().getSocketPort(), this);
        this.rmiServer = new RMIServer(GameConfig.getInstance().getRmiPort(), "server", this);

        //Register us as observer
        this.socketServer.addObserver(this);
        this.rmiServer.addObserver(this);

        //Start the servers
        this.rmiServer.start();
        (new Thread(this.socketServer)).start();

    }

    private Lobby getLobby(ClientHandler handler) throws NoSuchLobbyException {

        //Lookup the lobby to which the client belongs
        for (Lobby lobby : this.lobbies) {

            if (lobby.hasClientHandler(handler)) {

                return lobby;

            }

        }

        throw new NoSuchLobbyException("No lobby found for handler: " + handler);

    }

    private Lobby getLobbyAfterDisconnection(ClientHandler handler) throws PlayerNeverDisconnectedException {

        for (Lobby l : this.lobbies) {

            //Loop through each lobby
            if (l.hasStarted()) {

                //If the match did already start, maybe the player is here and wants to reconnect after a disconnection
                if (l.hasPlayer(handler.getUsername())) {

                    return l;

                }

            }

        }

        throw new PlayerNeverDisconnectedException("The player " + handler.getUsername() + " never left a match while playing");

    }

    private synchronized Lobby joinStandardLobby(ClientHandler handler) {

        for (Lobby lobby : this.lobbies) {

            if (!(lobby instanceof PersistenceLobby) && lobby.isJoinable()) {

                lobby.join(handler);

                return lobby;

            }

        }

        Lobby newLobby = new Lobby(handler);

        this.lobbies.add(newLobby);

        newLobby.addObserver(this);

        return newLobby;

    }

    private synchronized Lobby joinPersistanceLobby(ClientHandler handler, int previousMatchId) {

        for (Lobby lobby : this.lobbies) {

            if (lobby instanceof PersistenceLobby && lobby.isJoinable()) {

                lobby.join(handler);

                return lobby;

            }

        }

        PersistenceLobby persistenceLobby = new PersistenceLobby(handler, previousMatchId);

        this.lobbies.add(persistenceLobby);

        persistenceLobby.addObserver(this);

        return persistenceLobby;

    }

    private synchronized Lobby leaveLobby(ClientHandler handler) throws NoSuchLobbyException {

        Lobby lobby = this.getLobby(handler);

        try {

            if (lobby.leave(handler) == 0 && !lobby.hasStarted()) {

                //If after the leaving there are no more players in the lobby and the match has not started yet, it gets destroyed.
                lobby.destroy();

                this.lobbies.remove(lobby);

                Logger.log(Level.FINEST, "GameEngine", lobby.toString() + " closed");

            }

        }
        catch (NoSuchHanlderException e) {

            Logger.log(Level.SEVERE, "GameEngine", "Client does not belong to the lobby found", e);

        }

        return lobby;

    }

    public void onError(Server server) {

        Logger.log(Level.SEVERE, "GameEngine", "Error encountered on server " + server.toString());

    }

    public void onAuthentication(Server server, ClientHandler handler) {

        Logger.log(Level.FINE, "GameEngine", "New client authenticated with username = " + handler.getUsername());

        //Assume first that the new client may have been already playing into a lobby but disconnected and reconnected
        //Find the lobby and let the player continue the match
        try {

            //Search the lobby whose model contains the player with the just-logged-in client's username
            this.getLobbyAfterDisconnection(handler).getHandlers().add(handler);

            //Upon success it means that the client was already in a lobby, just wait for his observer to load.
            //An automatic procedure will be triggered to rejoin the user


        }
        catch (PlayerNeverDisconnectedException e) {

            //If we get here it means that no player with the username of the just-logged-in client was playing
            //Follow the standard procedure: join the first available lobby (Standard or Persistent)
            int previousMatch = Database.getInstance().isAnUnfinishedMatchPlayer(handler.getUsername());

            if (previousMatch > 0) {

                //Persistence: must finish the match
                this.joinPersistanceLobby(handler, previousMatch);

            }
            else {

                //Standard join
                this.joinStandardLobby(handler);

            }


        }

    }

    public void onDisconnection(Server server, ClientHandler handler) {

        Logger.log(Level.FINE, "GameEngine", "Player " + handler.getUsername() + " disconnected");

        try {

            //Try to leave the lobby
            this.leaveLobby(handler);

        }
        catch (NoSuchLobbyException e) {

            Logger.log(Level.SEVERE, "GameEngine", "Unable to find the lobby to leave!", e);

        }


    }

    public void onAction(Server server, ClientHandler handler, Action action) {

        try {

            this.getLobby(handler).getMatchController().dispatchNewPlayerAction(action);


        }
        catch (NoSuchLobbyException e) {

            Logger.log(Level.SEVERE, "GameEngine", "Unable to find the lobby where the player is playing!", e);

        }

    }

    @Override
    public void onObserverReady(Server server, ClientHandler handler, ObserverType observerType) {

        Lobby belonging = null;

        try {

            belonging = this.getLobby(handler);


        }
        catch (NoSuchLobbyException e) {

            Logger.log(Level.SEVERE, "GameEngine", "Unable to find the lobby where the player is playing or waiting!", e);

        }


        switch (observerType) {

            case Lobby:

                belonging.welcomeClient(handler);

                break;

            case RemotePlayer:

                if (belonging.getMatchController().getContext() == MatchControllerContext.Playing) {
                    belonging.joinAfterDisconnection(handler);
                }
                else {
                    belonging.getMatchController().getReadyObservers().add(observerType);
                }

                break;

            default:

                Logger.log(Level.WARNING, "GameEngine", "Don't know who notify for this observer.");

                break;

        }

    }

    public boolean hasAlreadyAuthenticated(String username) {

        //return this.rmiServer.hasHandlerWithUsername(username) || this.socketServer.hasHandlerWithUsername(username);
       return this.socketServer.hasHandlerWithUsername(username);

    }

    public static void main(String[] args) {
        new GameEngine();
    }

    public void onDestroyRequest(Lobby lobby) {

        this.lobbies.remove(lobby);

        lobby.destroy();

    }
}
