package exception;

/**
 * Created by LBARCELLA on 16/05/2017.
 */
/**
 * This exception is raised whenever a player has not enough victory points to do an Action
 */
public class NotEnoughVictoryPointsException extends NotEnoughPointsException {

    public NotEnoughVictoryPointsException(String message){
        super(message);
    }
}
