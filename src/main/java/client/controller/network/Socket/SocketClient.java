package client.controller.network.Socket;

import client.controller.network.Client;
import logger.Level;
import logger.Logger;
import netobject.request.auth.LoginRequest;
import netobject.NetObject;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/*
 * @author  ab3llini
 * @since   15/05/17.
 */

/**
 * Provides socket client functionality
 */
public class SocketClient extends Client implements Runnable {

    //The socket
    private Socket socket;

    //Host properties
    private String host;

    //The port of the host
    private int port;

    public SocketClient(String host, int port) {

        //Assign host & port
        this.host = host;
        this.port = port;

    }

    //Interface implementation
    public boolean connect() {

        //Create the tcp socket
        try {

            this.socket = new Socket(this.host, this.port);

            Logger.log(Level.FINE, "SocketClient::connect", "Connected.");

            return true;

        } catch (IOException e) {

            Logger.log(Level.SEVERE, "SocketClient::connect", "Unable to connect.", e);

        }

        return false;

    }

    public void login(LoginRequest authentication) {

        this.sendObject(authentication);

    }


    public void run() {

        while (!this.socket.isClosed() && this.socket.isConnected()) {

            try {

                ObjectInputStream socketIn = new ObjectInputStream(this.socket.getInputStream());

                //Try to read the object
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

                Logger.log(Level.WARNING, "SocketClient::run", "Broken pipe while listening", e);

                this.notifyDisconnection();

                break;

            }
            catch (ClassNotFoundException e) {

                Logger.log(Level.WARNING, "SocketClient::run", "Cast error", e);

                break;
            }

        }

        this.notifyDisconnection();

    }

    public boolean sendObject(NetObject object) {

        ObjectOutputStream socketOut;

        try {

            socketOut = new ObjectOutputStream(this.socket.getOutputStream());

            socketOut.flush();

            socketOut.writeObject(object);

            return true;

        } catch (IOException e) {

            Logger.log(Level.SEVERE, "SocketClient::SendObject", "IO Exception.");

        }

        return false;

    }

    public static void main(String[] args) {

        SocketClient s = new SocketClient("127.0.0.1", 4545);

        s.connect();

        new Thread(s).start();


        s.login(new LoginRequest("alberto", "unix"));

    }

}
