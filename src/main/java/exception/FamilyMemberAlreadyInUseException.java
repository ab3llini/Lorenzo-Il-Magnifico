package exception;

/**
 * Created by LBARCELLA on 16/05/2017.
 */

/**
 * This exception is raised whenever a player want to take a family member that is already in use
 */
public class FamilyMemberAlreadyInUseException extends ActionException {

    public FamilyMemberAlreadyInUseException(String message){

        super(message);
    }

}
