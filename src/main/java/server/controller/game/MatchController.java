package server.controller.game;
import exception.*;
import logger.Level;
import logger.Logger;
import netobject.NetObjectType;
import netobject.action.*;
import netobject.action.immediate.ImmediateActionType;
import netobject.action.immediate.ImmediateChoiceAction;
import netobject.action.immediate.ImmediatePlacementAction;
import netobject.action.standard.*;
import server.controller.network.ClientHandler;
import server.model.*;
import server.model.board.*;
import server.model.card.Deck;
import server.model.card.ban.*;
import server.model.card.developement.*;
import server.model.card.leader.LeaderCard;
import server.model.effect.*;
import server.model.effect.ActionType;
import server.model.valuable.*;
import server.utility.BoardConfigParser;
import singleton.GameConfig;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

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
     * Hold a reference to the lobby
     */
    private Lobby lobby;

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
    private static final int ACTION_TIMEOUT =  GameConfig.getInstance().getPlayerTimeout();


    /**
     * This is the match controller constructor.
     * It is called only by the lobby itself when the match starts
     * @param handlers the handlers of the model players
     */
    public MatchController(ArrayList<ClientHandler> handlers, Lobby lobby) {

        //Set the lobby
        this.lobby = lobby;

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
         * Gives the proper resources to the players
         * Order matters and follows game rules
         */
        this.initPlayerResource(players);

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

        /*
         * Initialize the blocking queue for the actions
         * Make it maximum size equals to the number of players so that in the draft we can block until it is full
         */
        this.actions = new LinkedBlockingQueue<Action>(players.size());

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

        //TODO: Draft the bonus tiles

        //Draft the leader cards first
        this.handleLeaderCardDraft();

        //Make sure that the match has already been initialized here!
        RoundIterator roundIterator = new RoundIterator(this.match);

        while (roundIterator.hasNext()) {

            //Obtain the next round
            Queue<Player> currentRound = roundIterator.next();

            if (this.match.getCurrentRound() == 1) {

                //Update the towers for the current combination of round / turn / period
                this.boardController.updateTowersForTurn(this.match.getCurrentTurn(), this.match.getCurrentPeriod().toInt());

                //Free family members for each player
                for (Player player:this.getMatch().getPlayers()) {

                    player.freeFamilyMembers();

                }

                //If the match has just started send the model before any move is performed
                if (this.match.getCurrentRound() == 1 && this.match.getCurrentPeriod() == Period.first) {

                    this.sendUpdatedModel();

                }

            }

            Logger.log(Level.FINEST, this.toString(), "New round started (Period = " +this.match.getCurrentPeriod() + " - Turn = " + this.match.getCurrentTurn() + " - Round = " +this.match.getCurrentRound() + ")");

            //Foreach round handle the current player
            for (Player p : currentRound) {

                //Skip each disabled player
                if (p.isDisabled()) {

                    Logger.log(Level.FINEST, this.toString(), "Skipping player " + p.getUsername() +" because he is disabled");

                    continue;
                }

                this.handlePlayerRound(p);

            }

            //After each round finishes we must send the updated model to each player, always
            this.sendUpdatedModel();

        }

    }

    /**
     * Handle the leader card draft
     * Creates a number of draftable decks equal to the number of players
     * It then sends every deck to the player and wait for any player to chose
     * When all the players have selected one card, the draft takes places and the decks received from the nearby player are sent.
     */
    private void handleLeaderCardDraft() {

        final int NUMBER_OF_CARDS_PER_DECK = 4;

        //Create a deck with all the 20 leader cards and shuffle it
        Deck<LeaderCard> deck = new Deck<>(GameSingleton.getInstance().getLeaderCards()).shuffle();

        //Create a temporary map for each player username
        TreeMap<String, Deck<LeaderCard>> draftingMap = new TreeMap<>();

        int i = 0;

        //Create n sub decks, where n is the number of players
        for (Player p : this.match.getPlayers()) {

            Deck<LeaderCard> draftableDeck = new Deck<>();

            for (int j = 0; j < NUMBER_OF_CARDS_PER_DECK; j++) {

                draftableDeck.addCard(deck.getCards().get((i * NUMBER_OF_CARDS_PER_DECK) + j));

            }

            draftingMap.put(p.getUsername(), draftableDeck);

            //Tell the user the deck from which he can select a card
            this.remotePlayerMap.get(p).notifyLeaderCardDraftRequest(draftableDeck, "Please select a leader card and draft");

            Logger.log(Level.FINEST, "Match controller", "Sending draftable deck to " + p.getUsername());

            i++;

        }


        for (i = 0; i < NUMBER_OF_CARDS_PER_DECK; i++) {

            //Four times we need to wait until each player performs his draft
            for (int j = 0; j < this.match.getPlayers().size(); j ++) {

                //Get the shuffle action
                ShuffleLeaderCardStandardAction shuffleAction = null;
                try {

                    shuffleAction = (ShuffleLeaderCardStandardAction)this.actions.take();

                } catch (InterruptedException e) {

                    Logger.log(Level.WARNING, "Match controller", "Thread stopped while waiting on action queue", e);

                }

                //Get the selected card, DEBUG ONLY
                LeaderCard selected = shuffleAction.getDeck().getCards().get(shuffleAction.getSelection());

                //Find the player in the map and remove
                draftingMap.get(shuffleAction.getSender()).removeCard(shuffleAction.getSelection());

                //Add the selected card to the leader cards of the player
                try {

                    this.match.getPlayerFromUsername(shuffleAction.getSender()).addLeaderCard(selected);

                    this.notifyAllActionPerformed(this.match.getPlayerFromUsername(shuffleAction.getSender()), shuffleAction, shuffleAction.getSender() + " drafted his leader cards, he selected '" + selected.getName() + "'");


                } catch (NoSuchPlayerException e) {

                    Logger.log(Level.FINEST, "Match controller", "Can't find player!", e);

                }

                Logger.log(Level.FINEST, "Match controller", "Leader draft, step " + (i + 1) + ", " + shuffleAction.getSender() + " selected '" + selected.getName() + "'");

            }


            Deck<LeaderCard> first = null;

            //Set the deck of each player to the deck of the previous one
            for (Map.Entry<String , Deck<LeaderCard>> e : draftingMap.entrySet()) {

                if (e.getKey().equals(draftingMap.firstEntry().getKey())) {

                    first = e.getValue();

                }

                if (e.getKey().equals(draftingMap.lastEntry().getKey())) {

                    e.setValue(first);

                }

                else {

                    e.setValue(draftingMap.higherEntry(e.getKey()).getValue());

                }

            }

            //Send again the drafted decks
            for (Player p : this.match.getPlayers()) {

                this.remotePlayerMap.get(p).notifyLeaderCardDraftRequest(draftingMap.get(p.getUsername()), "Please select a leader card and draft");

            }

        }

    }

    /**
     * Handles a player round.
     * Listens for actions
     * @param player the player
     */
    private void handlePlayerRound(Player player) {

        //Update the current player
        this.currentPlayer = player;

        Logger.log(Level.FINEST, this.toString(), "It is " + this.currentPlayer.getUsername() + "'s turn!");


        //Notify the turn of the player
        this.notifyAllTurnEnabled(this.currentPlayer);

        Action action;

        //Loop the players actions until he terminates his round
        do {

            try {

                action = this.waitForAction(ACTION_TIMEOUT * 1000);

                if (action instanceof TerminateRoundStandardAction) {

                    Logger.log(Level.FINEST, this.toString(), "The player " + this.currentPlayer.getUsername() + " terminated his round");

                    //Tell the players that the active one can't make any more actions
                    this.notifyAllTurnDisabled(this.currentPlayer);

                    break;

                }
                else {

                    Logger.log(Level.FINEST, this.toString(), "Parsing action request, the active player is " + this.currentPlayer.getUsername());

                    try {

                        //Handler the player action
                        String status = this.handlePlayerAction(this.currentPlayer, action);

                        //Update the model
                        this.sendUpdatedModel();


                        //If we get here without exceptions we can notify of the succeeded action
                        this.notifyAllActionPerformed(this.currentPlayer, action, status);

                    }
                    catch (ActionException reason) {

                        //Inform the player that he can't take that action
                        this.remotePlayerMap.get(this.currentPlayer).notifyActionRefused(GameMessage.InvalidAction.getLiteral() + " Reason: " + reason.getMessage());

                    }

                }


            } catch (NoActionPerformedException e) {

                Logger.log(Level.WARNING, this.toString(), "The action timeout for player " + this.currentPlayer.getUsername() + " expired", e);

                //Tell the players that the timeout has expired expired for the active player
                this.notifyAllActionTimeoutExpired(this.currentPlayer);

                //Disable the player
                this.currentPlayer.setDisabled(true);

                //Tell the players that the active one can't make any more actions
                this.notifyAllTurnDisabled(this.currentPlayer);

                //Break the loop
                break;


            } catch (InterruptedException e) {

                Logger.log(Level.WARNING, "Match controller", "Thread stopped while waiting on action queue", e);

            }

        }
        while (!this.currentPlayer.isDisabled());

    }

    /**
     * Suspends the thread until the user performs an action or the timeout expires
     * @return The action performed
     * @throws NoActionPerformedException When the timeout expires
     */
    private Action waitForAction(int timeout) throws NoActionPerformedException, InterruptedException {

        Action action;

        //Setup a new timeout for the action
        this.currentPlayerTimeout = new Timer();

        //Define what to do when, and if, the timeout expires
        this.currentPlayerTimeout.schedule(new TimerTask() {
            @Override
            public void run() {

                //By the time this method gets fired the player should has already taken his action.
                //If not, we set the player as disabled and continue
                MatchController.this.currentPlayer.setDisabled(true);

                //To wake up the thread, inject a poisonous action
                MatchController.this.actions.add(new Action());


            }
        }, timeout);


        //Take the action request in the queue and check if we shall proceed
        //Note that this is a blocking queue
        action = this.actions.take();

        //When we get here the player took its action or the timeout for the action expired, clear the interval.
        this.currentPlayerTimeout.cancel();

        //Check if the action is legit, if not skip this player. It might just have expired the timeout
        if (action.getType() == NetObjectType.Poison) {

            throw new NoActionPerformedException("No action was performed within the timeout provided");

        }

        return action;
    }

    /**
     * This method is the only one that should be called from other threads.
     * Specifically, it is used by client handler to dispatch their client actions
     * @param action the Action
     */
    public void dispatchNewPlayerAction(Action action) {

        this.actions.add(action);

    }

    /**
     * This method is called internally by the run loop
     * It decides, based on the Action performed by the active player, what should be performed
     * @param player the player that performed the Action, which is the active one
     * @param action the Action perfomed
     * @return A custom message of success
     * @throws NotStrongEnoughException Exception raised when the force is not enough strong
     * @throws FamilyMemberAlreadyInUseException Exception raised when the family member is already in use somewhere else
     * @throws NotEnoughPlayersException Exception raised when the zone is not enabled with the current amount of players
     * @throws PlaceOccupiedException Exception raised when the place is already in use
     * @throws NotEnoughResourcesException Exception raised when the player does not have enough resources
     * @throws NotEnoughPointsException Exception raised when the player does not have enough points
     * @throws SixCardsLimitReachedException Exception raised when the player cannot take another card of that type
     * @throws PlayerAlreadyOccupiedTowerException Exception raised when the player tries to put another player on a tower that has already been used by him
     */
    private String handlePlayerAction(Player player, Action action) throws ActionException, NoActionPerformedException {

        String message = "";

        if(action instanceof StandardPlacementAction){

            placeFamilyMember((StandardPlacementAction) action,player);

            message = "placed a family member on " + ((StandardPlacementAction)action).getActionTarget();

        }

        if(action instanceof LeaderCardActivationAction){

            message = "activated a leader card";


        }

        if(action instanceof RollDicesAction){

            rollDices();

            message = "rolled the dices";

        }

        return player.getUsername() + " " + message;


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

            this.remotePlayerMap.get(p).notifyTurnEnabled(current, "It is " + current.getUsername() + "'s turn");

        }

    }

    private void notifyAllTurnDisabled(Player current) {

        for (Player p : this.match.getPlayers()) {

            this.remotePlayerMap.get(p).notifyTurnDisabled(current,  current.getUsername() + " terminated his turn.");

        }

    }

    private void notifyAllActionTimeoutExpired(Player current) {

        for (Player p : this.match.getPlayers()) {

            this.remotePlayerMap.get(p).notifyActionTimeoutExpired(current, current.getUsername() + "'s timeout to take his move expired. He was disabled.");

        }

    }

    private void notifyAllActionPerformed(Player current, Action action, String message) {

        for (Player p : this.match.getPlayers()) {

            this.remotePlayerMap.get(p).notifyActionPerformed(current, action, message);

        }

    }

    private void notifyAllImmediateActionAvailable(ImmediateActionType immediateActionType, Player current, String message) {

        for (Player p : this.match.getPlayers()) {

            this.remotePlayerMap.get(p).notifyImmediateActionAvailable(immediateActionType, current, message);

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
    public void applyDvptCardCost(Player player, DvptCard card,ArrayList<Discount> discount,SelectionType costOptionType) throws ActionException {

        //territory cards doesn't have cost
        if(card != null){
        if(card.getType() == DvptCardType.territory)
            return;

        int i=0;

        //some cards could have a double cost
        if(card.getCost().size()>1){
            if(costOptionType == SelectionType.Second)
                i=1;
        }

        //get the choosen one cost
        //clone the cost in order to modify a temporary variable
        Cost costo = new Cost(card.getCost().get(i));

        //apply discount
            costo = applyDiscount(costo,discount);



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
    }
    /**
     * this method subtract a discount from a cost
     * @param costo
     * @param discount
     * @return
     */
    public Cost applyDiscount(Cost costo, ArrayList<Discount> discount){

        int choose = 0;

        if(discount.size() == 0)
            return costo;

        if(discount.size()>1){
            System.out.println("chiedo all'utente cosa vuole tra: "+discount.get(0).getDiscount().get(0).getType()+" "+discount.get(1).getDiscount().get(0).getType());
        }

        for (Resource resource:costo.getResources()) {

            for (Resource scount: discount.get(choose).getDiscount()) {

                if(scount.getType() == resource.getType()){


                    if(resource.getAmount() - scount.getAmount() >=0){
                        resource.setAmount(resource.getAmount() - scount.getAmount());}
                    else{
                        resource.setAmount(0);}
                }
            }
        }

        return costo;
    }

    /**
     * this method applies immediate effect of a card on the player who took it
     * @param player
     * @param card
     * @throws NotEnoughResourcesException
     * @throws NotEnoughPointsException
     */
    public void applyImmediateEffect(Player player, DvptCard card) throws ActionException, NoActionPerformedException {
        //TODO
        StandardPlacementAction action;
        if(card != null) {
            ImmediateEffect immediateEffect = card.getImmediateEffect();


            applyEffectSurplus(player, immediateEffect.getSurplus());


            if (immediateEffect.getEffectAction().getTarget() == ActionType.harvest)
                applyHarvestChain(player, immediateEffect.getEffectAction().getForce());

            if (immediateEffect.getEffectAction().getTarget() == ActionType.production)
                applyProductionChain(player, immediateEffect.getEffectAction().getForce());

            if (immediateEffect.getEffectAction().getTarget() == ActionType.card) {
                if (immediateEffect.getEffectAction().getTarget() == null) {
                    //può fare qualsiasi azione di posizionamento sulla torre
                }
                if (immediateEffect.getEffectAction().getType() == DvptCardType.territory)
                    //manda al client quale azione può essere fatta -----> BoardSectorType + Force + Discount
                    //mi salvo la forza che utilizzo poi per lanciare il metodo do immediate effect action
                    ;

                if (immediateEffect.getEffectAction().getType() == DvptCardType.character) {
                    //manda al client quale azione può essere fatta -----> BoardSectorType + Force + Discount
                    ;

                    //Richiede l'interazione..
                    this.notifyAllTurnEnabled(this.currentPlayer);


                }

                if (immediateEffect.getEffectAction().getType() == DvptCardType.building)
                    //manda al client quale azione può essere fatta -----> BoardSectorType + Force + Discount
                    ;

                if (immediateEffect.getEffectAction().getType() == DvptCardType.venture)
                    //manda al client quale azione può essere fatta -----> BoardSectorType + Force + Discount
                    ;
            }

            //multiplier immediate effect is always in the first slot of points array
            if (immediateEffect.getSurplus().getPoints().size() > 0) {
                if (immediateEffect.getSurplus().getPoints().get(0).getMultiplier() != null)
                    applyMultiplier(player, immediateEffect.getSurplus().getPoints().get(0).getMultiplier());
            }
        }
    }

    /**
     * this method receives a family placement action and its author and places the familiar in the correct place (if it is strong enough)
     * @param action
     * @param player
     * @throws NotStrongEnoughException
     */
    public void placeFamilyMember(StandardPlacementAction action, Player player) throws ActionException, NoActionPerformedException {

        FamilyMember familyMember = player.getFamilyMember(action.getColorType());
        boolean noMarket = false;

        //some players' ban card can reduce family member's force
        noMarket = applyDiceMalusBanCard(action, player, familyMember, noMarket);

        //apply character cards permanent effect
        action = actionCharacterFilter(action,player);

        action = applyLeaderCardEffect(player, action);

        //if boardSectorType is CouncilPalace we place the family member in the council palace
        //once positioned the council palace give to the player an effectSurplus
        if (action.getActionTarget() == BoardSectorType.CouncilPalace) {
            EffectSurplus surplus = boardController.placeOnCouncilPalace(familyMember, action.getAdditionalServants(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);

        }

        //if boardSectorType is Market we place the family member in the correct market place (from index 0 to index 3)
        //once positioned the market place give to the player an effectSurplus
        else if (action.getActionTarget() == BoardSectorType.Market) {

            if(noMarket)
                throw new NoMarketException("You can't place any family member in market because of the Special BanCard NoMarketMalus");
            else{
            EffectSurplus surplus = boardController.placeOnMarket(familyMember,action.getPlacementIndex(),action.getAdditionalServants(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);}

        }

        //if boardSectorType is SingleHarvestPlace we place the family member in the single harvest place of the harvest area
        //once positioned the single harvest place give to the player an effectSurplus
        //the harvestChain (activation of all the permanent effect of territory cards that has harvest type) is also started
        else if(action.getActionTarget() == BoardSectorType.SingleHarvestPlace) {

            EffectSurplus surplus = boardController.placeOnSingleHarvestPlace(familyMember,action.getAdditionalServants(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);
            applyHarvestChain(player,familyMember.getForce() + action.getAdditionalServants());

        }

        //if boardSectorType is CompositeHarvestPlace we place the family member in the composite harvest place of the harvest area
        //once positioned the composite harvest place give to the player an effectSurplus
        //the harvestChain (activation of all the permanent effect of territory cards that has harvest type) is also started with a malus on the activation force
        else if(action.getActionTarget() == BoardSectorType.CompositeHarvestPlace) {

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
        else if(action.getActionTarget() == BoardSectorType.CompositeProductionPlace) {

            EffectSurplus surplus = boardController.placeOnCompositeProductionPlace(familyMember,action.getAdditionalServants(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);

            //we have to subtract a force malus from activation force
            applyProductionChain(player,familyMember.getForce() + action.getAdditionalServants() - this.match.getBoard().getProductionArea().getSecondaryPlace().getForceMalus());

        }

        //if boardSectorType is a tower sector we place the family member in the correct (placementIndex) towerSlot of the tower
        //once positioned the towerSlot give to the player an effectSurplus
        else if (action.getActionTarget() == BoardSectorType.VentureTower || action.getActionTarget() == BoardSectorType.CharacterTower || action.getActionTarget() == BoardSectorType.BuildingTower || action.getActionTarget() == BoardSectorType.TerritoryTower) {

            //get tower type from board sector
            DvptCardType towerType = getTowerType(action.getActionTarget());

            //control if the player can take another territory card
            if(towerType == DvptCardType.territory){
                if(player.getMilitaryPoints() < BoardConfigParser.getMinimumMilitaryPoints(player.getPersonalBoard().getTerritoryCards().size() + 1) && !player.isPermanentLeaderActive(PermanentLeaderEffectType.cesareEffect))
                    throw new NotEnoughMilitaryPointsException("Not enough military points to take another territory card");
            }

            //control if the towerSlot is already occupied
            if(this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).isOccupied())
                throw new PlaceOccupiedException("This place is already occupied");

            //control if the player has another family member in the tower
            if (this.match.getBoard().getPlayersInTower(towerType).contains(player))
                throw new PlayerAlreadyOccupiedTowerException("the player already has a family member in this tower");

            //if the tower is already occupied the player has to pay 3 coins
            if (this.match.getBoard().getPlayersInTower(towerType).size() > 0 && !player.isPermanentLeaderActive(PermanentLeaderEffectType.filippoEffect))
                player.subtractCoins(3);

            //try to apply card cost to the player that made the action .. if this method return an exception no family members will be set here
            applyDvptCardCost(player, this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).getDvptCard(), action.getDiscounts(), action.getCostOptionType());

            EffectSurplus effectSurplus = boardController.placeOnTower(familyMember, action.getAdditionalServants(), this.match.getPlayers().size(), towerType, action.getPlacementIndex());

            if(player.isPermanentLeaderActive(PermanentLeaderEffectType.ritaEffect)){
                for(Resource resource: effectSurplus.getResources())
                    resource.setAmount(resource.getAmount()*2);
                }
                applyEffectSurplus(player, effectSurplus);

            //add to the personal board of the player the development card set in the tower slot
            player.getPersonalBoard().addCard(this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).getDvptCard());


            applyImmediateEffect(player, this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).getDvptCard());

            //set the dvptCard of the tower to null value because no one can choose or take it now
            this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).setDvptCard(null);

        }

        //subtract the additional servants used
        player.subtractServants(action.getAdditionalServants());

        //set the familiar busy
        familyMember.setBusy(true);
    }

    public StandardPlacementAction applyLeaderCardEffect(Player player, StandardPlacementAction action){

            for(LeaderCard leaderCard : player.getActiveLeaderCards()){

                if(player.isPermanentLeaderActive(PermanentLeaderEffectType.ariostoEffect)) {

                    if (action.getActionTarget() == BoardSectorType.TerritoryTower)
                        getMatch().getBoard().getTerritoryTower().get(action.getPlacementIndex()).setOccupied(false);

                    if (action.getActionTarget() == BoardSectorType.BuildingTower)
                        getMatch().getBoard().getBuildingTower().get(action.getPlacementIndex()).setOccupied(false);

                    if (action.getActionTarget() == BoardSectorType.CharacterTower)
                        getMatch().getBoard().getCharacterTower().get(action.getPlacementIndex()).setOccupied(false);

                    if (action.getActionTarget() == BoardSectorType.BuildingTower)
                        getMatch().getBoard().getBuildingTower().get(action.getPlacementIndex()).setOccupied(false);

                    if (action.getActionTarget() == BoardSectorType.CouncilPalace)
                        getMatch().getBoard().getCouncilPalace().getPlaces().get(action.getPlacementIndex()).setBusy(false);

                    if (action.getActionTarget() == BoardSectorType.Market)
                        getMatch().getBoard().getMarket().getMarketPlaces().get(action.getPlacementIndex()).setOccupied(false);

                    if (action.getActionTarget() == BoardSectorType.SingleHarvestPlace)
                        getMatch().getBoard().getHarvestArea().getMainPlace().setOccupied(false);

                    if (action.getActionTarget() == BoardSectorType.CompositeHarvestPlace)
                        getMatch().getBoard().getHarvestArea().getSecondaryPlace().getPlaces().get(action.getPlacementIndex()).setBusy(false);

                    if (action.getActionTarget() == BoardSectorType.SingleProductionPlace)
                        getMatch().getBoard().getProductionArea().getMainPlace().setOccupied(false);

                    if (action.getActionTarget() == BoardSectorType.CompositeProductionPlace)
                        getMatch().getBoard().getProductionArea().getSecondaryPlace().getPlaces().get(action.getPlacementIndex()).setBusy(false);

                }

                if(player.isPermanentLeaderActive(PermanentLeaderEffectType.sigismondoEffect) && action.getColorType() == ColorType.Nautral) {
                    action.increaseBonus(3);
                }

                if(player.isPermanentLeaderActive(PermanentLeaderEffectType.lucreziaEffect)) {
                    action.increaseBonus(2);
                }

                if(player.isPermanentLeaderActive(PermanentLeaderEffectType.moroEffect) && action.getColorType() != ColorType.Nautral) {
                    try {
                        action.increaseBonus(5-player.getFamilyMember(action.getColorType()).getForce());
                    } catch (FamilyMemberAlreadyInUseException e) {
                        e.printStackTrace();
                    }
                }

                if(player.isPermanentLeaderActive(PermanentLeaderEffectType.picoEffect)){
                    ArrayList <Resource> coinDiscount = new ArrayList<Resource>();
                    coinDiscount.add(new Resource(ResourceType.Coins, 3));
                    Discount discount = new Discount(coinDiscount);
                    action.getDiscounts().add(discount);
                }
            }

            return action;

    }


    public boolean applyDiceMalusBanCard(StandardPlacementAction action, Player player, FamilyMember familyMember, boolean noMarket) throws NotStrongEnoughException {
        for (BanCard bancard : player.getBanCards()) {
            if (bancard instanceof DiceBanCard) {

                if(((DiceBanCard) bancard).getEffectDiceMalus().getType() == DvptCardType.territory && action.getActionTarget() == BoardSectorType.TerritoryTower) {
                    if (familyMember.getForce() + action.getAdditionalServants() - ((DiceBanCard) bancard).getEffectDiceMalus().getMalus() < getMatch().getBoard().getTerritoryTower().get(action.getPlacementIndex()).getEntryForce()) {
                        throw new NotStrongEnoughException("Not strong enough to make this move because of Territory DiceBanCard");
                    }
                }

                if(((DiceBanCard) bancard).getEffectDiceMalus().getType() == DvptCardType.building && action.getActionTarget() == BoardSectorType.BuildingTower) {
                    if (familyMember.getForce() + action.getAdditionalServants() - ((DiceBanCard) bancard).getEffectDiceMalus().getMalus() < getMatch().getBoard().getBuildingTower().get(action.getPlacementIndex()).getEntryForce()) {
                        throw new NotStrongEnoughException("Not strong enough to make this move because of Building DiceBanCard");
                    }
                }

                if(((DiceBanCard) bancard).getEffectDiceMalus().getType() == DvptCardType.character && action.getActionTarget() == BoardSectorType.CharacterTower) {
                    if (familyMember.getForce() + action.getAdditionalServants() - ((DiceBanCard) bancard).getEffectDiceMalus().getMalus() < getMatch().getBoard().getCharacterTower().get(action.getPlacementIndex()).getEntryForce()) {
                        throw new NotStrongEnoughException("Not strong enough to make this move because of Character DiceBanCard");
                    }
                }

                if(((DiceBanCard) bancard).getEffectDiceMalus().getType() == DvptCardType.venture && action.getActionTarget() == BoardSectorType.VentureTower) {
                    if (familyMember.getForce() + action.getAdditionalServants() - ((DiceBanCard) bancard).getEffectDiceMalus().getMalus() < getMatch().getBoard().getVentureTower().get(action.getPlacementIndex()).getEntryForce()) {
                        throw new NotStrongEnoughException("Not strong enough to make this move because of Venture DiceBanCard");
                    }
                }
            }
            if(bancard instanceof SpecialBanCard){
                if(((SpecialBanCard) bancard).getSpecialEffect() == SpecialEffectType.servantsPowerMalus)
                    action.setAdditionalServants(action.getAdditionalServants() / 2);
            }
            if(bancard instanceof SpecialBanCard){
                if(((SpecialBanCard) bancard).getSpecialEffect() == SpecialEffectType.noMarketMalus)
                    noMarket = true;
            }
        }
        return noMarket;
    }

    /**
     * this method receive an immediate action and its author and do it
     * @param action
     * @param player
     * @throws ActionException
     */
    public void doImmediateAction(ImmediatePlacementAction action, Integer force, Player player) throws ActionException, NoActionPerformedException {

        //if boardSectorType is a tower sector we place the family member in the correct (placementIndex) towerSlot of the tower
        //once positioned the towerSlot give to the player an effectSurplus
        if (action.getActionTarget() == ImmediateBoardSectorType.VentureTower || action.getActionTarget() == ImmediateBoardSectorType.CharacterTower || action.getActionTarget() == ImmediateBoardSectorType.BuildingTower || action.getActionTarget() == ImmediateBoardSectorType.TerritoryTower) {

            //get tower type from board sector
            DvptCardType towerType = getTowerType(action.getActionTarget());

            //control if the towerSlot is already occupied
            if (this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).isOccupied())
                throw new PlaceOccupiedException("This place is already occupied");

            //control if the player has another family member in the tower
            if (this.match.getBoard().getPlayersInTower(towerType).contains(player))
                throw new PlayerAlreadyOccupiedTowerException("The player already has a family member in this tower");

            //if the tower is already occupied the player have to pay 3 coins
            if (this.match.getBoard().getPlayersInTower(towerType).size() > 0 && !player.isPermanentLeaderActive(PermanentLeaderEffectType.filippoEffect))
                player.subtractCoins(3);

            //try to apply card cost to the player that made the action .. if this method return an exception no family members will be set here
            applyDvptCardCost(player, this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).getDvptCard(), action.getDiscounts(), action.getCostOptionType());

            EffectSurplus effectSurplus = boardController.immediatePlacementOnTower(force+action.getAdditionalServants(), this.match.getPlayers().size(), towerType, action.getPlacementIndex());
            applyEffectSurplus(player, effectSurplus);

            //add to the personal board of the player the building card set in the tower slot
            player.getPersonalBoard().addCard(this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).getDvptCard());

            applyImmediateEffect(player, this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).getDvptCard());

            //set the dvptCard of the tower to null value because no one can choose or take it now
            this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).setDvptCard(null);


        }

        //if boardSectorType is harvest the harvestChain (activation of all the permanent effect of building cards that has production type) started
        else if(action.getActionTarget() == ImmediateBoardSectorType.Production) {

            //we have to subtract a force malus from activation force
            applyProductionChain(player,action.getAdditionalServants());

        }

        //if boardSectorType is harvest the productionChain (activation of all the permanent effect of territory cards that has harvest type) started
        else if(action.getActionTarget() == ImmediateBoardSectorType.Production) {

            //we have to subtract a force malus from activation force
            applyHarvestChain(player,action.getAdditionalServants());

        }

        //subtract the additional servants used
        player.subtractServants(action.getAdditionalServants());

    }

    /**
     * this method apply the effectSurplus to a player
     * @param player
     * @param surplus
     */
    public void applyEffectSurplus(Player player,EffectSurplus surplus) throws NoActionPerformedException {

        //effect surplus is composed by resources,points and council privilege
        surplus = applyValuableBanCard(player, surplus);
        ArrayList<Resource> resourcesSurplus = surplus.getResources();
        ArrayList<Point> pointsSurplus = surplus.getPoints();

        Integer council = surplus.getCouncil();

        player.addResources(resourcesSurplus);
        player.addPoints(pointsSurplus);

        //the client can choose which council privilege want to have
        if(council > 0) {

            //We have one or more privileges available and must ask the player to chose one
            ArrayList<Integer> selections = new ArrayList<>();

            ArrayList<EffectSurplus> councilPrivileges = BoardConfigParser.getCouncilPrivilegeOptions();

            for (int i = 0; i < council; i++) {

                ImmediateChoiceAction choice = null;

                //Foreach council privilege available, ask to chose
                while (choice == null || selections.contains(choice.getSelection())) {

                    //1 - Ask
                    this.notifyAllImmediateActionAvailable(ImmediateActionType.SelectCouncilPrivilege, this.currentPlayer, "Select a council privilege");

                    //2 - Wait
                    try {

                        choice = (ImmediateChoiceAction)this.waitForAction(ACTION_TIMEOUT * 1000);

                        if (selections.contains(choice.getSelection())) {

                            this.remotePlayerMap.get(player).notifyActionRefused("Each privilege must be different");

                        }
                        else {

                            this.notifyAllActionPerformed(player, choice, player.getUsername() + " performed an immediate action");

                        }

                    } catch (NoActionPerformedException e) {

                        //The user did not select anything
                        //Decide how to handle this event (Maybe assign a default privilege?)
                        //Rethrow the exception

                        throw e;

                    } catch (InterruptedException e) {

                        Logger.log(Level.WARNING, this.toString(), "Thread stopped while waiting for immediate action");

                    }

                }

                //Add the selection
                selections.add(choice.getSelection());


            }

            //Add to the player the correct resource
            for (Integer select : selections) {

                player.addResources(councilPrivileges.get(select).getResources());

                player.addPoints(councilPrivileges.get(select).getPoints());

            }

        }

    }

    /**
     * Utility method that checks if a builtin-type array contains an element
     * @param a the array
     * @param v the value
     * @return true of false
     */
    private boolean contains(int[] a, int v) {

        for (int x : a) {
            if (x == v)
                return true;
        }

        return false;

    }

    /**
     * this method starts the harvest chain.
     * this harvest chain consists in the activation of all the territory cards permament effect
     * @param player
     * @param force
     */
    public void applyHarvestChain(Player player, Integer force) throws NoActionPerformedException {

        force = applyHarvestBan(player, force);

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

    Integer applyHarvestBan (Player player, Integer force){
        for(BanCard bancard : player.getBanCards()){
            if(bancard instanceof  DiceBanCard){
                if(((DiceBanCard) bancard).getEffectDiceMalus().getTarget() == server.model.effect.ActionType.harvest)
                    force = force - ((DiceBanCard) bancard).getEffectDiceMalus().getMalus();
            }
        }
        return force;
    }

    Integer applyProductionBan (Player player, Integer force){
        for(BanCard bancard : player.getBanCards()){
            if(bancard instanceof  DiceBanCard){
                if(((DiceBanCard) bancard).getEffectDiceMalus().getTarget() == server.model.effect.ActionType.production)
                    force = force - ((DiceBanCard) bancard).getEffectDiceMalus().getMalus();
            }
        }
        return force;
    }

    /** this method controls if the surplus is reduced by some ban card effect **/
    EffectSurplus applyValuableBanCard(Player player, EffectSurplus surplus) {

        for (Resource resource : surplus.getResources()) {
            for (BanCard bancard : player.getBanCards()) {
                if (bancard instanceof ValuableBanCard) {
                    for (Resource resourceMalus : ((ValuableBanCard) bancard).getResources()) {
                        if (resourceMalus.getType() == resource.getType())
                            resource.setAmount(resource.getAmount() - resourceMalus.getAmount());
                    }
                }
            }
        }

        for (Point point : surplus.getPoints()) {
            for (BanCard bancard : player.getBanCards()) {
                if (bancard instanceof ValuableBanCard) {
                    for (Point pointMalus : ((ValuableBanCard) bancard).getPoints()) {
                        if (pointMalus.getType() == point.getType())
                            point.setAmount(point.getAmount() - pointMalus.getAmount());
                    }
                }
            }
        }
        return surplus;
    }

    /**this method actives a leader effect, activable once a round**/

    public void activateOnceARoundLeaderCard (LeaderOnceARoundActivationAction action, Player player) {

        LeaderCard leaderCard = GameSingleton.getInstance().getSpecificLeaderCard(action.getLeaderCardIndex());

            if (leaderCard.getLeaderEffect().getOnceARound() != null) {

                for (Resource resource : leaderCard.getLeaderEffect().getOnceARound().getResources())
                    player.addGenericResource(resource.getType(), resource.getAmount());

                for (Point point : leaderCard.getLeaderEffect().getOnceARound().getPoints())
                    player.addGenericPoint(point.getType(), point.getAmount());

                if (leaderCard.getLeaderEffect().getOnceARound().getAction().containsKey(ActionType.harvest)) {
                    try {
                        applyHarvestChain(player, leaderCard.getLeaderEffect().getOnceARound().getAction().get(ActionType.harvest));
                    } catch (NoActionPerformedException e) {
                        e.printStackTrace();
                    }
                }

                if (leaderCard.getLeaderEffect().getOnceARound().getAction().containsKey(ActionType.production)) {
                    try {
                        applyHarvestChain(player, leaderCard.getLeaderEffect().getOnceARound().getAction().get(ActionType.production));
                    } catch (NoActionPerformedException e) {
                        e.printStackTrace();
                    }
                }

                if (leaderCard.getLeaderEffect().getOnceARound().getSixEffect()) {
                    try {
                        applyHarvestChain(player, leaderCard.getLeaderEffect().getOnceARound().getAction().get(ActionType.production));
                    } catch (NoActionPerformedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    /** this method applies the Production Chain
     * this character chain consists in the activation of all the building card permanent effect**/

    public void applyProductionChain (Player player, Integer force) throws ActionException, NoActionPerformedException {


        //some ban cards can reduce player's power to activate production chain
        force = applyProductionBan(player, force);

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

    public void applyBuildingPermanentEffect (DvptCard card, Player player, Integer choice) throws ActionException, NoActionPerformedException {

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


    public void applyConversion (Player player, ArrayList<EffectConversion> conversionList, Integer choice) throws ActionException, NoActionPerformedException {

        if(!conversionList.get(choice).getFrom().getResources().isEmpty()) {

            for(Resource from: conversionList.get(choice).getFrom().getResources())

                player.subtractGenericResource(from.getType(), from.getAmount());

        }

        if(!conversionList.get(choice).getFrom().getPoints().isEmpty()) {

            for(Point from: conversionList.get(choice).getFrom().getPoints())

                player.subtractGenericPoint(from.getType(), from.getAmount());

        }

        applyEffectSurplus(player, conversionList.get(choice).getTo());


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

        //Randomize the dice values
        for (Dice d : this.match.getBoard().getDices()) {

            d.setValue(random.nextInt(5) + 1);

        }

        //Update the family member values of each player
        for (Player p : this.match.getPlayers()) {

            //Looping through each dice
            for (Dice d : this.match.getBoard().getDices()) {

                p.setFamilyMemberForce(d.getColor(), d.getValue());

            }

        }

    }

    /**
     * this method calculate the final score of the players
     */
    public LinkedHashMap<Player,Integer> calculatesFinalScore(){

        HashMap<Integer,Player> playersScore = new HashMap<>();
        LinkedHashMap<Player,Integer> finalScore = new LinkedHashMap<Player, Integer>();



        for (Player player : this.match.getPlayers()) {

            Integer totalScore = 0;

            totalScore += player.getVictoryPoints();

            HashMap<DvptCardType,Boolean> BanFlag = applyNoVictoryBan(player);



            //each venture card give a victory bonus
            if(BanFlag.get(DvptCardType.venture) == false){
                for (VentureDvptCard card: player.getPersonalBoard().getVentureCards()) {
                totalScore += card.getPermanentEffect().getvPoints();
            }}


            //one victory point from every 5 resources of all type
            totalScore += (player.getCoins() + player.getStones() + player.getWood() + player.getServants()) / 5;

            //victory points that depends on building card on the player personal board
            if(BanFlag.get(DvptCardType.territory) == false){
            totalScore += BoardConfigParser.getVictoryBonus(DvptCardType.territory,player.getPersonalBoard().getTerritoryCards().size());}

                //victory points that depends on character card on the player personal board

            if(BanFlag.get(DvptCardType.character) == false){
                totalScore += BoardConfigParser.getVictoryBonus(DvptCardType.character,player.getPersonalBoard().getCharacterCards().size());}

            totalScore += BoardConfigParser.getVictoryBonus(DvptCardType.venture,player.getPersonalBoard().getVentureCards().size());

            totalScore += BoardConfigParser.getVictoryBonus(DvptCardType.building,player.getPersonalBoard().getBuildingCards().size());

            //victory points that depends on faith points
            totalScore += BoardConfigParser.getVictoryBonusFromFaith(player.getFaithPoints());

            //victory points that depends on military points
            totalScore += getMilitaryPointsBonus(player);

            //If the player has some VictoryMalusBanCard, he's gonna lose some points according to the amount of his points or resources

            totalScore = totalScoreWithVictoryMalus(player, totalScore);

            finalScore.put(player,totalScore);

        }

       finalScore.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return  finalScore;
    }

    /**This method controls if there are some ban cards that could disable some bonus from amount of specific type card**/

    HashMap<DvptCardType,Boolean> applyNoVictoryBan(Player player){
        HashMap<DvptCardType, Boolean> banType = new HashMap<DvptCardType, Boolean>();

        banType.put(DvptCardType.territory,false);
        banType.put(DvptCardType.venture,false);
        banType.put(DvptCardType.character,false);
        banType.put(DvptCardType.building,false);

        for(BanCard banCard : player.getBanCards()) {
            if(banCard instanceof NoVictoryBanCard) {
                if (((NoVictoryBanCard) banCard).getCardType() == DvptCardType.territory)
                    banType.put(DvptCardType.territory, true);
                if (((NoVictoryBanCard) banCard).getCardType() == DvptCardType.venture)
                    banType.put(DvptCardType.venture, true);
                if (((NoVictoryBanCard) banCard).getCardType() == DvptCardType.character)
                    banType.put(DvptCardType.character, true);
            }
        }
        return banType;
    }


    /**This method controls if there are some ban cards that could reduce victory points considering a specific amount of resources or points**/

    Integer totalScoreWithVictoryMalus(Player player, Integer totalScore) {

        for (BanCard banCard : player.getBanCards()) {
            if (banCard instanceof VictoryMalusBanCard) {

                for (Resource resource : ((VictoryMalusBanCard) banCard).getEffectVictoryMalus().getCausedbyResources()) {

                    if (resource.getType() == ResourceType.Coins) {
                        totalScore -= (int) (player.getCoins() / resource.getAmount());
                    }

                    if (resource.getType() == ResourceType.Servants) {
                        totalScore -= (int) (player.getServants() / resource.getAmount());
                    }

                    if (resource.getType() == ResourceType.Stones) {
                        totalScore -= (int) (player.getStones() / resource.getAmount());
                    }

                    if (resource.getType() == ResourceType.Wood) {
                        totalScore -= (int) (player.getWood() / resource.getAmount());
                    }
                }

                for (Point point : ((VictoryMalusBanCard) banCard).getEffectVictoryMalus().getCausedByPoints()) {

                    if (point.getType() == PointType.Victory) {
                        totalScore -= (int) (player.getVictoryPoints() / point.getAmount());
                    } else if (point.getType() == PointType.Military) {
                        totalScore -= (int) (player.getMilitaryPoints() / point.getAmount());
                    } else totalScore -= (int) (player.getFaithPoints() / point.getAmount());

                }

                if (((VictoryMalusBanCard) banCard).getEffectVictoryMalus().isRelatedToBuilding()) {
                    for (BuildingDvptCard card : player.getPersonalBoard().getBuildingCards()) {
                        for (Cost cost : card.getCost()) {
                            for (Resource resource : cost.getResources()) {
                                if (resource.getType() == ResourceType.Wood)
                                    totalScore -= resource.getAmount() * ((VictoryMalusBanCard) banCard).getEffectVictoryMalus().getMalus();
                            }
                        }
                    }
                }
            }
        }
        return totalScore;
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
            else if(permanentEffectAction.getTarget() == server.model.effect.ActionType.production) {

                if (action.getActionTarget() == BoardSectorType.CompositeProductionPlace || action.getActionTarget() == BoardSectorType.SingleProductionPlace) {
                    action.increaseBonus(permanentEffectAction.getForceBonus());
                }
            }

            //if a permanent effect is relative to cardtype, check the DvptCardType type and modify the Action

            else if(permanentEffectAction.getTarget() == ActionType.card){

                if(permanentEffectAction.getType() == getTowerType(action.getActionTarget()))
                    action.increaseBonus(permanentEffectAction.getForceBonus());

                action.setDiscounts(permanentEffectAction.getDiscounts());
            }

            //if the effect is the preacher penality forbid the Action if the placement index is > 1
            else if(card.getPermanentEffect().isPenality()){
                if (action.getPlacementIndex()>1) {
                    throw new PreacherEffectException("Preacher's permanent effect forbid it");
                }
            }

        }

        return  action;

    }

    public void handleVaticanReport(Player player){

        //get minimum number of faith points for current period
        Integer minPeriodFaith = this.match.getBoard().getCathedral().getMinFaith(this.match.getCurrentPeriod());

        //if the player has not enough faith points he receive the excommunication
        if(player.getFaithPoints() < minPeriodFaith){
            player.addBanCard(this.match.getBoard().getCathedral().getBanCard(this.match.getCurrentPeriod()));}

        else
        {
            //TODO player interation

            if(true){
                //the player has enough faith points but doesn't want to use them to avoid excommunication
                player.addBanCard(this.match.getBoard().getCathedral().getBanCard(this.match.getCurrentPeriod()));
            }
            else {
                //the player use his faith points to avoid excommunication and receive a number of victory points depending on his faith points
                player.addVictoryPoints(BoardConfigParser.getVictoryBonusFromFaith(player.getFaithPoints()));
                if (player.isPermanentLeaderActive(PermanentLeaderEffectType.sistoEffect)) {
                    player.addVictoryPoints(5);
                }
            }
            //the player cannot choose how many faith points to use
            player.setFaithPoints(0);
        }
    }

    public DvptCardType getTowerType(BoardSectorType boardSectorType){

        if(boardSectorType == BoardSectorType.BuildingTower)
            return DvptCardType.building;

        else if(boardSectorType == BoardSectorType.CharacterTower)
            return DvptCardType.character;

        else if(boardSectorType == BoardSectorType.TerritoryTower)
            return DvptCardType.territory;

        else
            return DvptCardType.venture;
    }

    public DvptCardType getTowerType(ImmediateBoardSectorType boardSectorType){

        if(boardSectorType == ImmediateBoardSectorType.BuildingTower)
            return DvptCardType.building;

        else if(boardSectorType == ImmediateBoardSectorType.CharacterTower)
            return DvptCardType.character;

        else if(boardSectorType == ImmediateBoardSectorType.TerritoryTower)
            return DvptCardType.territory;


        else
            return DvptCardType.venture;
    }

    public BoardController getBoardController() {
        return boardController;
    }

    /**
     * give the correct init resources to each player
     * @param players
     */
    private void initPlayerResource(ArrayList<Player> players) {

        int position=1;

        for (Player player: players) {
            player.addResources(BoardConfigParser.getInitialResource(position));
            position++;
        }
    }

    @Override
    public String toString() {

        return this.lobby + " match controller";

    }
}