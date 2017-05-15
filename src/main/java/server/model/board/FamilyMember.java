package server.model.board;

import server.model.Player;

/**
 * Created by Federico on 11/05/2017.
 */
public class FamilyMember {
    private Player player;
    private ColorType color;


    public FamilyMember (Player player, ColorType color){
        this.player = player;
        this.color = color;
    }

    public ColorType getColor() {
        return color;
    }

    public Player getPlayer() {
        return player;
    }
}
