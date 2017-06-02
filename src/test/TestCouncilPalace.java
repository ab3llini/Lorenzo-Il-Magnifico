import org.junit.Test;
import server.model.board.ColorType;
import server.model.board.CouncilPalace;
import server.model.board.FamilyMember;
import server.model.board.Player;
import server.model.effect.EffectSurplus;

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

        CouncilPalace councilPalace = new CouncilPalace(new EffectSurplus(null,null,0),0,0);

        FamilyMember familyMember1 = new FamilyMember(player1, ColorType.Orange);
        FamilyMember familyMember2 = new FamilyMember(player2, ColorType.Black);
        FamilyMember familyMember3 = new FamilyMember(player3, ColorType.Nautral);
        FamilyMember familyMember4 = new FamilyMember(player3, ColorType.White);
        FamilyMember familyMember5 = new FamilyMember(player2, ColorType.Orange);
        FamilyMember familyMember6 = new FamilyMember(player1, ColorType.Black);
        FamilyMember familyMember7 = new FamilyMember(player3, ColorType.White);

        councilPalace.placeFamilyMember(familyMember2);
        councilPalace.placeFamilyMember(familyMember1);
        councilPalace.placeFamilyMember(familyMember3);
        councilPalace.placeFamilyMember(familyMember4);
        councilPalace.placeFamilyMember(familyMember5);
        councilPalace.placeFamilyMember(familyMember7);
        councilPalace.placeFamilyMember(familyMember6);

        assertEquals(player2, councilPalace.getCouncilPalaceOrder().get(0));
        assertEquals(player1, councilPalace.getCouncilPalaceOrder().get(1));
        assertEquals(player3, councilPalace.getCouncilPalaceOrder().get(2));



    }
}
