package server.controller.game;
import exception.*;
import logger.Level;
import logger.Logger;
import netobject.NetObjectType;
import netobject.action.*;
import netobject.action.ActionType;
import netobject.action.standard.LeaderCardActivationAction;
import netobject.action.standard.RollDicesAction;
import netobject.action.standard.StandardPlacementAction;
import server.controller.network.ClientHandler;
import server.model.*;
import server.model.board.*;
import server.model.card.ban.SpecialBanCard;
import server.model.card.ban.SpecialEffectType;
import server.model.card.developement.*;
import server.model.effect.*;
import server.model.valuable.Multiplier;
import server.model.valuable.Point;
import server.model.valuable.Resource;
import server.model.valuable.ResultType;
import server.utility.BoardConfigParser;
import singleton.GameConfig;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static server.utility.BoardConfigParser.getVictoryBonusFromRanking;

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
     * This property maps each player in the model with his relative remote one
     */
    private LinkedHashMap<Player, RemotePlayer> remotePlayerMap;

    /**
     * This queue holds all the actions that need processing from the active player
     */
    private BlockingQueue<Action> actions;

    /**
     * Holds a reference to the player of the model who is performing the move
     */
    private Player currentPlayer;

    /**
     * Timeout for the Action
     */
    private Timer currentPlayerTimeout;

    /**
     * Constants
     */
    private static final int MOVE_DELAY =  GameConfig.getInstance().getPlayerTimeout();


    /**
     * This is the match controller constructor.
     * It is called only by the lobby itself when the match starts
     * @param handlers the handlers of the model players
     */
    public MatchController(ArrayList<ClientHandler> handlers) {

        /*
         * Initialize the map
         */
        this.remotePlayerMap = new LinkedHashMap<Player, RemotePlayer>();

        /*
         * Create a temporary list of players that will be passed to the Match
         * For each handler create a map entry and add it to the temporary list
         */
        ArrayList<Player> players = new ArrayList<Player>();

        for (ClientHandler handler : handlers) {

            Player player = new Player(handler.getUsername());

            this.remotePlayerMap.put(player, handler);

            players.add(player);

        }

        /*
         * First up, create the model for the current match.
         * Note that this call will trigger every constructor in the model
         * The players are always provided
         */
        this.match = new Match(players);

        //TODO: DELETE THIS ! DEBUG ONLY

        this.match.getBoard().getCathedral().setBanCard(Period.first, new SpecialBanCard(1, Period.first.toInt(), SpecialEffectType.noFirstAction));
        this.match.getBoard().getCathedral().setBanCard(Period.second, new SpecialBanCard(2, Period.second.toInt(), SpecialEffectType.noFirstAction));
        this.match.getBoard().getCathedral().setBanCard(Period.third, new SpecialBanCard(3, Period.third.toInt(), SpecialEffectType.noFirstAction));


        /*
         * Assign the board controller
         * Keep in mind that match.board must be initialized at this time
         */
        this.boardController = new BoardController(this.match.getBoard());

        /*
         * Initialize the blocking queue for the actions
         */
        this.actions = new LinkedBlockingQueue<Action>();

        //Init anything else in the future here..

    }

    /**
     * costruttore temporaneo usato solo per testare le classi
     * @param players
     */
    public MatchController(ArrayList<Player> players,Integer xx) {

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


        //TODO: DELETE THIS ! DEBUG ONLY

        this.match.getBoard().getCathedral().setBanCard(Period.first, new SpecialBanCard(1, Period.first.toInt(), SpecialEffectType.noFirstAction));
        this.match.getBoard().getCathedral().setBanCard(Period.second, new SpecialBanCard(2, Period.second.toInt(), SpecialEffectType.noFirstAction));
        this.match.getBoard().getCathedral().setBanCard(Period.third, new SpecialBanCard(3, Period.third.toInt(), SpecialEffectType.noFirstAction));

        //Init anything else in the future here..

    }

    /**
     * The run method is the Runnable implementation of the match controller
     * Every match controller requires its own thread
     * This because it should be able to wait (literally) for the players to perform an Action/choice
     *
     * When this method is called (using a thread.start()) it automatically set up a timeout for the player move.
     */
    public void run() {

        //Make sure that the match has already been initialized here!
        RoundIterator roundIterator = new RoundIterator(this.match);

        while (roundIterator.hasNext()) {

            this.boardController.updateTowersForTurn(this.match.getCurrentPeriod().toInt(), this.match.getCurrentTurn());

            Logger.log(Level.FINEST, "MatchController", "New round started (Period = " +this.match.getCurrentPeriod() + " - Turn = " + this.match.getCurrentTurn() + " - Round = " +this.match.getCurrentRound() + ")");

            //Update the model
            this.sendUpdatedModel();

            Queue<Player> currentRound = roundIterator.next();

            //Foreach round handle the current player
            for (Player p : currentRound) {

                //Skip each disabled player
                if (p.isDisabled()) {

                    Logger.log(Level.FINEST, "MatchController", "Skipping player " + p.getUsername() +" because it is disabled");

                    continue;
                }

                //Update the current player
                this.currentPlayer = p;

                //Notify the turn of the player
                this.notifyAllTurnEnabled(this.currentPlayer);


                Action Action;


                //Loop the players actions until he terminates his round
                do {

                    Logger.log(Level.FINEST, "MatchController", "It is " + this.currentPlayer.getUsername() + "'s turn!");

                    //Setup a new timeout for the Action
                    this.currentPlayerTimeout = new Timer();

                    //Define what to do when, and if, the timeout expires
                    this.currentPlayerTimeout.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            //By the time this method gets fired the player should has already taken his Action.
                            //If not, we set the player as disabled and continue
                            MatchController.this.currentPlayer.setDisabled(true);

                            //To wake up the thread, inject a poisonous Action
                            MatchController.this.actions.add(new Action());


                        }
                    }, MOVE_DELAY * 1000);

                    try {
                        //Take the Action request in the queue and check if we shall proceed
                        //Note that this is a blocking queue
                        Action = this.actions.take();

                    } catch (InterruptedException e) {

                        Logger.log(Level.SEVERE, "MatchController", "Interrupted", e);

                        break;

                    }

                    //When we get here the player took its Action or the timeout for the Action expired, clear the interval.
                    this.currentPlayerTimeout.cancel();

                    //Check if the Action is legit, if not skip this player. It might just have expired the timeout
                    if (Action.getType() == NetObjectType.Poison) {

                        Logger.log(Level.FINEST, "MatchController", "Action timeout expired");

                        //Tell the players that the timeout has expired expired for the active player
                        this.notifyAllActionTimeoutExpired(this.currentPlayer);

                        //Disable the player
                        this.currentPlayer.setDisabled(true);

                        //Break the loop
                        break;

                    }
                    else {

                        Logger.log(Level.FINEST, "MatchController", "Parsing Action request, the active player is " + this.currentPlayer.getUsername());

                        try {

                            //Handler the player Action
                            this.onPlayerMove(this.currentPlayer, Action);

                        }
                        catch (ActionException reason) {

                            //Inform the player that he can't take that action
                            this.remotePlayerMap.get(this.currentPlayer).notifyActionRefused(GameMessage.InvalidAction.getLiteral() + " Reason: " + reason.getMessage());

                        }


                    }


                }
                while (!this.currentPlayer.isDisabled() && Action.getActionType() != ActionType.TerminateRound);

                Logger.log(Level.FINEST, "MatchController", "The player " + this.currentPlayer.getUsername() + "finished his round");

                //Tell the players that the active one can't make any more actions
                this.notifyAllTurnDisabled(this.currentPlayer);


            }

        }

    }

    /**
     * This method is the only one that should be called from other threads.
     * Specifically, it is used by client handler to dispatch their client actions
     * @param Action the Action
     */
    public void dispatchNewPlayerAction(Action Action) {

        this.actions.add(Action);

    }

    /**
     * This method is called internally by the run loop
     * It decides, based on the Action performed by the active player, what should be performed
     * @param player the player that performed the Action, which is the active one
     * @param Action the Action perfomed
     * @throws NotStrongEnoughException Exception raised when the force is not enough strong
     * @throws FamilyMemberAlreadyInUseException Exception raised when the family member is already in use somewhere else
     * @throws NotEnoughPlayersException Exception raised when the zone is not enabled with the current amount of players
     * @throws PlaceOccupiedException Exception raised when the place is already in use
     * @throws NotEnoughResourcesException Exception raised when the player does not have enough resources
     * @throws NotEnoughPointsException Exception raised when the player does not have enough points
     * @throws SixCardsLimitReachedException Exception raised when the player cannot take another card of that type
     * @throws PlayerAlreadyOccupiedTowerException Exception raised when the player tries to put another player on a tower that has already been used by him
     */
    private void onPlayerMove(Player player, Action Action) throws ActionException {

        if(Action instanceof StandardPlacementAction){

            placeFamilyMember((StandardPlacementAction) Action,player);

        }

        if(Action instanceof LeaderCardActivationAction){

            activateLeaderCard((LeaderCardActivationAction) Action, player);

        }

        if(Action instanceof RollDicesAction){

            rollDices();

        }

    }

    /**
     * Sends the updated model to every player
     */
    private void sendUpdatedModel() {


        for (Player p : this.match.getPlayers()) {

            this.remotePlayerMap.get(p).notifyModelUpdate(this.match);

        }

    }

    private void notifyAllTurnEnabled(Player current) {

        for (Player p : this.match.getPlayers()) {

            this.remotePlayerMap.get(p).notifyTurnEnabled(current, current.getUsername() + " can take his move.");

        }

    }

    private void notifyAllTurnDisabled(Player current) {

        for (Player p : this.match.getPlayers()) {

            this.remotePlayerMap.get(p).notifyTurnDisabled(current,  current.getUsername() + " terminated his turn.");

        }

    }

    private void notifyAllActionTimeoutExpired(Player current) {

        for (Player p : this.match.getPlayers()) {

            this.remotePlayerMap.get(p).notifyActionTimeoutExpired(current, GameMessage.TimeoutExpired.getLiteral());

        }

    }




    public LinkedHashMap<Player, RemotePlayer> getRemotePlayerMap() {
        return this.remotePlayerMap;
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
    public void applyDvptCardCost(Player player, DvptCard card, SelectionType costOptionType) throws ActionException {

        //territory cards doesn't have cost
        if(card.getType() == DvptCardType.territory)
            return;

        int i=0;

        //some cards could have a double cost
        if(card.getCost().size()>1)
            i = costOptionType.toInt();

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
     * this method applies immediate effect of a card on the player who took it
     * @param player
     * @param card
     * @throws NotEnoughResourcesException
     * @throws NotEnoughPointsException
     */
    public void applyImmediateEffect(Player player, DvptCard card) throws ActionException {
        //TODO
        StandardPlacementAction action;
        ImmediateEffect immediateEffect = card.getImmediateEffect();


        applyEffectSurplus(player,immediateEffect.getSurplus());


        if(immediateEffect.getEffectAction().getTarget() == server.model.effect.ActionType.harvest)
            applyHarvestChain(player,immediateEffect.getEffectAction().getForce());

        if(immediateEffect.getEffectAction().getTarget() == server.model.effect.ActionType.production)
            applyProductionChain(player,immediateEffect.getEffectAction().getForce());

        if(immediateEffect.getEffectAction().getTarget() == server.model.effect.ActionType.card){
            if(immediateEffect.getEffectAction().getType() == DvptCardType.territory)
                //manda al client quale azione può essere fatta -----> BoardSectorType + Force + Discount
                ;

            if(immediateEffect.getEffectAction().getType() == DvptCardType.character) {
                //manda al client quale azione può essere fatta -----> BoardSectorType + Force + Discount
                ;

                //Richiede l'interazione..
                this.notifyAllTurnEnabled(this.currentPlayer);


            }

            if(immediateEffect.getEffectAction().getType() == DvptCardType.building)
                //manda al client quale azione può essere fatta -----> BoardSectorType + Force + Discount
                ;

            if(immediateEffect.getEffectAction().getType() == DvptCardType.venture)
                //manda al client quale azione può essere fatta -----> BoardSectorType + Force + Discount
                ;}

        //multiplier immediate effect is always in the first slot of points array
        if(immediateEffect.getSurplus().getPoints().size()>0){
            if(immediateEffect.getSurplus().getPoints().get(0).getMultiplier()!= null)
                applyMultiplier(player,immediateEffect.getSurplus().getPoints().get(0).getMultiplier());
        }
    }

    /**
     * this method receives an Action and its author and places the familiar in the correct place (if it is strong enough)
     * @param action
     * @param player
     * @throws NotStrongEnoughException
     */
    public void placeFamilyMember(StandardPlacementAction action, Player player) throws ActionException {

        FamilyMember familyMember = player.getFamilyMember(action.getColorType());

        //apply character cards permanent effect
        action = actionCharacterFilter(action,player);

        //if boardSectorType is CouncilPalace we place the family member in the council palace
        //once positioned the council palace give to the player an effectSurplus
        if (action.getActionTarget() == BoardSectorType.CouncilPalace) {
            EffectSurplus surplus = boardController.placeOnCouncilPalace(familyMember, action.getAdditionalServants(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);
        }

        //if boardSectorType is Market we place the family member in the correct market place (from index 0 to index 3)
        //once positioned the market place give to the player an effectSurplus
        if (action.getActionTarget() == BoardSectorType.Market) {

            EffectSurplus surplus = boardController.placeOnMarket(familyMember,action.getPlacementIndex(),action.getAdditionalServants(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);

        }

        //if boardSectorType is SingleHarvestPlace we place the family member in the single harvest place of the harvest area
        //once positioned the single harvest place give to the player an effectSurplus
        //the harvestChain (activation of all the permanent effect of territory cards that has harvest type) is also started
        if(action.getActionTarget() == BoardSectorType.SingleHarvestPlace) {

            EffectSurplus surplus = boardController.placeOnSingleHarvestPlace(familyMember,action.getAdditionalServants(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);
            applyHarvestChain(player,familyMember.getForce() + action.getAdditionalServants());
        }

        //if boardSectorType is CompositeHarvestPlace we place the family member in the composite harvest place of the harvest area
        //once positioned the composite harvest place give to the player an effectSurplus
        //the harvestChain (activation of all the permanent effect of territory cards that has harvest type) is also started with a malus on the activation force
        if(action.getActionTarget() == BoardSectorType.CompositeHarvestPlace) {

            EffectSurplus surplus = boardController.placeOnCompositeHarvestPlace(familyMember,action.getAdditionalServants(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);

            //we have to subtract force malus from activation force
            applyHarvestChain(player,familyMember.getForce() + action.getAdditionalServants() - this.match.getBoard().getHarvestArea().getSecondaryPlace().getForceMalus());
        }

        //if boardSectorType is SingleProductionPlace we place the family member in the single production place of the production area
        //once positioned the single production place give to the player an effectSurplus
        //the productionChain (activation of all the permanent effect of building cards that has production type) is also started
        if(action.getActionTarget() == BoardSectorType.SingleProductionPlace) {

            EffectSurplus surplus = boardController.placeOnSingleProductionPlace(familyMember,action.getAdditionalServants(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);
            applyProductionChain(player,familyMember.getForce() + action.getAdditionalServants());

        }

        //if boardSectorType is CompositeProductionPlace we place the family member in the composite production place of the production area
        //once positioned the composite production place give to the player an effectSurplus
        //the productionChain (activation of all the permanent effect of building cards that has production type) is also started with a malus on the activation force
        if(action.getActionTarget() == BoardSectorType.CompositeProductionPlace) {

            EffectSurplus surplus = boardController.placeOnCompositeProductionPlace(familyMember,action.getAdditionalServants(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);

            //we have to subtract a force malus from activation force
            applyProductionChain(player,familyMember.getForce() + action.getAdditionalServants() - this.match.getBoard().getProductionArea().getSecondaryPlace().getForceMalus());

        }

        //if boardSectorType is a tower sector we place the family member in the correct (placementIndex) towerSlot of the tower
        //once positioned the towerSlot give to the player an effectSurplus
        if (action.getActionTarget() == BoardSectorType.VentureTower || action.getActionTarget() == BoardSectorType.CharacterTower || action.getActionTarget() == BoardSectorType.BuildingTower || action.getActionTarget() == BoardSectorType.TerritoryTower) {

            //get tower type from board sector
            DvptCardType towerType = getTowerType(action.getActionTarget());

            //control if the towerSlot is already occupied
            if(this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).isOccupied())
                throw new PlaceOccupiedException("This place is already occupied");

            //control if the player has another family member in the tower
            if (this.match.getBoard().getPlayersInTower(towerType).contains(player))
                throw new PlayerAlreadyOccupiedTowerException("the player already has a family member in this tower");

            //if the tower is already occupied tha player have to pay 3 coins
            if (this.match.getBoard().getTower(towerType).size() > 0)
                player.subtractCoins(3);

            //try to apply card cost to the player that made the action .. if this method return an exception no family members will be set here
            applyDvptCardCost(player, this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).getDvptCard(), action.getCostOptionType());

            EffectSurplus effectSurplus = boardController.placeOnTower(familyMember, action.getAdditionalServants(), this.match.getPlayers().size(), towerType, action.getPlacementIndex());
            applyEffectSurplus(player, effectSurplus);

            //add to the personal board of the player the building card set in the tower slot
            player.getPersonalBoard().addCard(this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).getDvptCard());

            applyImmediateEffect(player, this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).getDvptCard());

            //set the dvptCard of the tower to null value because no one can choose or take it now
            this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).setDvptCard(null);

        }

        //set the familiar busy
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
        //apply effect surplus of the personal bonus tile of the player
        if(player.getPersonalBoard().getBonusTile().getHarvestMinForce() <= force)
            applyEffectSurplus(player,player.getPersonalBoard().getBonusTile().getHarvestSurplus());
    }


    public void activateLeaderCard (LeaderCardActivationAction action, Player player) {

        if(player.hasEnoughLeaderRequirements(action.getLeaderCardIndex())) { //verify the requirements to activate Leader Card

            if (GameSingleton.getInstance().getSpecificLeaderCard(action.getLeaderCardIndex()).getLeaderEffect().getOnceARound() != null) {

                for (Resource resource : GameSingleton.getInstance().getSpecificLeaderCard(action.getLeaderCardIndex()).getLeaderEffect().getOnceARound().getResources())
                    player.addGenericResource(resource.getType(), resource.getAmount());

                for (Point point : GameSingleton.getInstance().getSpecificLeaderCard(action.getLeaderCardIndex()).getLeaderEffect().getOnceARound().getPoints())
                    player.addGenericPoint(point.getType(), point.getAmount());

            }

        }


    }

    /** this method applies the Production Chain
     * this character chain consists in the activation of all the building card permanent effect**/

    public void applyProductionChain (Player player, Integer force) throws ActionException {

        for (DvptCard card : player.getPersonalBoard().getBuildingCards()
                ) {

            if (force >= card.getPermanentEffect().getMinForce()) {

                applyBuildingPermanentEffect(card, player, 0);

            }

        }

        //apply effect surplus of the personal bonus tile of the player
        if(player.getPersonalBoard().getBonusTile().getProductionMinForce() <= force)
            applyEffectSurplus(player,player.getPersonalBoard().getBonusTile().getProductionSurplus());
    }

    /** this method applies the PermanentEffect of a Building card, that could be a surplus or a conversion
     *
     * */

    public void applyBuildingPermanentEffect (DvptCard card, Player player, Integer choice) throws ActionException{

        if(card.getPermanentEffect().getSurplus() != null)

            applyEffectSurplus(player, card.getPermanentEffect().getSurplus());

        if(card.getPermanentEffect().getConversion() != null )

            applyConversion(player, card.getPermanentEffect().getConversion() , choice);

        if(card.getPermanentEffect().getMultiplier() != null)

            applyMultiplier(player, card.getPermanentEffect().getMultiplier());

    }

    /** this method applies a conversion permanent effect of a development card to a particular player
     *
     * @param player
     * @param conversionList the list of conversion contained in the card effect
     * @param choice the choice of different conversion which can be made by the player
     * @throws NotEnoughResourcesException
     * @throws NotEnoughPointsException
     */


    public void applyConversion (Player player, ArrayList<EffectConversion> conversionList, Integer choice) throws ActionException {

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

    public void applyMultiplier (Player player, Multiplier multiplier) throws NotEnoughResourcesException, NotEnoughPointsException {

        int bonus = (int) (player.getSizeMultipliedType (multiplier.getWhat()) * multiplier.getCoefficient());

        if(multiplier.getResult() == ResultType.coins)
            player.addCoins(bonus);

        if(multiplier.getResult() == ResultType.victory)
            player.addVictoryPoints(bonus);

    }

    /** this method rolls dices and set them on the board */

    public void rollDices (){

        Random random = new Random();

        for (Dice d : this.match.getBoard().getDices()) {

            d.setValue(random.nextInt(5) + 1);

        }

    }

    /**
     * this method calculate the final score of the players
     */
    public LinkedHashMap<Player,Integer> calculatesFinalScore(){

        LinkedHashMap<Player,Integer> finalScore = new LinkedHashMap<Player, Integer>();

        for (Player player : this.match.getPlayers()) {

            Integer totalScore = 0;

            totalScore += player.getVictoryPoints();

            //each venture card give a victory bonus
            for (VentureDvptCard card: player.getPersonalBoard().getVentureCards()) {
                totalScore += card.getPermanentEffect().getvPoints();
            }

            //one victory point from every 5 resources of all type
            totalScore += (player.getCoins() + player.getStones() + player.getWood() + player.getServants()) / 5;

            //victory points that depends on building card on the player personal board
            totalScore += BoardConfigParser.getVictoryBonus(DvptCardType.building,player.getPersonalBoard().getBuildingCards().size());

            //victory points that depends on character card on the player personal board
            totalScore += BoardConfigParser.getVictoryBonus(DvptCardType.character,player.getPersonalBoard().getCharacterCards().size());

            //victory points that depends on faith points
            totalScore += BoardConfigParser.getVictoryBonusFromFaith(player.getFaithPoints());

            //victory points that depends on military points
            totalScore += getMilitaryPointsBonus(player);

            finalScore.put(player,totalScore);
        }

        return  finalScore;
    }

    /**
     * this method create the military points ranking and return the victory points for each player
     */
    public Integer getMilitaryPointsBonus(Player player){

        Integer position = 1;

        for (Player player1: this.match.getPlayers()) {

            if(player.getMilitaryPoints() < player1.getMilitaryPoints())
                position++;
        }

        return getVictoryBonusFromRanking(position);
    }

    /**
     * this method apply permanent effect of character cards when a player try to place his family member
     * @param action
     * @param player
     * @return
     */
    public StandardPlacementAction actionCharacterFilter(StandardPlacementAction action, Player player) throws PreacherEffectException {

        //scroll through the character cards of a player looking for permanent effect Action
        for (CharacterDvptCard card: player.getPersonalBoard().getCharacterCards()) {

            EffectPermanentAction permanentEffectAction = card.getPermanentEffect().getAction();

            //if a permanent effect is relative to harvest type, check whether the Action target is CompositeHarvestPlace or SingleHarvestPlace and modify the Action
            if(permanentEffectAction.getTarget() == server.model.effect.ActionType.harvest) {

                if (action.getActionTarget() == BoardSectorType.CompositeHarvestPlace || action.getActionTarget() == BoardSectorType.SingleHarvestPlace) {
                    action.increaseBonus(permanentEffectAction.getForceBonus());
                }
            }

            //if a permanent effect is relative to production type, check whether the Action target is CompositeProductionPlace or SingleProductionPlace and modify the Action
            if(permanentEffectAction.getTarget() == server.model.effect.ActionType.production) {

                if (action.getActionTarget() == BoardSectorType.CompositeProductionPlace || action.getActionTarget() == BoardSectorType.SingleProductionPlace) {
                    action.increaseBonus(permanentEffectAction.getForceBonus());
                }
            }

            //if a permanent effect is relative to cardtype, check the DvptCardType type and modify the Action

            if(permanentEffectAction.getTarget() == server.model.effect.ActionType.card){
                System.out.println(permanentEffectAction.getType());
                if(permanentEffectAction.getType() == DvptCardType.territory)
                    action.increaseBonus(permanentEffectAction.getForceBonus());

                if(permanentEffectAction.getType() == DvptCardType.building)
                    action.increaseBonus(permanentEffectAction.getForceBonus());

                if(permanentEffectAction.getType() == DvptCardType.character)
                    action.increaseBonus(permanentEffectAction.getForceBonus());

                if(permanentEffectAction.getType() == DvptCardType.venture)
                    action.increaseBonus(permanentEffectAction.getForceBonus());
                //TODO effect discount
            }

            //if the effect is the preacher penality forbid the Action if the placement index is > 1
            if(card.getPermanentEffect().isPenality()){
                if (action.getPlacementIndex()>1) {
                    throw new PreacherEffectException("Preacher's permanent effect forbid it");
                }
            }

            //TODO effect discount

        }

        return  action;

    }

    public void handleVaticanReport(Player player){

        //get minimum number of faith points for current period
        Integer minPeriodFaith = this.match.getBoard().getCathedral().getMinFaith(this.match.getCurrentPeriod());

        //if the player has not enough faith points he receive the excommunication
        if(player.getFaithPoints() < minPeriodFaith)
            player.addBanCard(this.match.getBoard().getCathedral().getBanCard(this.match.getCurrentPeriod()));

        else
        {
            //TODO player interation

            if(true){
                //the player has enough faith points but doesn't want to use them to avoid excommunication
                player.addBanCard(this.match.getBoard().getCathedral().getBanCard(this.match.getCurrentPeriod()));
            }
            else
                //the player use his faith points to avoid excommunication and receive a number of victory points depending on his faith points
                player.addVictoryPoints(BoardConfigParser.getVictoryBonusFromFaith(player.getFaithPoints()));

            //the player cannot choose how many faith points to use
            player.setFaithPoints(0);
        }
    }

    public DvptCardType getTowerType(BoardSectorType boardSectorType){

        if(boardSectorType == BoardSectorType.BuildingTower)
            return DvptCardType.building;

        if(boardSectorType == BoardSectorType.CharacterTower)
            return DvptCardType.character;

        if(boardSectorType == BoardSectorType.TerritoryTower)
            return DvptCardType.territory;


        return DvptCardType.venture;
    }

    public BoardController getBoardController() {
        return boardController;
    }
}