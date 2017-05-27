package client.controller.network;

import netobject.notification.Notification;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */
public interface ClientObserver {

    void onDisconnection(Client client);

    void onLoginFailed(Client client, String reason);

    void onLoginSuccess(Client client);

    void onNotification(Client client, Notification not);

}
