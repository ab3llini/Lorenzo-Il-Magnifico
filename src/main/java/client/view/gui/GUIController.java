package client.view.gui;

import client.controller.network.ObserverType;
import exception.NoSuchPlayerException;
import exception.gui.GuiException;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import client.controller.network.Client;
import client.controller.network.ClientObserver;
import client.controller.network.RemotePlayerObserver;
import client.view.LocalMatchController;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import logger.Level;
import logger.Logger;
import netobject.action.Action;
import netobject.action.ActionType;
import netobject.action.BoardSectorType;
import netobject.action.immediate.ImmediateActionType;
import netobject.action.standard.RollDicesAction;
import netobject.action.standard.StandardActionType;
import netobject.action.standard.TerminateRoundStandardAction;
import netobject.notification.MatchNotification;
import netobject.notification.ObserverReadyNotification;
import server.model.Match;
import server.model.board.BonusTile;
import server.model.board.ColorType;
import server.model.board.Player;
import server.model.board.TowerSlot;
import server.model.card.Deck;
import server.model.card.developement.*;
import server.model.card.leader.LeaderCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 * @author  ab3llini
 * @since   20/06/17.
 */
public class GUIController extends NavigationController implements ClientObserver, RemotePlayerObserver{

    @FXML
    private GridPane dvptCardGrid;

    @FXML
    private Label whiteDiceValueTextField;

    @FXML
    private Label blackDiceValueTextField;

    @FXML
    private Label orangeDiceValueTextField;

    @FXML
    private Label victoryValueTextField;

    @FXML
    private Label faithValueTextField;

    @FXML
    private Label militaryValueTextField;

    @FXML
    private Label woodValueTextField;

    @FXML
    private Label stonesValueTextField;

    @FXML
    private Label coinsValueTextField;

    @FXML
    private Label servantsValueTextField;

    @FXML
    private TextField turnIndicatorTextField;

    @FXML
    private Pane councilPalace;

    //The reference to the local match controller
    private LocalMatchController localMatchController;

    HashMap<ImageView, DvptCard> imageViewDvptCardCache = new HashMap<>();

    HashMap<Circle, ImageView> actionPlaceImageViewCache = new HashMap<>();


    //The local client instance
    private Client client;


    public GUIController() {

        this.localMatchController = new LocalMatchController();

    }

    /**
     * This method injects dvpt card images in their right image view and updates the cache for it.
     * Note that if a card is not present on a tower there will not be a cache map entry
     * @param model the model used to inject the cards
     */
    private synchronized void updatedDvptCardGrid(Match model) {

        final int TERRITORY_TOWER_COL = 0;
        final int CHARACTER_TOWER_COL = 2;
        final int BUILDING_TOWER_COL = 4;
        final int VENTURE_TOWER_COL = 6;


        int col, row;

        for (Node node : this.dvptCardGrid.getChildren()) {

            if (GridPane.getColumnIndex(node) == null || GridPane.getRowIndex(node) == null) {

                System.out.println("Skipping node : " + node);

                continue;

            }
            else {

                col = GridPane.getColumnIndex(node);
                row = GridPane.getRowIndex(node);

            }

            ArrayList<TowerSlot> tower = null;

            switch (col) {

                case TERRITORY_TOWER_COL:
                    tower = this.localMatchController.getMatch().getBoard().getTerritoryTower();
                    break;
                case CHARACTER_TOWER_COL:
                    tower = this.localMatchController.getMatch().getBoard().getCharacterTower();
                    break;
                case BUILDING_TOWER_COL:
                    tower = this.localMatchController.getMatch().getBoard().getBuildingTower();
                    break;
                case VENTURE_TOWER_COL:
                    tower = this.localMatchController.getMatch().getBoard().getVentureTower();
                    break;

            }

            //If the current node is an image view then..
            if (node instanceof ImageView) {

                ImageView imgView = (ImageView)node;

                //Take the card from the tower, if exists
                DvptCard card = tower.get(3-row/2).getDvptCard();

                if (card != null) {

                    //Assign the image
                    imgView.setImage(new Image("assets/cards/dvpt/devcards_f_en_c_" + card.getId() + ".png"));

                    //Round the borders
                    this.clipImageForView(imgView);

                }
                else {

                    imgView.setImage(null);

                }


                //Put a new pair in the cache : image view -> dvpt card (might be null)
                this.imageViewDvptCardCache.put(imgView, card);

            }

        }

    }

    private void clipImageForView(ImageView view) {

        Rectangle clip = new Rectangle(
                view.getFitWidth(), view.getFitHeight()
        );
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        view.setClip(clip);

        // snapshot the rounded image.
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage writableImage = view.snapshot(parameters, null);

        // remove the rounding clip so that our effect can show through.
        view.setClip(null);

        view.setImage(writableImage);

    }

    private DvptCard getDvptCardFromImageView(ImageView view) throws GuiException {

        DvptCard requested = this.imageViewDvptCardCache.get(view);

        if (requested == null) {

            throw new GuiException("No card set for the provided image view");

        }
        else {

            return requested;

        }

    }

    private ImageView getImageViewFromDvptCard(DvptCard card) throws GuiException {

        Iterator i = this.imageViewDvptCardCache.entrySet().iterator();

        while (i.hasNext()) {
            Map.Entry pair = (Map.Entry)i.next();
            if (pair.getValue() != null && (pair.getValue()).equals(card)) {

                return (ImageView) pair.getKey();

            }
        }

        throw new GuiException("Unable to fetch an image view for the provided card");

    }

    private void updateSidebars(Match model) {

        //Update points & resources
        try {

            Player me = model.getPlayerFromUsername(this.client.getUsername());

            this.woodValueTextField.setText(me.getWood().toString());
            this.stonesValueTextField.setText(me.getStones().toString());
            this.coinsValueTextField.setText(me.getCoins().toString());
            this.servantsValueTextField.setText(me.getServants().toString());
            this.victoryValueTextField.setText(me.getVictoryPoints().toString());
            this.faithValueTextField.setText(me.getFaithPoints().toString());
            this.militaryValueTextField.setText(me.getMilitaryPoints().toString());


        } catch (NoSuchPlayerException e) {

            Logger.log(Level.SEVERE, this.toString(), "Player not found", e);

        }

        //Update the dice values
        this.blackDiceValueTextField.setText(this.localMatchController.getMatch().getBoard().getDiceForce(ColorType.Black).toString());
        this.whiteDiceValueTextField.setText(this.localMatchController.getMatch().getBoard().getDiceForce(ColorType.White).toString());
        this.orangeDiceValueTextField.setText(this.localMatchController.getMatch().getBoard().getDiceForce(ColorType.Orange).toString());


    }

    private ArrayList<DvptCard> getDvptCardgetDvptCardsFromTowersFromTower(ArrayList<TowerSlot> tower) {

        ArrayList<DvptCard> cards = new ArrayList<>();

        for (TowerSlot towerSlot : tower) {

            cards.add(towerSlot.getDvptCard());

        }

        return cards;

    }

    public void setClient(Client client) {

        this.client = client;
        this.client.addClientObserver(this);
        this.client.addRemotePlayerObserver(this);
        this.localMatchController.setPlayerUsername(this.client.getUsername());
        this.client.sendNotification(new ObserverReadyNotification(ObserverType.RemotePlayer));

    }

    private void buildCache() {

        for (Node node : this.dvptCardGrid.getChildren()) {

            if (node instanceof AnchorPane) {

                int row = GridPane.getRowIndex(node);
                int col = GridPane.getColumnIndex(node);

                Circle actionPlace = (Circle)((AnchorPane)node).getChildren().get(0);

                ImageView relative = (ImageView)getNodeByRowColumnIndex(row, col - 1, this.dvptCardGrid);

                this.actionPlaceImageViewCache.put(actionPlace, relative);

            }

        }

    }

    public Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();

        for (Node node : childrens) {
            if(gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }

        return result;
    }

    /**
     * This method handles clicks on a card, which is made when someone is making an immediate action
     * @param event the mouse event
     */
    @FXML
    void onDvptCardClick(MouseEvent event) {

        ImageView clicked = (ImageView) event.getSource();
        DvptCard card = this.imageViewDvptCardCache.get(clicked);
        int index = this.getTowerPlacementIndex(card);
        ImmediateActionType type = this.localMatchController.getLastPendingImmediateAction();

        if (type == null) {

            this.showAsynchAlert(Alert.AlertType.ERROR, "Frobidden", "You can't take this card", "There is no immediate action available");
            return;

        }

        switch (type) {

            case TakeAnyCard:
                break;
            case TakeTerritoryCard:
                if (!(card instanceof TerritoryDvptCard)) {
                    this.showAsynchAlert(Alert.AlertType.WARNING, "Immediate action", "Invalid selection ", type.toString());
                    return;
                }
                break;
            case TakeCharacterCard:
                if (!(card instanceof CharacterDvptCard)) {
                    this.showAsynchAlert(Alert.AlertType.WARNING, "Immediate action", "Invalid selection ", type.toString());
                    return;
                }
                break;
            case TakeBuildingCard:
                if (!(card instanceof BuildingDvptCard)) {
                    this.showAsynchAlert(Alert.AlertType.WARNING, "Immediate action", "Invalid selection ", type.toString());
                    return;
                }
                break;
            case TakeVentureCard:
                if (!(card instanceof VentureDvptCard)) {
                    this.showAsynchAlert(Alert.AlertType.WARNING, "Immediate action", "Invalid selection ", type.toString());
                    return;
                }
                break;

            default:
                this.showAsynchAlert(Alert.AlertType.ERROR, "Frobidden", "You can't take this card", "This immediate action doesn't allow you to take a card");
                return;

        }



        ImmediatePlacementActionController controller = (ImmediatePlacementActionController) this.openNewStage(View.ImmediatePlacement);

        controller.setIndex(index);
        controller.setClient(client);
        controller.setLocalMatchController(this.localMatchController);
        controller.setImmediateBoardSectorType(card.getImmediateBoardSector());


    }

    @FXML
    void onDiceRollClick(MouseEvent event) {

        if (!this.checkTurnEnabled()) return;


        if (!this.localMatchController.diceAreRolled() && this.localMatchController.canRollDices()) {

            this.client.performAction(new RollDicesAction(this.client.getUsername()));


        }
        else {

            this.showAsynchAlert(Alert.AlertType.WARNING, "Action refused", "You can't roll the dices!", "They are already rolled or you are not the first in this turn");

        }

    }

    @FXML
    void onTerminateRoundClick(MouseEvent event) {

        if (!this.checkTurnEnabled()) return;

        //Check if the user can roll the dices
        if(!this.localMatchController.diceAreRolled()){

            this.showAsynchAlert(Alert.AlertType.ERROR, "Forbidden", "Dices not rolled", "You must roll the dices first");

            return;

        }

        this.client.performAction(new TerminateRoundStandardAction(this.client.getUsername()));

        this.localMatchController.flushActionsPerformed();

    }

    @FXML
    void placeFamilyMember(MouseEvent event) {

        if (!this.checkTurnEnabled()) return;

        //Check if the user can roll the dices
        if(!this.localMatchController.diceAreRolled()){

            this.showAsynchAlert(Alert.AlertType.ERROR, "Forbidden", "Dices not rolled", "You must roll the dices first");

            return;

        }

        if (this.actionPlaceImageViewCache.size() == 0) {

            this.buildCache();

        }

        if (!this.localMatchController.canPerformStandardAction(StandardActionType.FamilyMemberPlacement)) {

            this.showAsynchAlert(Alert.AlertType.ERROR, "Forbidden", "You cant place a family member again in this round", "Wait for the next round");

            return;

        }

        BoardSectorType boardSector = null;
        int index = 0;


        //If the action place clicked has a corresponding image view (aka has a card associated)
        if (this.actionPlaceImageViewCache.get(event.getSource()) != null) {

            //We need to find out the tower and the placement index

            ImageView relative = this.actionPlaceImageViewCache.get(event.getSource());
            DvptCard card = this.imageViewDvptCardCache.get(relative);

            if (card == null) {

                //The card has already been taken
                return;

            }

            boardSector = card.getBoardSector();
            index = this.getTowerPlacementIndex(card);

        }
        else if (event.getSource() instanceof Pane) {

            Pane pane = (Pane)event.getSource();

            if (pane.getId().equals("councilPalace")) {

                //We clicked on the council palace
                boardSector = BoardSectorType.CouncilPalace;

            }

        }

        StandardPlacementActionController controller = (StandardPlacementActionController) this.openNewStage(View.StandardPlacement);

        controller.setIndex(index);
        controller.setBoardSector(boardSector);
        controller.setClient(client);
        controller.setLocalMatchController(this.localMatchController);


    }

    private int getTowerPlacementIndex(DvptCard card) {

        try {

            return 3 - GridPane.getRowIndex(this.getImageViewFromDvptCard(card)) / 2;

        } catch (GuiException e) {

            Logger.log(Level.SEVERE, this.toString(), "Unable to find card relative to the provided card");

        }

        return 0;

    }

    private boolean checkTurnEnabled() {

        if (!this.localMatchController.hasTurn()) {

            this.showAsynchAlert(Alert.AlertType.ERROR, "Forbidden", "Not your turn", "You must wait for your turn to perform any action");

            return false;

        }

        return true;

    }


    @Override
    public void onDisconnection(Client client) {

    }

    @Override
    public void onLoginFailed(Client client, String reason) {

    }

    @Override
    public void onLoginSuccess(Client client) {

    }

    @Override
    public void onRegistrationSuccess(Client client) {

    }

    @Override
    public void onRegistrationFailed(Client client, String reason) {

    }


    @Override
    public void onNotification(Client sender, MatchNotification notification) {

    }

    @Override
    public void onModelUpdate(Client sender, Match model) {

        Logger.log(Level.FINE, this.toString(), "Model received");

        this.localMatchController.setMatch(model);

        Platform.runLater(() -> {

            GUIController.this.updatedDvptCardGrid(model);
            GUIController.this.updateSidebars(model);

        });



    }

    @Override
    public void onTurnEnabled(Client sender, Player player, String message) {

        //It it is our turn
        if (player.getUsername().equals(this.client.getUsername())) {

            Platform.runLater(() -> {
                this.turnIndicatorTextField.setText(player.getUsername() + ", is your turn!");
            });

            this.localMatchController.setTurnEnabled(true);

        }
        else {

            Platform.runLater(() -> {
                this.turnIndicatorTextField.setText(message);
            });


        }

    }

    private void showCouncilPrivilegeSelection() {

        CouncilPrivilegeSelectionController controller = (CouncilPrivilegeSelectionController) this.openNewStage(View.CouncilPrivilegeSelection);
        controller.setClient(client);
        controller.setLocalMatchController(this.localMatchController);

    }

    private void showFamilyMemberSelection() {

        SelectFamilyMemberController controller = (SelectFamilyMemberController) this.openNewStage(View.SelectFamilyMember);
        controller.setClient(client);
        controller.setLocalMatchController(this.localMatchController);

    }

    private void showCostSelection() {

        SelectFamilyMemberController controller = (SelectFamilyMemberController) this.openNewStage(View.SelectCost);
        controller.setClient(client);
        controller.setLocalMatchController(this.localMatchController);

    }


    private void showConversionSelection() {

        SelectFamilyMemberController controller = (SelectFamilyMemberController) this.openNewStage(View.SelectConversion);
        controller.setClient(client);
        controller.setLocalMatchController(this.localMatchController);

    }


    private void showDiscountOption() {

        SelectFamilyMemberController controller = (SelectFamilyMemberController) this.openNewStage(View.SelectDiscount);
        controller.setClient(client);
        controller.setLocalMatchController(this.localMatchController);

    }

    @Override
    public void onImmediateActionAvailable(Client sender, ImmediateActionType actionType, Player player, String message) {

        //It it is our turn
        if (player.getUsername().equals(this.client.getUsername())) {

            this.localMatchController.setLastPendingImmediateAction(actionType);

            switch (actionType) {

                case SelectCouncilPrivilege:
                    Platform.runLater(this::showCouncilPrivilegeSelection);
                    break;

                case SelectCost:
                    Platform.runLater(this::showCostSelection);
                    break;

                case SelectConversion:
                    Platform.runLater(this::showConversionSelection);
                    break;

                case SelectFamilyMember:
                    Platform.runLater(this::showFamilyMemberSelection);
                    break;

                case DecideDiscountOption:
                    Platform.runLater(this::showDiscountOption);
                    break;

            }

            this.showAsynchAlert(Alert.AlertType.INFORMATION, "Immediate action", "Immediate action available", message);

        }

    }

    @Override
    public void onTurnDisabled(Client sender, Player player, String message) {

        //It it is our turn
        if (player.getUsername().equals(this.client.getUsername())) {

            this.localMatchController.setTurnEnabled(false);

        }

    }

    @Override
    public void onTimeoutExpired(Client sender, Player player, String message) {

        //It it is our turn
        if (player.getUsername().equals(this.client.getUsername())) {

            this.showAsynchAlert(Alert.AlertType.WARNING, "Timeout expired", "Your move timeout expired", "Please reconnect to the game to continue playing");

        }

    }

    @Override
    public void onActionRefused(Client sender, Action action, String message) {

        this.showAsynchAlert(Alert.AlertType.WARNING, "Action refused", "The action you tried to perform was refused for the following reason:", message);

    }

    @Override
    public void onActionPerformed(Client sender, Player player, Action action, String message) {

        if (player.getUsername().equals(this.client.getUsername())) {

            if (action.getActionType() == ActionType.Standard) {

                this.localMatchController.confirmLastStandardPendingAction();

                this.showAsynchAlert(Alert.AlertType.CONFIRMATION, "Action performed", "Action performed", "Your action was performed successfully");


            } else {

                this.localMatchController.confirmLastPendingImmediateAction();

            }

        }

    }

    @Override
    public void onLeaderCardDraftRequest(Client sender, Deck<LeaderCard> cards, String message) {

    }

    @Override
    public void onBonusTileDraftRequest(Client sender, ArrayList<BonusTile> tiles, String message) {

    }

    @Override
    public String toString() {
        return "GUI Controller";
    }
}
