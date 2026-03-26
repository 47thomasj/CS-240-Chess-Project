package routers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import websocket.commands.UserGameCommand;

public class WebSocketRouter {

    private final String wsUrl;
    private final Gson gson;
    private final HttpClient client;

    private volatile WebSocket socket;

    /**
     * Optional callback invoked for every server message received after the
     * initial CONNECT handshake completes.  Set this before calling connect().
     */
    private Consumer<String> messageHandler;

    public WebSocketRouter(String serverUrl, Gson gson, HttpClient client) {
        if (serverUrl == null || serverUrl.isBlank()) {
            throw new IllegalArgumentException("serverUrl must be non-blank");
        }
        if (gson == null || client == null) {
            throw new IllegalArgumentException("gson and client must not be null");
        }
        this.gson = gson;
        this.client = client;
        this.wsUrl = toWebSocketBaseUrl(serverUrl);
    }

    public void setMessageHandler(Consumer<String> handler) {
        this.messageHandler = handler;
    }

    /**
     * Opens a WebSocket connection, sends a CONNECT command, and blocks until
     * the server responds with LOAD_GAME (success) or an ERROR (failure).
     *
     * @return true if the server acknowledged the connection with LOAD_GAME
     */
    public boolean connect(String authToken, int gameID) {
        if (authToken == null || authToken.isBlank()) {
            throw new IllegalArgumentException("authToken must be non-blank");
        }
        if (gameID <= 0) {
            throw new IllegalArgumentException("gameID must be > 0");
        }

        closeExistingSocket();

        UserGameCommand connectCommand =
                new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        String connectBody = gson.toJson(connectCommand);
        CompletableFuture<Boolean> connectOutcome = new CompletableFuture<>();

        ConnectListener listener = new ConnectListener(connectBody, connectOutcome);
        try {
            WebSocket ws = client.newWebSocketBuilder()
                    .buildAsync(URI.create(wsUrl + "/ws"), listener)
                    .get(5, TimeUnit.SECONDS);
            this.socket = ws;
        } catch (Exception e) {
            return false;
        }

        try {
            return connectOutcome.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Sends a command over the open WebSocket.
     *
     * @throws IllegalStateException if not currently connected
     */
    public void sendCommand(UserGameCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command must not be null");
        }
        if (socket == null) {
            throw new IllegalStateException("WebSocket is not connected");
        }
        socket.sendText(gson.toJson(command), true);
    }

    /** Closes the WebSocket with a normal closure code. */
    public void disconnect() {
        closeExistingSocket();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void closeExistingSocket() {
        if (socket != null) {
            socket.sendClose(WebSocket.NORMAL_CLOSURE, "Reconnecting");
            socket = null;
        }
    }

    private static String toWebSocketBaseUrl(String url) {
        if (url.startsWith("http://")) {
            return "ws://" + url.substring("http://".length());
        }
        if (url.startsWith("https://")) {
            return "wss://" + url.substring("https://".length());
        }
        if (url.startsWith("ws://") || url.startsWith("wss://")) {
            return url;
        }
        throw new IllegalArgumentException("Unsupported serverUrl scheme: " + url);
    }

    // -------------------------------------------------------------------------
    // Inner listener — kept as a named class to stay within indent limits
    // -------------------------------------------------------------------------

    private class ConnectListener implements WebSocket.Listener {

        private final String connectBody;
        private final CompletableFuture<Boolean> connectOutcome;
        private final StringBuilder buffer = new StringBuilder();

        ConnectListener(String connectBody, CompletableFuture<Boolean> connectOutcome) {
            assert connectBody != null : "connectBody must not be null";
            assert connectOutcome != null : "connectOutcome must not be null";
            this.connectBody = connectBody;
            this.connectOutcome = connectOutcome;
        }

        @Override
        public void onOpen(WebSocket ws) {
            ws.request(1);
            ws.sendText(connectBody, true);
        }

        @Override
        public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
            buffer.append(data);
            ws.request(1);
            if (last) {
                processMessage(buffer.toString());
                buffer.setLength(0);
            }
            return null;
        }

        @Override
        public CompletionStage<?> onClose(WebSocket ws, int statusCode, String reason) {
            connectOutcome.complete(false);
            return null;
        }

        @Override
        public void onError(WebSocket ws, Throwable error) {
            connectOutcome.completeExceptionally(error);
        }

        private void processMessage(String message) {
            JsonObject obj = JsonParser.parseString(message).getAsJsonObject();
            if (obj == null || !obj.has("serverMessageType")) {
                return;
            }
            String type = obj.get("serverMessageType").getAsString();
            if (!connectOutcome.isDone()) {
                if ("LOAD_GAME".equals(type)) {
                    connectOutcome.complete(true);
                } else if ("ERROR".equals(type)) {
                    connectOutcome.complete(false);
                }
            }
            if (messageHandler != null) {
                messageHandler.accept(message);
            }
        }
    }
}
