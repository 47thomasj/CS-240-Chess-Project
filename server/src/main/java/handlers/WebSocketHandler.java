package handlers;

import io.javalin.websocket.*;


public class WebSocketHandler {

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
        ctx.message();
    }

    private void onError(WsErrorContext ctx) {
        ctx.error();
    }

    private void onClose(WsCloseContext ctx) {
        ctx.reason();
    }
}
