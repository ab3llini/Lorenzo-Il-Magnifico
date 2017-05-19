package server.model.board;


/**
 * Created by LBARCELLA on 18/05/2017.
 */
public enum Period {
    first,
    second,
    third;

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
}

