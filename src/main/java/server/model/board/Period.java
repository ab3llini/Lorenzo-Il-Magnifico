package server.model.board;


/**
 * Created by LBARCELLA on 18/05/2017.
 */
public enum Period {
    first(1),
    second(2),
    third(3);
    int value;

    Period(int value){
        this.value = value;
    }

    public int toInt(){
        return this.value;
    }

    /**
     * this method return the next element of this enum
     * @param period
     * @return
     */
    public static Period next(Period period) {

        if(period==first)
            return second;
        if(period==second)
            return third;
        else
            return first;
        }

    public static Period toEnum(int num){

        if(num == 1)
            return Period.first;

        if(num == 2)
            return Period.second;

        else
            return Period.third;
    }

}

