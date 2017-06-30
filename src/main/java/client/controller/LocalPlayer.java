package client.controller;


import netobject.action.Action;
import netobject.notification.Notification;

/*
 * @author  ab3llini
 * @since   02/06/17.
 */
public interface LocalPlayer {

    void performAction(Action action);

    void sendNotification(Notification notification);

}
