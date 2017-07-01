import org.junit.Test;
import server.model.board.*;
import server.model.effect.EffectSurplus;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by LBARCELLA on 02/06/2017.
 */
public class TestCouncilPalace {

    @Test

    public void councilPalaceOrderTest() {

        Player player1 = new Player("test1");
        Player player2 = new Player("test2");
        Player player3 = new Player("test3");

        player1.setColor(PlayerColor.Black);
        player2.setColor(PlayerColor.Yellow);
        player3.setColor(PlayerColor.Green);

        FamilyMember familyMember1 = new FamilyMember(player1.getColor(), ColorType.Orange);
        FamilyMember familyMember2 = new FamilyMember(player2.getColor(), ColorType.Black);
        FamilyMember familyMember3 = new FamilyMember(player3.getColor(), ColorType.Nautral);
        FamilyMember familyMember4 = new FamilyMember(player3.getColor(), ColorType.White);
        FamilyMember familyMember5 = new FamilyMember(player2.getColor(), ColorType.Orange);
        FamilyMember familyMember6 = new FamilyMember(player1.getColor(), ColorType.Black);
        FamilyMember familyMember7 = new FamilyMember(player3.getColor(), ColorType.White);

        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);

        Board board = new Board(players);


        board.getCouncilPalace().placeFamilyMember(familyMember2);
        board.getCouncilPalace().placeFamilyMember(familyMember1);
        board.getCouncilPalace().placeFamilyMember(familyMember3);
        board.getCouncilPalace().placeFamilyMember(familyMember4);
        board.getCouncilPalace().placeFamilyMember(familyMember5);
        board.getCouncilPalace().placeFamilyMember(familyMember7);
        board.getCouncilPalace().placeFamilyMember(familyMember6);

        assertEquals(player2, board.getCouncilPalaceOrder().get(0));
        assertEquals(player1, board.getCouncilPalaceOrder().get(1));
        assertEquals(player3, board.getCouncilPalaceOrder().get(2));



    }
}
