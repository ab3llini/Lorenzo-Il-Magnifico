package server.controller.network.RMI;

/*
 * Created by alberto on 10/05/17.
 */

import client.controller.network.RMI.RMIClientInterface;
import exception.ActionException;
import logger.Level;
import logger.Logger;
import netobject.NetObject;
import netobject.notification.Notification;
import server.controller.network.ClientHandler;
import server.controller.network.ClientHandlerObserver;
import server.controller.network.Observable;
import server.model.Match;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class RMIClientHandler extends ClientHandler implements Observable<ClientHandlerObserver> {

    /**
     * The reference to the client interface
     */
    private RMIClientInterface clientRef;

    /**
     * The connection token
     */
    private String token;

    /**
     * Heartbeat system delay constant
     */
    private static int HEARTBEAT_DELAY =  2000;

    /**
     * Heartbeat system status variable
     */
    private boolean response = false;

    //The observer list
    protected ArrayList<ClientHandlerObserver> observers = new ArrayList<ClientHandlerObserver>();

    /**
     * Constructor
     * @param clientRef The proxy reference to the client
     */

    public RMIClientHandler(RMIClientInterface clientRef, String token) {

        //Assign the reference to the RMI Client in order to make callbacks
        this.clientRef = clientRef;

        this.token = token;
    }

    /**
     * Heartbeat system to detect client disconnection
     */
    public synchronized void run() {

        while(true) {

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (RMIClientHandler.this) {

                        if (!RMIClientHandler.this.response) {

                            RMIClientHandler.this.notifyDisconnection();

                        }

                        RMIClientHandler.this.notify();

                    }
                }
            }, HEARTBEAT_DELAY);

            try {

                synchronized (this) {

                    response = this.clientRef.heartbeat();

                    this.wait();

                }


            } catch (InterruptedException e) {

                Logger.log(Level.FINEST, "RMIClientHandler", "Thread terminated");

                break;

            } catch (RemoteException e) {

                //If we catch this remote exception it means that probably the client went down
                if (e instanceof ConnectException) {

                    //Went down
                    this.notifyDisconnection();

                }
                break;

            }


        }

    }

    public String getToken() {
        return token;
    }

    /* Observable<ClientHandlerObserver> implementation */
    public boolean addObserver(ClientHandlerObserver o) {

        //Check whether the observer is not null
        if (o != null) {
            this.observers.add(o);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean removeObserver(ClientHandlerObserver o) {

        return this.observers.remove(o);

    }

    public final void notifyDisconnection() {

        for (ClientHandlerObserver o : this.observers) {

            o.onDisconnect(this);

        }

    }


    public void sendNotification(Notification not) {

        try {

            this.clientRef.notify(not);

        } catch (RemoteException e) {

            Logger.log(Level.SEVERE, "RMIClientHandler", "Unable to send notification", e);
        }

    }

    public void updateModel(Match model) {

    }

    public void notifyMoveEnabled() {

    }

    public void notifyMoveDisabled() {

    }

    public void notifyMoveTimeoutExpired() {

    }

    public void notifyActionRefused(ActionException exception) {

    }
}
