package server.model.board;

import server.model.card.ban.BanCard;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by LBARCELLA on 18/05/2017.
 */
public class Cathedral  implements Serializable {

    private HashMap<Period,BanCard> banCards;

    //represent the minimum number of faith points to avoid excommunication in each period
    private Integer firstMinFaith;
    private Integer secondMinFaith;
    private Integer thirdMinFaith;

    public Cathedral(Integer firstMinFaith, Integer secondMinFaith, Integer thirdMinFaith){
        this.firstMinFaith = firstMinFaith;
        this.secondMinFaith = secondMinFaith;
        this.thirdMinFaith = thirdMinFaith;
        this.banCards = new HashMap<>();
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

    public Integer getMinFaith(Period period){
        
        if(period == Period.first)
            return getFirstMinFaith();

        if(period == Period.second)
            return getSecondMinFaith();

        else
            return getThirdMinFaith();
    }


}
