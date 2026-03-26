package websocket.messages;

/**
 * Sent by the server when a client issues an invalid command.
 * The {@code errorMessage} field must contain the word "error" (case-insensitive) per spec.
 */
public class ErrorMessage extends ServerMessage {

    private final String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        if (errorMessage == null || errorMessage.isBlank()) {
            throw new IllegalArgumentException("errorMessage must not be blank");
        }
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
