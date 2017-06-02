package exception;

/**
 * Created by LBARCELLA on 16/05/2017.
 */

/**
 * This exception is raised whenever a player has not enough wood to do an Action
 */
public class NotEnoughWoodException extends NotEnoughResourcesException {

    public NotEnoughWoodException(String message) {

        super(message);

    }
}
