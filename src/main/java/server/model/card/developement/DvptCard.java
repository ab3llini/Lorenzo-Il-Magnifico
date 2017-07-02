package server.model.card.developement;/*
 * Created by alberto on 09/05/17.
 */

import logger.AnsiColors;
import netobject.action.BoardSectorType;
import netobject.action.ImmediateBoardSectorType;
import server.model.effect.ActionType;
import server.model.effect.ImmediateEffect;
import server.model.effect.PermanentEffect;
import server.utility.UnicodeChars;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class DvptCard implements Serializable {

    private final Integer id;
    private final DvptCardType type;
    private final String name;
    private final Integer period;
    private final ImmediateEffect immediateEffect;
    private final PermanentEffect permanentEffect;


    protected DvptCard(Integer id, DvptCardType type, String name, Integer period, ImmediateEffect immediateEffect, PermanentEffect permanentEffect) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.period = period;
        this.immediateEffect = immediateEffect;
        this.permanentEffect = permanentEffect;
    }

    public Integer getId() {
        return id;
    }

    public Integer getPeriod() {
        return period;
    }

    public DvptCardType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Cost> getCost(){
        return null;
    }

    public PermanentEffect getPermanentEffect() {
        return permanentEffect;
    }

    public ImmediateEffect getImmediateEffect() {
        return immediateEffect;
    }

    @Override
    public String toString() {

        String card = "";

        if(type == DvptCardType.territory)
            card += (AnsiColors.ANSI_GREEN + this.name.toUpperCase()+" ( " +this.id+" ) "+"\n" + AnsiColors.ANSI_RESET);

        if(type == DvptCardType.building)
            card += (AnsiColors.ANSI_YELLOW + this.name.toUpperCase()+" ( " +this.id+" ) "+"\n" + AnsiColors.ANSI_RESET);

        if(type == DvptCardType.character)
            card += (AnsiColors.ANSI_BLUE + this.name.toUpperCase()+" ( " +this.id+" ) "+"\n" + AnsiColors.ANSI_RESET);

        if(type == DvptCardType.venture)
            card += (AnsiColors.ANSI_PURPLE + this.name.toUpperCase()+" ( " +this.id+" ) "+"\n" + AnsiColors.ANSI_RESET);


        card += "Type : "+this.type+"\n";

        card += "Period : "+this.period+"\n";

        if(getCost()!=null) {
            int i=1;

            for (Cost cost : this.getCost()) {
                card += i+"° "+"COST : " + this.getCost().get(i-1).toString() + "\n";
                i++;
            }

        }


        card += "Immediate Effect "+UnicodeChars.Immediate+" : "+"\n"+this.getImmediateEffect().toString();

        card += "Permanent Effect "+UnicodeChars.Permanent+" : "+"\n"+this.getPermanentEffect().toString();

        return card;
    }

    public abstract BoardSectorType getBoardSector();

    public abstract ImmediateBoardSectorType getImmediateBoardSector();

}

