package exception;

/*
 * @author  ab3llini
 * @since   15/05/17.
 */

/**
 * This exception is raised whenever a client tries to call any remote method before being registered on the server.
 */
public class NotRegisteredException extends Exception {

    public NotRegisteredException(String message) {

        super(message);

    }

}

