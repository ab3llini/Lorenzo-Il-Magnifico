package client;

import server.controller.network.RMIServerInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/*
 * @author  ab3llini
 * @since   14/05/17.
 */
public class RMIClient extends UnicastRemoteObject implements RMIClientInterface {

    RMIServerInterface serverRef;
    Registry registry;


    public RMIClient(String host, int port, String remoteName) throws RemoteException {
        super();

        try {

            this.registry = LocateRegistry.getRegistry(host, port);
            this.serverRef = (RMIServerInterface) registry.lookup(remoteName);


            System.out.println("Remote stub obtained. RMI ready.");


        } catch (Exception e) {
            e.getMessage();
        }

    }

    public static void main(String[] args) {

        try {
            new RMIClient("127.0.0.1" , 1099, "server");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public RMIServerInterface getServerRef() {
        return serverRef;
    }


    public void registrationFailed() throws RemoteException {

        System.out.println("Registration failed.. Username already in use.");

    }
}
