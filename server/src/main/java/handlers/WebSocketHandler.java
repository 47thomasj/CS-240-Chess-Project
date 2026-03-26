package handlers;

import io.javalin.websocket.*;


public class WebSocketHandler {

    public void configure(WsConfig wsConfig) {
        wsConfig.onConnect(this::onConnect);
    }

    private void onConnect(WsConnectContext ctx) {
        
    }
}
