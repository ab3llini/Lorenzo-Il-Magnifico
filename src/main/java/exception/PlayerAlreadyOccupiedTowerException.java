package exception;

/**
 * Created by LBARCELLA on 28/05/2017.
 */

/**
 * This exception is raised whenever a player already have a family member on the tower
 */
public class PlayerAlreadyOccupiedTowerException extends ActionException {

    public PlayerAlreadyOccupiedTowerException(String message) {
        super(message);
    }
}
