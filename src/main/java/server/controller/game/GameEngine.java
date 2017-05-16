package server.controller.game;

/*
 * Created by alberto on 09/05/17.
 */

import exception.NoSuchLobbyException;
import logger.Logger;
import netobject.Action;
import server.controller.network.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Rappresenta il server di gioco.
 */
public class GameEngine implements AbstractServerListener {

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
        this.socketServer = new SocketServer(4545);
        this.rmiServer = new RMIServer(1099, "server");

        //Register us as listeners
        this.socketServer.addEventListener(this);
        this.rmiServer.addEventListener(this);

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
    private Lobby getLobbyForClientHanlder(AbstractClientHandler handler) throws NoSuchLobbyException {

        //Lookup the lobby to which the client belongs
        for (Lobby lobby : this.lobbies) {

            if (lobby.hasClientHandler(handler)) {

                return lobby;

            }

        }

        throw new NoSuchLobbyException("No lobby found for handler: " + handler);

    }

    public void onDisconnect(AbstractClientHandler handler) {

        try {
            this.getLobbyForClientHanlder(handler).onClientDisconnection(handler);
        }
        catch (NoSuchLobbyException e) {

            Logger.log(logger.Level.SEVERE, "Game engine", "Lobby not found", e);

        }

    }

    public void onAction(AbstractClientHandler handler, Action action) {

        try {
            this.getLobbyForClientHanlder(handler).onClientAction(handler, action);
        }
        catch (NoSuchLobbyException e) {

            Logger.log(logger.Level.SEVERE, "Game engine", "Lobby not found", e);

        }

    }

    /**
     * Checks whether exists a client with the provided username
     * @param username The username to perform the check on
     * @return Upon existence, true. False otherwise.
     */
    public boolean existsClientWithUsername(String username) {

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

            String uname = ((SocketClientHandler) pair.getKey()).getUsername();

            if (uname != null && uname.equals(username)) {

                return true;

            }

        }

        return false;
    }

    public void onServerFault(AbstractServer server) {

    }

    public static void main(String[] args) {
        new GameEngine();
    }

}
