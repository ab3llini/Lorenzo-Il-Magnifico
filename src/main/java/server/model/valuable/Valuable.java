package server.model.valuable;

import java.io.Serializable;

/**
 * Created by Federico on 10/05/2017.
 */
public class Valuable implements Serializable {

    private final Integer amount;

    public Valuable(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }
}
