package exception;

/**
 * Created by LBARCELLA on 28/05/2017.
 */

/**
 * This exception is raised whenever a player already have 6 card of one single type
 */
public class SixCardsLimitReachedException extends ActionException {

    public SixCardsLimitReachedException(String message) {
        super(message);
    }
}
