package handlers;

import io.javalin.websocket.*;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;

import service.GameService;
import models.requests.LeaveGameRequest;
import models.results.LeaveGameResult;
import dataaccess.DataAccessException;

public class WebSocketHandler {

    private final Gson gson;
    private final GameService gameService;

    public WebSocketHandler(Gson gson, GameService gameService) {
        this.gson = gson;
        this.gameService = gameService;
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
            case LEAVE -> onLeave(command);
            case RESIGN -> onResign(command);
        }
    }

    private void onMakeMove(UserGameCommand command) {
        System.out.println("Making move: " + command.getGameID());
    }

    private void onLeave(UserGameCommand command) {
        try {
            LeaveGameRequest request = new LeaveGameRequest(command.getAuthToken(), command.getGameID());
            LeaveGameResult result = gameService.leaveGame(request);
            if (result.success()) {
                System.out.println("Successfully left game: " + command.getGameID());
            } else {
                System.out.println("Failed to leave game: " + command.getGameID());
            }
        } catch (DataAccessException e) {
            System.out.println("Error leaving game: " + e.getMessage());
        }
    }

    private void onResign(UserGameCommand command) {
        System.out.println("Resigning game: " + command.getGameID());
    }

    private void onError(WsErrorContext ctx) {
        ctx.error();
    }

    private void onClose(WsCloseContext ctx) {
        System.out.println("WebSocket closed");
    }
}
