package server.model;

import exception.NoSuchPlayerException;
import netobject.NetObject;
import netobject.NetObjectType;
import server.model.board.Board;
import server.model.board.Period;
import server.model.board.Player;
import java.util.*;



/**
 * Created by Federico on 15/05/2017.
 * Additions made by ab3llini
 */

public class Match extends NetObject {

    private Board board;
    private ArrayList<Player> players;

    private ArrayList<Player> roundOrder;

    private int currentPeriod;
    private int currentTurn;
    private int currentRound;


    /**
     * Constructor: the match object get initialized with an array of players.
     * @param players the players who are actually in game
     */
    public Match(ArrayList<Player> players) {

        super(NetObjectType.Model);

        //Assign the players to the model reference
        this.players = players;

        //Assume the beginning order is the same as when the players joined the lobby
        this.roundOrder = players;

        //Initialize the board
        this.board = new Board();

        this.currentPeriod = 1;
        this.currentTurn = 1;
        this.currentRound = 1;

    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Player> getRoundOrder() {
        return roundOrder;
    }

    public Player getPlayerFromUsername(String username) throws NoSuchPlayerException {

        for (Player p : this.players) {

            if (p.getUsername().equals(username)) {

                return p;

            }

        }

        throw new NoSuchPlayerException("There is no player with username = " + username);

    }


    public Period getCurrentPeriod() {
        return Period.toEnum(currentPeriod);
    }


    public int getCurrentTurn() {
        return currentTurn;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentPeriod(int currentPeriod) {
        this.currentPeriod = currentPeriod;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public void setRoundOrder(ArrayList<Player> roundOrder) {
        this.roundOrder = roundOrder;
    }
}
