package client.controller.network.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;

/*
 * @author  ab3llini
 * @since   14/05/17.
 */

public interface RMIClientInterface extends Remote {

    /**
     * Method called by the server to check connection status
     * @return true
     * @throws RemoteException required
     */
    public boolean heartbeat() throws RemoteException;

    /**
     * Method called by the server to disconnect the client
     * @throws RemoteException required
     */
    public void disconnect() throws RemoteException;

}
