package exception;

import java.rmi.ServerException;

/**
 * This exceptions is raised whenever a client chooses a username already in use
 */
public class UsernameAlreadyInUseException extends Exception {

    public UsernameAlreadyInUseException(String message) {

        super(message);

    }

}
