package exception;

/**
 * Created by LBARCELLA on 24/05/2017.
 */

/**
 * This exception is raised whenever a place is already occupied by another family member
 */

public class PlaceOccupiedException extends ActionException {

    public PlaceOccupiedException(String message) {
        super(message);
    }
}
