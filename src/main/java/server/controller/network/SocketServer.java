package server.controller.network;

/*
 * Created by alberto on 10/05/17.
 */


import netobject.Action;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The server which handles the clients connected via socket.
 * It extends thread due to the fact that the listing is a blocking procedure
 */
public class SocketServer implements Runnable, AbstractClientListener {

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

            System.out.println("Socket server up and running on port " + this.port);

        }
        catch (IOException e) {
            System.out.println("Unable to open the acceptor socket");
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

                System.out.println("Exception while listening:" + e.getMessage());

            }

        }

    }


    public void onDisconnect(AbstractClientHandler handler) {

        System.out.println("Server detected client disconnection. Stopping handler thread & removing");

        //Terminate the thread
        this.clientHandlers.get(handler).interrupt();

        //Remove the reference
        this.clientHandlers.remove(handler);

    }

    public void onAction(AbstractClientHandler handler, Action action) {

        //Propagate the action to the game engine.. toward the lobby
        System.out.println("The client " + handler + " performed the action " + action);

    }

    public boolean doesExistClientWithUsername(String username) {

        if (username == null) {

            return false;

        }

        for (Object o : this.clientHandlers.entrySet()) {

            HashMap.Entry pair = (HashMap.Entry) o;

            String uname = ((SocketClientHandler) pair.getKey()).username;

            if (uname != null && uname.equals(username)) {

                return true;

            }

        }

        return false;

    }

    public static void main(String[] args) {
        (new Thread(new SocketServer())).start();
    }
}
