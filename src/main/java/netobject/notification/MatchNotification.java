package netobject.notification;

public class MatchNotification extends Notification {


    private final MatchNotificationType matchNotificationType;

    private final String message;

    public MatchNotification(MatchNotificationType matchNotificationType, String message) {

        super(NotificationType.Match);
        this.matchNotificationType = matchNotificationType;
        this.message = message;
    }

    public MatchNotificationType getMatchNotificationType() {
        return matchNotificationType;
    }

    public String getMessage() {
        return message;
    }
}
