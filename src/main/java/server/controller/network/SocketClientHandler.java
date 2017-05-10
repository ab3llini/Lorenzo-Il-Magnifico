package server.controller.network;

/*
 * Created by alberto on 10/05/17.
 */

import server.controller.game.Action;
import server.model.Player;

import java.net.Socket;

public class SocketClientHandler extends AbstractClientHandler implements Runnable {

    //The socket of the handler
    Socket socket;

    /**
     * Runnable interface implementation of run()
     */
    public void run() {
        //Loop to listen for incoming "messages"
    }

    public void onClientAction(Action action) {

    }

    public void notifyPlayerForAction(Action action, Player sender) {

    }
}
