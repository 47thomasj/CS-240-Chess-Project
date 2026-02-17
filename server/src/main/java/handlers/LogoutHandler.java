package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import models.requests.LogoutRequest;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class LogoutHandler implements Handler {

    private final Gson gson;
    private final UserService service;

    public LogoutHandler(Gson gson, UserService service) {
        this.gson = gson;
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context context) {
        LogoutRequest request = gson.fromJson(context.body(), LogoutRequest.class);

        try {
            service.logout(request);
            context.status(200);
        } catch (DataAccessException e) {
            context.json(gson.toJson(e));
            if (e.getMessage().equals("Error: unauthorized")) {
                context.status(401);
            } else {
                context.status(500);
            }
        }
    }
}
