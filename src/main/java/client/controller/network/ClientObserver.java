package client.controller.network;

import netobject.notification.LobbyNotification;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */
public interface ClientObserver {

    void onDisconnection(Client client);

    void onLoginFailed(Client client, String reason);

    void onLoginSuccess(Client client);

    void onLobbyNotification(Client client, LobbyNotification not);

}
