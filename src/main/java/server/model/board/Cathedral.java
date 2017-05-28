package server.model.board;

import server.model.card.ban.BanCard;

import java.util.HashMap;

/**
 * Created by LBARCELLA on 18/05/2017.
 */
public class Cathedral {

    private HashMap<Period,BanCard> banCards;

    //represent the minimum number of faith points to avoid excommunication in each period
    private Integer firstMinFaith;
    private Integer secondMinFaith;
    private Integer thirdMinFaith;

    public Cathedral(Integer firstMinFaith, Integer secondMinFaith, Integer thirdMinFaith){
        this.firstMinFaith = firstMinFaith;
        this.secondMinFaith = secondMinFaith;
        this.thirdMinFaith = thirdMinFaith;
        this.banCards = new HashMap<Period, BanCard>();
    }

    public Integer getFirstMinFaith() {
        return firstMinFaith;
    }

    public Integer getSecondMinFaith() {
        return secondMinFaith;
    }

    public Integer getThirdMinFaith() {
        return thirdMinFaith;
    }

    public void setBanCard(Period period, BanCard banCard){
        banCards.put(period,banCard);
    }

    public BanCard getBanCard(Period period){
        return banCards.get(period);
    }

}
