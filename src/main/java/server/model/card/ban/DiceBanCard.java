package server.model.card.ban;

import javafx.scene.effect.Effect;

/**
 * Created by LBARCELLA on 20/05/2017.
 */
public class DiceBanCard extends BanCard {

    EffectDiceMalus effectDiceMalus;

    public DiceBanCard(Integer id, Integer period, EffectDiceMalus effectDiceMalus) {
        super(id, period, BanType.dice);
        this.effectDiceMalus = effectDiceMalus;
    }

    public EffectDiceMalus getEffectDiceMalus() {
        return effectDiceMalus;
    }
}
