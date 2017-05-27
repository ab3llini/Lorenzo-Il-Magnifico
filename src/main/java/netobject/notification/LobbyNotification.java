package netobject.notification;

/*
 * @author  ab3llini
 * @since   26/05/17.
 */
public class LobbyNotification extends Notification {


    private final LobbyNotificationType lobbyNotificationType;

    private final String message;

    public LobbyNotification(LobbyNotificationType lobbyNotificationType, String message) {

        super(NotificationType.Lobby);
        this.lobbyNotificationType = lobbyNotificationType;
        this.message = message;
    }

    public LobbyNotificationType getLobbyNotificationType() {
        return lobbyNotificationType;
    }

    public String getMessage() {
        return message;
    }
}
