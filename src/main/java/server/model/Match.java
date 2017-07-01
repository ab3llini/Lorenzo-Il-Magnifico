package server.model;

import exception.NoSuchPlayerException;
import netobject.NetObject;
import netobject.NetObjectType;
import server.model.board.*;
import server.model.card.leader.LeaderCard;

import java.util.*;



/**
 * Created by Federico on 15/05/2017.
 * Additions made by ab3llini
 */

public class Match extends NetObject {

    private Board board;
    private ArrayList<Player> players;

    private ArrayList<Player> roundOrder;

    private Period currentPeriod;
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
        this.board = new Board(players);

        this.currentPeriod = Period.undefined;
        this.currentTurn = 0;
        this.currentRound = 0;

        //Assign a random color to the player
        generateRandomColor();
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

    public ArrayList<Player> getDisabledPlayers() {

        ArrayList<Player> ans = new ArrayList<>();

        for (Player p : this.players) {

            if (p.isDisabled())

                ans.add(p);

        }

        return ans;

    }

    public ArrayList<Player> getActivePlayers() {

        ArrayList<Player> ans = new ArrayList<>();

        for (Player p : this.players) {

            if (!p.isDisabled())

                ans.add(p);

        }

        return ans;

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
        return this.currentPeriod;
    }


    public int getCurrentTurn() {
        return currentTurn;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentPeriod(int currentPeriod) {
        if (currentPeriod == 1) {

            this.currentPeriod = Period.first;

        }
        else if (currentPeriod == 2) {

            this.currentPeriod = Period.second;

        }
        else if (currentPeriod == 3) {

            this.currentPeriod = Period.third;

        }
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

    public void generateRandomColor(){
        ArrayList<Integer> colorIndex = new ArrayList<Integer>();
        for(int i=0; i<8; i++)
            colorIndex.add(i);
        Collections.shuffle(colorIndex);
        for(int i=0; i<players.size(); i++) {
            players.get(i).setColor(PlayerColor.toEnum(colorIndex.get(i)));
            players.get(i).setFamilyMembersPlayerColor();
          }
        }
}
