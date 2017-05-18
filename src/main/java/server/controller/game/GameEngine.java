package server.controller.game;

/*
 * Created by alberto on 09/05/17.
 */

import exception.NoSuchLobbyException;
import logger.Level;
import logger.Logger;
import server.controller.network.*;
import server.controller.network.RMI.RMIClientHandler;
import server.controller.network.RMI.RMIServer;
import server.controller.network.Socket.SocketServer;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Rappresenta il server di gioco.
 */
public class GameEngine implements ServerObserver {

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
    private Lobby getLobbyForClientHanlder(ClientHandler handler) throws NoSuchLobbyException {

        //Lookup the lobby to which the client belongs
        for (Lobby lobby : this.lobbies) {

            if (lobby.hasClientHandler(handler)) {

                return lobby;

            }

        }

        throw new NoSuchLobbyException("No lobby found for handler: " + handler);

    }

    public void onError(Server server) {

        Logger.log(Level.SEVERE, "GameEngine", "Error encountered on server " + server.toString());

    }

    public void onConnection(Server server, ClientHandler handler) {

        Logger.log(Level.FINE, "GameEngine", "New client connected, username = " + handler.getUsername());

    }

    public void onDisconnection(Server server, ClientHandler handler) {

        Logger.log(Level.WARNING, "GameEngine", "New client disconnected, username = " + handler.getUsername());


    }

    public boolean onRegistrationRequest(Server server, String username) {

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
