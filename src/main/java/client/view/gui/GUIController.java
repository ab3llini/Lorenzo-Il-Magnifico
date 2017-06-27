package client.view.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import client.controller.network.Client;
import client.controller.network.ClientObserver;
import client.controller.network.RemotePlayerObserver;
import client.view.cli.LocalMatchController;
import javafx.scene.input.MouseEvent;
import netobject.action.Action;
import netobject.action.immediate.ImmediateActionType;
import netobject.notification.LobbyNotification;
import netobject.notification.MatchNotification;
import server.model.Match;
import server.model.board.Board;
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

    @FXML
    public void initialize() {

    }

    @FXML
    void onDvptCardClick(MouseEvent event) {

        Platform.runLater(new Runnable() {
            @Override public void run() {
                GUIController.this.showAlert(Alert.AlertType.ERROR, "Click", "Click on card", "You clicked on a card");
            }
        });

    }

    //The reference to the local match controller
    private LocalMatchController localMatchController;

    HashMap<DvptCardType, HashMap<ArrayList<DvptCard>, ImageView>> dvptCardCache = new HashMap<>();


    //The local client instance
    private Client client;

    public GUIController() {

        this.localMatchController = new LocalMatchController();

    }

    private void loadDvptCards() {



    }

    private Node upodatedDvptCardGrid(Board board) {
        for (Node node : this.dvptCardGrid.getChildren()) {



        }
        return null;
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

        this.localMatchController.setPlayerUsername(this.client.getUsername());

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
    public void onLobbyNotification(Client client, LobbyNotification not) {

    }

    @Override
    public void onNotification(Client sender, MatchNotification notification) {

    }

    @Override
    public void onModelUpdate(Client sender, Match model) {

    }

    @Override
    public void onTurnEnabled(Client sender, Player player, String message) {

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
}
