package client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;

/*
 * @author  ab3llini
 * @since   14/05/17.
 */
public interface RMIClientInterface extends Remote {

    public boolean heartbeat() throws RemoteException;

    public void disconnect() throws RemoteException;

}
