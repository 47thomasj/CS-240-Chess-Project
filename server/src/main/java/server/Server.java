package server;

import handlers.RegisterHandler;
import io.javalin.*;


public class Server {

    private final Javalin javalin;
    private RegisterHandler registrationHandler;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.post("/user", context ->
                registrationHandler.handle(context));

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

}
