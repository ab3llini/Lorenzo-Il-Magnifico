package server.controller.network;

/*
 * Created by alberto on 10/05/17.
 */


import exception.UsernameAlreadyInUseException;
import netobject.Action;
import netobject.Message;
import netobject.MessageType;
import server.model.Player;

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
            System.out.println("Unable to get input stream");
        }

        while (!this.socket.isClosed()) {

            try {
                //Try to read the error
                Object obj = socketIn.readObject();

                //Parse the received object
                this.parseObject(obj);

            }
            catch (EOFException e) {

                System.out.println("Client disconnected");

                //Notify observers so that they can remove the handler
                for (AbstractClientListener l : this.listeners) {

                    l.onDisconnect(this);

                }

                break;

            }
            catch (IOException e) {

                System.out.println("IOException");

                break;

            }
            catch (ClassNotFoundException e) {

                System.out.println("Class not found");

                break;
            }

        }

    }

    private void parseMessage(Message m) {

        if (m.type == MessageType.Registration) {

            if (!this.server.doesExistClientWithUsername(m.value)) {

                //Assign the username
                this.username = m.value;

                System.out.println("New socket client registered with username " + m.value);

            }
            else {

                System.out.println("Registration for new Socket client failed, username '"+username+"' is already in use");

            }

        }

    }

    private void parseObject(Object obj) {

        if (obj instanceof Message) {

            this.parseMessage((Message)obj);

        }

    }

    public void onClientAction(Action action) {

    }

    public void notifyPlayerForAction(Action action, Player sender) {

    }
}
