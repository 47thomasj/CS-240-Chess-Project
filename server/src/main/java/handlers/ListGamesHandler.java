package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import models.requests.ListGamesRequest;
import models.results.ErrorResult;
import models.results.ListGamesResult;
import org.jetbrains.annotations.NotNull;
import service.GameService;

public class ListGamesHandler implements Handler {
    private final Gson gson;
    private final GameService service;

    public ListGamesHandler(Gson gson, GameService service) {
        this.gson = gson;
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context context) {
        String authToken = context.header("authorization");
        ListGamesRequest request = new ListGamesRequest(authToken);

        try {
            ListGamesResult result = service.listGames(request);
            context.status(200);
            context.json(gson.toJson(result));
        } catch (DataAccessException e) {
            ErrorResult errorResult = new ErrorResult(e.getMessage());
            context.json(gson.toJson(errorResult));
            if (e.getMessage().equals("Error: unauthorized")) {
                context.status(401);
            } else {
                context.status(500);
            }
        }
    }
}
