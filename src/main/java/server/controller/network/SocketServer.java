package server.controller.network;

/*
 * Created by alberto on 10/05/17.
 */


import logger.Level;
import logger.Logger;
import netobject.Action;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * The server which handles the clients connected via socket.
 * It extends thread due to the fact that the listing is a blocking procedure
 */
public class SocketServer extends AbstractServer implements Runnable {

    //The socket to perform the asynchronous listening on
    private ServerSocket acceptor;

    //The listening port
    private int port;

    //The TCP Socket client handlers
    private HashMap<SocketClientHandler, Thread> clientHandlers;

    /**
     * Constructor
     * @param port The port on which the listening will be performed
     */
    public SocketServer(int port) {

        //Initialize the handlers
        this.clientHandlers = new HashMap<SocketClientHandler, Thread>();

        //Assign the port
        this.port = port;

    }

    /**
     * Constructor with default 4545 port
     */
    public SocketServer() {

        this(4545);

    }

    public void run() {

        //Block start method if no one registered as listener.
        //There would be a null pointer exception when trying to raise an event
        if (this.listener == null) {

            Logger.log(logger.Level.SEVERE, "Server (Socket)", "The server can't run without a listener");

            return;

        }

        //Try to open a server socket
        try {
            this.acceptor = new ServerSocket(this.port);

            Logger.log(logger.Level.INFO, "Server (Socket)", "Socket server up and running on port " + this.port);

        }
        catch (IOException e) {

            Logger.log(logger.Level.SEVERE, "Server (Socket)", "Unable to open the acceptor socket", e);

            return;
        }

        //Run forever
        while (true) {

            try {

                //Accept every connection and handle its relative socket
                Socket newClientSocket = this.acceptor.accept();

                //Create a new client handler
                SocketClientHandler client = new SocketClientHandler(newClientSocket, this);

                //Register us as listeners
                client.addEventListener(this);

                //Prepare a dedicated thread for the client.
                Thread thread = new Thread(client);

                //Map the client
                clientHandlers.put(client, thread);

                //Start the handler
                thread.start();

            }
            catch (Exception e) {

                Logger.log(logger.Level.SEVERE, "Server (Socket)", "Exception while listening", e);

            }

        }

    }


    public void onDisconnect(AbstractClientHandler handler) {

        Logger.log(Level.WARNING, "Server (Socket)", "Client disconnected " + handler);

        //Terminate the thread
        this.clientHandlers.get(handler).interrupt();

        //Remove the reference
        this.clientHandlers.remove(handler);

    }

    public void onAction(AbstractClientHandler handler, Action action) {

        //Propagate the action to the game engine.. toward the lobby
        Logger.log(logger.Level.INFO, "Server (Socket)", "Client '"+handler+"' performed the action '"+action+"'");

    }

    public boolean existsClientWithUsername(String username) {

        return this.listener.existsClientWithUsername(username);

    }


    public HashMap<SocketClientHandler, Thread> getClientHandlers() {
        return clientHandlers;
    }

    public static void main(String[] args) {
        (new Thread(new SocketServer())).start();
    }
}
