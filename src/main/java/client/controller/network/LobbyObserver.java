package client.controller.network;

import netobject.notification.LobbyNotification;

/*
 * @author  ab3llini
 * @since   29/06/17.
 */
public interface LobbyObserver {

    void onLobbyNotification(Client client, LobbyNotification not);


}
