package exception;

/**
 * Created by LBARCELLA on 16/05/2017.
 */

/**
 * This exception is raised whenever a player has not enough coins to do an action
 */
public class NotEnoughCoinsException extends Exception {

    public NotEnoughCoinsException(String message) {

        super(message);

    }
}
