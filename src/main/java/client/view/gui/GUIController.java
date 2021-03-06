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
import javafx.scene.control.ScrollPane;
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logger.Level;
import logger.Logger;
import netobject.action.Action;
import netobject.action.ActionType;
import netobject.action.BoardSectorType;
import netobject.action.immediate.ImmediateActionType;
import netobject.action.immediate.ImmediateChoiceAction;
import netobject.action.standard.RollDicesAction;
import netobject.action.standard.StandardActionType;
import netobject.action.standard.TerminateRoundStandardAction;
import netobject.notification.MatchNotification;
import netobject.notification.ObserverReadyNotification;
import server.model.Match;
import server.model.board.*;
import server.model.card.Deck;
import server.model.card.ban.BanCard;
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
    private GridPane leaderCardGrid;

    @FXML
    private ImageView personalTile;

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
    private GridPane territoryPersonalGrid;

    @FXML
    private GridPane buildingPersonalGrid;

    @FXML
    private Pane councilPalace;

    @FXML
    private ImageView marketFourImageView;

    @FXML
    private ImageView marketThreeImageView;

    @FXML
    private ImageView marketTwoImageView;

    @FXML
    private ImageView marketOneImageView;

    @FXML
    private ImageView singleHarvestImageView;

    @FXML
    private ImageView singleProductionImageView;

    @FXML
    private Pane compositeProduction;

    @FXML
    private Pane compositeHarvest;

    @FXML
    private Label notificationTextField;

    @FXML
    private Pane playersPane;

    @FXML
    private ImageView firstBanCard;

    @FXML
    private ImageView secondBanCard;

    @FXML
    private ImageView thirdBanCard;

    @FXML
    private ScrollPane allCardsPane;


    //The reference to the local match controller
    private LocalMatchController localMatchController;

    HashMap<ImageView, DvptCard> imageViewDvptCardCache = new HashMap<>();

    HashMap<ImageView, LeaderCard> imageViewLeaderCardCache = new HashMap<>();

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


        int col, row, towerIndex;

        for (Node node : this.dvptCardGrid.getChildren()) {

            if (GridPane.getColumnIndex(node) == null || GridPane.getRowIndex(node) == null) {

                System.out.println("Skipping node : " + node);

                continue;

            }
            else {

                col = GridPane.getColumnIndex(node);
                row = GridPane.getRowIndex(node);
                towerIndex = 3-row/2;

            }

            ArrayList<TowerSlot> tower = null;

            switch (col) {

                case TERRITORY_TOWER_COL:
                case TERRITORY_TOWER_COL + 1:
                    tower = this.localMatchController.getMatch().getBoard().getTerritoryTower();
                    break;
                case CHARACTER_TOWER_COL:
                case CHARACTER_TOWER_COL + 1:
                    tower = this.localMatchController.getMatch().getBoard().getCharacterTower();
                    break;
                case BUILDING_TOWER_COL:
                case BUILDING_TOWER_COL + 1:
                    tower = this.localMatchController.getMatch().getBoard().getBuildingTower();
                    break;
                case VENTURE_TOWER_COL:
                case VENTURE_TOWER_COL + 1:
                    tower = this.localMatchController.getMatch().getBoard().getVentureTower();
                    break;

            }

            //If the current node is an image view then..
            if (node instanceof ImageView) {

                ImageView imgView = (ImageView)node;

                //Take the card from the tower, if exists
                DvptCard card = tower.get(towerIndex).getDvptCard();

                if (card != null) {

                    //Assign the image
                    imgView.setImage(new Image("assets/cards/dvpt/devcards_f_en_c_" + card.getId() + ".png"));

                    //Round the borders
                    //this.clipImageForView(imgView);

                }
                else {

                    imgView.setImage(null);

                }


                //Put a new pair in the cache : image view -> dvpt card (might be null)
                this.imageViewDvptCardCache.put(imgView, card);

            }
            else if (node instanceof AnchorPane) {

                //Check whether or not to put a family member
                AnchorPane container = (AnchorPane)node;
                ImageView familyMemberImageView = (ImageView) container.getChildren().get(0);

                this.setSingleActionPlaceImageView(familyMemberImageView, tower.get(towerIndex));

            }

        }

    }

    private void setSingleActionPlaceImageView(ImageView view, SingleActionPlace place) {

        if (place.isOccupied() && place.getFamilyMember() != null) {

            //We need to add the family member to the image view
            view.setImage(this.getFamilyMemberImage(place.getFamilyMember().getColor(),place.getFamilyMember().getPlayerColor()));

        }
        else {

            view.setImage(null);

        }
    }

    private void setCompositeActionPlaceImageViews(Pane container, CompositeActionPlace place) {


        final int COUNT_MAX = 5;
        final int OFFSET = 40;
        final double FIT = 32;

        double startX = 80;
        double startY = container.getHeight() / 2 - FIT / 2;
        int count = 0;

        if (place.getFamilyMembers().size() == 0) {

            container.getChildren().clear();

        };

        //Display maximum 5 family members per pane
        for (FamilyMember member : place.getFamilyMembers()) {

            if (count >= COUNT_MAX ) return;

            ImageView imageView = new ImageView();
            imageView.setImage(this.getFamilyMemberImage(member.getColor(), member.getPlayerColor()));
            imageView.setFitHeight(FIT);
            imageView.setFitWidth(FIT);
            imageView.setPreserveRatio(true);

            container.getChildren().add(imageView);

            imageView.setLayoutX(startX + OFFSET * count);
            imageView.setLayoutY(startY);

            count++;

        }

    }

    private void updateFamilyMembers(Match model) {

        //Update all family member image views
        this.setSingleActionPlaceImageView(this.singleProductionImageView, model.getBoard().getProductionArea().getMainPlace());
        this.setSingleActionPlaceImageView(this.singleHarvestImageView, model.getBoard().getHarvestArea().getMainPlace());
        this.setSingleActionPlaceImageView(this.marketOneImageView, model.getBoard().getMarket().getMarketPlaces().get(0));
        this.setSingleActionPlaceImageView(this.marketTwoImageView, model.getBoard().getMarket().getMarketPlaces().get(1));
        this.setSingleActionPlaceImageView(this.marketThreeImageView, model.getBoard().getMarket().getMarketPlaces().get(2));
        this.setSingleActionPlaceImageView(this.marketFourImageView, model.getBoard().getMarket().getMarketPlaces().get(3));
        this.setCompositeActionPlaceImageViews(this.councilPalace, model.getBoard().getCouncilPalace());
        this.setCompositeActionPlaceImageViews(this.compositeHarvest, model.getBoard().getHarvestArea().getSecondaryPlace());
        this.setCompositeActionPlaceImageViews(this.compositeProduction, model.getBoard().getProductionArea().getSecondaryPlace());

    }

    private Image getFamilyMemberImage(ColorType memberColor, PlayerColor playerColor){

        return new Image("assets/members/fm_" +playerColor.toInt() + "_" + memberColor.getValue() + ".png");

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

    private void updateBanCard(Match model) {

        BanCard first = this.localMatchController.getMatch().getBoard().getCathedral().getBanCard(Period.first);
        BanCard second = this.localMatchController.getMatch().getBoard().getCathedral().getBanCard(Period.second);
        BanCard third = this.localMatchController.getMatch().getBoard().getCathedral().getBanCard(Period.third);

        this.firstBanCard.setImage(new Image("assets/cards/ban/excomm_" + Period.first.toInt() + "_" + first.getId() + ".png"));
        this.secondBanCard.setImage(new Image("assets/cards/ban/excomm_" + Period.second.toInt() + "_" + (second.getId() - 7) + ".png"));
        this.thirdBanCard.setImage(new Image("assets/cards/ban/excomm_" + Period.third.toInt() + "_" + (third.getId() - 14) + ".png"));


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

                Circle actionPlace = (Circle)((AnchorPane)node).getChildren().get(1);

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
    void onLeaderCardClick(MouseEvent event) {

        if (!this.checkMoveEnabled()) return;

        LeaderCard clicked = this.imageViewLeaderCardCache.get(event.getSource());

        if (clicked != null) {

            PlayerLeaderCardsController controller = (PlayerLeaderCardsController)this.openNewStage(View.LeaderCardActivationDiscard);
            controller.setClient(client);
            controller.setLocalMatchController(this.localMatchController);
            controller.setClicked(clicked);

        }

    }

    private void updateLeaderCards(Match model) {

        this.imageViewLeaderCardCache.clear();

        Player me = null;

        try {
            me = model.getPlayerFromUsername(this.client.getUsername());
        } catch (NoSuchPlayerException e) {
            e.printStackTrace();
        }

        ArrayList<LeaderCard> leaderCards = me.getLeaderCards();

        for (Node node : this.leaderCardGrid.getChildren()) {

            if (node instanceof ImageView) {

                ImageView imgView = (ImageView) node;

                if(me.getLeaderCards().size() > GridPane.getColumnIndex(node)){

                    LeaderCard card = leaderCards.get(GridPane.getColumnIndex(node));

                    this.imageViewLeaderCardCache.put(imgView, card);

                    if(card.getId() < 10){
                        //Assign the image
                        imgView.setImage(new Image("assets/cards/leader/leaders_f_c_0" + card.getId() + ".jpg"));}
                    else{
                        imgView.setImage(new Image("assets/cards/leader/leaders_f_c_" + card.getId() + ".jpg"));
                    }

                } else {

                    imgView.setImage(null);

                }

            }
        }
    }


    private void updatePersonalBoard(Match model) {

        Player me = null;
        try {
            me = model.getPlayerFromUsername(this.client.getUsername());
        } catch (NoSuchPlayerException e) {
            e.printStackTrace();
        }
        ArrayList<TerritoryDvptCard> territoryCard = me.getPersonalBoard().getTerritoryCards();
        ArrayList<BuildingDvptCard> buildingCard = me.getPersonalBoard().getBuildingCards();

        for (Node node : this.territoryPersonalGrid.getChildren()) {


            if (node instanceof ImageView) {

                ImageView imgView = (ImageView) node;

                if(me.getPersonalBoard().getTerritoryCards().size() > GridPane.getColumnIndex(node)){
                    DvptCard card = territoryCard.get(GridPane.getColumnIndex(node));


                    //Assign the image
                    imgView.setImage(new Image("assets/cards/dvpt/devcards_f_en_c_" + card.getId() + ".png"));


                } else {

                    imgView.setImage(null);

                }

            }
        }

        for (Node node : this.buildingPersonalGrid.getChildren()) {


            if (node instanceof ImageView) {

                ImageView imgView = (ImageView) node;

                if(me.getPersonalBoard().getBuildingCards().size() > GridPane.getColumnIndex(node)){
                    DvptCard card = buildingCard.get(GridPane.getColumnIndex(node));


                    //Assign the image
                    imgView.setImage(new Image("assets/cards/dvpt/devcards_f_en_c_" + card.getId() + ".png"));


                } else {
                    imgView.setImage(null);
                }
            }
        }
        personalTile.setImage(new Image("assets/tiles/personalbonustile_" + me.getPersonalBoard().getBonusTile().getId() + ".png"));

    }

    @FXML
    void onDiceRollClick(MouseEvent event) {

        if (!this.checkMoveEnabled()) return;


        if (!this.localMatchController.diceAreRolled() && this.localMatchController.canRollDices()) {

            this.client.performAction(new RollDicesAction(this.client.getUsername()));


        }
        else {

            this.showAsynchAlert(Alert.AlertType.WARNING, "Action refused", "You can't roll the dices!", "They are already rolled or you are not the first in this turn");

        }

    }

    @FXML
    void onTerminateRoundClick(MouseEvent event) {

        if (!this.checkMoveEnabled()) return;

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

        if (!this.checkMoveEnabled()) return;

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


        /* Find out where the click came from */

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

            else if (pane.getId().equals("compositeProduction")) {

                boardSector = BoardSectorType.CompositeProductionPlace;

            }

            else if (pane.getId().equals("compositeHarvest")) {

                boardSector = BoardSectorType.CompositeHarvestPlace;


            }

        }

        else if (event.getSource() instanceof Circle) {

            Circle circle = (Circle)event.getSource();

            if (circle.getId().equals("singleProduction")) {

                //We clicked on the council palace
                boardSector = BoardSectorType.SingleProductionPlace;

            }

            else if (circle.getId().equals("singleHarvest")) {

                boardSector = BoardSectorType.SingleHarvestPlace;

            }

            else if (circle.getId().equals("marketOne")) {

                boardSector = BoardSectorType.Market;
                index = 0;


            }
            else if (circle.getId().equals("marketTwo")) {

                boardSector = BoardSectorType.Market;
                index = 1;


            }
            else if (circle.getId().equals("marketThree")) {

                boardSector = BoardSectorType.Market;
                index = 2;

            }
            else if (circle.getId().equals("marketFour")) {

                boardSector = BoardSectorType.Market;
                index = 3;

            }
            else {

                Logger.log(Level.SEVERE, this.toString(), "Event not bound!");

                return;

            }

        }


        StandardPlacementActionController controller = (StandardPlacementActionController) this.openNewStage(View.StandardPlacement);

        controller.setIndex(index);
        controller.setBoardSector(boardSector);
        controller.setClient(client);
        controller.setLocalMatchController(this.localMatchController);


    }

    private void updatePlayers(Match model) {

        this.playersPane.getChildren().clear();

        final int OFFSET = 30;
        final double FIT = 20;

        double startX = 0;
        double startY;

        int count = 0;

        for (Player p : model.getRoundOrder()) {



            FamilyMember member = p.getSpecificFamilyMemberInfo(ColorType.Black);

            ImageView imageView = new ImageView();
            imageView.setImage(this.getFamilyMemberImage(member.getColor(), member.getPlayerColor()));
            imageView.setFitHeight(FIT);
            imageView.setFitWidth(FIT);
            imageView.setPreserveRatio(true);

            startY = this.playersPane.getHeight() - OFFSET * (model.getRoundOrder().size() - count);

            this.playersPane.getChildren().add(imageView);

            imageView.setLayoutX(startX);
            imageView.setLayoutY(startY);

            Label label;

            if (p.isDisabled()) {

                label = new Label(p.getUsername() + " - disabled");

                label.setStyle("-fx-text-fill: grey");

            }
            else {
                label = new Label(p.getUsername() + " - " + (count + 1) + "°");

                if (p.getUsername().equals(this.localMatchController.getMatch().getCurrentPlayer().getUsername())) {
                    label.setStyle("-fx-text-fill: #03A9F4");
                }
                else {
                    label.setStyle("-fx-text-fill: white");
                }

            }

            this.playersPane.getChildren().add(label);

            label.setLayoutX(startX + OFFSET);
            label.setLayoutY(startY);

            //add click observers
            imageView.setOnMouseClicked(event -> {
                this.showAlert(Alert.AlertType.INFORMATION, "Player info", p.getUsername(), p.toString());
            });

            label.setOnMouseClicked(event -> {
                this.showAlert(Alert.AlertType.INFORMATION, "Player info", p.getUsername(), p.toString2());
            });

            count++;

        }

        startY = this.playersPane.getHeight() - OFFSET * (model.getRoundOrder().size() + 2);

        Label info = new Label("Click on a player for info");

        info.setStyle("-fx-text-fill: white");

        this.playersPane.getChildren().add(info);

        info.setLayoutX(startX);
        info.setLayoutY(startY);


    }

    private void updateMyCards(Match model) {

        final double FIT = 200;
        final double UPPER_Y = this.allCardsPane.getHeight() / 4;
        final double LOWER_Y = this.allCardsPane.getHeight() / 4 * 3;

        final double OFFSET = 160;

        Player me = null;
        Pane root = new Pane();

        root.setLayoutY(UPPER_Y);
        root.setPrefHeight(LOWER_Y - UPPER_Y);
        root.setMinHeight(LOWER_Y - UPPER_Y);
        root.setMaxHeight(LOWER_Y - UPPER_Y);


        try {
             me = this.localMatchController.getMatch().getPlayerFromUsername(this.client.getUsername());
        } catch (NoSuchPlayerException e) {
            Logger.log(Level.SEVERE, this.toString(), "Unable to find player", e);
        }

        int count = 0;

        for (CharacterDvptCard c : me.getPersonalBoard().getCharacterCards()) {

            this.createImageForMyCards(root, FIT, OFFSET, count, c, 0);

            count++;

        }

        count = 0;

        for (VentureDvptCard c : me.getPersonalBoard().getVentureCards()) {

            this.createImageForMyCards(root, FIT, OFFSET, count, c, FIT + 20);

            count++;

        }

        count = 0;

        for (BanCard banCard : me.getBanCards()) {

            this.createBanImageForMyCards(root, FIT, OFFSET, count, banCard,  2 * FIT + 40);

            count++;

        }

        this.allCardsPane.setContent(root);

    }

    private void createBanImageForMyCards(Pane root, double FIT, double OFFSET, int count, BanCard c, double Y) {

        int id = c.getId();

        if (c.getPeriod() == 2) {

            id -= 7;

        }
        else if (c.getPeriod() == 3) {

            id -= 14;

        }

        ImageView imageView = new ImageView(new Image("assets/cards/ban/excomm_" + c.getPeriod() + "_" + id + ".png"));

        imageView.setFitHeight(FIT);
        imageView.setPreserveRatio(true);

        root.getChildren().add(imageView);

        imageView.setLayoutX(OFFSET * count);
        imageView.setLayoutY(Y);

        imageView.setStyle(" -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0 , 0, 0);");

    }

    private void createImageForMyCards(Pane root, double FIT, double OFFSET, int count, DvptCard c, double Y) {

        ImageView imageView = new ImageView(new Image("assets/cards/dvpt/devcards_f_en_c_" + c.getId() + ".png"));

        imageView.setFitHeight(FIT);
        imageView.setPreserveRatio(true);

        root.getChildren().add(imageView);

        imageView.setLayoutX(OFFSET * count);
        imageView.setLayoutY(Y);

        imageView.setStyle(" -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0 , 0, 0);");

    }

    private int getTowerPlacementIndex(DvptCard card) {

        try {

            return 3 - GridPane.getRowIndex(this.getImageViewFromDvptCard(card)) / 2;

        } catch (GuiException e) {

            Logger.log(Level.SEVERE, this.toString(), "Unable to find card relative to the provided card");

        }

        return 0;

    }



    private boolean checkMoveEnabled() {

        if (!this.localMatchController.hasTurn()) {

            this.showAsynchAlert(Alert.AlertType.ERROR, "Forbidden", "Not your turn", "You must wait for your turn to perform any action");

            return false;

        }

        if (this.localMatchController.getLastPendingImmediateAction() != null) {

            this.showAsynchAlert(Alert.AlertType.ERROR, "Forbidden", "Immediate action pending", "You must complete the immediate action first");

            return false;
        }

        return true;

    }


    @Override
    public void onDisconnection(Client client) {

        this.showAsynchAlert(Alert.AlertType.WARNING, "Connection lost", "Connection lost", "The server might have shut down.");


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

        Platform.runLater(() -> this.setNotificationText(notification.getMessage()));

    }

    @Override
    public void onModelUpdate(Client sender, Match model) {

        this.localMatchController.setMatch(model);

        Platform.runLater(() -> {

            this.updatedDvptCardGrid(model);
            this.updateSidebars(model);
            this.updatePersonalBoard(model);
            this.updateFamilyMembers(model);
            this.updateLeaderCards(model);
            this.updateBanCard(model);
            this.updatePlayers(model);
            this.updateMyCards(model);

        });



    }

    @Override
    public void onTurnEnabled(Client sender, Player player, String message) {


        String text = "";
        //It it is our turn
        if (player.getUsername().equals(this.client.getUsername())) {

            text = player.getUsername() + ", is your turn!";

            this.localMatchController.setTurnEnabled(true);

        }
        else {

            text = message;

        }

        text += "     -     Period = " + this.localMatchController.getMatch().getCurrentPeriod().toInt();
        text += ", Turn = " + this.localMatchController.getMatch().getCurrentTurn();
        text += ", Round = " + this.localMatchController.getMatch().getCurrentRound();

        final String lambda = text;

        Platform.runLater(() -> {
            this.turnIndicatorTextField.setText(lambda);
        });


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

        SelectCostController controller = (SelectCostController) this.openNewStage(View.SelectCost);
        controller.setClient(client);
        controller.setLocalMatchController(this.localMatchController);

    }


    private void showConversionSelection() {

        SelectConversionController controller = (SelectConversionController) this.openNewStage(View.SelectConversion);
        controller.setClient(client);
        controller.setLocalMatchController(this.localMatchController);

    }


    private void showDiscountOption() {

        DecideDiscountOptionController controller = (DecideDiscountOptionController) this.openNewStage(View.SelectDiscount);
        controller.setClient(client);
        controller.setLocalMatchController(this.localMatchController);

    }

    private void showBanOption() {

        DecideBanOptionController controller = (DecideBanOptionController) this.openNewStage(View.SelectBanOption);
        controller.setClient(client);
        controller.setLocalMatchController(this.localMatchController);

    }

    private void showHarvestActivation() {

        HarvestActivationController controller = (HarvestActivationController) this.openNewStage(View.ActivateHarvest);
        controller.setClient(client);
        controller.setLocalMatchController(this.localMatchController);

    }
    private void showProductionActivation() {

        ProductionActivationController controller = (ProductionActivationController) this.openNewStage(View.ActivateProduction);
        controller.setClient(client);
        controller.setLocalMatchController(this.localMatchController);

    }
    private void showLeaderSelection() {

        LeaderCardSelectionController controller = (LeaderCardSelectionController) this.openNewStage(View.LeaderCardSelection);
        controller.setClient(client);
        controller.setLocalMatchController(this.localMatchController);

    }

    @Override
    public void onImmediateActionAvailable(Client sender, ImmediateActionType actionType, Player player, String message) {

        //It it is our turn
        if (player.getUsername().equals(this.client.getUsername())) {

            this.localMatchController.setLastPendingImmediateAction(actionType);

            switch (actionType) {

                case ActivateHarvest:
                    Platform.runLater(this::showHarvestActivation);
                    break;

                case ActivateProduction:
                    Platform.runLater(this::showProductionActivation);
                    break;

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

                case DecideBanOption:
                    Platform.runLater(this::showBanOption);
                    break;

                case SelectActiveLeaderCard:
                    Platform.runLater(this::showLeaderSelection);
                    break;
            }

            this.showAsynchAlert(Alert.AlertType.INFORMATION, "Immediate action", "Immediate action available", message);

        }
        else {

            Platform.runLater(() -> this.setNotificationText(message));

        }

    }

    @Override
    public void onTurnDisabled(Client sender, Player player, String message) {

        //It it is our turn
        if (player.getUsername().equals(this.client.getUsername())) {

            this.localMatchController.setTurnEnabled(false);

        }
        else {
            Platform.runLater(() -> this.setNotificationText(message));
        }

    }

    @Override
    public void onTimeoutExpired(Client sender, Player player, String message) {

        //It it is our turn
        if (player.getUsername().equals(this.client.getUsername())) {

            this.showAsynchAlert(Alert.AlertType.WARNING, "Timeout expired", "Your move timeout expired", "Please reconnect to the game to continue playing");

        }
        else {
            Platform.runLater(() -> this.setNotificationText(message));
        }

    }

    @Override
    public void onActionRefused(Client sender, Action action, String message) {

        this.showAsynchAlert(Alert.AlertType.WARNING, "Action refused", "The action you tried to perform was refused for the following reason:", message);

        if (action.getActionType() == ActionType.Immediate) {

            if (action instanceof ImmediateChoiceAction) {

                //Remove the last immediate action from the stack
                this.localMatchController.confirmLastPendingImmediateAction();

            }

        }

    }

    @Override
    public void onActionPerformed(Client sender, Player player, Action action, String message) {

        if (player.getUsername().equals(this.client.getUsername())) {

            if (action.getActionType() == ActionType.Standard) {

                this.localMatchController.confirmLastStandardPendingAction();

                Platform.runLater(() -> {
                    this.setNotificationText("Your action was performed successfully");
                });


            } else {

                this.localMatchController.confirmLastPendingImmediateAction();

            }

        }
        else {

            Platform.runLater(() -> this.setNotificationText(message));

        }

    }

    @Override
    public void onLeaderCardDraftRequest(Client sender, Deck<LeaderCard> cards, String message) {

        if (cards.getCards().size() == 0) return;

        Platform.runLater(() -> {

            this.localMatchController.setDraftableLeaderCards(cards);

            //Open the stage to allow the user to select something
            LeaderCardDraftController controller = (LeaderCardDraftController) this.openNewStage(View.DraftLeaderCards);
            controller.setClient(client);
            controller.setLocalMatchController(this.localMatchController);

        });

    }

    @Override
    public void onBonusTileDraftRequest(Client sender, ArrayList<BonusTile> tiles, String message) {

        if (tiles.size() == 0) return;

        Platform.runLater(() -> {

            this.localMatchController.setDraftableBonusTiles(tiles);

            //Open the stage to allow the user to select something
            BonusTileDraftController controller = (BonusTileDraftController) this.openNewStage(View.DraftBonusTiles);
            controller.setClient(client);
            controller.setLocalMatchController(this.localMatchController);

        });

    }

    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);

        stage.setOnCloseRequest((WindowEvent e) -> {

            //Terminate the process upon closure
            System.exit(0);

        });

    }

    private void setNotificationText(String text) {

        this.notificationTextField.setText(text.replace("\n", ", ").replace("\r", ", "));

    }

    @Override
    public String toString() {
        return "GUI Controller";
    }
}