package routers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import websocket.commands.UserGameCommand;

public class WebSocketRouter {

    private final String wsUrl;
    private final Gson gson;
    private final HttpClient client;

    private volatile WebSocket socket;

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
    
    public boolean connect(String authToken, int gameID) {
        if (authToken == null || authToken.isBlank()) {
            throw new IllegalArgumentException("authToken must be non-blank");
        }
        if (gameID <= 0) {
            throw new IllegalArgumentException("gameID must be > 0");
        }

        if (this.socket != null) {
            this.socket.sendClose(WebSocket.NORMAL_CLOSURE, "Reconnecting");
            this.socket = null;
        }

        UserGameCommand connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        String connectBody = gson.toJson(connectCommand);

        CompletableFuture<Boolean> connectOutcome = new CompletableFuture<>();
        WebSocket webSocket;
        try {
            webSocket = client.newWebSocketBuilder();
        } catch (Exception e) {
            return false;
        }

        this.socket = webSocket;

        webSocket.sendText(connectBody, true);

        try {
            return connectOutcome.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            return false;
        } catch (Exception e) {
            return false;
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
        throw new IllegalArgumentException("Unsupported serverUrl: " + url);
    }
}