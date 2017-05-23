package server.controller.network;

/*
 * @author  ab3llini
 * @since   17/05/17.
 */

/**
 * This template describes a collection of methods that an observed element should implement
 * @param <T> The observer authenticationType
 */
public interface Observable<T> {

    /**
     * Adds an observer of authenticationType T
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
