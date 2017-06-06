package server.model.board;

import server.model.effect.EffectSurplus;
import server.utility.UnicodeChars;

import java.io.Serializable;

/**
 * Created by LBARCELLA on 18/05/2017.
 */
public class BonusTile implements Serializable {
    private Integer id;
    private Integer productionMinForce;
    private EffectSurplus productionSurplus;
    private Integer harvestMinForce;
    private EffectSurplus harvestSurplus;



    public BonusTile(Integer id, Integer productionMinForce, EffectSurplus productionSurplus, Integer harvestMinForce, EffectSurplus harvestSurplus){
        this.id=id;
        this.harvestMinForce= harvestMinForce;
        this.harvestSurplus = harvestSurplus;
        this.productionMinForce = productionMinForce;
        this.productionSurplus = productionSurplus;
    }

    public Integer getId() {
        return id;
    }

    public Integer getHarvestMinForce() {
        return harvestMinForce;
    }

    public EffectSurplus getHarvestSurplus() {
        return harvestSurplus;
    }

    public Integer getProductionMinForce() {
        return productionMinForce;
    }

    public EffectSurplus getProductionSurplus() {
        return productionSurplus;
    }

    @Override
    public String toString() {

        String tile="";

        tile += "BONUS TILE N°"+this.id+"\n";

        tile += "-->HARVEST "+ UnicodeChars.Harvest+"\n";

        tile += "   Minforce: "+this.harvestMinForce+"\n";

        tile += "   Surplus: "+this.getHarvestSurplus().toString()+"\n";

        tile += "-->PRODUCTION "+ UnicodeChars.Production+"\n";

        tile += "   Minforce: "+this.productionMinForce+"\n";

        tile += "   Surplus: "+this.getProductionSurplus().toString()+"\n";

        return tile;
    }
}
