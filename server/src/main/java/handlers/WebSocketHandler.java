package handlers;

import io.javalin.websocket.*;

public class WebSocketHandler implements WsConfig {
    
    @Override
    public void configure(WsConfig wsConfig) {
        wsConfig.onConnect(session -> {
            System.out.println("Connected to websocket");
        });
        wsConfig.onMessage(session -> {
            System.out.println("Message received from websocket");
        });
        wsConfig.onClose(session -> {
            System.out.println("Websocket closed");
        });
    }
}
