package client.view.gui;

import client.controller.network.ObserverType;
import exception.NoSuchPlayerException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import client.controller.network.Client;
import client.controller.network.ClientObserver;
import client.controller.network.RemotePlayerObserver;
import client.view.cli.LocalMatchController;
import javafx.scene.input.MouseEvent;
import logger.Level;
import logger.Logger;
import netobject.action.Action;
import netobject.action.immediate.ImmediateActionType;
import netobject.notification.LobbyNotification;
import netobject.notification.MatchNotification;
import netobject.notification.ObserverReadyNotification;
import server.model.Match;
import server.model.board.BonusTile;
import server.model.board.Player;
import server.model.board.TowerSlot;
import server.model.card.Deck;
import server.model.card.developement.DvptCard;
import server.model.card.developement.DvptCardType;
import server.model.card.leader.LeaderCard;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * @author  ab3llini
 * @since   20/06/17.
 */
public class GUIController extends NavigationController implements ClientObserver, RemotePlayerObserver{

    @FXML
    private GridPane dvptCardGrid;

    @FXML
    private ImageView territory3;

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


    //The reference to the local match controller
    private LocalMatchController localMatchController;

    HashMap<DvptCardType, HashMap<ArrayList<DvptCard>, ImageView>> dvptCardCache = new HashMap<>();


    //The local client instance
    private Client client;


    public GUIController() {

        this.localMatchController = new LocalMatchController();

    }

    private void prepareDvptCardGrid() {



    }
    private Node updatedDvptCardGrid(Match model) {

        int col = 0, row = 0;

        for (Node node : this.dvptCardGrid.getChildren()) {

            System.out.println(node + " c = " + col + " r = " + row);

            if (row > 3) {

                row = 0;
                col++;

            }

            ArrayList<TowerSlot> tower = null;
            DvptCardType type;
            ImageView imgView = (ImageView)node;

            switch (col) {

                case 0:
                    type = DvptCardType.territory;
                    tower = this.localMatchController.getMatch().getBoard().getTerritoryTower();
                    break;
                case 1:
                    type = DvptCardType.character;
                    tower = this.localMatchController.getMatch().getBoard().getCharacterTower();
                    break;
                case 2:
                    type = DvptCardType.building;
                    tower = this.localMatchController.getMatch().getBoard().getBuildingTower();
                    break;
                case 3:
                    type = DvptCardType.venture;
                    tower = this.localMatchController.getMatch().getBoard().getVentureTower();
                    break;

            }

            imgView.setImage(new Image("assets/cards/dvpt/devcards_f_en_c_" + tower.get(3-row).getDvptCard().getId() + ".png"));

            row++;

        }


        return null;
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

    private ArrayList<DvptCard> getDvptCardsFromTower(ArrayList<TowerSlot> tower) {

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

    @FXML
    public void initialize() {

    }

    @FXML
    void onDvptCardClick(MouseEvent event) {

        this.showAsynchAlert(Alert.AlertType.ERROR, "Click", "Click on card", "You clicked on a card");

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

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                GUIController.this.updatedDvptCardGrid(model);
                GUIController.this.updateSidebar(model);

            }
        });



    }

    @Override
    public void onTurnEnabled(Client sender, Player player, String message) {

        Logger.log(Level.FINE, this.toString(), "Turn enabled!");

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
        return "GUI Controller for " + this.client.getUsername();
    }
}
