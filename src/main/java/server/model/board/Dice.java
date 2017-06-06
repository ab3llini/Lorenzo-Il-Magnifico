package server.model.board;

import java.io.Serializable;

/**
 * Created by Federico on 11/05/2017.
 */
public class Dice implements Serializable {

    private ColorType color;
    private Integer value;

    private static final int DEFAULT_VALUE = 0;

    public Dice (ColorType color){
        this.color = color;
        this.value = DEFAULT_VALUE;
    }

    public ColorType getColor() {
        return color;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public void setColor(ColorType color) {
        this.color = color;
    }
}
