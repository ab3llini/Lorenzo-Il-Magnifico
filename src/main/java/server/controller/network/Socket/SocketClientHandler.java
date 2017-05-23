package server.controller.network.Socket;

/*
 * Created by alberto on 10/05/17.
 */


import logger.Level;
import logger.Logger;
import netobject.NetObject;
import server.controller.network.ClientHandler;
import server.controller.network.Observable;

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

    public boolean send(Object object) {

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

            Logger.log(Level.WARNING, "Client handler (Socket)", "Broken pipe: the client disconnected while writing", e);

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

            Logger.log(Level.SEVERE, this.toString(), "Unable to get input stream", e);

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

                Logger.log(Level.WARNING, "Client handler (Socket)", "Broken pipe while listening", e);

                this.notifyDisconnection();

                this.running = false;

                break;

            }
            catch (ClassNotFoundException e) {

                Logger.log(Level.SEVERE, "Client handler (Socket)", "Class not found", e);

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

}