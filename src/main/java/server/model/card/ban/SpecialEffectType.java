package server.model.card.ban;

import java.io.Serializable;

/**
 * Created by LBARCELLA on 29/05/2017.
 */
public enum SpecialEffectType implements Serializable {

    noMarketMalus("You can’t place your Family Members in the Market action spaces"),
    servantsPowerMalus("You have to spend 2 servants to increase your action value by 1"),
    noFirstAction("Each round, you skip your first turn. You start taking actions from the second turn. When all players have taken all their turns, you may still place your last Family Member.");

    String description;

    SpecialEffectType(String description) {

        this.description = description;

    }

    @Override
    public String toString() {
        return description;
    }
}
