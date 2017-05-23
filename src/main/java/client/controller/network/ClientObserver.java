package client.controller.network;

import netobject.NetObject;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */
public interface ClientObserver {

    void onObjectReceived(Client client, NetObject object);

    void onDisconnection(Client client);

    void onLoginFailed(Client client, String reason);

    void onLoginSuccess(Client client);

}
