package server.controller.network;

/*
 * @author  ab3llini
 * @since   17/05/17.
 */

public interface Observable<T> {

    /**
     * Adds an observer of type T
     * @param o The observer
     * @return True upon success, False otherwise
     */
    boolean addObserver(T o);

    /**
     * Removes the specified observer
     * @return True upon success, False otherwise
     */
    boolean removeObserver(T o);

}
