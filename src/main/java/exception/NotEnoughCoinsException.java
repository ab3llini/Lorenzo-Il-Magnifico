package exception;

/**
 * Created by LBARCELLA on 16/05/2017.
 */

/**
 * This exception is raised whenever a player has not enough coins to do an Action
 */
public class NotEnoughCoinsException extends NotEnoughResourcesException {

    public NotEnoughCoinsException(String message) {

        super(message);

    }
}
