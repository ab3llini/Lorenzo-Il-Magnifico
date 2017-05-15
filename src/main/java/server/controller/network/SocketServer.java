package server.controller.network;

/*
 * Created by alberto on 10/05/17.
 */

import java.net.Socket;
import java.util.ArrayList;

/**
 * The server which handles the clients connected via socket.
 * It implements runnable due to the fact that the listing is a blocking procedure
 */
public class SocketServer implements Runnable {

    //The socket to perform the asynchronous listening on
    Socket listener;

    //The TCP Socket client handlers
    ArrayList<SocketClientHandler> clientHandlers;

    public void run() {
        //Main listening loop (listen blocks)
    }
}
