package server.controller.network.Socket;

/*
 * Created by alberto on 10/05/17.
 */


import logger.Level;
import logger.Logger;
import netobject.LoginAuthentication;
import netobject.NetObject;
import server.controller.network.Server;
import server.controller.network.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * The server which handles the clients connected via socket.
 * It extends thread due to the fact that the listing is a blocking procedure
 */
public class SocketServer extends Server implements Runnable, SocketClientHandlerObserver {

    //The socket to perform the asynchronous listening on
    private ServerSocket acceptor;

    //The listening port
    private int port;

    /**
     * Constructor
     * @param port The port on which the listening will be performed
     */
    public SocketServer(int port) {

        //Assign the port
        this.port = port;

    }

    /**
     * Parses an object received by the socket client handler
     * @param handler the socket client handler
     * @param object the object to be parsed
     */
    private void parseObject(SocketClientHandler handler, NetObject object) {

        if (object instanceof LoginAuthentication) {

            this.authenticate(handler, object);

        }

    }

    /**
     * The run loop where the socket server listens for new connections
     */
    public void run() {


        //Try to open a server socket
        try {

            this.acceptor = new ServerSocket(this.port);

            Logger.log(Level.FINE, "Server (Socket)", "Up and running on port " + this.port);

        }
        catch (IOException e) {

            Logger.log(Level.SEVERE, "Server (Socket)", "Unable to open the acceptor socket", e);

            return;
        }

        //Run forever
        while (!this.acceptor.isClosed()) {

            try {

                //Accept every connection and handle its relative socket
                Socket newClientSocket = this.acceptor.accept();

                //Create a new client handler
                SocketClientHandler handler = new SocketClientHandler(newClientSocket);

                //Register us as observers
                handler.addObserver(this);

                //Add the client
                this.addClientHandler(handler);

                Logger.log(Level.FINEST, "Server (Socket)", "New client connected, waiting for authentication..");


            }
            catch (Exception e) {

                Logger.log(Level.SEVERE, "Server (Socket)", "Exception while listening", e);

            }

        }

    }

    public synchronized void onDisconnect(ClientHandler handler) {

        Logger.log(Level.FINEST, "Server (Socket)", "Client " + handler.getUsername() + " disconnected");

        //If the username was not null the client had authenticated and reached the game engine in the past
        if (handler.getUsername() != null) {

            //Notify the game engine
            this.notifyDisconnection(handler);

        }

        this.removeClientHandler(handler);

    }

    public void onObjectReceived(SocketClientHandler handler, NetObject object) {

        this.parseObject(handler, object);

    }


    public static void main(String[] args) {
        (new Thread(new SocketServer(4545))).start();
    }

}
