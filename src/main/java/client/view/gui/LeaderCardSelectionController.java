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
import javafx.stage.WindowEvent;
import netobject.action.immediate.ImmediateChoiceAction;
import netobject.action.standard.ShuffleLeaderCardStandardAction;
import server.model.board.Player;
import server.model.card.Deck;
import server.model.card.leader.LeaderCard;

import java.util.ArrayList;
import java.util.HashMap;

public class LeaderCardSelectionController extends DialogController {

    @FXML
    private GridPane leaderCardGrid;


    private Integer selection = 0;

    private ArrayList<LeaderCard> activeLeaderCards;

    @FXML
    void onLeaderCardSelection(MouseEvent event) {

        Integer clicked = GridPane.getColumnIndex((Node) event.getSource());

        if (clicked + 1 > activeLeaderCards.size()) return;

        selection = clicked;

        //Send the action
        this.client.performAction(new ImmediateChoiceAction(selection, this.client.getUsername()));

        //Close the stage
        stage.close();

    }

    @FXML
    public void initialize(){
        for(Player player : this.localMatchController.getMatch().getPlayers()) {
            if(!player.getUsername().equals(localMatchController.getLocalPlayer().getUsername()))
                activeLeaderCards.addAll(player.getPlayedLeaderCards());
        }

        //Display the cards
        for (Node node : leaderCardGrid.getChildren()) {

            if (node instanceof ImageView) {

                ImageView imgView = (ImageView) node;

                if(activeLeaderCards.size() > GridPane.getColumnIndex(node)){

                    LeaderCard card = activeLeaderCards.get(GridPane.getColumnIndex(node));

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

        stage.setOnCloseRequest((WindowEvent e) -> {
            //We send the first choice always before closing the stage!
            this.client.performAction(new ImmediateChoiceAction(0, this.client.getUsername()));

        });

    }
}
