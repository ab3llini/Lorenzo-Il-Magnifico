package exception;

/**
 * Created by LBARCELLA on 16/05/2017.
 */

/**
 * This exception is raised whenever a player has not enough faith points to do an Action
 */
public class NotEnoughFaithPointsException extends NotEnoughPointsException {

    public NotEnoughFaithPointsException(String message){
        super(message);
    }
}
