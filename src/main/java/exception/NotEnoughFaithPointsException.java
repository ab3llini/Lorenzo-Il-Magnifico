package exception;

/**
 * Created by LBARCELLA on 16/05/2017.
 */

/**
 * This exception is raised whenever a player has not enough faith points to do an action
 */
public class NotEnoughFaithPointsException extends Exception {

    public NotEnoughFaithPointsException(String message){
        super(message);
    }
}
