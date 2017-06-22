package client.view.gui;

import client.controller.network.Client;
import client.view.cli.LocalMatchController;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import server.model.board.TowerSlot;
import server.model.card.developement.DvptCard;
import server.model.card.developement.DvptCardType;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * @author  ab3llini
 * @since   20/06/17.
 */
public class GUIController extends NavigationController {

    //The reference to the local match controller
    private LocalMatchController localMatchController;

    //The local client instance
    private Client client;

    public GUIController() {

        this.localMatchController = new LocalMatchController();

    }

    private void loadDvptCards() {

        HashMap<DvptCardType, ArrayList<DvptCard>> dvptCardCache = new HashMap<>();

        dvptCardCache.put(DvptCardType.territory, this.getDvptCardsFromTower(this.localMatchController.getMatch().getBoard().getTerritoryTower()));
        dvptCardCache.put(DvptCardType.character, this.getDvptCardsFromTower(this.localMatchController.getMatch().getBoard().getCharacterTower()));
        dvptCardCache.put(DvptCardType.building, this.getDvptCardsFromTower(this.localMatchController.getMatch().getBoard().getBuildingTower()));
        dvptCardCache.put(DvptCardType.venture, this.getDvptCardsFromTower(this.localMatchController.getMatch().getBoard().getVentureTower()));

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

    /**
     * FXML Implementation below
     */


    @FXML
    public void initialize() {

    }

    @FXML
    private GridPane dvptCardGrid;

    @FXML
    void onDvptCardClick(MouseEvent event) {


    }


}
