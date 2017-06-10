package server.model.board;

import java.io.Serializable;

/**
 * Created by Federico on 11/05/2017.
 */
public enum ColorType implements Serializable {

    White("White", "1"),
    Orange("Orange", "2"),
    Black("Black", "3"),
    Nautral("Neutral", "4");


    private final String name;
    private final String value;

    ColorType(String name, String value) {

        this.name = name;
        this.value = value;

    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public ColorType toEnum() {

        for (ColorType t : ColorType.values()) {

            if (this.getName() == t.getName())

                return t;

        }

        return null;
    }
}
