package exception;

/**
 * Created by LBARCELLA on 16/05/2017.
 */

/**
 * This exception is raised whenever a player has not enough servants to do an Action
 */
public class NotEnoughServantsException extends NotEnoughResourcesException {

    public  NotEnoughServantsException(String message){

        super(message);
    }
}
