package Server.Model.Card.Ban;/*
 * Created by alberto on 09/05/17.
 */

public class BanCard {
    private Integer id;
    private Integer period;
    private BanEffect banEffect;

public BanCard(Integer id, Integer period, BanEffect banEffect) {
    this.id = id;
    this.period = period;
    this.banEffect = banEffect;
}

}