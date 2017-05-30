package server.model.card.ban;

import server.model.card.developement.DvptCardType;

import java.io.Serializable;

/**
 * Created by LBARCELLA on 20/05/2017.
 */
public class NoVictoryBanCard extends BanCard implements Serializable {

    DvptCardType cardType;

    public NoVictoryBanCard(Integer id, Integer period,DvptCardType cardType) {
        super(id, period, BanType.noVictoryPoints);
        this.cardType = cardType;
    }

    public DvptCardType getCardType() {
        return cardType;
    }
}
