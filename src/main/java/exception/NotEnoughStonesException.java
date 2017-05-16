package exception;

/**
 * Created by LBARCELLA on 16/05/2017.
 */

/**
 * This exception is raised whenever a player has not enough stones to do an action
 */

public class NotEnoughStonesException extends Exception {

    public  NotEnoughStonesException(String message){

        super(message);
    }

}
