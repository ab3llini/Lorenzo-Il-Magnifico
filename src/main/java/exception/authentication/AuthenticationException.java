package exception.authentication;

import java.io.Serializable;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */

public class AuthenticationException extends Exception implements Serializable {

    public AuthenticationException(String message) {

        super(message);

    }

}
