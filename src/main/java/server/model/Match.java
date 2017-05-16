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
    private Integer era;
    private Integer round;
    private MatchSettings matchSettings;
    private final int dvptCardOffset = 8; //Offset to split the development cards in subarrays for different eras(3) and towers (4)

    public void prepareBoard(){};

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
