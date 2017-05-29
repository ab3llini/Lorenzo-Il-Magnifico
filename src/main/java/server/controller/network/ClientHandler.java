package server.controller.network;
import netobject.NetObject;
import netobject.notification.Notification;
import server.controller.game.RemotePlayer;
import server.model.Match;

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

    public abstract void sendNotification(Notification not);

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
