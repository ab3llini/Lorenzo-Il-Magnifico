package server.controller.game;
import exception.*;
import logger.Level;
import logger.Logger;
import netobject.*;
import netobject.request.action.*;
import server.model.*;
import server.model.board.*;
import server.model.card.developement.Cost;
import server.model.card.developement.DvptCard;
import server.model.card.developement.DvptCardType;
import server.model.card.developement.TerritoryDvptCard;
import server.model.effect.EffectConversion;
import server.model.effect.EffectSurplus;
import server.model.valuable.Point;
import server.model.valuable.Resource;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import static java.util.Collections.shuffle;
import static server.model.board.ColorType.Black;
import static server.model.board.ColorType.Orange;
import static server.model.board.ColorType.White;

/*
 * @author  ab3llini
 * @since   15/05/17.
 */

/**
 * The controller of the match.
 * Will handle the model instance reacting to game events.
 */
public class MatchController implements Runnable {

    /**
     * The model instance of the match
     */
    private Match match;

    /**
     * The instance of the board controller
     */
    private BoardController boardController;


    /**
     * This queue holds all the action that need processing from the active player
     */
    private BlockingQueue<ActionRequest> actionRequests;

    /**
     * Holds a reference to the player of the model who is performing the move
     */
    private Player activePlayer;

    /**
     * Describes who has the turn
     */
    private Player currentPlayer;

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

    /**
     * The run method is the Runnable implementation of the match controller
     * Every match controller requires its own thread
     * This because it should be able to wait (literally) for the players to perform an action/choice
     */
    public void run() {

        while (true) {

            try {

                //This call will pause the thread until a new request will be put in the queue
                ActionRequest actionRequest = this.actionRequests.take();

                Logger.log(Level.SEVERE, "MatchController", "Parsing request");

            } catch (InterruptedException e) {

                Logger.log(Level.SEVERE, "MatchController", "Interrupted", e);

            }

        }

    }

    public void onPlayerAction(Player player, ActionRequest action) throws NotStrongEnoughException, FamilyMemberAlreadyInUseException, NotEnoughPlayersException, PlaceOccupiedException {

        if(action instanceof FamilyMemberPlacementActionRequest){

            placeFamilyMember((FamilyMemberPlacementActionRequest) action,player);

        }

        if(action instanceof LeaderCardActivationActionRequest){

            activateLeaderCard((LeaderCardActivationActionRequest) action, player);

        }

        if(action instanceof RollDiceActionRequest){

            rollDices ();

        }

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
    public void ApplyDvptCardCost(Player player, DvptCard card,Integer choosenCost) throws NotEnoughResourcesException, NotEnoughMilitaryPointsException {

        //territory cards doesn't have cost
        if(card.getType() == DvptCardType.territory)
            return;

        int i=0;
        System.out.println(card.getCost().size());
        //some cards could have a double cost
        if(card.getCost().size()>1)
          i = choosenCost;

        //get the choosen one cost
        Cost costo = card.getCost().get(i);

        //try to apply military cost, if it does not succeed it returns an exception
        if(costo.getMilitary().getRequired() <= player.getMilitaryPoints())
            player.subtractMilitaryPoints(costo.getMilitary().getMalus());

        else{
            throw new NotEnoughMilitaryPointsException("Not enough military point to do this");}

        //check if there are enough resources to apply the cost in order to have an atomic transaction, if it does not succeed it returns an exception
        //deducts the cost of the card from the player's resources
        if(player.hasEnoughCostResources(costo)) {
            player.subtractResources(costo);
        }
        else
            throw new NotEnoughResourcesException("Not enough resources to do this");

    }

    /**
     * this method receives an action and its author and places the familiar in the correct place (if it is strong enough)
     * @param action
     * @param player
     * @throws NotStrongEnoughException
     */
    public void placeFamilyMember(FamilyMemberPlacementActionRequest action, Player player) throws NotStrongEnoughException, FamilyMemberAlreadyInUseException, NotEnoughPlayersException, PlaceOccupiedException {

        FamilyMember familyMember = player.getFamilyMember(action.getColorType());

        //TODO
        //if boardSectorType is CouncilPalace we place the family member in the council palace
        if (action.getActionTarget() == BoardSectorType.CouncilPalace) {
            EffectSurplus surplus = boardController.placeOnCouncilPalace(familyMember, action.getAdditionalServants(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);
            }

        //if boardSectorType is Market we place the family member in the correct market place (from index 0 to index 3)
        if (action.getActionTarget() == BoardSectorType.Market) {

            EffectSurplus surplus = boardController.placeOnMarket(familyMember,action.getPlacementIndex(),action.getAdditionalServants(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);

        }

        if(action.getActionTarget() == BoardSectorType.SingleHarvestPlace) {

            EffectSurplus surplus = boardController.placeOnSingleHarvestPlace(familyMember,action.getAdditionalServants(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);
            applyHarvestChain(player,familyMember.getForce() + action.getAdditionalServants());
        }

        if(action.getActionTarget() == BoardSectorType.CompositeHarvestPlace) {

            EffectSurplus surplus = boardController.placeOnCompositeHarvestPlace(familyMember,action.getAdditionalServants(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);

            //we have to subtract force malus from activation force
            applyHarvestChain(player,familyMember.getForce() + action.getAdditionalServants() - this.match.getBoard().getHarvestArea().getSecondaryPlace().getForceMalus());
        }


        familyMember.setBusy(true);
    }

    /**
     * this method apply the effectSurplus to a player
     * @param player
     * @param surplus
     */

    public void applyEffectSurplus(Player player,EffectSurplus surplus){

        //effect surplus is composed by resources,points and council privilege
        ArrayList<Resource> resourcesSurplus = surplus.getResources();
        ArrayList<Point> pointsSurplus = surplus.getPoints();
        Integer council = surplus.getCouncil();

        player.addResources(resourcesSurplus);
        player.addPoints(pointsSurplus);

        //the client can choose which council privilege want to have
        if(council >= 1)
            //TODO
            ;
    }

    /**
     * this method starts the harvest chain.
     * this harvest chain consists in the activation of all the territory cards permament effect
     * @param player
     * @param force
     */
    public void applyHarvestChain(Player player, Integer force){

        for (TerritoryDvptCard card:player.getPersonalBoard().getTerritoryCards()) {

            //apply territory card permanent effect only if the player has enough force
            if(card.getPermanentEffect().getMinForce() <= force){

                applyEffectSurplus(player,card.getPermanentEffect().getSurplus());

            }
        }
    }


    public void activateLeaderCard (LeaderCardActivationActionRequest action, Player player) {

        if(player.hasEnoughLeaderRequirements(action.getLeaderCardIndex())){ //verify the requirements to activate Leader Card
        }

    }

    /** this method applies the Production Chain
     * this character chain consists in the activation of all the building card permanent effect**/

    public void applyProductionChain (Player player, FamilyMember familyMember) throws NotEnoughPointsException,NotEnoughResourcesException {

        for (DvptCard card : player.getPersonalBoard().getBuildingCards()
                ) {

            if (familyMember.getForce() >= card.getPermanentEffect().getMinForce()) {

                applyBuildingPermanentEffect(card, player, 0);

            }

        }
    }

    /** this method applies the PermanentEffect of a Building card, that could be a surplus or a conversion
     *
     * */

    public void applyBuildingPermanentEffect (DvptCard card, Player player, Integer choice) throws NotEnoughResourcesException, NotEnoughPointsException{

        if(card.getPermanentEffect().getSurplus() != null)

            applyEffectSurplus(player, card.getPermanentEffect().getSurplus());

        if(!card.getPermanentEffect().getConversion().isEmpty())

            applyConversion(player, card.getPermanentEffect().getConversion() , choice);

    }

    /** this method applies a conversion permanent effect of a development card to a particular player
     *
     * @param player
     * @param conversionList the list of conversion contained in the card effect
     * @param choice the choice of different conversion which can be made by the player
     * @throws NotEnoughResourcesException
     * @throws NotEnoughPointsException
     */


    public void applyConversion (Player player, ArrayList<EffectConversion> conversionList, Integer choice) throws NotEnoughResourcesException, NotEnoughPointsException {

        if(!conversionList.get(choice).getFrom().getResources().isEmpty()) {

            for(Resource from: conversionList.get(choice).getFrom().getResources())

                player.subtractGenericResource(from.getType(), from.getAmount());

        }

        if(!conversionList.get(choice).getFrom().getPoints().isEmpty()) {

            for(Point from: conversionList.get(choice).getFrom().getPoints())

                player.subtractGenericPoint(from.getType(), from.getAmount());

        }

        if(!conversionList.get(choice).getTo().getResources().isEmpty()) {

            for(Resource to: conversionList.get(choice).getTo().getResources())

                player.addGenericResource(to.getType(), to.getAmount());

        }

        if(!conversionList.get(choice).getTo().getPoints().isEmpty()) {

            for(Point to: conversionList.get(choice).getTo().getPoints())

                player.addGenericPoint(to.getType(), to.getAmount());

        }

    }

    /** this method rolls dices and set them on the board */

    public void rollDices (){

        Random random = new Random();

        for (Dice d : this.match.getBoard().getDices()) {

            d.setValue(random.nextInt(5) + 1);

        }

    }

}