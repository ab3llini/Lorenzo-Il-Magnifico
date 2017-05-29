package exception;

/**
 * Created by LBARCELLA on 22/05/2017.
 */
/**
 * This exception is raised whenever a family member has not enough force to be positioned in the chosen place
 */
public class NotStrongEnoughException extends ActionException {

    public NotStrongEnoughException(String message){
        super(message);
    }
}
