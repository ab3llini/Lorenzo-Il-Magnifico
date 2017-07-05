package client.view.cli;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */

import client.controller.network.*;
import client.controller.network.RMI.RMIClient;
import client.controller.network.Socket.SocketClient;
import client.view.LocalMatchController;
import client.view.cli.cmd.*;
import client.utility.AsyncInputStream;
import client.utility.AsyncInputStreamObserver;
import exception.NoActionPerformedException;
import exception.NoSuchPlayerException;
import logger.Level;
import logger.Logger;
import netobject.action.*;
import netobject.action.immediate.ImmediateActionType;
import netobject.action.immediate.ImmediateActionTypeImpl;
import netobject.action.immediate.ImmediateChoiceAction;
import netobject.action.immediate.ImmediatePlacementAction;
import netobject.action.standard.*;
import netobject.notification.*;
import netobject.request.auth.LoginRequest;
import netobject.request.auth.RegisterRequest;
import server.model.GameSingleton;
import server.model.Match;
import server.model.board.BonusTile;
import server.model.board.ColorType;
import server.model.board.Player;
import server.model.card.Deck;
import server.model.card.developement.DvptCardType;
import server.model.card.leader.LeaderCard;
import server.model.effect.EffectSurplus;
import server.utility.BoardConfigParser;
import singleton.GameConfig;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * The command line interface for the game :/
 */
public class CLI implements AsyncInputStreamObserver, ClientObserver, LobbyObserver, RemotePlayerObserver {

    /**
     * The client handler that will be dynamically bounded and used
     */
    private Client client;

    /**
     * An asynchronous stream that listens for user input. It is event based with observers.
     */
    private final AsyncInputStream keyboard;

    /**
     * This is the blocking queue that will be filled with new user input
     */
    private final BlockingQueue<String> inputQueue;

    /**
     * This is the blocking queue that will be filled with notifications
     */
    private final BlockingQueue<Notification> notificationQueue;

    private final BlockingQueue<ImmediateActionType> immediateActionQueue;

    /**
     * This mutex is used to wait until a connection-driven event occurs
     */
    private final BlockingQueue<Object> serverTokenQueue;

    private final Object selectionMutex;

    /**
     * Event though the CLI thread might be suspended, the Asynchronous stream will never be
     * Therefore it is a priority to prevent the user from entering any input
     */
    private boolean keyboardEnabled = true;

    /**
     * Defines the context on which the CLI is operating
     * Please see the relative enum for more details
     */
    private CliContext ctx;

    private LocalMatchController localMatchController;


    /**
     * The command line interface constructor
     * Initializes the required objects and proceeds with a play phase
     */
    private CLI() {

        //Init blocking queue for user input
        this.inputQueue = new ArrayBlockingQueue<>(1);

        //Init blocking queue for server data
        this.notificationQueue = new ArrayBlockingQueue<>(10);

        //Init the immediate action queue
        this.immediateActionQueue = new ArrayBlockingQueue<>(1);

        //Init the mutex
        this.serverTokenQueue = new ArrayBlockingQueue<>(1);

        this.selectionMutex = new Object();

        //Initialization procedure
        this.keyboard = new AsyncInputStream(System.in);

        //Register us as observer
        this.keyboard.addObserver(this);

        //Launch the keyboard listener and wait for events
        this.keyboard.start();

        //Init the local match controller
        this.localMatchController = new LocalMatchController();

        //Log only important messages
        Logger.setMinLevel(Level.WARNING);


    }

    /**
     * Pauses the main thread until a onLobbyNotification is performed on the provided mutex object
     *
     * @param mutex the object on which the lock is acquired
     */
    private void waitOnMutex(Object mutex) {

        synchronized (mutex) {

            try {

                mutex.wait();

            } catch (InterruptedException e) {

                e.printStackTrace();

            }
        }

    }

    /**
     * Starts the command line interface
     * Requests the server ip address and the connection type
     */
    private void play() throws InterruptedException {

        //Set the context
        this.ctx = CliContext.Bootstrap;

        String hostIP ;
        String connection;

        EnumCommand<ClientType> clientCmd = new EnumCommand<ClientType>(ClientType.class);

        //Request the IP
        Cmd.askFor("Please enter the IP address of the host");

        //Read the IP
        hostIP = this.inputQueue.take();


        while (!NetUtil.isIPv4(hostIP) && !hostIP.equals("localhost")) {

            Cmd.error("'" + hostIP + "' is not a valid IPv4 address");

            Cmd.askFor("Please enter the IP address of the host");

            hostIP = this.inputQueue.take();

        }


        //Request the connection type
        Cmd.askFor("Select which connection method you would like to use");

        //Print the available choices
        clientCmd.printChoiches();

        //Read the selection
        connection = this.inputQueue.take();

        //Check it
        while (!clientCmd.isValid(connection)) {

            connection = this.inputQueue.take();

        }

        //Select the proper client interface
        if (clientCmd.choiceMatch(connection, ClientType.Socket)) {

            this.client = new SocketClient(hostIP, GameConfig.getInstance().getSocketPort());

        } else if (clientCmd.choiceMatch(connection, ClientType.RMI)) {

            this.client = new RMIClient(hostIP, GameConfig.getInstance().getRmiPort(), "server");

        } else {

            Logger.log(Level.SEVERE, "Bootstrap", "Bad selection");

            return;

        }

        //Connect the client
        try {

            this.client.connect();

        } catch (Exception e) {

            Logger.log(Level.SEVERE, this.toString(), "Unable to connect to the server", e);

            System.exit(0);

        }

        //Register us as observer
        this.client.addClientObserver(this);
        this.client.addLobbyObserver(this);
        this.client.addRemotePlayerObserver(this);


        this.authenticate();

    }

    /**
     * Authenticates the user
     *
     * @throws InterruptedException blockingQueue take
     */
    private void authenticate() throws InterruptedException {

        //Switch context
        this.ctx = CliContext.Authentication;

        EnumCommand<AuthType> authCmd = new EnumCommand<AuthType>(AuthType.class);

        //Request the IP
        Cmd.askFor("Please select how to authenticate");

        //Print the available choices
        authCmd.printChoiches();

        String choice = this.inputQueue.take();

        //Check it
        while (!authCmd.isValid(choice)) {

            choice = this.inputQueue.take();

        }

        if (authCmd.choiceMatch(choice, AuthType.Login)) {

            this.login();

        } else if (authCmd.choiceMatch(choice, AuthType.Registration)) {

            this.register();

        } else {

            Logger.log(Level.SEVERE, "Authentication", "Bad selection");


        }

        this.interactWithLobby();

    }

    /**
     * Logs in the user
     *
     * @throws InterruptedException blockingQueue take
     */
    private void login() throws InterruptedException {

        //Switch context
        this.ctx = CliContext.Login;

        String username;
        String password;

        do {

            //Request the name
            Cmd.askFor("Please enter your username");

            username = this.inputQueue.take();

            //Request the password
            Cmd.askFor("Please enter your password");

            password = this.inputQueue.take();

            //Perform login request
            this.client.login(new LoginRequest(username, password));

            //Wait for the server response
            this.serverTokenQueue.take();


        }
        while (!this.client.hasAuthenticated());

        this.localMatchController.setPlayerUsername(this.client.getUsername());

        Cmd.success("Login successful, welcome back " + client.getUsername());

    }

    private void register() throws InterruptedException {

        //Switch context
        this.ctx = CliContext.Registration;

        String username;
        String password;
        String confirmPassword;
        boolean twoIdentical = false;

        do {
            Cmd.askFor("Please enter your username");

            username = this.inputQueue.take();

            do {

                Cmd.askFor("Please enter your password");

                password = this.inputQueue.take();

                Cmd.askFor("Please re - enter your password");

                confirmPassword = this.inputQueue.take();

                if (password.equals(confirmPassword))
                    twoIdentical = true;
            }
            while (!twoIdentical);

            //Perform login request
            this.client.registration(new RegisterRequest(username, password));

            //Wait for the server response
            this.serverTokenQueue.take();

        }
        while (!this.client.hasAuthenticated());

        this.localMatchController.setPlayerUsername(this.client.getUsername());

        Cmd.success("Registration successful, welcome " + client.getUsername());


    }

    /**
     * Notify the user about the events that happen while waiting for the match to start
     *
     * @throws InterruptedException blockingQueue take
     */
    private void interactWithLobby() throws InterruptedException {

        this.ctx = CliContext.Lobby;

        this.client.sendNotification(new ObserverReadyNotification(ObserverType.Lobby));

        this.keyboardEnabled = false;

        //Assuming that before the match start the client will receive just Lobby notifications
        //Read the first notification
        LobbyNotification o = (LobbyNotification) this.notificationQueue.take();

        //Keep posting notifications until the match starts
        while (
                o.getLobbyNotificationType() != LobbyNotificationType.MatchStart &&
                        o.getLobbyNotificationType() != LobbyNotificationType.ResumeGame &&
                        o.getLobbyNotificationType() != LobbyNotificationType.ResumeLeaderCardDraft &&
                        o.getLobbyNotificationType() != LobbyNotificationType.ResumeBonusTileDraft
                ) {

            Cmd.notify(o.getMessage());

            o = (LobbyNotification) this.notificationQueue.take();

        }

        Cmd.notify(o.getMessage());

        this.keyboardEnabled = true;

        this.interactWithMatchController(o.getLobbyNotificationType());

    }

    /**
     * Handles the round
     *
     * @throws InterruptedException
     */
    private void interactWithMatchController(LobbyNotificationType status) throws InterruptedException {

        this.ctx = CliContext.Match;

        this.client.sendNotification(new ObserverReadyNotification(ObserverType.RemotePlayer));


        switch (status) {

            //Resume the game, draft already done before..
            case ResumeGame:
                break;
            //Give the user the possibility do draft
            case ResumeLeaderCardDraft:
                //this.draftLeaderCards();
                break;
            //Give the user the possibility do draft
            case ResumeBonusTileDraft:
                //this.draftBonusTiles();
                break;
            //Normal bootstrap procedure
            case MatchStart:
            default:
                //this.draftLeaderCards();
                //this.draftBonusTiles();

        }

        while (!this.localMatchController.matchHasEnded()) {

            //Wait for the next token
            this.serverTokenQueue.take();

            try {

                StandardActionType actionPerformed;

                do {

                    //Do a standard action
                    actionPerformed = this.makeStandardAction();

                }
                while (actionPerformed != StandardActionType.TerminateRound);

            } catch (NoActionPerformedException e) {

                Cmd.notify("The timeout for your action expired.");

            } catch (NoSuchPlayerException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Takes care of handling the leader card draft at the beginning of the match
     *
     * @throws InterruptedException
     */
    private void draftLeaderCards() throws InterruptedException {

        System.out.println("Bonus tiles bug finder: waiting for the leader cards..");

        this.serverTokenQueue.take();

        System.out.println("Bonus tiles bug finder: token received. going on..");


        int drafRound = 0;

        while (drafRound < 4) {

            if (drafRound == 0) {

                Cmd.notify("Leader cards draft started");

            }

            ArrayCommand<LeaderCard> leaderCardSelection = new ArrayCommand<>(this.localMatchController.getDraftableLeaderCards().getCards());

            String choice = "";

            Cmd.askFor("Which leader card would you like?");

            leaderCardSelection.printChoiches();

            try {

                choice = this.waitForCommandSelection();

                while (!leaderCardSelection.isValid(choice)) {

                    Cmd.askFor("Which leader card would you like?");

                    leaderCardSelection.printChoiches();

                    choice = this.waitForCommandSelection();

                }

            } catch (NoActionPerformedException e) {

                Cmd.notify("The timeout to take your action has expired.");

                //TODO: Skip further selections cause the player is now disabled

                return;

            }


            this.client.performAction(new ShuffleLeaderCardStandardAction(Integer.parseInt(choice) - 1, this.localMatchController.getDraftableLeaderCards(), this.client.getUsername()));

            System.out.println("Bonus tiles bug finder: waiting for the leader card selection confirmation..");

            //Wait for the action confirmation
            this.serverTokenQueue.take();

            System.out.println("Bonus tiles bug finder: token received. going on..");

            System.out.println("Bonus tiles bug finder: waiting for the next leader card deck..");
            //Wait for the next draft request
            this.serverTokenQueue.take();

            System.out.println("Bonus tiles bug finder: token received. going on..");


            drafRound++;
        }


    }

    /**
     * Interacts with the user asking him which bonus tile he would like
     * It might even throw an exception if on the server side the timeout for the action expires
     *
     * @throws InterruptedException
     */
    private void draftBonusTiles() throws InterruptedException {

        System.out.println("Bonus tiles bug finder: Waiting for the bonus tiles ");

        //Wait for the next token
        this.serverTokenQueue.take();

        System.out.println("Bonus tiles bug finder: token received. going on..");


        ArrayCommand<BonusTile> bonusTileSelection = new ArrayCommand<>(this.localMatchController.getDraftableBonusTiles());

        String choice = "";

        Cmd.askFor("Which bonus tile would you like?");

        bonusTileSelection.printChoiches();

        try {

            choice = this.waitForCommandSelection();

            while (!bonusTileSelection.isValid(choice)) {

                Cmd.askFor("Which bonus tile would you like?");

                bonusTileSelection.printChoiches();

                choice = this.waitForCommandSelection();

            }

        } catch (NoActionPerformedException e) {

            Cmd.notify("The timeout to take your action has expired.");

            //TODO: Skip further selections cause the player is now disabled

            return;

        }

        this.client.performAction(new ShuffleBinusTileStandardAction(Integer.parseInt(choice) - 1, this.localMatchController.getDraftableBonusTiles(), this.client.getUsername()));

        //Wait for the action confirmation
        this.serverTokenQueue.take();

    }

    private StandardActionType makeStandardAction() throws InterruptedException, NoActionPerformedException, NoSuchPlayerException {


        //Ask the user which action he wants to perform printing the choices
        Cmd.askFor("Which action would you like to perform ?");

        //Create a command to show the possible actions
        EnumCommand<StandardActionType> actionSelection = new EnumCommand<StandardActionType>(StandardActionType.class);

        //Show the choices
        actionSelection.printChoiches();

        //Try to do an action before the timeout goes out
        String choice = this.waitForCommandSelection();

        boolean valid = actionSelection.isValid(choice);

        this.localMatchController.setActionPerformed(StandardActionType.DiscardLeaderCard, false);
        this.localMatchController.setActionPerformed(StandardActionType.LeaderCardActivation, false);


        //Check if the user can roll the dices
        if(!this.localMatchController.diceAreRolled()){

            this.localMatchController.setActionPerformed(StandardActionType.RollDice, false);

        }

        while (!valid || !this.localMatchController.canPerformStandardAction(actionSelection.getEnumEntryFromChoice(choice))) {

            if (valid && !this.localMatchController.canPerformStandardAction(actionSelection.getEnumEntryFromChoice(choice))) {

                Cmd.error("The action '" + actionSelection.getEnumEntryFromChoice(choice) + "' can't be performed again!");

            }

            //Ask the user which action he wants to perform printing the choices
            Cmd.askFor("Which action would you like to perform ?");

            choice = this.waitForCommandSelection();

            valid = actionSelection.isValid(choice);

        }

        if (!this.localMatchController.diceAreRolled() && actionSelection.getEnumEntryFromChoice(choice) != StandardActionType.RollDice) {

            Cmd.error("You must roll the dices first!");

            //Ask the user which action he wants to perform printing the choices
            Cmd.askFor("Which action would you like to perform ?");

            choice = this.waitForCommandSelection();

            while (!actionSelection.isValid(choice) || !actionSelection.choiceMatch(choice, StandardActionType.RollDice)) {

                Cmd.error("You must roll the dices first!");

                //Ask the user which action he wants to perform printing the choices
                Cmd.askFor("Which action would you like to perform ?");

                choice = this.waitForCommandSelection();

            }

        }

        if ((actionSelection.getEnumEntryFromChoice(choice) == StandardActionType.LeaderCardActivation ||actionSelection.getEnumEntryFromChoice(choice) == StandardActionType.DiscardLeaderCard) && this.localMatchController.getLocalPlayer().getLeaderCards().size() == 0) {

            Cmd.error("You haven't any leader card!");

            //Ask the user which action he wants to perform printing the choices
            Cmd.askFor("Which action would you like to perform ?");

            choice = this.waitForCommandSelection();

            while ((!actionSelection.isValid(choice) || this.localMatchController.getLocalPlayer().getPlayedLeaderCards().size() == 0) && (actionSelection.getEnumEntryFromChoice(choice) == StandardActionType.LeaderCardActivation ||actionSelection.getEnumEntryFromChoice(choice) == StandardActionType.DiscardLeaderCard)) {

                Cmd.error("You haven't any leader card!");

                //Ask the user which action he wants to perform printing the choices
                Cmd.askFor("Which action would you like to perform ?");

                choice = this.waitForCommandSelection();

            }

        }



        //If we got here then we entered a valid choice, go on asking the user what to do
        //However the timeout is still ticking.
        if (actionSelection.choiceMatch(choice, StandardActionType.FamilyMemberPlacement)) {

            this.placeFamilyMember();

        } else if (actionSelection.choiceMatch(choice, StandardActionType.RollDice)) {

            this.client.performAction(new RollDicesAction(this.client.getUsername()));

        } else if (actionSelection.choiceMatch(choice, StandardActionType.ShowDvptCardDetail)) {

            this.showDvptCardDetail();

        }
        else if (actionSelection.choiceMatch(choice, StandardActionType.ShowPersonalBoard)) {

            this.showPersonalBoard();

        }
        else if (actionSelection.choiceMatch(choice, StandardActionType.LeaderCardActivation)) {

            this.activateLeaderCard();


        }

        else if (actionSelection.choiceMatch(choice, StandardActionType.DiscardLeaderCard)) {

            this.discardLeaderCard();

        }


        else if (actionSelection.choiceMatch(choice, StandardActionType.TerminateRound)) {

            this.terminateRound();

        }

        boolean requiresServerConfirmation = !(actionSelection.choiceMatch(choice, StandardActionType.ShowDvptCardDetail) || actionSelection.choiceMatch(choice, StandardActionType.ShowPersonalBoard)) ;

        if (requiresServerConfirmation) {

            //Before setting the move as done, wait for server confirmation or refusal
            this.localMatchController.setLastPendingStandardAction(actionSelection.getEnumEntryFromChoice(choice));

            //Wait until a token comes
            Object token = this.serverTokenQueue.take();

            boolean isActionConfirmationOrRefusal = (token instanceof Action) && ((Action)token).getActionType() == ActionType.Standard;

            //Wait for the token that confirms the standard action.
            //In the meanwhile some immediate action requests may arrive
            while (!isActionConfirmationOrRefusal) {

                this.handleRecursiveImmediateAction();

                //Wait until we receive either a confirmation or a refusal for the current standard action or a new immediate one
                token = this.serverTokenQueue.take();

                //Recheck the condition
                isActionConfirmationOrRefusal = (token instanceof Action) && ((Action)token).getActionType() == ActionType.Standard;

            }

        }

        //After the action was performed, return the choice made so that if the user wants to terminate the round we can know it
        return actionSelection.getEnumEntryFromChoice(choice);

    }


    private void handleRecursiveImmediateAction() throws InterruptedException, NoActionPerformedException {

        //Take the immediate action to do
        ImmediateActionType action = this.immediateActionQueue.take();

        do {

            //Set this immediate action as the last one pending
            this.localMatchController.setLastPendingImmediateAction(action);

            //Perform the immediate action
            this.performImmediateAction(action);

            //Wait for a token : immediate action confirmation/refusal or new immediate action available
            this.serverTokenQueue.take();

            //This action may need to trigger more than one sub immediate actions
            while (this.immediateActionQueue.size() > 0) {

                //A new immediate action request has come
                this.handleRecursiveImmediateAction();

                //Wait for a token : immediate action confirmation/refusal or new immediate action available
                this.serverTokenQueue.take();

            }

        }
        while (this.localMatchController.getLastPendingImmediateAction() == action);


    }

    private void activateLeaderCard() throws NoActionPerformedException, InterruptedException {


        Cmd.askFor("Which leader card would you like to play / activate?");

        String choice = "";

        ArrayCommand<LeaderCard> leaderCardSelection = new ArrayCommand<>(this.localMatchController.getLocalPlayer().getLeaderCards());

        leaderCardSelection.printChoiches();

        choice = this.waitForCommandSelection();

        while (!leaderCardSelection.isValid(choice)) {

            Cmd.askFor("Which leader card would you like to play / activate?");

            leaderCardSelection.printChoiches();

            choice = this.waitForCommandSelection();

        }

        int selection = Integer.parseInt(choice) - 1;

        this.client.performAction(new LeaderCardActivationAction(this.localMatchController.getLocalPlayer().getLeaderCards().get(selection).getId(), this.client.getUsername()));
    }

    private void discardLeaderCard() throws NoActionPerformedException, InterruptedException {


        Cmd.askFor("Which leader card would you like to discard to obtain a council privilege?");

        String choice = "";

        ArrayCommand<LeaderCard> leaderCardSelection = new ArrayCommand<>(this.localMatchController.getLocalPlayer().getLeaderCards());

        leaderCardSelection.printChoiches();

        choice = this.waitForCommandSelection();

        while (!leaderCardSelection.isValid(choice)) {

            Cmd.askFor("Which leader card would you like to discard to obtain a council privilege?");

            leaderCardSelection.printChoiches();

            choice = this.waitForCommandSelection();

        }

        int selection = Integer.parseInt(choice) - 1;

        this.client.performAction(new DiscardLeaderCardAction(this.localMatchController.getLocalPlayer().getLeaderCards().get(selection).getId(), this.client.getUsername()));
    }

    /**
     * Attempts to perform the immediate action.
     * Upon success, the last pending immediate action gets cleared and the method return
     * Otherwise the loop continues
     *
     * @param type the immediate action type
     */
    private void performImmediateAction(ImmediateActionType type) throws NoActionPerformedException, InterruptedException {

        String choice;

        if (type.getImpl() == ImmediateActionTypeImpl.Choice) {

            ImmediateChoiceAction immediateChoiceAction = null;

            switch (type) {

                case SelectCouncilPrivilege:

                    ArrayCommand<EffectSurplus> privilegeSelection = new ArrayCommand<>(BoardConfigParser.getCouncilPrivilegeOptions());

                    Cmd.askFor("Which council privilege would you like?");

                    privilegeSelection.printChoiches();

                    choice = this.waitForCommandSelection();

                    while (!privilegeSelection.isValid(choice)) {

                        Cmd.askFor("Which council privilege would you like?");

                        privilegeSelection.printChoiches();

                        choice = this.waitForCommandSelection();

                    }

                    immediateChoiceAction = new ImmediateChoiceAction(Integer.parseInt(choice) - 1, this.client.getUsername());

                    break;

                case DecideBanOption:

                    ArrayList<String> selection = new ArrayList<>();

                    selection.add("Yes");
                    selection.add("No");

                    ArrayCommand<String> selectionCommand = new ArrayCommand<>(selection);

                    Cmd.askFor("Make your choice");

                    selectionCommand.printChoiches();

                    choice = this.waitForCommandSelection();

                    while (!selectionCommand.isValid(choice)) {

                        Cmd.askFor("Make your choice");

                        choice = this.waitForCommandSelection();

                    }

                    immediateChoiceAction = new ImmediateChoiceAction(Integer.parseInt(choice) - 1, this.client.getUsername());

                    break;

                case DecideDiscountOption:

                    ArrayList<String> options = new ArrayList<>();

                    options.add("First discount");
                    options.add("Second discount");

                    ArrayCommand<String> optionsCommand = new ArrayCommand<>(options);

                    Cmd.askFor("Make your choice");

                    optionsCommand.printChoiches();

                    choice = this.waitForCommandSelection();

                    while (!optionsCommand.isValid(choice)) {

                        Cmd.askFor("Make your choice");

                        choice = this.waitForCommandSelection();

                    }

                    immediateChoiceAction = new ImmediateChoiceAction(Integer.parseInt(choice) - 1, this.client.getUsername());

                    break;

                case SelectFamilyMember:

                    EnumCommand<ColorType> colorSelection = new EnumCommand<>(ColorType.class);

                    Cmd.askFor("Which family member do you want to use force = 6?");

                    colorSelection.printChoiches();

                    choice = this.waitForCommandSelection();

                    while (!colorSelection.isValid(choice)) {

                        Cmd.askFor("Which family member do you want to use force = 6?");

                        colorSelection.printChoiches();

                        choice = this.waitForCommandSelection();

                    }

                    immediateChoiceAction = new ImmediateChoiceAction(Integer.parseInt(choice), this.client.getUsername());

                    break;

                    case SelectConversion:

                        ArrayList<String> conversionOptions = new ArrayList<>();

                        conversionOptions.add("First conversion");
                        conversionOptions.add("Second conversion");

                        ArrayCommand<String> conversionOptionsCommand = new ArrayCommand<>(conversionOptions);

                        Cmd.askFor("Make your choice");

                        conversionOptionsCommand.printChoiches();

                        choice = this.waitForCommandSelection();

                        while (!conversionOptionsCommand.isValid(choice)) {

                            Cmd.askFor("Make your choice");

                            choice = this.waitForCommandSelection();

                        }

                        immediateChoiceAction = new ImmediateChoiceAction(Integer.parseInt(choice) - 1, this.client.getUsername());

                        break;

                    case SelectCost:

                        ArrayList<String> costOptions = new ArrayList<>();

                        costOptions.add("First cost");
                        costOptions.add("Second cost");

                        ArrayCommand<String> costOptionsCommand = new ArrayCommand<>(costOptions);

                        Cmd.askFor("Make your choice");

                        costOptionsCommand.printChoiches();

                        choice = this.waitForCommandSelection();

                        while (!costOptionsCommand.isValid(choice)) {

                            Cmd.askFor("Make your choice");

                            choice = this.waitForCommandSelection();

                        }

                        immediateChoiceAction = new ImmediateChoiceAction(Integer.parseInt(choice) - 1, this.client.getUsername());

                        break;


                    case SelectActiveLeaderCard:

                    ArrayList<LeaderCard> container = new ArrayList<>();

                    for(Player player : this.localMatchController.getMatch().getPlayers()) {
                        if(!player.getUsername().equals(localMatchController.getLocalPlayer().getUsername()))
                            container.addAll(player.getPlayedLeaderCards());
                    }

                    ArrayCommand<LeaderCard> leaderSelection = new ArrayCommand<>(container);

                    Cmd.askFor(type.toString());

                    leaderSelection.printChoiches();

                    choice = this.waitForCommandSelection();

                    while (!leaderSelection.isValid(choice)) {

                        Cmd.askFor(type.toString());

                        leaderSelection.printChoiches();

                        choice = this.waitForCommandSelection();

                    }

                    int selected = container.get(Integer.parseInt(choice) - 1).getId();

                    immediateChoiceAction = new ImmediateChoiceAction(selected, this.client.getUsername());

                    break;

            }

            //Send the immediate choice
            this.client.performAction(immediateChoiceAction);

        } else if (type.getImpl() == ImmediateActionTypeImpl.Placement) {

            ImmediatePlacementAction immediatePlacementAction = null;

            switch (type) {

                case ActivateHarvest:

                    immediatePlacementAction = new ImmediatePlacementAction(ImmediateBoardSectorType.Harvest, this.askForServants(), this.client.getUsername());

                    break;

                case ActivateProduction:

                    immediatePlacementAction = new ImmediatePlacementAction(ImmediateBoardSectorType.Production, this.askForServants(), this.client.getUsername());

                    break;

                case TakeBuildingCard:

                    immediatePlacementAction = new ImmediatePlacementAction(ImmediateBoardSectorType.BuildingTower, this.askForPlacementIndex(), this.askForServants(), this.client.getUsername());

                    break;

                case TakeCharacterCard:

                    immediatePlacementAction = new ImmediatePlacementAction(ImmediateBoardSectorType.CharacterTower, this.askForPlacementIndex(), this.askForServants(),this.client.getUsername());

                    break;

                case TakeVentureCard:

                    immediatePlacementAction = new ImmediatePlacementAction(ImmediateBoardSectorType.VentureTower, this.askForPlacementIndex(), this.askForServants(), this.client.getUsername());

                    break;

                case TakeTerritoryCard:

                    immediatePlacementAction = new ImmediatePlacementAction(ImmediateBoardSectorType.TerritoryTower, this.askForPlacementIndex(), this.askForServants(), this.client.getUsername());

                    break;

                case TakeAnyCard:

                    immediatePlacementAction = new ImmediatePlacementAction(this.askForTypeOption(), this.askForPlacementIndex(), this.askForServants(), this.client.getUsername());

                    break;
            }

            //Send the immediate placement
            this.client.performAction(immediatePlacementAction);


        }

    }

    /**
     * Creates an actions to place a family member and sends it to the server
     *
     * @throws InterruptedException       if the thread gets interrupted
     * @throws NoActionPerformedException if the action timeout expires
     */
    private void placeFamilyMember() throws InterruptedException, NoActionPerformedException {

        StandardPlacementAction standardPlacementAction;

        String choice;

        BoardSectorType sectorType;
        Integer index = 0;
        ColorType memberColor;
        Integer additionalServants;
        SelectionType costOption = SelectionType.First;


        Cmd.askFor("Please select where you would like to place your family member");

        EnumCommand<BoardSectorType> sectorSelection = new EnumCommand<>(BoardSectorType.class);

        sectorSelection.printChoiches();

        do {

            choice = this.waitForCommandSelection();

        }
        while (!sectorSelection.isValid(choice));

        sectorType = sectorSelection.getEnumEntryFromChoice(choice);

        if (sectorType.canChoseIndex()) {

            Cmd.askFor("Please select the placement index [1-4]");

            choice = this.inputQueue.take();

            while (!this.isIntegerInRange(choice, 1, 4)) {

                Cmd.forbidden("Invalid input or index out of bounds, try again.");

                choice = this.inputQueue.take();


            }

            index = Integer.parseInt(choice) - 1;

        }

        Cmd.askFor("Please select the color of the family member you would like to use");

        EnumCommand<ColorType> colorSelection = new EnumCommand<ColorType>(ColorType.class);

        colorSelection.printChoiches();

        do {

            choice = this.waitForCommandSelection();

        }
        while (!colorSelection.isValid(choice));


        memberColor = colorSelection.getEnumEntryFromChoice(choice);

        additionalServants = this.askForServants();



        standardPlacementAction = new StandardPlacementAction(sectorType, index, memberColor, additionalServants, this.client.getUsername());

        this.client.performAction(standardPlacementAction);

    }

    private void terminateRound() {

        this.client.performAction(new TerminateRoundStandardAction(this.client.getUsername()));

        this.localMatchController.flushActionsPerformed();

    }

    /**
     * Asks the user for a development card ID and prints the card details
     *
     * @throws InterruptedException
     */
    private void showDvptCardDetail() throws InterruptedException {

        String id;

        Cmd.askFor("Enter the card ID of which you would like to see more details");

        do {

            id = this.inputQueue.take();

        }
        while (!this.isIntegerInRange(id, 1, GameSingleton.getInstance().getDvptCards().size()));


        System.out.println(GameSingleton.getInstance().getSpecificDvptCard(Integer.parseInt(id) - 1));

    }

    /**
     * Show personal board of the player
     *
     * @throws InterruptedException
     */
    private void showPersonalBoard() throws NoSuchPlayerException {

        System.out.println(this.localMatchController.getMatch().getPlayerFromUsername(this.client.getUsername()).getPersonalBoard().toString());

    }

    /**
     * Waits until the user select something
     *
     * @return The selection
     * @throws NoActionPerformedException if the timeout expired for taking the move
     * @throws InterruptedException       wait exception
     */
    private String waitForCommandSelection() throws InterruptedException, NoActionPerformedException {

        //Suspend the thread on a mutex and wait for the user to enter a command choice or for the timeout to expire
        this.waitOnMutex(this.selectionMutex);

        //If it was a move.. send it
        if (this.inputQueue.isEmpty()) {

            //The mutex was notified but the queue was empty. The timeout must have expired
            throw new NoActionPerformedException("The user did not performed any move within the timeout provided");

        } else {

            //The user performed an action, handle it
            return this.inputQueue.take();

        }

    }


    private int askForServants() throws InterruptedException {

        Cmd.askFor("Enter the amount of additional servants");

        String choice = this.inputQueue.take();

        try {

            while (!this.isIntegerInRange(choice, 0, this.localMatchController.getMatch().getPlayerFromUsername(this.client.getUsername()).getServants())) {

                Cmd.forbidden("Invalid input or not enough servants, try again");

                choice = this.inputQueue.take();


            }

        } catch (NoSuchPlayerException e) {

            e.printStackTrace();

        }

        return Integer.parseInt(choice);

    }

    private int askForPlacementIndex() throws InterruptedException {

        Cmd.askFor("Enter the index of the tower with the card you want to take");

        String choice = this.inputQueue.take();

        while (!this.isIntegerInRange(choice, 0, 4)) {

            Cmd.forbidden("Invalid input, try again");

            choice = this.inputQueue.take();
        }

        return Integer.parseInt(choice)-1;

    }

    private SelectionType askForCostOption() throws InterruptedException, NoActionPerformedException {

        SelectionType costOption;

        Cmd.askFor("Enter the cost option");

        EnumCommand<SelectionType> costSelection = new EnumCommand<SelectionType>(SelectionType.class);

        costSelection.printChoiches();

        String choice;

        do {

            choice = this.waitForCommandSelection();

        }
        while (!costSelection.isValid(choice));

        costOption = costSelection.getEnumEntryFromChoice(choice);

        return costOption;

    }

    private ImmediateBoardSectorType askForTypeOption() throws InterruptedException, NoActionPerformedException {

        DvptCardType typeOption;

        Cmd.askFor("Select card type");

        EnumCommand<DvptCardType> typeSelection = new EnumCommand<>(DvptCardType.class);

        typeSelection.printChoiches();

        String choice;

        do {

            choice = this.waitForCommandSelection();

        }
        while (!typeSelection.isValid(choice));

        typeOption = typeSelection.getEnumEntryFromChoice(choice);

        if(typeOption == DvptCardType.building)
            return ImmediateBoardSectorType.BuildingTower;

        if(typeOption == DvptCardType.venture)
            return ImmediateBoardSectorType.VentureTower;

        if(typeOption == DvptCardType.character)
            return ImmediateBoardSectorType.CharacterTower;

        else
            return ImmediateBoardSectorType.TerritoryTower;

    }


    public void printBoardAndPlayers() {

        if (this.localMatchController.getMatch() == null) return;

        //Print the board every time the turn changes
        System.out.println(this.localMatchController.getMatch().getBoard());

        for (Player p : this.localMatchController.getMatch().getPlayers()) {

            System.out.println(p);

        }

    }

    private void addTokenToQueue(BlockingQueue<Object> queue) {

        if (queue.remainingCapacity() > 0) {

            queue.add(new Object());

        }

    }

    private void addTokenToQueue(BlockingQueue<Object> queue, Object o) {

        if (queue.remainingCapacity() > 0) {

            queue.add(o);

        }

    }

    /**
     * Interface implementation for AsyncInputStreamObserver
     * @param stream the stream that raised the event
     * @param value the value associated with the stream, supposed to be always a string in this case
     */
    public void onInput(AsyncInputStream stream, String value) {

        if (stream == this.keyboard && this.keyboardEnabled) {

            this.inputQueue.clear();

            this.inputQueue.add(value);

            if (this.ctx == CliContext.Match) {

                synchronized (this.selectionMutex) {

                    this.selectionMutex.notify();

                }
            }

        }

    }

    public void onDisconnection(Client client) {

        Cmd.error("Connection lost.");

    }

    public void onLoginFailed(Client client, String reason) {

        Cmd.forbidden(reason);
        this.addTokenToQueue(this.serverTokenQueue);

    }

    public void onLoginSuccess(Client client) {

        this.addTokenToQueue(this.serverTokenQueue);

    }

    @Override
    public void onRegistrationSuccess(Client client) {
        this.addTokenToQueue(this.serverTokenQueue);

    }

    @Override
    public void onRegistrationFailed(Client client, String reason) {
        Cmd.forbidden(reason);
        this.addTokenToQueue(this.serverTokenQueue);
    }

    public void onLobbyNotification(Client client, LobbyNotification not) {

        this.notificationQueue.add(not);

    }

    public void onNotification(Client sender, MatchNotification notification) {

        Cmd.notify(notification.getMessage());

    }

    public void onModelUpdate(Client sender, Match model) {

        this.localMatchController.setMatch(model);

    }

    public void onTurnEnabled(Client sender, Player player, String message) {

        this.printBoardAndPlayers();

        //It it is our turn
        if (player.getUsername().equals(this.client.getUsername())) {

            Cmd.notify("It is your turn!");

            this.addTokenToQueue(this.serverTokenQueue);


        }
        else {

            Cmd.notify(message);

        }

    }

    public void onImmediateActionAvailable(Client sender, ImmediateActionType actionType, Player player, String message) {


        //It it is our turn
        if (player.getUsername().equals(this.client.getUsername())) {

            Cmd.notify(message);

            //Add the action to the queue
            this.immediateActionQueue.add(actionType);


            //Wake up the thread
            this.addTokenToQueue(this.serverTokenQueue);

        }
        else {

            Cmd.notify(player.getUsername() + " can make an immediate action");

        }

    }

    public void onTurnDisabled(Client sender, Player player, String message) {

        //It it is our turn
        if (player.getUsername().equals(this.client.getUsername())) {

            Cmd.notify("Your turn ended");

        }
        else {

            Cmd.notify(message);

        }

    }

    public void onTimeoutExpired(Client sender, Player player, String message) {


        //It it is our turn
        if (player.getUsername().equals(this.client.getUsername())) {

            Cmd.notify("Your were disabled because your timeout to take the action expired");

            synchronized (this.selectionMutex) {

                this.selectionMutex.notify();

            }


        }
        else {

            Cmd.notify(message);

        }


    }

    public void onActionRefused(Client sender,  Action action, String message) {

        Cmd.forbidden("Action refused for reason: " + message);

        this.addTokenToQueue(this.serverTokenQueue, action);

    }

    public void onActionPerformed(Client sender, Player player, Action action, String message) {
                //It it is our turn
        if (player.getUsername().equals(this.client.getUsername())) {


            //Confirm last action
            if (action.getActionType() == ActionType.Standard) {

                this.localMatchController.confirmLastStandardPendingAction();

                if (this.localMatchController.getMatch() != null) {

                    this.printBoardAndPlayers();

                }

                Cmd.success("Action performed successfully");

            }
            else {

                this.localMatchController.confirmLastPendingImmediateAction();

                Cmd.success("Immediate action performed successfully");

            }

            this.addTokenToQueue(this.serverTokenQueue, action);



        }
        else {

            Cmd.notify(message);

        }

    }

    public void onLeaderCardDraftRequest(Client sender, Deck<LeaderCard> cards, String message) {

        this.localMatchController.setDraftableLeaderCards(cards);

        if (cards.getCards().size() > 0)
            Cmd.notify(message);

        this.addTokenToQueue(this.serverTokenQueue);

    }

    public void onBonusTileDraftRequest(Client sender, ArrayList<BonusTile> tiles, String message) {

        System.out.println("Bonus tiles received!, size = " + tiles.size());

        //Assign the tiles just received
        this.localMatchController.setDraftableBonusTiles(tiles);

        //Notify the user
        Cmd.notify(message);

        this.addTokenToQueue(this.serverTokenQueue);

    }

    private boolean isInteger(String s) {

        try {

            Integer.parseInt(s);

            return true;

        } catch(Exception e) {

            return false;

        }


    }

    private boolean isIntegerInRange(String s, int min, int max) {

        if (this.isInteger(s)) {

            if (Integer.parseInt(s) < min || Integer.parseInt(s) > max) {

                return false;

            }

            return true;

        }

        return false;

    }

    public static void main(String[] args) throws InterruptedException {

        (new CLI()).play();

    }

}
