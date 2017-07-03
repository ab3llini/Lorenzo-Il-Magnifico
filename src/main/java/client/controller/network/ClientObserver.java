package client.controller.network;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */
public interface ClientObserver extends Observer {

    void onDisconnection(Client client);
    void onLoginFailed(Client client, String reason);
    void onLoginSuccess(Client client);
    void onRegistrationSuccess(Client client);
    void onRegistrationFailed(Client client, String reason);

}
