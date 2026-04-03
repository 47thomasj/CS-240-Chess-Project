package handlers;

import io.javalin.websocket.*;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;

import service.GameService;
import service.WsService;
import models.requests.LeaveGameRequest;
import models.requests.MakeMoveRequest;
import models.results.LeaveGameResult;
import dataaccess.DataAccessException;
import models.results.MakeMoveResult;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.GameData;

public class WebSocketHandler {

    private final Gson gson;
    private final GameService gameService;
    private final WsService wsService;
    private final HashMap<Integer, List<WsContext>> gameIdToContext;
    private final HashMap<String, WsContext> authTokenToContext;

    public WebSocketHandler(Gson gson, GameService gameService, WsService wsService, HashMap<Integer, List<WsContext>> gameIdToContext, HashMap<String, WsContext> authTokenToContext) {
        this.gson = gson;
        this.gameService = gameService;
        this.wsService = wsService;
        this.gameIdToContext = gameIdToContext;
        this.authTokenToContext = authTokenToContext;
    }

    public void configure(WsConfig wsConfig) {
        wsConfig.onMessage(this::onMessage);
        wsConfig.onError(this::onError);
        wsConfig.onClose(this::onClose);
    }

    private void onMessage(WsMessageContext ctx) {
        String message = ctx.message();
        System.out.println("WebSocket message: " + message);
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> onConnect(command, ctx, gameIdToContext);
            case MAKE_MOVE -> onMakeMove(command, gameIdToContext);
            case LEAVE -> onLeave(command, gameIdToContext);
            case RESIGN -> onResign(command);
        }
    }

    private void onConnect(UserGameCommand command, WsMessageContext ctx, HashMap<Integer, List<WsContext>> gameIdToContext) {
        ctx.enableAutomaticPings();
        
        try {
            String username = wsService.getUsername(command.getAuthToken());
            GameData game = wsService.getGame(command.getGameID());
            String notification = "\n" + username + " joined the game" + (game.gameName()) + " as " + (game.whiteUsername() != null ? "white" : "black");
            NotificationMessage notificationMessage = new NotificationMessage(notification);

            LoadGameMessage message = new LoadGameMessage(game.game());
            ctx.send(gson.toJson(message));

            List<WsContext> contexts = gameIdToContext.get(command.getGameID());
            if (contexts != null) {
                for (WsContext context : contexts) {
                    context.send(gson.toJson(notificationMessage));
                }
            }
        } catch (DataAccessException e) {
            System.out.println("Error sending notifications: " + e.getMessage());
            return;
        }

        gameIdToContext
            .computeIfAbsent(command.getGameID(), k -> new ArrayList<>())
            .add(ctx);
        authTokenToContext.put(command.getAuthToken(), ctx);

        
    }

    private void onMakeMove(UserGameCommand command, HashMap<Integer, List<WsContext>> gameIdToContext) {
        try {
            MakeMoveRequest request = new MakeMoveRequest(command.getAuthToken(), command.getGameID(), command.getMove());
            MakeMoveResult result = gameService.makeMove(request);
            if (result.success()) {
                LoadGameMessage message = new LoadGameMessage(result.game().game());
                List<WsContext> contexts = gameIdToContext.get(command.getGameID());
                for (WsContext context : contexts) {
                    context.send(gson.toJson(message));
                }
            } else {
                System.out.println("Failed to make move: " + command.getGameID());
            }
        } catch (DataAccessException e) {
            System.out.println("Error making move: " + e.getMessage());
        }
    }

    private void onLeave(UserGameCommand command, HashMap<Integer, List<WsContext>> gameIdToContext) {
        try {
            LeaveGameRequest request = new LeaveGameRequest(command.getAuthToken(), command.getGameID());
            LeaveGameResult result = gameService.leaveGame(request);
            if (result.success()) {
                WsContext ctx = authTokenToContext.remove(command.getAuthToken());
                gameIdToContext.get(command.getGameID()).remove(ctx);
                ctx.session.close();
                System.out.println("Successfully left game: " + command.getGameID());
            } else {
                System.out.println("Failed to leave game: " + command.getGameID());
            }
        } catch (DataAccessException e) {
            System.out.println("Error leaving game: " + e.getMessage());
        }
    }

    private void onResign(UserGameCommand command) {
        try {
            LeaveGameRequest request = new LeaveGameRequest(command.getAuthToken(), command.getGameID());
            LeaveGameResult result = gameService.resignGame(request);
            if (result.success()) {
                System.out.println("Successfully resigned game: " + command.getGameID());
            } else {
                System.out.println("Failed to resign game: " + command.getGameID());
            }
        } catch (DataAccessException e) {
            System.out.println("Error resigning game: " + e.getMessage());
        }
    }

    private void onError(WsErrorContext ctx) {
        ctx.error();
    }

    private void onClose(WsCloseContext ctx) {
        System.out.println("WebSocket closed");
    }
}
