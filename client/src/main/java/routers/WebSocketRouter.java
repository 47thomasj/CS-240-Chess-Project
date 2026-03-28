package routers;

import java.net.URI;
import java.util.function.Consumer;

import com.google.gson.Gson;

import websocket.commands.UserGameCommand;

import jakarta.websocket.*;

@ClientEndpoint
public class WebSocketRouter {

    private final String wsUrl;
    private final WebSocketContainer webSocketContainer;
    private Session session;
    private final Gson gson;
    /**
     * Optional callback invoked for every server message received after the
     * initial CONNECT handshake completes.  Set this before calling connect().
     */
    private Consumer<String> messageHandler;

    public WebSocketRouter(Gson gson, WebSocketContainer webSocketContainer) {
        this.wsUrl = "ws://localhost:8080/ws";
        this.webSocketContainer = webSocketContainer;
        this.session = null;
        this.gson = gson;
    }

    public void setMessageHandler(Consumer<String> handler) {
        this.messageHandler = handler;
    }

    /**
     * 
     * @return true if the server acknowledged the connection with LOAD_GAME
     */
    public boolean connect(String authToken, int gameID) throws Exception {
        if (authToken == null || authToken.isBlank()) {
            throw new IllegalArgumentException("authToken must be non-blank");
        }
        if (gameID <= 0) {
            throw new IllegalArgumentException("gameID must be > 0");
        }

        URI uri = URI.create(wsUrl);
        this.session = webSocketContainer.connectToServer(this, uri);
        return true;
    }

    public void send(UserGameCommand command) throws Exception {
        if (session == null) {
            throw new Exception("Not connected to a server");
        }
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void close() throws Exception {
        if (session == null) {
            throw new Exception("Not connected to a server");
        }
        session.close();
    }

    @OnMessage
    public void onMessage(String message) {
        if (messageHandler != null) {
            messageHandler.accept(message);
        }
    }
    
    @OnError
    public void onError(Throwable t) {
        System.err.println("Error: " + t.getMessage());
    }
}
