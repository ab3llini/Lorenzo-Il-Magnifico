import org.junit.Assert;
import org.junit.Test;
import server.controller.game.MatchController;
import server.model.board.Player;
import server.model.card.developement.DvptCard;

import java.util.ArrayList;

/*
 * @author  ab3llini
 * @since   01/06/17.
 */
public class ControllerLogic {

    MatchController mc;

    @Test
    public void prepTowers() {

        mc.getBoardController().updateTowersForTurn(1,1);

    }

    @Test
    public void init() {

        ArrayList<Player> players = new ArrayList<Player>();

        players.add(new Player("Alberto"));
        players.add(new Player("Lorenzo"));
        players.add(new Player("Federico"));

        mc = new MatchController(players, 5);

        mc.getBoardController().updateTowersForTurn(1, 1);

        DvptCard exp = mc.getMatch().getBoard().getBuildingTower().get(0).getDvptCard();
        DvptCard exp2 = mc.getMatch().getBoard().getBuildingTower().get(1).getDvptCard();

        DvptCard exp3 = mc.getMatch().getBoard().getBuildingTower().get(2).getDvptCard();
        DvptCard exp4 = mc.getMatch().getBoard().getBuildingTower().get(3).getDvptCard();



        for (int i = 0; i <= 300; i++) {

            this.prepTowers();

        }

        Assert.assertEquals(exp , mc.getMatch().getBoard().getBuildingTower().get(0).getDvptCard());
        Assert.assertEquals(exp2 , mc.getMatch().getBoard().getBuildingTower().get(1).getDvptCard());
        Assert.assertEquals(exp3 , mc.getMatch().getBoard().getBuildingTower().get(2).getDvptCard());
        Assert.assertEquals(exp4 , mc.getMatch().getBoard().getBuildingTower().get(3).getDvptCard());

    }

}
