package server.controller.network;
import netobject.NetObject;

/*
 * @author  ab3llini
 * @since   10/05/17.
 */
public abstract class ClientHandler implements Runnable  {

    //A representative username
    protected String username;

    //A flag indicating whether or not the client did authenticate
    protected boolean authenticated;

    public abstract boolean sendObject(NetObject object);

    public String getUsername() {

        if (this.username == null) {

            return "Username not set";

        }
        else {

            return this.username;

        }

    }

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
