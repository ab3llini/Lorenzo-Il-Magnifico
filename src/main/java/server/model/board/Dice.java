package server.model.board;

/**
 * Created by Federico on 11/05/2017.
 */
public class Dice {
    private ColorType color;
    private Integer value;

    public Dice (ColorType color){
        this.color = color;
        this.value = value;
    }

    public ColorType getColor() {
        return color;
    }

    public Integer getValue() {
        return value;
    }
}
