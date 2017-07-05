package server.model.card.ban;/*
 * Created by alberto on 09/05/17.
 */

import java.io.Serializable;

public abstract class BanCard implements Serializable {
    private Integer id;
    private Integer period;
    private BanType type;

    public BanCard(Integer id, Integer period,BanType type) {
        this.id = id;
        this.period = period;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public Integer getPeriod() {
        return period;
    }

    public BanType getType() {return type;}

    @Override
    public String toString() {

        String banString = "";

        banString += "ID : "+id+"\t";

        banString += "Period : "+this.getPeriod()+"\n";

        return banString;
    }
}
