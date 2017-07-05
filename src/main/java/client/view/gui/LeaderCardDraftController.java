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
import netobject.action.standard.ShuffleLeaderCardStandardAction;
import server.model.card.Deck;
import server.model.card.leader.LeaderCard;

import java.util.HashMap;

public class LeaderCardDraftController extends DialogController {

    @FXML
    private ImageView firstLeaderCard;

    @FXML
    private ImageView secondLeaderCard;

    @FXML
    private ImageView thirdLeaderCard;

    @FXML
    private ImageView fourthLeaderCard;

    private HashMap<Integer, ImageView> deckCache;

    private Integer selection = 0;

    private Deck<LeaderCard> draftable;

    @FXML
    void onLeaderCardSelection(MouseEvent event) {

        Integer clicked = GridPane.getColumnIndex((Node) event.getSource());

        if (clicked + 1 > draftable.getCards().size()) return;

        selection = clicked;

        //Send the action
        this.client.performAction(new ShuffleLeaderCardStandardAction(selection, this.localMatchController.getDraftableLeaderCards(), this.client.getUsername()));

        //Close the stage
        stage.close();

    }

    @FXML
    void initialize() {

        //Build a cache for further usage
        this.deckCache = new HashMap<>();
        this.deckCache.put(0, firstLeaderCard);
        this.deckCache.put(1, secondLeaderCard);
        this.deckCache.put(2, thirdLeaderCard);
        this.deckCache.put(3, fourthLeaderCard);

    }

    @Override
    public void setLocalMatchController(LocalMatchController localMatchController) {
        super.setLocalMatchController(localMatchController);

        draftable = this.localMatchController.getDraftableLeaderCards();

        //Display the cards
        for (int index = 0; index < draftable.getCards().size(); index++) {

            Integer id = draftable.getCards().get(index).getId();
            String accessCode = (draftable.getCards().get(index).getId() > 9) ? id.toString() : "0" + id.toString();
            String url = "assets/cards/leader/leaders_f_c_" + accessCode + ".jpg";
            this.deckCache.get(index).setImage(new Image(url));

        }

        stage.setOnCloseRequest((WindowEvent e) -> {
            //We send the first choice always before closing the stage!
            this.client.performAction(new ShuffleLeaderCardStandardAction(selection, this.localMatchController.getDraftableLeaderCards(), this.client.getUsername()));

        });

    }
}
