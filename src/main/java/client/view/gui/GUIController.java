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
import client.view.cli.LocalMatchController;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import logger.Level;
import logger.Logger;
import netobject.action.Action;
import netobject.action.immediate.ImmediateActionType;
import netobject.notification.MatchNotification;
import netobject.notification.ObserverReadyNotification;
import server.model.Match;
import server.model.board.BonusTile;
import server.model.board.Player;
import server.model.board.TowerSlot;
import server.model.card.Deck;
import server.model.card.developement.DvptCard;
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



    //The reference to the local match controller
    private LocalMatchController localMatchController;

    HashMap<ImageView, DvptCard> imageViewDvptCardCache = new HashMap<>();

    HashMap<Circle, ImageView> actionPlaceImageViewCache = new HashMap<>();


    //The local client instance
    private Client client;


    public GUIController() {

        this.localMatchController = new LocalMatchController();

    }

    private void prepareDvptCardGrid() {



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
                this.imageViewDvptCardCache.replace(imgView, card);

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
            if ((pair.getValue()).equals(card)) {

                return (ImageView) pair.getKey();

            }
        }

        throw new GuiException("Unable to fetch an image view for the provided card");

    }

    private void updateSidebar(Match model) {

        try {
            this.woodValueTextField.setText(model.getPlayerFromUsername(this.client.getUsername()).getWood().toString());
            this.stonesValueTextField.setText(model.getPlayerFromUsername(this.client.getUsername()).getStones().toString());
            this.coinsValueTextField.setText(model.getPlayerFromUsername(this.client.getUsername()).getCoins().toString());
            this.servantsValueTextField.setText(model.getPlayerFromUsername(this.client.getUsername()).getServants().toString());
            this.victoryValueTextField.setText(model.getPlayerFromUsername(this.client.getUsername()).getVictoryPoints().toString());
            this.faithValueTextField.setText(model.getPlayerFromUsername(this.client.getUsername()).getFaithPoints().toString());
            this.militaryValueTextField.setText(model.getPlayerFromUsername(this.client.getUsername()).getMilitaryPoints().toString());

        } catch (NoSuchPlayerException e) {
            Logger.log(Level.SEVERE, this.toString(), "Player not found", e);
        }

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

            if (node instanceof ImageView) {

                this.imageViewDvptCardCache.put((ImageView)node, null);

            }

        }

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

    @FXML
    void onDvptCardClick(MouseEvent event) {

        ImageView clicked = (ImageView) event.getSource();

        try {

            this.showAsynchAlert(Alert.AlertType.CONFIRMATION, "Click", "Click on card", "You clicked on the card with id " + this.getDvptCardFromImageView(clicked));

        } catch (GuiException e) {

            this.showAsynchAlert(Alert.AlertType.ERROR, "GuiException", "Card not found", e.getMessage());


        }


    }

    @FXML
    void placeFamilyMember(MouseEvent event) {



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
    public void onNotification(Client sender, MatchNotification notification) {

    }

    @Override
    public void onModelUpdate(Client sender, Match model) {

        Logger.log(Level.FINE, this.toString(), "Model received");

        this.localMatchController.setMatch(model);

        Platform.runLater(() -> {

            GUIController.this.updatedDvptCardGrid(model);
            GUIController.this.updateSidebar(model);

        });



    }

    @Override
    public void onTurnEnabled(Client sender, Player player, String message) {

        //It it is our turn
        if (player.getUsername().equals(this.client.getUsername())) {

            Platform.runLater(() -> {
                this.turnIndicatorTextField.setText("It is your turn!");
            });

        }
        else {

            Platform.runLater(() -> {
                this.turnIndicatorTextField.setText(message);
            });

        }

    }

    @Override
    public void onImmediateActionAvailable(Client sender, ImmediateActionType actionType, Player player, String message) {

    }

    @Override
    public void onTurnDisabled(Client sender, Player player, String message) {

    }

    @Override
    public void onTimeoutExpired(Client sender, Player player, String message) {

    }

    @Override
    public void onActionRefused(Client sender, Action action, String message) {

    }

    @Override
    public void onActionPerformed(Client sender, Player player, Action action, String message) {

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
