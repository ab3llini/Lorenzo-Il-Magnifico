package server.model.card.ban;

import javafx.scene.effect.Effect;

import java.io.Serializable;

/**
 * Created by LBARCELLA on 20/05/2017.
 */
public class DiceBanCard extends BanCard implements Serializable {

    EffectDiceMalus effectDiceMalus;

    public DiceBanCard(Integer id, Integer period, EffectDiceMalus effectDiceMalus) {
        super(id, period, BanType.dice);
        this.effectDiceMalus = effectDiceMalus;
    }

    public EffectDiceMalus getEffectDiceMalus() {
        return effectDiceMalus;
    }

    @Override
    public String toString() {

        String diceBanCardString = "";

        diceBanCardString += super.toString();

        diceBanCardString += effectDiceMalus.toString();

        return diceBanCardString;

    }
}
