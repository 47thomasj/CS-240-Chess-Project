package handlers;

import io.javalin.websocket.*;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;


public class WebSocketHandler {

    private final Gson gson;

    public WebSocketHandler(Gson gson) {
        this.gson = gson;
    }

    public void configure(WsConfig wsConfig) {
        wsConfig.onConnect(this::onConnect);
        wsConfig.onMessage(this::onMessage);
        wsConfig.onError(this::onError);
        wsConfig.onClose(this::onClose);
    }

    private void onConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
    }

    private void onMessage(WsMessageContext ctx) {
        String message = ctx.message();
        System.out.println("WebSocket message: " + message);
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> {
                ctx.enableAutomaticPings();
            }
            case MAKE_MOVE -> onMakeMove(command);
            case LEAVE -> onLeave(ctx);
            case RESIGN -> onResign(ctx);
        }
    }

    private void onMakeMove(UserGameCommand command) {
        
    }

    private void onError(WsErrorContext ctx) {
        ctx.error();
    }

    private void onClose(WsCloseContext ctx) {
        System.out.println("WebSocket closed");
    }
}
