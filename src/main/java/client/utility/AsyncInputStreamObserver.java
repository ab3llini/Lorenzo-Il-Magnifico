package client.utility;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */
public interface AsyncInputStreamObserver {

    void onInput(AsyncInputStream stream, String value);

}
