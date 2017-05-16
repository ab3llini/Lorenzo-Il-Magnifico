package server.model.board;

import server.model.Player;

/**
 * Created by Federico on 11/05/2017.
 */
public class FamilyMember {
    private Player player;
    private ColorType color;
    private Boolean busy;
    private Integer force;


    public FamilyMember (Player player, ColorType color){
        this.player = player;
        this.color = color;
        this.busy=false;
    }

    public ColorType getColor() {
        return color;
    }

    public Player getPlayer() {
        return player;
    }

    public Boolean isBusy() {
        return busy;
    }

    public void setForce(Integer force) {
        this.force = force;
    }

    public Integer getForce() {
        return force;
    }
}
