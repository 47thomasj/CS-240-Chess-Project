package websocket.messages;

import chess.ChessGame;

/**
 * Sent by the server to a client to deliver the current state of the game.
 * The {@code game} field name must remain "game" per the WebSocket protocol spec.
 */
public class LoadGameMessage extends ServerMessage {

    private final ChessGame game;

    public LoadGameMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        if (game == null) {
            throw new IllegalArgumentException("game must not be null");
        }
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }
}
