package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import models.requests.LoginRequest;
import models.results.LoginResult;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class LoginHandler implements Handler {

    private final Gson gson;
    private final UserService service;

    public LoginHandler(Gson gson, UserService service) {
        this.gson = gson;
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context context) {
        LoginRequest request = gson.fromJson(context.body(), LoginRequest.class);

        try {
            LoginResult result = service.login(request);
            context.status(200);
            context.json(result);
        } catch (DataAccessException e) {
            context.json(e);
            switch (e.getMessage()) {
                case "Error: bad request" -> context.status(400);
                case "Error: unauthorized" -> context.status(401);
                default -> context.status(500);
            }
        }
    }
}
