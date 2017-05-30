package server.model.board;

import java.io.Serializable;

/**
 * Created by Federico on 11/05/2017.
 */
public class FamilyMember implements Serializable {
    private Player player;
    private ColorType color;
    private Boolean busy;
    private Integer force;


    public FamilyMember (Player player, ColorType color){
        this.player = player;
        this.color = color;
        this.force = 0;
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

    public void setBusy(Boolean busy) {
        this.busy = busy;
    }

    public void setForce(Integer force) {
        this.force = force;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setColor(ColorType color) {
        this.color = color;
    }

    public Integer getForce() {
        return force;
    }
}
