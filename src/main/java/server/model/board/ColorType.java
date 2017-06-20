package server.model.board;

import java.io.Serializable;

/**
 * Created by Federico on 11/05/2017.
 */
public enum ColorType implements Serializable {

    White("White", 1),
    Orange("Orange", 2),
    Black("Black", 3),
    Nautral("Neutral", 4);


    private final String name;
    private final int value;

    ColorType(String name, int value) {

        this.name = name;
        this.value = value;

    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

}
