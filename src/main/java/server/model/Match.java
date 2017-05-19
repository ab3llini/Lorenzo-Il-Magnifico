package server.model;

import server.model.board.Board;
import server.model.board.Player;
import server.model.board.TowerSlot;
import server.model.card.developement.DvptCard;

import java.util.ArrayList;
import java.util.Random;
import static java.util.Collections.shuffle;


/**
 * Created by Federico on 15/05/2017.
 */

public class Match {

    private Board board;
    private ArrayList<Player> players;
    private ArrayList<Player> playersOrder;
    private Integer period;
    private Integer round;
    private MatchSettings matchSettings;

    /**
     * Constructor: the match object get initialized with an array of players.
     * @param players the players who are actually in game
     */
    public Match(ArrayList<Player> players) {

        //Assign the players to the model reference
        this.players = players;

    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
