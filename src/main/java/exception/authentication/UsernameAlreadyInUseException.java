package exception.authentication;

import java.rmi.ServerException;

/**
 * This exceptions is raised whenever a client chooses a username already in use
 */
public class UsernameAlreadyInUseException extends AuthenticationException {

    public UsernameAlreadyInUseException(String message) {

        super(message);

    }

}
