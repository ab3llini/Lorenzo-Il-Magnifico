package server.controller.game;

/*
 * Created by alberto on 09/05/17.
 */

import exception.NoSuchHanlderException;
import exception.NoSuchLobbyException;
import logger.Level;
import logger.Logger;
import server.controller.network.*;
import server.controller.network.RMI.RMIClientHandler;
import server.controller.network.RMI.RMIServer;
import server.controller.network.Socket.SocketServer;
import singleton.GameConfig;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Represents the game engine.
 */

public class GameEngine implements ServerObserver {

    //The game configuration
    private GameConfig config = GameConfig.getInstance();

    //Il server in ascolto per nuove connessioni TCP su socket
    private SocketServer socketServer;

    //Il server in ascolto per nuove connessioni RMI
    private RMIServer rmiServer;

    //Le lobby esistenti
    private ArrayList<Lobby> lobbies;

    public GameEngine() {

        //Initialize the lobbies
        this.lobbies = new ArrayList<Lobby>();

        //Initialize the servers
        this.socketServer = new SocketServer(config.getSocketPort());
        this.rmiServer = new RMIServer(config.getRmiPort(), "server");

        //Register us as observer
        this.socketServer.addObserver(this);
        this.rmiServer.addObserver(this);

        //Start the servers
        this.rmiServer.start();
        (new Thread(this.socketServer)).start();

    }


    /**
     * Get a reference to the lobby which holds a reference to the provided handler
     * @param handler The handler to perform the lookup with
     * @return An active lobby is returned when is found.
     * @throws NoSuchLobbyException
     */
    private synchronized Lobby getLobbyForClientHanlder(ClientHandler handler) throws NoSuchLobbyException {

        //Lookup the lobby to which the client belongs
        for (Lobby lobby : this.lobbies) {

            if (lobby.hasClientHandler(handler)) {

                return lobby;

            }

        }

        throw new NoSuchLobbyException("No lobby found for handler: " + handler);

    }

    private synchronized Lobby joinLobby(ClientHandler handler) {

        for (Lobby lobby : this.lobbies) {

            if (lobby.isJoinable()) {

                lobby.join(handler);

                return lobby;

            }

        }

        Lobby newLobby = new Lobby(handler);

        this.lobbies.add(newLobby);

        return newLobby;

    }

    private synchronized Lobby leaveLobby(ClientHandler handler) throws NoSuchLobbyException {

        Lobby lobby = this.getLobbyForClientHanlder(handler);

        try {

            if (lobby.leave(handler) == 0) {

                //If after the leaving there are no more players in the lobby, it gets destroyed.
                this.lobbies.remove(lobby);

                Logger.log(Level.FINEST, "GameEngine", lobby.toString() + " closed");


            }

        }
        catch (NoSuchHanlderException e) {

            Logger.log(Level.SEVERE, "GameEngine", "Client does not belong to the lobby specified", e);

        }

        return lobby;

    }


    public void onError(Server server) {

        Logger.log(Level.SEVERE, "GameEngine", "Error encountered on server " + server.toString());

    }

    public void onConnection(Server server, ClientHandler handler) {

        Logger.log(Level.FINE, "GameEngine", "New client connected, username = " + handler.getUsername());

        this.joinLobby(handler);

    }

    public void onDisconnection(Server server, ClientHandler handler) {

        Logger.log(Level.FINE, "GameEngine", "Client disconnected, username = " + handler.getUsername());

        try {

            //Try to leave the lobby
            this.leaveLobby(handler);

        }
        catch (NoSuchLobbyException e) {

            Logger.log(Level.FINE, "GameEngine", "Unable to find the lobby to leave!", e);

        }


    }

    /**
     * Loops through all the clients (either RMI or Socket) and looks for a client with the provided username
     * Must be synchronized because access cannot be simultaneous
     * What if two client attempt to register with the same name at the same time ?
     * @param server The server
     * @param username The username to check the new client with
     * @return true if exists a client with the same username, false otherwise
     */
    public synchronized boolean onRegistrationRequest(Server server, String username) {

        if (username == null) {

            return false;

        }

        for (RMIClientHandler client : this.rmiServer.getClientHandlers()) {

            if (client.getUsername().equals(username)) {

                return true;
            }

        }


        for (Object o : this.socketServer.getClientHandlers().entrySet()) {

            HashMap.Entry pair = (HashMap.Entry) o;

            String uname = ((server.controller.network.Socket.SocketClientHandler) pair.getKey()).getUsername();

            if (uname != null && uname.equals(username)) {

                return true;

            }

        }

        return false;
    }

    public static void main(String[] args) {
        new GameEngine();
    }

}
