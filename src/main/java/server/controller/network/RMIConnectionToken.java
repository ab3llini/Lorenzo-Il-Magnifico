package server.controller.network;

import java.io.Serializable;

/*
 * @author  ab3llini
 * @since   20/05/17.
 */
public class RMIConnectionToken implements Serializable {

    private final String boundableIP;
    private final int token;


    public RMIConnectionToken(String boundableIP, int token) {
        this.boundableIP = boundableIP;
        this.token = token;
    }

    public int getToken() {
        return token;
    }

    public String getBoundableIP() {
        return boundableIP;
    }
}
