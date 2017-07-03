package exception.authentication;

/**
 * Created by LBARCELLA on 03/07/2017.
 */
public class RegistrationFailedException extends AuthenticationException {

    public RegistrationFailedException(String message) {
        super(message);
    }

}
