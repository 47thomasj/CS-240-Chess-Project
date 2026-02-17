package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import models.requests.ClearAllRequest;
import models.results.ClearAllResult;
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
        ClearAllRequest request = gson.fromJson(context.body(), ClearAllRequest.class);

        try {
            ClearAllResult result = service.clear(request);
            context.status(200);
        } catch (Exception e) {
            context.json(e);
            context.status(500);
        }
    }
}