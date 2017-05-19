package server.utility;

/*
 * @author  ab3llini
 * @since   18/05/17.
 */
public interface CountdownObserver {

    /**
     * Triggered whenever a countdown expires
     * @param countdown the countdown
     */
    void onExpiration(Countdown countdown);

}
