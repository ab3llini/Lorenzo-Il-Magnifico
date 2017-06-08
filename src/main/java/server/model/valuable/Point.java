package server.model.valuable;

import server.utility.UnicodeChars;

import java.io.Serializable;

/**
 * Created by Federico on 10/05/2017.
 */
public class Point extends Valuable implements Serializable {

    private PointType type;
    private Multiplier multiplier;

    public Point(PointType type,Integer amount, Multiplier multiplier) {
        super(amount);
        this.type = type;
        this.multiplier=multiplier;
    }


    public Multiplier getMultiplier() {
        return multiplier;
    }

    public PointType getType() {
        return type;
    }

    @Override
    public String toString() {
        String point= "";

        if(type == PointType.Victory){

            point += "Victory Points "+ UnicodeChars.VictoryPoints;

            point += " : ";

            point += this.getAmount();

        }

        if(type == PointType.Faith){

            point += "Faith Points "+UnicodeChars.FaithPoints;

            point += " : ";

            point += this.getAmount();
        }

        if(type == PointType.Military){

            point += "Military Points "+UnicodeChars.MilitaryPoints;

            point += " : ";

            point += this.getAmount();
        }

        if(multiplier!=null) {

            point+= "Multiplier->";
            point+= multiplier.getWhat()+" X"+multiplier.getCoefficient()+"= "+multiplier.getResult();
        }


        return point;
    }
}
