package server.model.card.ban;

/**
 * Created by LBARCELLA on 20/05/2017.
 */
public class SpecialBanCard extends BanCard {

    SpecialEffectType specialEffect;

    public SpecialBanCard(Integer id, Integer period, SpecialEffectType specialEffect) {
        super(id, period, BanType.special);
        this.specialEffect = specialEffect;
    }

    public SpecialEffectType getSpecialEffect() {
        return specialEffect;
    }
}
