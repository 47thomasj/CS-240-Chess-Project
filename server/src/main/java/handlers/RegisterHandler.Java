package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import models.requests.RegisterRequest;
import models.results.RegisterResult;
import models.results.ErrorResult;
import org.jetbrains.annotations.NotNull;
import service.UserService;


public class RegisterHandler implements Handler {

    private final Gson gson;
    private final UserService service;

    public RegisterHandler(Gson gson, UserService service) {
        this.gson = gson;
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context context) {
        RegisterRequest request = gson.fromJson(context.body(), RegisterRequest.class);

        try {
            RegisterResult result = service.register(request);
            context.status(200);
            context.json(gson.toJson(result));
        } catch (DataAccessException e) {
            context.json(gson.toJson(new ErrorResult(e.getMessage())));
            switch (e.getMessage()) {
                case "Error: bad request" -> context.status(400);
                case "Error: already taken" -> context.status(403);
                default -> context.status(500);
            }
        }
    }

}