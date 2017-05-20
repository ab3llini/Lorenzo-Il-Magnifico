package server.model.card.ban;

/**
 * Created by LBARCELLA on 20/05/2017.
 */
public class SpecialBanCard extends BanCard {

    String specialEffect;

    public SpecialBanCard(Integer id, Integer period, String specialEffect) {
        super(id, period, BanType.special);
        this.specialEffect = specialEffect;
    }

    public String getSpecialEffect() {
        return specialEffect;
    }
}
