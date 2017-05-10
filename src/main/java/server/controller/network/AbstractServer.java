package server.controller.network;

/*
 * Created by alberto on 10/05/17.
 */

import server.controller.game.Action;
import server.controller.game.GameEngine;

import java.util.ArrayList;

public abstract class AbstractServer {

    //A reference to the game engine
    private GameEngine engine;

    /**
     * This method forwards the actions received from the handlers to the game engine via the ServerInterface he implements
     * Must not be override
     */
    public final void forwardPlayerActionToGameEngine(Action action, AbstractClientHandler clientHandler) {

        this.engine.onClientAction(clientHandler, action);

    }

}
