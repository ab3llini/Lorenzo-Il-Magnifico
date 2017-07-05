package server.model.card.ban;

import java.io.Serializable;

/**
 * Created by LBARCELLA on 20/05/2017.
 */
public class VictoryMalusBanCard extends BanCard implements Serializable {

    EffectVictoryMalus effectVictoryMalus;

    public VictoryMalusBanCard(Integer id, Integer period, EffectVictoryMalus effectVictoryMalus) {
        super(id, period, BanType.victoryMalus);
        this.effectVictoryMalus = effectVictoryMalus;
    }

    public EffectVictoryMalus getEffectVictoryMalus() {
        return effectVictoryMalus;
    }

    @Override
    public String toString() {

        String banString = "";

        banString += super.toString();

        banString += effectVictoryMalus.toString();

        return banString;
    }
}
