package server.model.board;

import java.io.Serializable;

/**
 * Created by Federico on 11/05/2017.
 */
public class FamilyMember implements Serializable {
    private PlayerColor playerColor;
    private ColorType color;
    private Boolean busy;
    private Integer force;


    public FamilyMember (PlayerColor playerColor, ColorType color){
        this.playerColor = playerColor;
        this.color = color;
        this.force = 0;
        this.busy=false;
    }

    public ColorType getColor() {
        return color;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
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

    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public void setColor(ColorType color) {
        this.color = color;
    }

    public Integer getForce() {
        return force;
    }
}
