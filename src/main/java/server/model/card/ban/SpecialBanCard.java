package server.model.card.ban;

import java.io.Serializable;

/**
 * Created by LBARCELLA on 20/05/2017.
 */
public class SpecialBanCard extends BanCard implements Serializable {

    SpecialEffectType specialEffect;

    public SpecialBanCard(Integer id, Integer period, SpecialEffectType specialEffect) {
        super(id, period, BanType.special);
        this.specialEffect = specialEffect;
    }

    public SpecialEffectType getSpecialEffect() {
        return specialEffect;
    }

    @Override
    public String toString() {

        String banString = "";

        banString += super.toString();

        banString += specialEffect;

        return banString;
    }
}
