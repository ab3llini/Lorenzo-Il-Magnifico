package server.controller.network;
import java.util.ArrayList;

/*
 * @author  ab3llini
 * @since   10/05/17.
 */
public abstract class ClientHandler  {

    //A representative username
    protected String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
