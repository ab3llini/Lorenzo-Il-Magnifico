package server.controller.game;
import exception.*;
import netobject.Action;
import netobject.BoardSector;
import netobject.FamilyPlacementAction;
import netobject.NetObject;
import server.controller.network.Observable;
import server.model.*;
import server.model.board.Board;
import server.model.board.CouncilPalace;
import server.model.board.Player;
import server.model.board.TowerSlot;
import server.model.card.developement.Cost;
import server.model.card.developement.DvptCard;
import server.model.card.developement.DvptCardType;
import server.utility.DvptCardParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;

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

    public void onPlayerAction(Player player,NetObject  action) {



    }

    public Match getMatch() {
        return match;
    }

    /**
     * this method applies the cost of a card to a player
     * @param player
     * @param card
     * @throws NotEnoughResourcesException
     * @throws NotEnoughMilitaryPointsException
     */
    public void ApplyDvptCardCost(Player player, DvptCard card) throws NotEnoughResourcesException, NotEnoughMilitaryPointsException {

        //territory cards doesn't have cost
        if(card.getType() == DvptCardType.territory)
            return;

        int i=0;

        //some cards could have a double cost
        if(card.getCost().size()>1)
          i = selectCost(card.getCost());

        //get the choosen one cost
        Cost costo = card.getCost().get(i);

        //try to apply military cost, if it does not succeed it returns an exception
        if(costo.getMilitary().getRequired() <= player.getMilitaryPoints())
            player.subtractMilitaryPoints(costo.getMilitary().getMalus());
        else{
            throw new NotEnoughMilitaryPointsException("Not enough military point to do this");}

        //check if there are enough resources to apply the cost in order to have an atomic transaction, if it does not succeed it returns an exception
        for(int j=0;j<costo.getResources().size();j++){
            player.hasEnough(costo.getResources().get(j).getType(),costo.getResources().get(j).getAmount());
        }

        //deducts the cost of the card from the player's resources
        for(int j=0;j<card.getCost().get(i).getResources().size();j++){
            player.subtract(costo.getResources().get(j).getType(),costo.getResources().get(j).getAmount());
        }



    }

    public Integer selectCost(ArrayList<Cost> costs){
        //TODO
        return 0;
    }

    /**
     * this method receive an action and its author and place the familiar in the correct place (if it is strong enough)
     * @param action
     * @param player
     * @throws NotStrongEnoughException
     */
    public void placeFamilyMember(FamilyPlacementAction action, Player player) throws NotStrongEnoughException {

        //TODO only implemented one BoardSector...
        if(action.getActionTarget() == BoardSector.CouncilPalace){
            action.getFamilyMember().setPlayer(player);
            boardController.placeOnCouncilPalace(action.getFamilyMember());}
    }


}