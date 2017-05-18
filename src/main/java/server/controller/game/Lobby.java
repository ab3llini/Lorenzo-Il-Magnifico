package server.controller.game;

/*
 * Created by alberto on 10/05/17.
 */

import netobject.Action;
import server.controller.network.ClientHandler;
import server.model.board.Player;

import java.util.HashMap;

/**
 * The Lobby represents a virtual room where players wait for other players to join and for the match to begin.
 */

public class Lobby {

    //This table maps each clientHandler to it relative player object in the model
    HashMap<ClientHandler, Player> players = new HashMap<ClientHandler, Player>();

    //Match model
    //Match match = new Match();

    //Match controller
    //MatchController matchController = new MatchController();

    //Start timeout
    Integer timeout;

    public boolean hasClientHandler(ClientHandler handler) {

        return this.players.containsKey(handler);

    }

    public boolean onClientAction(ClientHandler handler, Action action) {

        //TODO: Map the handler to the player and forward the action to the match controller

        return true;

    }

    public boolean onClientDisconnection(ClientHandler handler) {

        //TODO: Inform the controller, take right measures.

        return true;

    }

}
