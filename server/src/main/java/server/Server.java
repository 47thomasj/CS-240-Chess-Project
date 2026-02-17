package server;

import dataaccess.*;
import handlers.*;
import service.*;
import io.javalin.*;

import com.google.gson.Gson;

public class Server {

    private final Javalin javalin;

    public Server() {

        Gson gson = new Gson();

        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();
        UserDAO userDAO = new UserDAO();

        ClearApplicationService clearApplicationService = new ClearApplicationService(authDAO, gameDAO, userDAO);
        GameService gameService = new GameService(authDAO, gameDAO);
        UserService userService = new UserService(userDAO, authDAO);

        ClearApplicationHandler clearApplicationHandler = new ClearApplicationHandler(gson, clearApplicationService);
        CreateGamesHandler createGamesHandler = new CreateGamesHandler(gson, gameService);
        JoinGameHandler joinGameHandler = new JoinGameHandler(gson, gameService);
        ListGamesHandler listGamesHandler = new ListGamesHandler(gson, gameService);
        LoginHandler loginHandler = new LoginHandler(gson, userService);
        LogoutHandler logoutHandler = new LogoutHandler(gson, userService);
        RegisterHandler registerHandler = new RegisterHandler(gson, userService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", clearApplicationHandler);
        javalin.post("/user", registerHandler);
        javalin.post("/session", loginHandler);
        javalin.delete("/session", logoutHandler);
        javalin.get("/game", listGamesHandler);
        javalin.post("/game", createGamesHandler);
        javalin.put("/game", joinGameHandler);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

}
