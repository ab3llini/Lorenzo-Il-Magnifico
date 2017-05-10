package server.controller.game;

/*
 * Created by alberto on 10/05/17.
 */

import server.model.Player;

import java.util.HashMap;

/**
 * The Lobby represents a virtual room where players wait for other players to join and for the match to begin.
 */

public class Lobby {

    //This table maps each clientHandler to it relative player object in the model
    HashMap<ClientHandlerInterface, Player> players;

    //Match Controller

    //Start timeout
    Integer timeout;

}
