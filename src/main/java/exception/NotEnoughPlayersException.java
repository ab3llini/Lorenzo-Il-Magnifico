package exception;

/**
 * Created by LBARCELLA on 23/05/2017.
 */
/**
 * This exception is raised whenever a match has not enough players to activate a particular place
 */
public class NotEnoughPlayersException extends ActionException {

    public NotEnoughPlayersException(String message) {
        super(message);
    }
}
