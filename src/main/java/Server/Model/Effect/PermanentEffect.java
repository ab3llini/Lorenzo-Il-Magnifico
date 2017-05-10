package Server.Model.Effect;/*
 * Created by alberto on 09/05/17.
 */

import java.util.ArrayList;

public class PermanentEffect {
    private Integer minForce;
    private EffectSurplus surplus;
    private ArrayList<EffectConversion> conversion;
    private EffectAction action;
    private EffectDiscount discount;
    private boolean penality;


    public PermanentEffect (Integer minForce, EffectSurplus surplus, ArrayList<EffectConversion> conversion, EffectAction action, EffectDiscount discount, boolean penality){
        this.minForce = minForce;
        this.surplus = surplus;
        this.conversion = conversion;
        this.action = action;
        this.discount = discount;
        this.penality = penality;
    }

}
