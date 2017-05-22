package client;

import exception.LoginFailedException;
import netobject.LoginAuthentication;
import server.controller.network.RMI.RMIConnectionToken;
import server.controller.network.RMI.RMIServerInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/*
 * @author  ab3llini
 * @since   14/05/17.
 */

interface RMIClientObserver {

    void RMIConnectionReady();

}

public class RMIClient extends Thread implements client.RMIClientInterface {

    RMIServerInterface serverRef;
    Registry registry;

    ArrayList<RMIClientObserver> observers = new ArrayList<RMIClientObserver>();

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

            RMIConnectionToken token = rmic.getServerRef().connect(rmic);

            System.out.println("Got " + token.toString());

            rmic.getServerRef().login(token.getToken(), new LoginAuthentication("#" + Math.round(Math.random() * 10000), null));


        } catch (ServerNotActiveException e1) {
            e1.printStackTrace();
        } catch (RemoteException e1) {
            e1.printStackTrace();
        } catch (LoginFailedException e) {
            e.printStackTrace();
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

    public boolean heartbeat() throws RemoteException {
        return true;
    }
}
