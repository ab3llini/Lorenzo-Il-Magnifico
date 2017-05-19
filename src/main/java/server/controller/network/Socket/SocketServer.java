package server.controller.network.Socket;

/*
 * Created by alberto on 10/05/17.
 */


import logger.Level;
import logger.Logger;
import netobject.NetObject;
import netobject.RegistrationRequest;
import netobject.RegistrationResponse;
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
                SocketClientHandler client = new SocketClientHandler(newClientSocket, this);

                //Register us as observers
                client.addObserver(this);

                //Prepare a dedicated thread for the client.
                Thread thread = new Thread(client);

                //Map the client
                clientHandlers.put(client, thread);

                //Start the handler
                thread.start();

                Logger.log(Level.FINEST, "Server (Socket)", "New client added.. waiting for registration");


            }
            catch (Exception e) {

                Logger.log(Level.SEVERE, "Server (Socket)", "Exception while listening", e);

            }

        }

    }


    public void onDisconnect(ClientHandler handler) {

        Logger.log(Level.FINEST, "Server (Socket)", "Client disconnected " + handler);

        //If the username was not null the client had authenticated and reached the game engine in the past
        if (handler.getUsername() != null) {

            //Notify the game engine
            this.notifyDisconnection(handler);

        }

        this.removeClient(handler);

    }

    public void removeClient(ClientHandler handler) {

        //Terminate the thread
        this.clientHandlers.get(handler).interrupt();

        //Remove the reference
        this.clientHandlers.remove(handler);

    }

    public void onObjectReceived(SocketClientHandler handler, NetObject object) {

        this.parseObject(handler, object);

    }

    private void parseObject(SocketClientHandler handler, NetObject object) {

        if (object instanceof RegistrationRequest) {

            Logger.log(Level.FINEST, "Server (Socket)", "Temporary client sent an object: '"+ object +"'");

            this.parseRequest(handler, (RegistrationRequest)object);

        }

    }

    private void parseRequest(SocketClientHandler handler, RegistrationRequest request) {

        if (request.username != null) {

            Logger.log(Level.FINEST, "Server (Socekt)", "New registration request with username " + request.username);

            if (!this.checkUsernameAvailable(request.username)) {

                //Register the client
                this.register(handler, request.username);

                //Notify observer
                this.notifyConnection(handler);

                //Send a response to the client
                handler.sendObject(new RegistrationResponse(true));


            }
            else {

                handler.sendObject(new RegistrationResponse(false));

                //Remove the client
                this.removeClient(handler);

            }

        }



    }


    public HashMap<SocketClientHandler, Thread> getClientHandlers() {
        return clientHandlers;
    }


    public static void main(String[] args) {
        (new Thread(new SocketServer())).start();
    }

}
