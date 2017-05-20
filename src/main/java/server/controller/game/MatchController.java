package server.controller.game;
import netobject.Action;
import server.controller.network.Observable;
import server.model.*;
import server.model.board.Board;
import server.model.board.Player;
import server.model.board.TowerSlot;
import server.model.card.developement.DvptCard;
import server.utility.DvptCardParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import static java.util.Collections.shuffle;

/**
 * Created by Alberto on 19/05/2017.
 */

/**
 * The controller of the match.
 * Will handle the model instance reacting to game events.
 */
public class MatchController {

    /**
     * The model instance of the match
     */
    private Match match;

    /**
     * The instance of the board controller
     */
    private BoardController boardController;

    /**
     * This is the match controller constructor.
     * It is called only by the lobby itself when the match starts
     * @param players the players in the match.
     */
    public MatchController(ArrayList<Player> players) {

        /*
         * First up, create the model for the current match.
         * Note that this call will trigger every constructor in the model
         * The players are always provided
         */
        this.match = new Match(players);


        /*
         * Assign the board controller
         * Keep in mind that match.board must be initialized at this time
         */
        this.boardController = new BoardController(this.match.getBoard());

        //Init anything else in the future here..

    }

    public void onPlayerAction(Player player, Action action) {



    }

    public Match getMatch() {
        return match;
    }


}