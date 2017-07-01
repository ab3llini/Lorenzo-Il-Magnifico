package client.view.gui.lobby;

import client.controller.network.*;
import client.view.gui.GUIController;
import client.view.gui.NavigationController;
import client.view.gui.View;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import netobject.notification.LobbyNotification;
import netobject.notification.ObserverReadyNotification;

/*
 * @author  ab3llini
 * @since   29/06/17.
 */
public class LobbyController extends NavigationController implements ClientObserver, LobbyObserver{


    @FXML
    private TableView<TableCellEntry> notificationTableView;

    @FXML
    private TableColumn<TableCellEntry, String> statusColumn;

    private ObservableList<TableCellEntry> notifications = FXCollections.observableArrayList();

    private Client client;

    @FXML
    public void initialize() {

        this.statusColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        this.notificationTableView.getItems().setAll(this.notifications);


    }

    private void newLogEntry(String entry) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                LobbyController.this.notifications.add(
                        new TableCellEntry(entry)
                );

                LobbyController.this.notificationTableView.getItems().setAll(LobbyController.this.notifications);
            }
        });

    }

    public void setClient(Client client) {

        this.client = client;
        this.client.addClientObserver(this);
        this.client.addLobbyObserver(this);
        this.client.sendNotification(new ObserverReadyNotification(ObserverType.Lobby));

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

        this.newLogEntry(not.getMessage());

        switch (not.getLobbyNotificationType()) {


            case MatchStart:
            case ResumeGame:
            case ResumeBonusTileDraft:
            case ResumeLeaderCardDraft:

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        ((GUIController)LobbyController.this.navigateTo(View.Gui)).setClient(LobbyController.this.client);

                    }
                });

                break;

        }


    }

}

