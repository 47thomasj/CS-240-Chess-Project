package websocket.messages;

/**
 * Sent by the server to inform players and observers of game events
 * (e.g. a player joined, made a move, left, or resigned).
 */
public class NotificationMessage extends ServerMessage {

    private final String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
