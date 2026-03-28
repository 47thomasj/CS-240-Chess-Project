package client;

import com.google.gson.Gson;
import java.net.http.HttpClient;

import routers.RegisterRouter;
import routers.RegisterRouter.RegisterOutcome;

import routers.LoginRouter;
import routers.LoginRouter.LoginOutcome;

import routers.LogoutRouter;
import routers.LogoutRouter.LogoutOutcome;

import routers.ListGamesRouter;
import routers.ListGamesRouter.ListGamesOutcome;

import routers.CreateGameRouter;
import routers.CreateGameRouter.CreateGameOutcome;

import routers.ObserveGameRouter;

import chess.ChessGame.TeamColor;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import routers.JoinGameRouter;
import routers.WebSocketRouter;
import menu.Menu;

public class ServerFacade {

    private final Gson gson;

    private final GamesManager gamesManager;

    private final LogoutRouter logoutRouter;
    private final LoginRouter loginRouter;
    private final RegisterRouter registerRouter;
    private final ListGamesRouter listGamesRouter;
    private final CreateGameRouter createGameRouter;
    private final ObserveGameRouter observeGameRouter;
    private final JoinGameRouter joinGameRouter;
    private final WebSocketRouter webSocketRouter;

    public ServerFacade(String serverUrl, GamesManager gamesManager) {
        this(serverUrl, gamesManager, HttpClient.newHttpClient(), ContainerProvider.getWebSocketContainer());
    }

    public ServerFacade(String serverUrl, GamesManager gamesManager, HttpClient client, WebSocketContainer webSocketContainer) {
        this.gson = new Gson();
        this.gamesManager = gamesManager;

        this.logoutRouter = new LogoutRouter(serverUrl, client);
        this.loginRouter = new LoginRouter(serverUrl, gson, client);
        this.registerRouter = new RegisterRouter(serverUrl, gson, client);
        this.listGamesRouter = new ListGamesRouter(serverUrl, gson, client);
        this.createGameRouter = new CreateGameRouter(serverUrl, gson, client);
        this.observeGameRouter = new ObserveGameRouter(gamesManager);
        this.joinGameRouter = new JoinGameRouter(gamesManager, serverUrl, gson, client);
        this.webSocketRouter = new WebSocketRouter(gson, webSocketContainer);
    }

    public String register(Menu prelogin, Menu postlogin) {
        RegisterOutcome outcome = this.registerRouter.doRegister();
        if (outcome instanceof RegisterOutcome.Success) {
            String authToken = ((RegisterOutcome.Success) outcome).auth().authToken();
            return authToken;
        } else {
            System.out.println("Could not register. Did you enter a valid username, password, and email?");
            return null;
        }
    }

    public String login(Menu postlogin) {
        LoginOutcome outcome = this.loginRouter.doLogin();
        if (outcome instanceof LoginOutcome.Success) {
            String authToken = ((LoginOutcome.Success) outcome).auth().authToken();
            return authToken;
        } else {
            System.out.println("Could not login. Did you enter the right username and password?");
            return null;
        }
    }

    public boolean logout(String authToken, Menu prelogin, Menu postlogin) {
        LogoutOutcome outcome = logoutRouter.doLogout(authToken);
        if (outcome instanceof LogoutOutcome.Success) {
            return true;
            
        } else {
            System.out.println("Could not logout. Try restarting the program.");
            return false;
        }
    };

    public void listGames(String authToken) {
        ListGamesOutcome outcome = this.listGamesRouter.doListGames(authToken);
        if (outcome instanceof ListGamesOutcome.Success) {
            gamesManager.setGames(((ListGamesOutcome.Success) outcome).games());
            System.out.println("\nsGames available:");
            gamesManager.printGames();
        } else {
            System.out.println("No games available");
        }
    }

    public void observeGame(String authToken) {
        ListGamesOutcome outcome = this.listGamesRouter.doListGames(authToken);
        if (outcome instanceof ListGamesOutcome.Success) {
            gamesManager.setGames(((ListGamesOutcome.Success) outcome).games());
        } else {
            System.out.println("No games available");
            return;
        }
        observeGameRouter.doObserveGame(TeamColor.WHITE);
    }

    public void createGame(String authToken) {
        CreateGameOutcome outcome = this.createGameRouter.doCreateGame(authToken);
        if (outcome instanceof CreateGameOutcome.Success) {
            System.out.println("Game created successfully!");
        } else {
            System.out.println("Could not create game. Did you enter a valid name?");
        }
    }

    public void joinGame(String authToken, Menu gameplay) {
        ListGamesOutcome outcome = this.listGamesRouter.doListGames(authToken);
        if (outcome instanceof ListGamesOutcome.Success) {
            gamesManager.setGames(((ListGamesOutcome.Success) outcome).games());
        } else {
            System.out.println("No games available");
            return;
        }
        int gameID = joinGameRouter.doJoinGame(authToken);
        if (gameID != -1) {
            try {
                webSocketRouter.connect(authToken, gameID);
            } catch (Exception e) {
                System.out.println("Could not connect to game. " + e.getMessage());
                return;
            }
            gameplay.interactWithMenu();
            return;
        } else {
            System.out.println("Could not join game. Did you enter a valid game ID?");
            return;
        }
    }
}
