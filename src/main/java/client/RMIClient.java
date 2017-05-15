package client;

import com.sun.org.apache.regexp.internal.RE;
import exception.UsernameAlreadyInUseException;
import netobject.Message;
import netobject.MessageType;
import server.controller.network.RMIServerInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/*
 * @author  ab3llini
 * @since   14/05/17.
 */

interface RMIClientObserver {

    void RMIConnectionReady();

}

public class RMIClient extends Thread implements RMIClientInterface {

    RMIServerInterface serverRef;
    Registry registry;

    ArrayList<RMIClientObserver> observers;

    String remoteName;
    String host;
    int port;



    public RMIClient(String host, int port, String remoteName) throws RemoteException {

        super();

        this.host = host;
        this.port = port;
        this.remoteName = remoteName;

        this.observers = new ArrayList<RMIClientObserver>();

    }

    public void prepareRmiConnection() {

        try {

            this.registry = LocateRegistry.getRegistry(host, port);
            this.serverRef = (RMIServerInterface) registry.lookup(remoteName);

            System.out.println("Remote stub obtained.");

            UnicastRemoteObject.exportObject(this, 0);

            System.out.println("Exported RMI local object.");

        } catch (Exception e) {
            e.getMessage();
        }

    }


    public void run() {

        this.prepareRmiConnection();

        //Notify observers
        for (RMIClientObserver o : this.observers) {

            o.RMIConnectionReady();

        }


    }

    public static void main(String[] args) {

        try {
            RMIClient rmic = new RMIClient("127.0.0.1" , 1099, "server");

            rmic.prepareRmiConnection();
            if(rmic.getServerRef().register(rmic, new Message(MessageType.Registration, "Alberto"))) {

                System.out.println("Connected");

            }

        }

        catch (UsernameAlreadyInUseException e) {

            System.out.println("Username already in use");

        }

        catch (RemoteException e) {

            System.out.println("RemoteException:  " + e.getMessage());

        }

    }

    /**
     * Adds an observer to the client
     * @param o The observer
     * @return Upon success, true. False otherwise.
     */
    public boolean addObserver(RMIClientObserver o) {

        return this.observers.add(o);

    }


    public RMIServerInterface getServerRef() {
        return serverRef;
    }
}
