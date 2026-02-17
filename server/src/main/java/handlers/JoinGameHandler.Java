package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import models.requests.JoinGameRequest;
import models.results.JoinGameResult;
import org.jetbrains.annotations.NotNull;
import service.GameService;

public class JoinGameHandler implements Handler {
    private final Gson gson;
    private final GameService service;

    public JoinGameHandler(Gson gson, GameService service) {
        this.gson = gson;
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context context) {
        JoinGameRequest request = gson.fromJson(context.body(), JoinGameRequest.class);

        try {
            JoinGameResult result = service.joinGame(request);
            context.status(200);
            context.json(result);
        } catch (DataAccessException e) {
            context.json(e);
            switch (e.getMessage()) {
                case "Error: bad request" -> context.status(400);
                case "Error: unauthorized" -> context.status(401);
                case "Error: already taken" -> context.status(403);
                default -> context.status(500);
            }
        }
    }
}