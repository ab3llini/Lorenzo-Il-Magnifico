package exception;

/**
 * Created by LBARCELLA on 23/05/2017.
 */

/**
 * This exception is raised whenever a player has not enough points to do an Action
 */
public class NotEnoughPointsException extends ActionException {

    public NotEnoughPointsException(String message){
        super(message);
    }
}
