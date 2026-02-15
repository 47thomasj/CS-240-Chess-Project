package handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class RegisterHandler implements Handler {

    @Override
    public void handle(@NotNull Context context) throws Exception {
        RegistrationRequest request = (RegistrationRequest)gson.fromJson(context.body(), RegistrationRequest.class);
        String body = context.body();
    }

}