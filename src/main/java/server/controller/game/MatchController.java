package server.controller.game;
import client.controller.network.*;
import exception.*;
import logger.Level;
import logger.Logger;
import netobject.NetObjectType;
import netobject.action.*;
import netobject.action.immediate.ImmediateActionType;
import netobject.action.immediate.ImmediateChoiceAction;
import netobject.action.immediate.ImmediatePlacementAction;
import netobject.action.standard.*;
import netobject.notification.LobbyNotification;
import netobject.notification.LobbyNotificationType;
import netobject.notification.MatchNotification;
import netobject.notification.MatchNotificationType;
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
import server.utility.BonusTilesParser;
import singleton.Database;
import singleton.GameConfig;
import server.controller.network.Observable;

import java.sql.SQLException;
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
public class MatchController implements Runnable, Observable<MatchControllerObserver> {

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
     * The thread on which the controller is running
     */
    private Thread daemon;

    /**
     * The context in which the match controller is
     */
    private MatchControllerContext context;

    /**
     * The observers
     */
    ArrayList<MatchControllerObserver> observers;

    /**
     * A blocking queue used to wait for the clients to be ready to receive events
     */
    private BlockingQueue<ObserverType> readyObservers;


    /**
     * Constants
     */
    private static final int ACTION_TIMEOUT =  GameConfig.getInstance().getPlayerTimeout();

    /**
     * This is the match controller constructor (when we start a new game).
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

        //Init observer
        this.observers = new ArrayList<>();

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

        this.readyObservers = new LinkedBlockingQueue<>(players.size());


        //Init anything else in the future here..

    }

    /**
     * This is the match controller constructor (when we start a preexisting game).
     * It is called only by the lobby itself when the match starts
     * @param handlers the handlers of the model players
     */
    public MatchController(ArrayList<ClientHandler> handlers, Lobby lobby, Match match) throws NoSuchPlayerException {

        //Set the lobby
        this.lobby = lobby;

        /*
         * Initialize the map
         */
        this.remotePlayerMap = new LinkedHashMap<>();

        //Init observer
        this.observers = new ArrayList<>();

        for (ClientHandler handler : handlers) {

            Player player = match.getPlayerFromUsername(handler.getUsername());

            this.remotePlayerMap.put(player, handler);

        }

        /*
         * First up, restore the model for the current match.
         * The players are always provided
         */
        this.match = match;

        /*
         * Assign the board controller
         * Keep in mind that match.board must be initialized at this time
         */
        this.boardController = new BoardController(this.match.getBoard());

        /*
         * Initialize the blocking queue for the actions
         * Make it maximum size equals to the number of players so that in the draft we can block until it is full
         */
        this.actions = new LinkedBlockingQueue<>(match.getPlayers().size());

        this.readyObservers = new LinkedBlockingQueue<>(match.getPlayers().size());

        //set the current player
        this.currentPlayer = this.match.getCurrentPlayer();

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


        //Inform the players that the match started
        this.lobby.notifyAll(new LobbyNotification(LobbyNotificationType.MatchStart, "The match is starting..."));


        //Wait fot CLI / GUI to fully load their observers..
        this.waitUntilPlayerObserversAreSet();

       /* //Draft the leader cards first
        this.context = MatchControllerContext.LeaderCardDraft;
        this.handleLeaderCardDraft();

        //Draft the bonus tiles
        //Draft the leader cards first
        this.context = MatchControllerContext.BonusTileDraft;
        this.handleBonusTileDrat();*/
        //We are now going to play
        //Draft the leader cards first
        this.context = MatchControllerContext.Playing;

        //Make sure that the match has already been initialized here!
        RoundIterator roundIterator = new RoundIterator(this.match);

        while (roundIterator.hasNext()) {

            if (this.match.getCurrentRound() == 4) {

                //we have to pull dices another time
                this.boardController.cleanDices();

                //Look at council palace order to calculate new order of precedence
                changePlayerOrder();

                this.notifyAllOfNewOrder();
            }


            //Obtain the next round
            Queue<Player> currentRound = roundIterator.next();

            if (this.match.getCurrentRound() == 1) {

                //Update the towers for the current combination of round / turn / period
                this.boardController.updateTowersForTurn(this.match.getCurrentPeriod().toInt(), this.match.getCurrentTurn());

                //clean the council palace place
                this.boardController.cleanCouncilPalace();

                //clean the market place
                this.boardController.cleanMarket();

                //clean the harvest area
                this.boardController.cleanHarvestArea();

                //clean the production area
                this.boardController.cleanProductionArea();

                for(Player player : this.getMatch().getPlayers()){
                    player.getTurnActiveLeaderCard().clear();
                }

                //Free family members for each player
                for (Player player:this.getMatch().getPlayers()) {

                    player.freeFamilyMembers();

                }

            }

            //save on database the updated model
            this.save();

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

        }

        Logger.log(Level.FINEST, this.toString(), "Match ended.");

        for (MatchControllerObserver o : this.observers) {

            o.onMatchEnded();

        }

    }

    /**
     * Suspends the thread until the players are ready to receive events
     */
    private void waitUntilPlayerObserversAreSet() {

        int notReady = this.match.getPlayers().size();


        Logger.log(Level.FINEST, this.toString(), "Waiting for " + this.match.getPlayers().size() + " players to load their GUI / CLI");


        for (Player p : this.match.getPlayers()) {

            try {

                this.readyObservers.take();

                notReady--;

                Logger.log(Level.FINEST, this.toString(), "A player is ready.. " + notReady + " more to go.");


            } catch (InterruptedException e) {

                Logger.log(Level.WARNING, this.toString(), "Thread interrupted while waiting for observers ready");

            }

        }

    }

    /**
     * Removes a player while playing
     * @param handler the handler of the player that have left
     */
    public void removePlayer(ClientHandler handler) throws NoSuchPlayerException {

        //Find the player
        Player belonging = this.match.getPlayerFromUsername(handler.getUsername());

        Logger.log(Level.FINEST, this.toString(), "Disabling player " + belonging.getUsername());

        //Disable the player
        belonging.setDisabled(true);

        //The first thing to do is check if the player has the current turn
        //If so we need to insert a poisonous action to stop his handling
        //Of course the current player should be set (aka not drafting)
        if (this.context != MatchControllerContext.Playing) {

            //We are still drafting either the leader cards or the tiles
            //Inset an empty action to avoid a deadlock in the draft system
            this.actions.add(new Action());

        }
        else if (belonging.getUsername().equals(this.currentPlayer.getUsername())) {

            //Inset an empty action to trigger the exception that will disable the player
            this.actions.add(new Action());

        }

        //Then we can remove the map reference
        this.remotePlayerMap.remove(belonging);

        this.notifyAll("Player " + belonging.getUsername() + " left the match");

        Logger.log(Level.FINEST, this.toString(), "Player " + belonging.getUsername() + " left the match");


    }

    /**
     * Re adds a player that had left before
     * @param handler the handler of that player
     * @throws NoSuchPlayerException if the player does not exists (Should never occur)
     */
    public void addPlayer(ClientHandler handler) throws NoSuchPlayerException {

        //Find the player
        Player belonging = this.match.getPlayerFromUsername(handler.getUsername());

        //Re enable the player
        belonging.setDisabled(false);

        //Re add the map entry
        this.remotePlayerMap.put(belonging, handler);

        //Send a model update to him
        handler.notifyModelUpdate(this.match);

        this.notifyAll("Player " + belonging.getUsername() + " reconnected");

        //If the players are playing
        if (this.currentPlayer != null) {

            handler.notifyTurnEnabled(this.currentPlayer,"It is " + this.currentPlayer.getUsername() + "'s turn!");

        }
        Logger.log(Level.FINEST, this.toString(), "Player " + belonging.getUsername() + " reconnected");


    }

    public void changePlayerOrder(){

        ArrayList <Player> oldPlayerOrder = this.match.getRoundOrder();

        ArrayList <Player> newPlayerOrder = new ArrayList<>();

        for (Player player : this.match.getBoard().getCouncilPalaceOrder()) {

            newPlayerOrder.add(player);

        }

        for (Player player : oldPlayerOrder) {

            if(!newPlayerOrder.contains(player))

                newPlayerOrder.add(player);
        }

        this.match.setRoundOrder(newPlayerOrder);

    }


    /**
     * Handle the leader card draft
     * Creates a number of draftable decks equal to the number of players
     * It then sends every deck to the player and wait for any player to chose
     * When all the players have selected one card, the draft takes places and the decks received from the nearby player are sent.
     */
    private void handleLeaderCardDraft() {

        //TODO: Fix a bug : when a user stop the process at the beginning of the draft without selecting anything something goes wrong with the tiles.

        final int NUMBER_OF_CARDS_PER_DECK = 4;

        //The amount of players that can draft when we begin the process, this number may decrease if a player disconnects while drafting
        ArrayList<Player> enabledToDraft = this.match.getActivePlayers();

        //Create a deck with all the 20 leader cards and shuffle it
        Deck<LeaderCard> deck = new Deck<>(GameSingleton.getInstance().getLeaderCards()).shuffle();

        //Create a temporary map for each player username
        TreeMap<String, Deck<LeaderCard>> draftingMap = new TreeMap<>();

        int i = 0;

        //Create n sub decks, where n is the number of players
        for (Player p : enabledToDraft) {

            Deck<LeaderCard> draftableDeck = new Deck<>();

            for (int j = 0; j < NUMBER_OF_CARDS_PER_DECK; j++) {

                draftableDeck.addCard(deck.getCards().get((i * NUMBER_OF_CARDS_PER_DECK) + j));

            }

            draftingMap.put(p.getUsername(), draftableDeck);

            //Tell the user the deck from which he can select a card
            this.remotePlayerMap.get(p).notifyLeaderCardDraftRequest(draftableDeck, "Please select a leader card and draft");

            Logger.log(Level.FINEST, this.toString(), "Sending draftable deck to " + p.getUsername());

            i++;

        }


        for (i = 0; i < NUMBER_OF_CARDS_PER_DECK; i++) {

            //The amount of players that drafted
            int j = 0;

            //Four times we need to wait until each active player performs his draft
            while (j < enabledToDraft.size()) {

                //Get the shuffle action
                ShuffleLeaderCardStandardAction shuffleAction = null;
                try {

                    shuffleAction = (ShuffleLeaderCardStandardAction)this.waitForAction(ACTION_TIMEOUT * 1000);

                    //Increment j to go on and get the action from a different player
                    j++;

                } catch (InterruptedException e) {

                    Logger.log(Level.WARNING, this.toString(), "Thread stopped while waiting on action queue", e);

                } catch (NoActionPerformedException e) {

                    //Detect who did not perform the move  (disconnected or timeout expired) and update enabledToDraft array
                    this.filterOutPlayers(enabledToDraft, this.match.getDisabledPlayers());

                    continue;

                }

                //Get the selected card, DEBUG ONLY
                LeaderCard selected = shuffleAction.getDeck().getCards().get(shuffleAction.getSelection());

                //Find the player in the map and remove
                draftingMap.get(shuffleAction.getSender()).removeCard(shuffleAction.getSelection());

                //Add the selected card to the leader cards of the player
                try {

                    this.match.getPlayerFromUsername(shuffleAction.getSender()).addLeaderCard(GameSingleton.getInstance().getSpecificLeaderCard(selected.getId()));

                    this.notifyAllActionPerformed(this.match.getPlayerFromUsername(shuffleAction.getSender()), shuffleAction, shuffleAction.getSender() + " drafted his leader cards, he selected '" + selected.getName() + "'");


                } catch (NoSuchPlayerException e) {

                    Logger.log(Level.FINEST, this.toString(), "Can't find player!", e);

                }

                Logger.log(Level.FINEST, this.toString(), "Leader draft, step " + (i + 1) + ", " + shuffleAction.getSender() + " selected '" + selected.getName() + "'");

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
            for (Player p : enabledToDraft) {

                if (!p.isDisabled()) {

                    Deck<LeaderCard> draftable = draftingMap.get(p.getUsername());
                    this.remotePlayerMap.get(p).notifyLeaderCardDraftRequest(draftable, "Please select a leader card and draft");
                }

            }

        }

    }


    /**
     * Handles the draft of the bonus tiles
     */
    private void handleBonusTileDrat() {

        final int DEFAULT_BONUS_TILE = 0;

        //Get the bonus tile array directly from the parser
        ArrayList<BonusTile> bonusTileSet = BonusTilesParser.parse();

        this.notifyAll("The bonus tiles are being drafted, please wait for your turn.");

        //Send a request to each player beginning from the last one
        for (int i = this.match.getActivePlayers().size() - 1; i >= 0; i--) {

            Player curr = this.match.getPlayers().get(i);

            //If the player is disabled he can't make any move
            if (curr.isDisabled()) {

                //If we don't get any selection from the player, then select the first tile available in the set
                curr.getPersonalBoard().setBonusTile(bonusTileSet.get(DEFAULT_BONUS_TILE));

                continue;

            }

            //Send a request to each player and wait for a response
            this.remotePlayerMap.get(curr).notifyBonusTileDraftRequest(bonusTileSet, "Please select a bonus tile. BEBUG: The size of the tiles is " + bonusTileSet.size());

            ShuffleBinusTileStandardAction shuffleAction;

            try {

                //Wait for the player to take his action
                shuffleAction = (ShuffleBinusTileStandardAction)this.waitForAction(ACTION_TIMEOUT * 1000);

                BonusTile selected = bonusTileSet.get(shuffleAction.getSelection());

                //Assign the selected tile
                this.match.getPlayers().get(i).getPersonalBoard().setBonusTile(selected);

                //Once we get the selection, remove the proper tile from the set
                bonusTileSet.remove(shuffleAction.getSelection());

                this.notifyAllActionPerformed(this.match.getPlayerFromUsername(shuffleAction.getSender()), shuffleAction, shuffleAction.getSender() + " selected his tile: #" + selected.getId());


            }
            catch (NoActionPerformedException e) {

                //If we don't get any selection from the player, then select the first tile available in the set
                curr.getPersonalBoard().setBonusTile(bonusTileSet.get(DEFAULT_BONUS_TILE));

                //Then remove it
                bonusTileSet.remove(0);

            }
            catch (InterruptedException e) {

                Logger.log(Level.WARNING, this.toString(), "Thread stopped while waiting on action queue for tiles", e);

            }
            catch (NoSuchPlayerException e) {

                Logger.log(Level.FINEST, this.toString(), "Can't find player!", e);

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

        //Useful to save it also in the model
        this.match.updateCurrentPlayer(currentPlayer);

        //Save it in order to avoid two consecutive actions for a player that has just terminate his round
        this.save();

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

                    //If its time to choose whether or not to get banned ask the player before his round finishes
                    if (this.match.getCurrentRound() == 4 && this.match.getCurrentTurn() == 2) {

                        this.handleVaticanReport(this.currentPlayer);

                    }

                    //If we get here without exceptions we can notify of the succeeded action
                    this.notifyAllActionPerformed(this.currentPlayer, action, this.currentPlayer.getUsername() + " wants to terminate his turn");

                    //Tell the players that the active one can't make any more actions
                    this.notifyAllTurnDisabled(this.currentPlayer);

                    save();

                    break;

                }
                else {

                    Logger.log(Level.FINEST, this.toString(), "Parsing action request for player " + this.currentPlayer.getUsername());


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
                        this.remotePlayerMap.get(this.currentPlayer).notifyActionRefused(action, GameMessage.InvalidAction.getLiteral() + " Reason: " + reason.getMessage());

                    }

                }


            } catch (NoActionPerformedException e) {

                Logger.log(Level.FINEST, this.toString(), "Action timeout expired for player " + this.currentPlayer.getUsername());

                this.handleActionTimeoutExpiration(e);

                //Break the loop
                break;


            } catch (InterruptedException e) {

                Logger.log(Level.WARNING, this.toString(), "Thread stopped while waiting on action queue", e);

            }

        }
        while (!this.currentPlayer.isDisabled());

    }

    private void handleActionTimeoutExpiration(NoActionPerformedException e) {

        //Tell the players that the timeout has expired expired for the active player
        this.notifyAllActionTimeoutExpired(this.currentPlayer);

        //Tell the players that the active one can't make any more actions
        this.notifyAllTurnDisabled(this.currentPlayer);

        //Disable the player
        this.currentPlayer.setDisabled(true);

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
    private String handlePlayerAction(Player player, Action action) throws ActionException, NoActionPerformedException, InterruptedException {

        String message = "";

        if(action instanceof StandardPlacementAction){

            System.out.println("Placement target : " + ((StandardPlacementAction) action).getActionTarget());

            placeFamilyMember((StandardPlacementAction) action,player);

            System.out.println("Placement successful");


            message = "placed a family member on " + ((StandardPlacementAction)action).getActionTarget();

        }

        if(action instanceof LeaderCardActivationAction){

            activateLeaderCard((LeaderCardActivationAction) action, player);

            message = "activated the leader card" + GameSingleton.getInstance().getSpecificLeaderCard(((LeaderCardActivationAction) action).getLeaderCardIndex());

        }

        if(action instanceof DiscardLeaderCardAction){

            discardLeaderCard((DiscardLeaderCardAction) action, player);

            message = "discarded the leader card" + GameSingleton.getInstance().getSpecificLeaderCard(((DiscardLeaderCardAction) action).getLeaderCardIndex());

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

            if (!p.isDisabled()) {
                this.remotePlayerMap.get(p).notifyModelUpdate(this.match);
            }
        }

    }

    private void notifyAll(String message) {

        for (Player p : this.match.getPlayers()) {
            if (!p.isDisabled()) {
                this.remotePlayerMap.get(p).notify(new MatchNotification(MatchNotificationType.Message, message));
            }

        }

    }

    private void notifyAllTurnEnabled(Player current) {

        for (Player p : this.match.getPlayers()) {
            if (!p.isDisabled()) {
                this.remotePlayerMap.get(p).notifyModelUpdate(this.match);
                this.remotePlayerMap.get(p).notifyTurnEnabled(current, "It is " + current.getUsername() + "'s turn");
            }

        }

    }

    private void notifyAllTurnDisabled(Player current) {

        for (Player p : this.match.getPlayers()) {

            if (!p.isDisabled()) {
                this.remotePlayerMap.get(p).notifyTurnDisabled(current, current.getUsername() + " terminated his turn.");
            }

        }

    }

    private void notifyAllActionTimeoutExpired(Player current) {

        for (Player p : this.match.getPlayers()) {

            if (!p.isDisabled()) {
                this.remotePlayerMap.get(p).notifyActionTimeoutExpired(current, current.getUsername() + "'s timeout to take his move expired. He was disabled.");
            }

        }

    }

    private void notifyAllActionPerformed(Player current, Action action, String message) {

        for (Player p : this.match.getPlayers()) {
            if (!p.isDisabled()) {
                this.remotePlayerMap.get(p).notifyActionPerformed(current, action, message);
            }

        }

    }

    private void notifyAllImmediateActionAvailable(ImmediateActionType immediateActionType, Player current, String message) {

        for (Player p : this.match.getPlayers()) {
            if (!p.isDisabled()) {
                this.remotePlayerMap.get(p).notifyImmediateActionAvailable(immediateActionType, current, message);
            }

        }

    }

    private void notifyAllOfNewOrder(){

        String newOrder = "";

        int i=1;

        for (Player player : this.match.getRoundOrder()) {

            newOrder += i+"° "+player.getUsername()+"\n";

            i++;

        }

        this.notifyAll("The player order has changed" + "\n" +newOrder );

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
    public void applyDvptCardCost(Player player, DvptCard card,ArrayList<Discount> discount) throws ActionException, NoActionPerformedException, InterruptedException {

        //territory cards doesn't have cost
        if(card != null){
            if(card.getType() == DvptCardType.territory)
                return;

            //some cards could have a double cost
            int choose=0;

            if(card.getCost().size()>1) {

                this.notifyAllImmediateActionAvailable(ImmediateActionType.SelectCost, this.currentPlayer, "Which cost do you want to apply?");

                ImmediateChoiceAction choice = (ImmediateChoiceAction) this.waitForAction(ACTION_TIMEOUT * 1000);

                if (choice.getSelection() == 1) {

                    choose = 1;

                }

                this.notifyAllActionPerformed(this.currentPlayer, choice, this.currentPlayer.getUsername() + " has selected discount");

            }


            //get the choosen one cost
            //clone the cost in order to modify a temporary variable
            Cost costo = new Cost(card.getCost().get(choose));

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
    public Cost applyDiscount(Cost costo, ArrayList<Discount> discount) throws InterruptedException, NoActionPerformedException {

        int choose = 0;

        if(discount.size() == 0)
            return costo;

        if(discount.size()>1) {

            this.notifyAllImmediateActionAvailable(ImmediateActionType.DecideDiscountOption, this.currentPlayer, "Which discount do you want ?");

            ImmediateChoiceAction choice = (ImmediateChoiceAction)this.waitForAction(ACTION_TIMEOUT * 1000);

            if(choice.getSelection() == 1){

                choose = 1;

            }

            this.notifyAllActionPerformed(this.currentPlayer, choice, this.currentPlayer.getUsername() + " has selected discount");

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
    public void applyImmediateEffect(Player player, DvptCard card) throws ActionException, NoActionPerformedException, InterruptedException {

        StandardPlacementAction action;

        if(card != null) {
            ImmediateEffect immediateEffect = card.getImmediateEffect();
            if(player.isPermanentLeaderActive(PermanentLeaderEffectType.ritaEffect)){
                for(Resource resource: immediateEffect.getSurplus().getResources())
                    resource.setAmount(resource.getAmount()*2);
            }

            ImmediatePlacementAction placementAction;

            //apply effect surplus of the immediate effect
            applyEffectSurplus(player, immediateEffect.getSurplus());

            //multiplier immediate effect is always in the first slot of points array
            if (immediateEffect.getSurplus().getPoints().size() > 0) {
                if (immediateEffect.getSurplus().getPoints().get(0).getMultiplier() != null)
                    applyMultiplier(player, immediateEffect.getSurplus().getPoints().get(0).getMultiplier());
            }

            if(immediateEffect.getEffectAction().getTarget() != ActionType.unknown){

                if (immediateEffect.getEffectAction().getTarget() == ActionType.harvest){

                    this.notifyAllImmediateActionAvailable(ImmediateActionType.ActivateHarvest, this.currentPlayer, "You can do an harvest action with force: " + immediateEffect.getEffectAction().getForce());



                }

                else if (immediateEffect.getEffectAction().getTarget() == ActionType.production){

                    this.notifyAllImmediateActionAvailable(ImmediateActionType.ActivateProduction, this.currentPlayer, "You can do a production action with force: " + immediateEffect.getEffectAction().getForce());

                }

                else if (immediateEffect.getEffectAction().getTarget() == ActionType.card) {

                    if (immediateEffect.getEffectAction().getType() == null) {

                        this.notifyAllImmediateActionAvailable(ImmediateActionType.TakeAnyCard, this.currentPlayer, "You can take a card of any type with force: " + immediateEffect.getEffectAction().getForce());

                    }

                    else if (immediateEffect.getEffectAction().getType() == DvptCardType.territory){

                        this.notifyAllImmediateActionAvailable(ImmediateActionType.TakeTerritoryCard, this.currentPlayer, "You can take a territory card with force: " + immediateEffect.getEffectAction().getForce());

                    }

                    else if (immediateEffect.getEffectAction().getType() == DvptCardType.character) {

                        this.notifyAllImmediateActionAvailable(ImmediateActionType.TakeCharacterCard, this.currentPlayer, "You can take a character card with force: " + immediateEffect.getEffectAction().getForce());

                    }

                    else if (immediateEffect.getEffectAction().getType() == DvptCardType.building){

                        this.notifyAllImmediateActionAvailable(ImmediateActionType.TakeBuildingCard, this.currentPlayer, "You can take a building card with force: " + immediateEffect.getEffectAction().getForce());

                    }

                    else if (immediateEffect.getEffectAction().getType() == DvptCardType.venture){

                        this.notifyAllImmediateActionAvailable(ImmediateActionType.TakeVentureCard, this.currentPlayer, "You can take a venture card with force: " + immediateEffect.getEffectAction().getForce());

                    }
                }


                placementAction = (ImmediatePlacementAction) this.waitForAction(ACTION_TIMEOUT * 1000);

                doImmediateAction(placementAction,immediateEffect.getEffectAction().getForce(), player);

                this.notifyAllActionPerformed(player, placementAction, player.getUsername() + " performed an immediate action");

            }




        }
    }

    /**
     * this method receives a family placement action and its author and places the familiar in the correct place (if it is strong enough)
     * @param action
     * @param player
     * @throws NotStrongEnoughException
     */
    public void placeFamilyMember(StandardPlacementAction action, Player player) throws ActionException, NoActionPerformedException, InterruptedException {

        FamilyMember familyMember = player.getFamilyMember(action.getColorType());


        //apply character permanent effect
        ActionBonus bonus = actionCharacterFilter(player,action);

        //apply leader card effect
        bonus = applyLeaderCardEffect(player, action, bonus);

        boolean noMarket = false;

        //some players' ban card can reduce family member's force
        noMarket = applyDiceMalusBanCard(action, player, familyMember, noMarket);



        //if boardSectorType is CouncilPalace we place the family member in the council palace
        //once positioned the council palace give to the player an effectSurplus
        if (action.getActionTarget() == BoardSectorType.CouncilPalace) {

            EffectSurplus surplus = boardController.placeOnCouncilPalace(familyMember, action.getAdditionalServants()+bonus.getForceBonus(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);

        }

        //if boardSectorType is Market we place the family member in the correct market place (from index 0 to index 3)
        //once positioned the market place give to the player an effectSurplus
        else if (action.getActionTarget() == BoardSectorType.Market) {

            if(noMarket)
                throw new NoMarketException("You can't place any family member in market because of the Special BanCard NoMarketMalus");
            else{
                EffectSurplus surplus = boardController.placeOnMarket(familyMember,action.getPlacementIndex(),action.getAdditionalServants() + bonus.getForceBonus(),this.match.getPlayers().size());
                applyEffectSurplus(player,surplus);}

        }

        //if boardSectorType is SingleHarvestPlace we place the family member in the single harvest place of the harvest area
        //once positioned the single harvest place give to the player an effectSurplus
        //the harvestChain (activation of all the permanent effect of territory cards that has harvest type) is also started
        else if(action.getActionTarget() == BoardSectorType.SingleHarvestPlace) {

            EffectSurplus surplus = boardController.placeOnSingleHarvestPlace(familyMember,action.getAdditionalServants() + bonus.getForceBonus(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);
            applyHarvestChain(player,familyMember.getForce() + action.getAdditionalServants() + bonus.getForceBonus());

        }

        //if boardSectorType is CompositeHarvestPlace we place the family member in the composite harvest place of the harvest area
        //once positioned the composite harvest place give to the player an effectSurplus
        //the harvestChain (activation of all the permanent effect of territory cards that has harvest type) is also started with a malus on the activation force
        else if(action.getActionTarget() == BoardSectorType.CompositeHarvestPlace) {

            EffectSurplus surplus = boardController.placeOnCompositeHarvestPlace(familyMember,action.getAdditionalServants() + bonus.getForceBonus(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);

            //we have to subtract force malus from activation force
            applyHarvestChain(player,familyMember.getForce() + bonus.getForceBonus() + action.getAdditionalServants() - this.match.getBoard().getHarvestArea().getSecondaryPlace().getForceMalus());

        }

        //if boardSectorType is SingleProductionPlace we place the family member in the single production place of the production area
        //once positioned the single production place give to the player an effectSurplus
        //the productionChain (activation of all the permanent effect of building cards that has production type) is also started
        if(action.getActionTarget() == BoardSectorType.SingleProductionPlace) {

            EffectSurplus surplus = boardController.placeOnSingleProductionPlace(familyMember,action.getAdditionalServants() + bonus.getForceBonus(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);
            applyProductionChain(player,familyMember.getForce() + bonus.getForceBonus() + action.getAdditionalServants());

        }

        //if boardSectorType is CompositeProductionPlace we place the family member in the composite production place of the production area
        //once positioned the composite production place give to the player an effectSurplus
        //the productionChain (activation of all the permanent effect of building cards that has production type) is also started with a malus on the activation force
        else if(action.getActionTarget() == BoardSectorType.CompositeProductionPlace) {

            EffectSurplus surplus = boardController.placeOnCompositeProductionPlace(familyMember,action.getAdditionalServants() + bonus.getForceBonus(),this.match.getPlayers().size());
            applyEffectSurplus(player,surplus);

            //we have to subtract a force malus from activation force
            applyProductionChain(player,familyMember.getForce()+ bonus.getForceBonus() + action.getAdditionalServants() - this.match.getBoard().getProductionArea().getSecondaryPlace().getForceMalus());

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
            //but only if the number of player is less than 5, anyway he can place it without constraints
            //This regulation change has been chosen in order to improve playability

            if (this.match.getBoard().getPlayersInTower(towerType).contains(player) && this.match.getPlayers().size() != 5)
                throw new PlayerAlreadyOccupiedTowerException("the player already has a family member in this tower");

            //check if the tower slot is already in use
            if(this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).isOccupied()){
                throw new PlaceOccupiedException("This place is already occupied");}

            //check if the player has enough force to set on the tower slot
            if(!(familyMember.getForce() + action.getAdditionalServants() + bonus.getForceBonus() >= this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).getEntryForce())) {
                throw new NotStrongEnoughException("Not strong enough to do this action");
            }

            //if the tower is already occupied the player has to pay 3 coins, but only if he has not Filippo da Montefeltro Leader Card active
            if (this.match.getBoard().getPlayersInTower(towerType).size() > 0 && !player.isPermanentLeaderActive(PermanentLeaderEffectType.filippoEffect))
                player.subtractCoins(3);

            //try to apply card cost to the player that made the action .. if this method return an exception no family members will be set here
            applyDvptCardCost(player, this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).getDvptCard(), bonus.getDiscounts());

            EffectSurplus effectSurplus = boardController.placeOnTower(familyMember, action.getAdditionalServants() + bonus.getForceBonus(), this.match.getPlayers().size(), towerType, action.getPlacementIndex());

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
    public ActionBonus applyLeaderCardEffect(Player player, StandardPlacementAction action, ActionBonus bonus){
        for(LeaderCard leaderCard : player.getPlayedLeaderCards()){

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
                    getMatch().getBoard().getCouncilPalace().getFamilyMembers().get(action.getPlacementIndex()).setBusy(false);

                if (action.getActionTarget() == BoardSectorType.Market)
                    getMatch().getBoard().getMarket().getMarketPlaces().get(action.getPlacementIndex()).setOccupied(false);

                if (action.getActionTarget() == BoardSectorType.SingleHarvestPlace)
                    getMatch().getBoard().getHarvestArea().getMainPlace().setOccupied(false);

                if (action.getActionTarget() == BoardSectorType.CompositeHarvestPlace)
                    getMatch().getBoard().getHarvestArea().getSecondaryPlace().getFamilyMembers().get(action.getPlacementIndex()).setBusy(false);

                if (action.getActionTarget() == BoardSectorType.SingleProductionPlace)
                    getMatch().getBoard().getProductionArea().getMainPlace().setOccupied(false);

                if (action.getActionTarget() == BoardSectorType.CompositeProductionPlace)
                    getMatch().getBoard().getProductionArea().getSecondaryPlace().getFamilyMembers().get(action.getPlacementIndex()).setBusy(false);

            }

            if(player.isPermanentLeaderActive(PermanentLeaderEffectType.sigismondoEffect) && action.getColorType() == ColorType.Neutral) {
                bonus.setForceBonus(bonus.getForceBonus()+3);
            }

            if(player.isPermanentLeaderActive(PermanentLeaderEffectType.lucreziaEffect) && action.getColorType() != ColorType.Neutral) {
                bonus.setForceBonus(bonus.getForceBonus()+2);
            }

            if(player.isPermanentLeaderActive(PermanentLeaderEffectType.moroEffect) && action.getColorType() != ColorType.Neutral) {
                try {
                    bonus.setForceBonus(bonus.getForceBonus()+5-player.getFamilyMember(action.getColorType()).getForce());
                } catch (FamilyMemberAlreadyInUseException e) {
                    e.printStackTrace();
                }
            }

            if(player.isPermanentLeaderActive(PermanentLeaderEffectType.picoEffect)){
                ArrayList <Resource> coinDiscount = new ArrayList<Resource>();
                coinDiscount.add(new Resource(ResourceType.Coins, 3));
                Discount discount = new Discount(coinDiscount);
                bonus.getDiscounts().add(discount);
            }
        }

        return bonus;

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
    public void doImmediateAction(ImmediatePlacementAction action, Integer force, Player player) throws ActionException, NoActionPerformedException, InterruptedException {

        //TODO character permanent effect -------> ImmediatePlacementAction && Standard Placement Action has to extend PlacementAction in order to use only one single character filter

        //if boardSectorType is a tower sector we place the family member in the correct (placementIndex) towerSlot of the tower
        //once positioned the towerSlot give to the player an effectSurplus
        if (action.getActionTarget() == ImmediateBoardSectorType.VentureTower || action.getActionTarget() == ImmediateBoardSectorType.CharacterTower || action.getActionTarget() == ImmediateBoardSectorType.BuildingTower || action.getActionTarget() == ImmediateBoardSectorType.TerritoryTower) {

            //get tower type from board sector
            DvptCardType towerType = getTowerType(action.getActionTarget());

            //control if the towerSlot is already occupied
            if (this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).isOccupied())
                throw new PlaceOccupiedException("This place is already occupied");

            //if the tower is already occupied the player have to pay 3 coins
            if (this.match.getBoard().getPlayersInTower(towerType).size() > 0 && !player.isPermanentLeaderActive(PermanentLeaderEffectType.filippoEffect))
                player.subtractCoins(3);

            //try to apply card cost to the player that made the action .. if this method return an exception no family members will be set here
            applyDvptCardCost(player, this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).getDvptCard(), action.getDiscounts());

            EffectSurplus effectSurplus = boardController.immediatePlacementOnTower(force + action.getAdditionalServants(), this.match.getPlayers().size(), towerType, action.getPlacementIndex());
            applyEffectSurplus(player, effectSurplus);

            //add to the personal board of the player the building card set in the tower slot
            player.getPersonalBoard().addCard(this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).getDvptCard());

            applyImmediateEffect(player, this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).getDvptCard());

            //set the dvptCard of the tower to null value because no one can choose or take it now
            this.match.getBoard().getTower(towerType).get(action.getPlacementIndex()).setDvptCard(null);


        }

        //if boardSectorType is harvest the harvestChain (activation of all the permanent effect of building cards that has production type) started
        else if(action.getActionTarget() == ImmediateBoardSectorType.Production) {


            applyProductionChain(player,action.getAdditionalServants() + force);

        }

        //if boardSectorType is harvest the productionChain (activation of all the permanent effect of territory cards that has harvest type) started
        else if(action.getActionTarget() == ImmediateBoardSectorType.Harvest) {


            applyHarvestChain(player,action.getAdditionalServants() + force);

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

                            this.remotePlayerMap.get(player).notifyActionRefused(choice, "Each privilege must be different");

                        }
                        else {
                            if(!selections.isEmpty() && selections.contains(choice.getSelection()))
                                selections.remove(choice.getSelection());
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

    public void activateLeaderCard (LeaderCardActivationAction action, Player player) throws NotEnoughLeaderRequirementsException, LeaderCardAlreadyActiveTurnException, InterruptedException, NoActionPerformedException, LeaderCardAlreadyActiveException {

        LeaderCard leaderCard = GameSingleton.getInstance().getSpecificLeaderCard(action.getLeaderCardIndex());

        //  if(!player.hasEnoughLeaderRequirements(action.getLeaderCardIndex()) && !player.getPlayedLeaderCards().contains(leaderCard))
        //     throw new NotEnoughLeaderRequirementsException("Not enough requirements to activate this leader card");

        if (player.getPlayedLeaderCards().contains(leaderCard))
            throw new LeaderCardAlreadyActiveException("You have already played this card!");

        else {

            player.getPlayedLeaderCards().add(leaderCard);

            if (leaderCard.getLeaderEffect().getOnceARound() != null) {

                if(player.getTurnActiveLeaderCard().contains(leaderCard))
                    throw new LeaderCardAlreadyActiveTurnException("You have already activated the effect of this Leader card in this turn");

                if(!player.getTurnActiveLeaderCard().contains(leaderCard)) {

                    EffectSurplus surplus = new EffectSurplus(leaderCard.getLeaderEffect().getOnceARound().getResources(),leaderCard.getLeaderEffect().getOnceARound().getPoints(),leaderCard.getLeaderEffect().getOnceARound().getCouncil());
                    applyEffectSurplus(player, surplus);

                    ImmediatePlacementAction placementAction;

                    if (leaderCard.getLeaderEffect().getOnceARound().getAction().containsKey(ActionType.harvest)) {

                        this.notifyAllImmediateActionAvailable(ImmediateActionType.ActivateHarvest, this.currentPlayer, "You can do an harvest action");

                        placementAction = (ImmediatePlacementAction) this.waitForAction(ACTION_TIMEOUT * 1000);

                        try {
                            doImmediateAction(placementAction, leaderCard.getLeaderEffect().getOnceARound().getAction().get(ActionType.harvest), player);
                        } catch (ActionException e) {
                            e.printStackTrace();
                        }

                        this.notifyAllActionPerformed(player, placementAction, player.getUsername() + " performed an immediate action");
                    }

                    if (leaderCard.getLeaderEffect().getOnceARound().getAction().containsKey(ActionType.production)) {

                        this.notifyAllImmediateActionAvailable(ImmediateActionType.ActivateProduction, this.currentPlayer, "You can do a production action");

                        placementAction = (ImmediatePlacementAction) this.waitForAction(ACTION_TIMEOUT * 1000);

                        try {
                            doImmediateAction(placementAction, leaderCard.getLeaderEffect().getOnceARound().getAction().get(ActionType.production), player);
                        } catch (ActionException e) {
                            e.printStackTrace();
                        }

                        this.notifyAllActionPerformed(player, placementAction, player.getUsername() + " performed an immediate action");
                    }

                    if (leaderCard.getLeaderEffect().getOnceARound().getSixEffect() == true) {

                        this.notifyAllImmediateActionAvailable(ImmediateActionType.SelectFamilyMember, this.currentPlayer, "");

                        ImmediateChoiceAction choice = (ImmediateChoiceAction) this.waitForAction(ACTION_TIMEOUT * 1000);

                        if (choice.getSelection() == 1) {
                            player.setFamilyMemberForce(ColorType.White, 6);
                        } else if (choice.getSelection() == 2) {
                            player.setFamilyMemberForce(ColorType.Orange, 6);
                        } else if (choice.getSelection() == 3) {
                            player.setFamilyMemberForce(ColorType.Black, 6);
                        } else if (choice.getSelection() == 4) {

                            player.setFamilyMemberForce(ColorType.Neutral, 6);

                        } else {

                            Logger.log(Level.SEVERE, this.toString(), "Received a choice out of bounds");

                        }

                        this.notifyAllActionPerformed(this.currentPlayer, choice, player.getUsername() + " performed an immediate action");


                    }

                    player.getTurnActiveLeaderCard().add(leaderCard);

                }

            }

            if (leaderCard.getLeaderEffect().getPermanentEffect() == PermanentLeaderEffectType.lorenzoEffect) {

                this.notifyAllImmediateActionAvailable(ImmediateActionType.SelectActiveLeaderCard, this.currentPlayer, "You can select a leader card to copy");

                ImmediateChoiceAction choice = (ImmediateChoiceAction) this.waitForAction(ACTION_TIMEOUT * 1000);

                LeaderCard leader = GameSingleton.getInstance().getSpecificLeaderCard(choice.getSelection());

                player.getLeaderCards().add(leader);
                player.getLeaderCards().remove(leaderCard);

                this.notifyAllActionPerformed(this.currentPlayer, choice,player.getUsername() + " performed an immediate action");

            }
        }

    }

    public void discardLeaderCard (DiscardLeaderCardAction action, Player player) throws NoActionPerformedException {

        LeaderCard leaderCardToDiscard = GameSingleton.getInstance().getSpecificLeaderCard(action.getLeaderCardIndex());
        for(LeaderCard leaderCard : player.getLeaderCards()){
            if(action.getLeaderCardIndex() == leaderCard.getId()) {
                player.getLeaderCards().remove(leaderCardToDiscard);
                EffectSurplus effectSurplus = new EffectSurplus(new ArrayList<Resource>(), new ArrayList<Point>(), 1);
                applyEffectSurplus(player, effectSurplus);

                if(player.getPlayedLeaderCards().contains(leaderCardToDiscard))
                    player.getPlayedLeaderCards().remove(leaderCardToDiscard);
                if(player.getTurnActiveLeaderCard().contains(leaderCardToDiscard))
                    player.getTurnActiveLeaderCard().remove(leaderCardToDiscard);

                break;

            }

        }
    }

    /** this method applies the Production Chain
     * this character chain consists in the activation of all the building card permanent effect**/

    public void applyProductionChain (Player player, Integer force) throws ActionException, NoActionPerformedException, InterruptedException {


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

    public void applyBuildingPermanentEffect (DvptCard card, Player player, Integer choice) throws ActionException, NoActionPerformedException, InterruptedException {

        if(card.getPermanentEffect().getSurplus() != null)

            applyEffectSurplus(player, card.getPermanentEffect().getSurplus());

        if(card.getPermanentEffect().getConversion() != null )

            applyConversion(player, card.getPermanentEffect().getConversion());

        if(card.getPermanentEffect().getMultiplier() != null)

            applyMultiplier(player, card.getPermanentEffect().getMultiplier());

    }

    /** this method applies a conversion permanent effect of a development card to a particular player
     *
     * @param player
     * @param conversionList the list of conversion contained in the card effect
     * @throws NotEnoughResourcesException
     * @throws NotEnoughPointsException
     */


    public void applyConversion (Player player, ArrayList<EffectConversion> conversionList) throws ActionException, NoActionPerformedException, InterruptedException {

        int choose = 0;

        if(conversionList.size()>1) {

            this.notifyAllImmediateActionAvailable(ImmediateActionType.SelectConversion, this.currentPlayer, "Which conversion do you want to apply?");

            ImmediateChoiceAction choice = (ImmediateChoiceAction) this.waitForAction(ACTION_TIMEOUT * 1000);

            if (choice.getSelection() == 1) {

                choose = 1;

            }

            this.notifyAllActionPerformed(this.currentPlayer, choice, this.currentPlayer.getUsername() + " has selected conversion");

        }

        if (!conversionList.get(choose).getFrom().getResources().isEmpty()) {

            for (Resource from : conversionList.get(choose).getFrom().getResources())

                player.subtractGenericResource(from.getType(), from.getAmount());

        }

        if (!conversionList.get(choose).getFrom().getPoints().isEmpty()) {

            for (Point from : conversionList.get(choose).getFrom().getPoints())

                player.subtractGenericPoint(from.getType(), from.getAmount());

        }

        applyEffectSurplus(player, conversionList.get(choose).getTo());

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

        //avoid space problems if we have 5 players
        if(this.match.getPlayers().size() == 5) {

            raiseDiceValue();

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
     This method raises dice values following an algorithm to improve playability in case of five players, to allow them to place
     any family members even in the highest floors of the towers, since the lower are easily already occupied, avoiding space problems.
     Specifically, the value of all dices increases by one until it reaches a total of 14 */

    public void raiseDiceValue() {

        int sum = 0;

        for (Dice d : this.match.getBoard().getDices()) {

            sum += d.getValue();

        }

        //14 is a good number because we can put family members in a lot of tower spaces
        while(sum <=14) {

            sum = 0;

            for (Dice d : this.match.getBoard().getDices()) {

                //increase all dices value
                if(d.getValue()<6)
                    d.increaseValue(1);
                sum += d.getValue();

            }

        }
    }

    /**
     * this method create the final standings
     */
    public FinalStanding createFinalStanding() {

        HashMap<Player,Integer> scores = calculatesFinalScore();

        return  new FinalStanding(scores);

    }

    /**
     * this method calculate the final score of the players
     */
    public HashMap<Player,Integer> calculatesFinalScore(){

        HashMap<Player,Integer> finalScore = new LinkedHashMap<Player, Integer>();

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
    public ActionBonus actionCharacterFilter(Player player, StandardPlacementAction action) throws PreacherEffectException {

        ActionBonus bonus = new ActionBonus();

        //scroll through the character cards of a player looking for permanent effect Action
        for (CharacterDvptCard card: player.getPersonalBoard().getCharacterCards()) {

            EffectPermanentAction permanentEffectAction = card.getPermanentEffect().getAction();

            //if a permanent effect is relative to harvest type, check whether the Action target is CompositeHarvestPlace or SingleHarvestPlace and modify the Action
            if(permanentEffectAction.getTarget() == ActionType.harvest) {

                if (action.getActionTarget() == BoardSectorType.CompositeHarvestPlace || action.getActionTarget() == BoardSectorType.SingleHarvestPlace) {
                    bonus.increaseForceBonus(permanentEffectAction.getForceBonus());
                }
            }

            //if a permanent effect is relative to production type, check whether the Action target is CompositeProductionPlace or SingleProductionPlace and modify the Action
            else if(permanentEffectAction.getTarget() == ActionType.production) {

                if (action.getActionTarget() == BoardSectorType.CompositeProductionPlace || action.getActionTarget() == BoardSectorType.SingleProductionPlace) {
                    bonus.increaseForceBonus(permanentEffectAction.getForceBonus());
                }
            }

            //if a permanent effect is relative to cardtype, check the DvptCardType type and modify the Action

            else if(permanentEffectAction.getTarget() == ActionType.card){

                if(permanentEffectAction.getType() == getTowerType(action.getActionTarget()))
                    bonus.increaseForceBonus(permanentEffectAction.getForceBonus());

                bonus.setDiscounts(permanentEffectAction.getDiscounts());
            }

            //if the effect is the preacher penality forbid the Action if the placement index is > 1
            else if(card.getPermanentEffect().isPenality()){
                if (action.getPlacementIndex()>1) {
                    throw new PreacherEffectException("Preacher's permanent effect forbid it");
                }
            }

        }

        return  bonus;

    }

    public void handleVaticanReport(Player player) throws InterruptedException, NoActionPerformedException {


        //get minimum number of faith points for current period
        Integer minPeriodFaith = this.match.getBoard().getCathedral().getMinFaith(this.match.getCurrentPeriod());

        //if the player has not enough faith points he receive the excommunication
        if(player.getFaithPoints() < minPeriodFaith){

            player.addBanCard(this.match.getBoard().getCathedral().getBanCard(this.match.getCurrentPeriod()));

            this.notifyAll(player.getUsername() + " has been banned.");

        }
        else
        {

            this.notifyAllImmediateActionAvailable(ImmediateActionType.DecideBanOption, this.currentPlayer, "Would you like to get banned and keep the faith points or not ?");

            ImmediateChoiceAction choice = (ImmediateChoiceAction)this.waitForAction(ACTION_TIMEOUT * 1000);

            if(choice.getSelection() == 0){
                //the player has enough faith points but doesn't want to use them to avoid excommunication
                player.addBanCard(this.match.getBoard().getCathedral().getBanCard(this.match.getCurrentPeriod()));

                this.notifyAllActionPerformed(this.currentPlayer, choice, this.currentPlayer.getUsername() + " has been banned");

            }
            else {

                //the player use his faith points to avoid excommunication and receive a number of victory points depending on his faith points
                player.addVictoryPoints(BoardConfigParser.getVictoryBonusFromFaith(player.getFaithPoints()));
                if (player.isPermanentLeaderActive(PermanentLeaderEffectType.sistoEffect)) {
                    player.addVictoryPoints(5);
                }

                //the player cannot choose how many faith points to use
                player.setFaithPoints(0);

                this.notifyAllActionPerformed(this.currentPlayer, choice, this.currentPlayer.getUsername() + " has not been banned");

            }

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

    public void setDaemon(Thread daemon) {
        this.daemon = daemon;
    }

    public void destroy() {

        this.currentPlayerTimeout.cancel();

        if (this.daemon.isAlive())
            this.daemon.interrupt();

        Logger.log(Level.FINEST, this.toString(), "Daemon stopped");

    }

    /**
     * Username-based removal
     * n^2 complexity.. pretty slow but useful
     * @param src wjere to look to remove the players
     * @param removables the players that will be removed
     * @return the filtered array
     */
    private void filterOutPlayers(ArrayList<Player> src, ArrayList<Player> removables) {

        for (Player p1 : removables) {

            for (Player p2 : src) {

                if (p1.getUsername().equals(p2.getUsername())) {

                    src.remove(p2);

                    break;
                }

            }

        }

    }

    public MatchControllerContext getContext() {
        return context;
    }

    public BlockingQueue<ObserverType> getReadyObservers() {
        return readyObservers;
    }

    @Override
    public String toString() {

        return this.lobby + " match controller";

    }

    @Override
    public boolean addObserver(MatchControllerObserver o) {
        return this.observers.add(o);
    }

    @Override
    public boolean removeObserver(MatchControllerObserver o) {
        return this.observers.remove(o);
    }

    public void save(){

        try {
            Database.getInstance().save(this.match);

        } catch (SQLException e) {

            Logger.log(Level.SEVERE, "Database::save", "Possible errors in saving", e);

        }

    }
}