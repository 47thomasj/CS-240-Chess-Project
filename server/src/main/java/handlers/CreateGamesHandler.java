package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import models.requests.CreateGameRequest;
import models.results.CreateGameResult;
import models.results.ErrorResult;
import dataaccess.DataAccessException;
import org.jetbrains.annotations.NotNull;
import service.GameService;

public class CreateGamesHandler implements Handler {
    private final Gson gson;
    private final GameService service;

    public CreateGamesHandler(Gson gson, GameService service) {
        this.gson = gson;
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context context) {
        CreateGameRequest requestBody = gson.fromJson(context.body(), CreateGameRequest.class);
        CreateGameRequest request = new CreateGameRequest(context.header("authorization"), requestBody.gameName());

        try {
            CreateGameResult result = service.createGame(request);
            context.status(200);
            context.json(gson.toJson(result));
        } catch (DataAccessException e) {
            context.json(gson.toJson(new ErrorResult(e.getMessage())));
            switch (e.getMessage()) {
                case "Error: bad request" -> context.status(400);
                case "Error: unauthorized" -> context.status(401);
                default -> context.status(500);
            }
        }
    }
}
