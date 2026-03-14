package client;

import menu.Menu;
import menu.MenuOption;

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

import models.GameData;

public class Client {

    private String serverUrl;
    private Menu prelogin;
    private Menu postlogin;

    private RegisterRouter registerRouter;
    private LoginRouter loginRouter;
    private LogoutRouter logoutRouter;
    private ListGamesRouter listGamesRouter;
    private CreateGameRouter createGameRouter;

    private Gson gson;
    private HttpClient client;

    private String authToken;

    public Client() {
        this("http://localhost:8080", new Gson());
        this.authToken = null;
        this.client = HttpClient.newHttpClient();
        this.registerRouter = new RegisterRouter(serverUrl, gson, client);
        this.loginRouter = new LoginRouter(serverUrl, gson, client);
        this.logoutRouter = new LogoutRouter(serverUrl, client);
        this.listGamesRouter = new ListGamesRouter(serverUrl, gson, client);
        this.createGameRouter = new CreateGameRouter(serverUrl, gson, client);
    }

    public Client(String serverUrl, Gson gson) {
        this.serverUrl = serverUrl;
        this.gson = gson;
        String preLoginHelpString = "This is the landing page menu. You can select an option by entering the number of the option."
        + "\n\nOptions:"
        + "\n1. Login with your username and password"
        + "\n2. Register a new account"
        + "\n3. Quit"
        + "\n4. Display this help message again";
        
        String postLoginHelpString = "This is the main menu. You can select an option by entering the number of the option."
        + "\n\nOptions:"
        + "\n1. Create a new Chess Game"
        + "\n2. List all Chess Games available"
        + "\n3. Join and begin playing a pre-existing Chess Game"
        + "\n4. Observe a pre-existing Chess Game, but not participate in it"
        + "\n5. Logout of your account"
        + "\n6. Display this help message again";

        this.prelogin = new Menu("Quit", null, preLoginHelpString, "Welcome to the Chess Game!");
        this.postlogin = new Menu("Logout", () -> {
            LogoutOutcome outcome = this.logoutRouter.doLogout(this.authToken);
            if (outcome instanceof LogoutOutcome.Success) {
                this.authToken = null;
                this.prelogin.interactWithMenu();
            } else {
                System.out.println("Logout failed: " + ((LogoutOutcome.Failure) outcome).message());
                this.postlogin.interactWithMenu();
            }
        }, postLoginHelpString, "You are now logged in. Welcome!");

        this.prelogin.addOption(new MenuOption("Login", () -> {
            LoginOutcome outcome = this.loginRouter.doLogin();
            if (outcome instanceof LoginOutcome.Success) {
                this.authToken = ((LoginOutcome.Success) outcome).auth().authToken();
                this.postlogin.interactWithMenu();
            } else {
                System.out.println("Login failed: " + ((LoginOutcome.Failure) outcome).message());
            }
        }));
        this.prelogin.addOption(new MenuOption("Register", () -> {
            RegisterOutcome outcome = this.registerRouter.doRegister();
            if (outcome instanceof RegisterOutcome.Success) {
                this.authToken = ((RegisterOutcome.Success) outcome).auth().authToken();
                this.postlogin.interactWithMenu();
            } else {
                System.out.println("Registration failed: " + ((RegisterOutcome.Failure) outcome).message());
            }
        }));


        this.postlogin.addOption(new MenuOption("Create a new Chess Game", () -> {
            CreateGameOutcome outcome = this.createGameRouter.doCreateGame(this.authToken);
            if (outcome instanceof CreateGameOutcome.Success) {
                System.out.println("Game created successfully!");
            } else {
                System.out.println("Create Game failed: " + ((CreateGameOutcome.Failure) outcome).message());
            }
        }));
        this.postlogin.addOption(new MenuOption("List all Chess Games available", () -> {
            ListGamesOutcome outcome = this.listGamesRouter.doListGames(this.authToken);
            if (outcome instanceof ListGamesOutcome.Success) {
                System.out.println("\nsGames available:");
                for (GameData game : ((ListGamesOutcome.Success) outcome).games()) {
                    System.out.println(
                        "Game Name: " + game.gameName() +
                        " - White Username: " + game.whiteUsername() +
                        " - Black Username: " + game.blackUsername()
                    );
                }
            } else {
                System.out.println("List Games failed: " + ((ListGamesOutcome.Failure) outcome).message());
            }
        }));
        this.postlogin.addOption(new MenuOption("Join and begin playing a pre-existing Chess Game", () -> {
            System.out.println("Join and begin playing a pre-existing Chess Game");
        }));
        this.postlogin.addOption(new MenuOption("Observe a pre-existing Chess Game, but not participate in it", () -> {
            System.out.println("Observe a pre-existing Chess Game, but not participate in it");
        }));
    }

    public void run() {
        this.prelogin.interactWithMenu();
    }
}
