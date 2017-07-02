package server.controller.network;
import netobject.notification.LobbyNotification;
import server.controller.game.RemotePlayer;

/*
 * @author  ab3llini
 * @since   10/05/17.
 */
public abstract class ClientHandler implements Runnable, RemotePlayer {

    //A representative username
    protected String username;

    //A flag indicating whether or not the client did authenticate
    protected boolean authenticated;

    public String getUsername() {

        return (this.username != null) ? this.username : "(Never authenticated)";

    }

    public abstract void sendLobbyNotification(LobbyNotification not);

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    protected abstract void disconnect();
}
