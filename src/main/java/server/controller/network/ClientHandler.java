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

    /**
     * Sends an object to the client
     * @param object the object
     * @return true upon success
     */
    public final boolean sendObject(NetObject object) {
        return this.send(object);
    }

    /**
     * Sends an exception to the client
     * @param e the exception
     * @return true upon success
     */
    public final boolean sendException(Exception e) {
        return this.send(e);
    }

    protected abstract boolean send(Object o);

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
