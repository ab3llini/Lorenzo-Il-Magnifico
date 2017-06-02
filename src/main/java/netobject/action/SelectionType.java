package netobject.action;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */
public enum SelectionType {

    First(0),
    Second(1);
    int value;

    SelectionType(int value){
        this.value = value;
    }

    public int toInt(){
        return this.value;
    }


}
