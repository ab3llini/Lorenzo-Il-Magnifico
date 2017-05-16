package server.controller.network;

/*
 * Created by alberto on 10/05/17.
 */


import logger.Level;
import logger.Logger;
import netobject.Action;
import netobject.Message;
import netobject.MessageType;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class SocketClientHandler extends AbstractClientHandler implements Runnable {

    //The socket of the handler
    Socket socket;

    //Pointer to the server to check if the username is already in use
    SocketServer server;

    public SocketClientHandler(Socket socket, SocketServer server) {

        //Assign the socket
        this.socket = socket;
        this.server = server;

    }

    /**
     * Runnable interface implementation of run()
     */
    public void run() {

        ObjectInputStream socketIn = null;

        try {
            socketIn = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {

            Logger.log(logger.Level.SEVERE, this.toString(), "Unable to get input stream", e);

            return;

        }

        while (!this.socket.isClosed()) {

            try {
                //Try to read the error
                Object obj = socketIn.readObject();

                //Parse the received object
                this.parseObject(obj);

            }
            catch (EOFException e) {

                Logger.log(Level.WARNING, "Client handler (Socket)", "Client disconnected " + this);

                //Notify observers so that they can remove the handler
                this.listener.onDisconnect(this);

                break;

            }
            catch (IOException e) {

                System.out.println("IOException");

                Logger.log(Level.SEVERE, "Client handler (Socket)", "Error while writing to the socket", e);


                break;

            }
            catch (ClassNotFoundException e) {

                Logger.log(Level.SEVERE, "Client handler (Socket)", "Class not found", e);

                break;
            }

        }

    }

    private void parseMessage(Message m) {

        if (m.type == MessageType.Registration) {

            if (!this.listener.existsClientWithUsername(m.value)) {

                //Assign the username
                this.username = m.value;

                Logger.log(Level.INFO, "Client handler (Socket)", "New client added " + m.value);

            }
            else {

                Logger.log(Level.WARNING, "Client handler (Socket)", "Registration failed, username '"+m.value+"' already in use");


            }

        }

    }

    private void parseObject(Object obj) {

        if (obj instanceof Message) {

            this.parseMessage((Message)obj);

        }

        if (obj instanceof Action) {

            //Notify the listeners
            this.listener.onAction(this, (Action)obj);

        }

    }


    public void notifyPlayerForAction(Action action, AbstractClientHandler sender) {

    }
}
