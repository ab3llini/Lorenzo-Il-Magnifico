package client.view.gui;

/**
 * Created by Alberto
 */

import client.view.LocalMatchController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import netobject.action.immediate.ImmediateChoiceAction;
import netobject.action.standard.ShuffleLeaderCardStandardAction;
import server.model.GameSingleton;
import server.model.board.Player;
import server.model.card.Deck;
import server.model.card.leader.LeaderCard;

import java.util.ArrayList;
import java.util.HashMap;

public class LeaderCardSelectionController extends DialogController {

    @FXML
    private Pane container;

    @Override
    public void setLocalMatchController(LocalMatchController localMatchController) {

        super.setLocalMatchController(localMatchController);

        double OFFSET = 20;
        double FIT = 100;

        int count = 0;

        for(Player player : localMatchController.getMatch().getPlayers()) {

            if(!player.getUsername().equals(localMatchController.getLocalPlayer().getUsername()))

                for (LeaderCard c : player.getPlayedLeaderCards()) {

                    ImageView imgView = new ImageView();

                    if(c.getId() < 10){
                        //Assign the image
                        imgView.setImage(new Image("assets/cards/leader/leaders_f_c_0" + c.getId() + ".jpg"));}
                    else{
                        imgView.setImage(new Image("assets/cards/leader/leaders_f_c_" + c.getId() + ".jpg"));
                    }

                    imgView.setFitHeight(100);
                    imgView.setPreserveRatio(true);
                    imgView.setLayoutX(OFFSET * count + FIT);

                    this.container.getChildren().add(imgView);

                    imgView.setOnMouseClicked(event -> {

                        this.client.performAction(new ImmediateChoiceAction(c.getId(), this.client.getUsername()));

                    });

                    count++;

                }


        }

    }
}
