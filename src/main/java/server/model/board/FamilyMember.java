package server.model.board;

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

    public void setForce(Integer force) {
        this.force = force;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Integer getForce() {
        return force;
    }
}
