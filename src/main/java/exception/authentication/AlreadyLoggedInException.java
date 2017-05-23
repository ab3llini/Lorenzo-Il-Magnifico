package exception.authentication;

public class AlreadyLoggedInException extends AuthenticationException {

    public AlreadyLoggedInException(String message) {

        super(message);

    }

}
