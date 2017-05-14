package server.controller.network;

/*
 * Created by alberto on 10/05/17.
 */

import client.RMIClientInterface;
import server.controller.game.Action;
import server.model.Player;

public class RMIClientHandler extends AbstractClientHandler {

    private RMIClientInterface clientRef;

    public RMIClientHandler(RMIClientInterface clientRef, String username) {

        super(username);

        //Assign the reference to the RMI Client in order to make callbacks
        this.clientRef = clientRef;

    }

    public void onClientAction(Action action) {

    }

    public void notifyPlayerForAction(Action action, Player sender) {

    }

    public void performAction(Action action) {

    }
}
