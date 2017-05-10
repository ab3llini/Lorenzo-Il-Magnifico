package server.controller.network;

/*
 * Created by alberto on 10/05/17.
 */

import server.controller.game.Action;
import server.controller.game.Lobby;

/**
 * Interface implemented in the GameEngine.
 * Notifies the engine that a client has performed an action
 */
public interface ServerInterface {

    void onClientAction(Action action, AbstractClientHandler clientHandler);
    void dispatchClientAction(Lobby lobby, AbstractClientHandler clientHandler, Action action);

}
