package client.utility;

/*
 * @author  ab3llini
 * @since   23/05/17.
 */


import server.controller.network.Observable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class is very useful for getting a keyboard input asynchronously, cli & similar gui will benefit from it
 * It is event based, therefore remember to register each observer
 */
public class AsyncInputStream extends Thread implements Observable<AsyncInputStreamObserver> {

    private ArrayList<AsyncInputStreamObserver> observers;

    public InputStream in;

    /**
     * The constructor
     * Receives a stream to watch in background
     * @param in the stream that will be watched
     */
    public AsyncInputStream(InputStream in) {

        //Init the stream
        this.in = in;

        //Init observers
        this.observers = new ArrayList<AsyncInputStreamObserver>();

    }

    /**
     * For every line that is sent onLobbyNotification the observers
     */
    @Override
    public void run() {

        Scanner scanner = new Scanner(this.in);

        while (scanner.hasNextLine()) {

            //Read next line, when available
            String mex = scanner.nextLine();

            //Notify the observers
            this.notifyInput(mex);

        }

        scanner.close();

    }

    /**
     * Notify observers of new input
     * @param input
     */
    private void notifyInput(String input) {

        for (AsyncInputStreamObserver o : this.observers) {

            o.onInput(this, input);

        }

    }

    public boolean addObserver(AsyncInputStreamObserver o) {
        return this.observers.add(o);
    }

    public boolean removeObserver(AsyncInputStreamObserver o) {
        return this.observers.remove(o);
    }
}