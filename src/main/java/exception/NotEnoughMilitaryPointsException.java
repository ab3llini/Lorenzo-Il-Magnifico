package exception;

/**
 * Created by LBARCELLA on 16/05/2017.
 */

/**
 * This exception is raised whenever a player has not enough military points to do an Action
 */
public class NotEnoughMilitaryPointsException extends NotEnoughPointsException {

    public NotEnoughMilitaryPointsException(String message){
        super(message);
    }
}
