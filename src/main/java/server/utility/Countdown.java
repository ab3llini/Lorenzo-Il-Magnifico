package server.utility;

/*
 * @author  ab3llini
 * @since   18/05/17.
 */

import server.controller.network.Observable;

import java.util.ArrayList;

/**
 * A countdown.
 * Could be helpful in the lobby & player timeout implementation
 */
public class Countdown extends Thread implements Observable<CountdownObserver> {

    /**
     * The timeout of the countdown
     */
    private int timeout;

    /**
     * The observer list
     */
    ArrayList<CountdownObserver> observers = new ArrayList<CountdownObserver>();

    /**
     * Constructor
     * @param timeout the timeout
     */
    public Countdown(int timeout) {

        //Init
        this.timeout = timeout;

    }

    public boolean hasExpired() {

        return (this.timeout > 0);

    }

    @Override
    public void run() {

        while (this.timeout > 0) {

            this.timeout--;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        this.notifyExpiration();

    }

    public boolean addObserver(CountdownObserver o) {
        return this.observers.add(o);
    }

    public boolean removeObserver(CountdownObserver o) {
        return this.observers.remove(o);
    }

    private void notifyExpiration() {

        for (CountdownObserver o : this.observers) {

            o.onExpiration(this);

        }

    }
}
