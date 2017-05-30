package server.model.valuable;

import java.io.Serializable;

/**
 * Created by LBARCELLA on 12/05/2017.
 */
public class Multiplier implements Serializable {
    private MultipliedType what;
    private ResultType result;
    private Float coefficient;

    public Multiplier(MultipliedType what, ResultType result, Float coefficient){
        this.what=what;
        this.result=result;
        this.coefficient=coefficient;
    }

    public Float getCoefficient() {
        return coefficient;
    }

    public MultipliedType getWhat() {
        return what;
    }

    public ResultType getResult() {
        return result;
    }
}
