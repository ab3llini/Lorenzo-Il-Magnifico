package server.controller.network;

/*
 * Created by alberto on 10/05/17.
 */

import server.controller.game.Action;
import server.model.Player;

public class RMIClientHandler extends AbstractClientHandler implements RemoteClientHandlerInterface {

    public void onClientAction(Action action, AbstractClientHandler sender) {

    }

    public void notifyPlayerForAction(Action action, Player sender) {

    }

    public void performAction(Action action) {

    }
}
