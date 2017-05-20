package server.model.card.ban;

/**
 * Created by LBARCELLA on 20/05/2017.
 */
public class VictoryMalusBanCard extends BanCard {

    EffectVictoryMalus effectVictoryMalus;

    public VictoryMalusBanCard(Integer id, Integer period, EffectVictoryMalus effectVictoryMalus) {
        super(id, period, BanType.victoryMalus);
        this.effectVictoryMalus = effectVictoryMalus;
    }

    public EffectVictoryMalus getEffectVictoryMalus() {
        return effectVictoryMalus;
    }
}
