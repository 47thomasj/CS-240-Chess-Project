package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import models.results.ErrorResult;
import org.jetbrains.annotations.NotNull;
import service.ClearApplicationService;

public class ClearApplicationHandler implements Handler {
    private final Gson gson;
    private final ClearApplicationService service;

    public ClearApplicationHandler(Gson gson, ClearApplicationService service) {
        this.gson = gson;
        this.service = service;
    }

    @Override
    public void handle(@NotNull Context context) {
        try {
            service.clear();
            context.status(200);
        } catch (Exception e) {
            context.json(gson.toJson(new ErrorResult(e.getMessage())));
            context.status(500);
        }
    }
}