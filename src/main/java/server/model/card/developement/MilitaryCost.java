package server.model.card.developement;

import java.io.Serializable;

/**
 * Created by Federico on 10/05/2017.
 */
public class MilitaryCost implements Serializable {
    private Integer required;
    private Integer malus;


    public MilitaryCost (Integer required, Integer malus){
        this.malus = malus;
        this.required = required;
    }

    public Integer getMalus() {
        return malus;
    }

    public Integer getRequired() {
        return required;
    }
}
