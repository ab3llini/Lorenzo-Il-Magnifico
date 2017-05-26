package netobject.request.action;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */
public enum CostOptionType {

    First(0),
    Second(1);
    int value;

    CostOptionType(int value){
        this.value = value;
    }

    public int toInt(){
        return this.value;
    }


}
