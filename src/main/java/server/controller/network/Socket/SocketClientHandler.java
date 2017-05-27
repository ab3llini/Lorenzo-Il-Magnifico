package server.controller.network.Socket;

/*
 * Created by alberto on 10/05/17.
 */


import logger.Level;
import logger.Logger;
import netobject.NetObject;
import netobject.notification.Notification;
import server.controller.network.ClientHandler;
import server.controller.network.Observable;
import server.model.Match;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SocketClientHandler extends ClientHandler implements Observable<SocketClientHandlerObserver> {

    //The socket of the handler
    private Socket socket;

    //A state variable to assure performance
    private boolean running = true;

    //The observer list
    private ArrayList<SocketClientHandlerObserver> observers = new ArrayList<SocketClientHandlerObserver>();

    /**
     * The constructor
     * @param socket the socket of the client
     */
    public SocketClientHandler(Socket socket) {

        //Assign the socket
        this.socket = socket;

    }

    /**
     * Sends an object to the client
     * @param object the object
     * @return true upon success
     */
    public boolean sendObject(NetObject object) {

        if (this.socket.isClosed()) {

            return false;

        }

        ObjectOutputStream socketOut;

        try {

            socketOut = new ObjectOutputStream(this.socket.getOutputStream());

            socketOut.flush();

            socketOut.writeObject(object);

            return true;

        } catch (IOException e) {

            Logger.log(Level.WARNING, this.toString(), "Broken pipe: the client disconnected while writing", e);

            this.notifyDisconnection();

            this.running = false;

        }

        return false;

    }

    /**
     * Runnable interface implementation of run()
     */
    public void run() {


        ObjectInputStream socketIn = null;

        try {

            socketIn = new ObjectInputStream(this.socket.getInputStream());

        } catch (IOException e) {

            Logger.log(Level.SEVERE, this.toString(), "Unable to get input stream, client probably disconnected.");

            //Notify observers so that they can remove the handler
            this.notifyDisconnection();

            return;

        }

        while (this.running && !this.socket.isClosed() && this.socket.isConnected()) {

            try {

                //Try to read the error
                Object obj = socketIn.readObject();

                //Notify that an object was received
                this.notifyObjectReceived((NetObject) obj);

            }
            catch (EOFException e) {

                //Notify observers so that they can remove the handler
                this.notifyDisconnection();

                break;

            }
            catch (IOException e) {

                Logger.log(Level.WARNING, this.toString(), "Broken pipe while listening", e);

                this.notifyDisconnection();

                this.running = false;

                break;

            }
            catch (ClassNotFoundException e) {

                Logger.log(Level.WARNING, this.toString(), "Class not found", e);

                break;
            }

        }

    }

    /* Observable<SocketClientHandlerObserver> implementation */
    public boolean addObserver(SocketClientHandlerObserver o) {

        //Check whether the observer is not null
        if (o != null) {
            this.observers.add(o);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "ClientHandler (Socket)";
    }

    public boolean removeObserver(SocketClientHandlerObserver o) {

        return this.observers.remove(o);

    }

    private void notifyDisconnection() {

        for (SocketClientHandlerObserver o : this.observers) {

            o.onDisconnect(this);

        }

    }

    private void notifyObjectReceived(NetObject object) {

        for (SocketClientHandlerObserver o : this.observers) {

            o.onObjectReceived(this, object);

        }

    }

    public void sendNotification(Notification not) {
        this.sendObject(not);
    }

    public void updateModel(Match model) {

    }
}