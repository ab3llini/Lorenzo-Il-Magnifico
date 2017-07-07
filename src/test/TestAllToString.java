import javafx.scene.effect.Effect;
import org.junit.Test;
import server.model.GameSingleton;
import server.model.board.BonusTile;
import server.model.board.Player;
import server.model.card.ban.BanCard;
import server.model.card.developement.DvptCard;
import server.model.card.leader.LeaderCard;
import server.model.effect.EffectSurplus;
import server.model.valuable.Resource;
import server.utility.BoardConfigParser;
import server.utility.GameConfigParser;

import java.util.ArrayList;

/**
 * Created by LBARCELLA on 07/07/2017.
 */
public class TestAllToString {

    //lot of times toString generate nullPointerException!!

    @Test
    public void testToString() {

        ArrayList<DvptCard> dvptCards = GameSingleton.getInstance().getDvptCards();
        ArrayList<BanCard> banCards = GameSingleton.getInstance().getBanCards();
        ArrayList<LeaderCard> leaderCards = GameSingleton.getInstance().getLeaderCards();
        ArrayList<BonusTile> bonusTiles = GameSingleton.getInstance().getBonusTiles();
        ArrayList<EffectSurplus> surpluses = BoardConfigParser.getCouncilPrivilegeOptions();
        ArrayList<Resource> resources = BoardConfigParser.getInitialResource(1);
        Player player = new Player("test");

        player.toString();
        player.toString2();
        player.getPersonalBoard().toString();

        for (DvptCard card : dvptCards) {

            card.toString();

        }

        for (BanCard card : banCards) {

            card.toString();

        }


        for (LeaderCard card : leaderCards) {

            card.toString();

        }

        for (BonusTile tile : bonusTiles) {

            tile.toString();

        }

        for (EffectSurplus surplus : surpluses) {

            surplus.toString();

        }

        for (Resource resource : resources) {

            resource.toString();

        }



    }
}
