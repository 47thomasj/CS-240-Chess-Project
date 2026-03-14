package client;

import menu.Menu;
import menu.MenuOption;

import com.google.gson.Gson;
import java.net.http.HttpClient;

import routers.RegisterRouter;
import routers.RegisterRouter.RegisterOutcome;

import routers.LoginRouter;
import routers.LoginRouter.LoginOutcome;

public class Client {

    private String serverUrl;
    private Menu prelogin;
    private Menu postlogin;

    private RegisterRouter registerRouter;
    private LoginRouter loginRouter;

    private Gson gson;
    private HttpClient client;

    public Client() {
        this("http://localhost:8080", new Gson());
        this.client = HttpClient.newHttpClient();
        this.registerRouter = new RegisterRouter(serverUrl, gson, client);
        this.loginRouter = new LoginRouter(serverUrl, gson, client);
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

        this.prelogin = new Menu("Quit", preLoginHelpString, "Welcome to the Chess Game!");
        this.postlogin = new Menu("Logout", postLoginHelpString, "You are now logged in. Welcome!");

        this.prelogin.addOption(new MenuOption("Login", () -> {
            LoginOutcome outcome = this.loginRouter.doLogin();
            if (outcome instanceof LoginOutcome.Success) {
                this.postlogin.interactWithMenu();
            } else {
                System.out.println("Login failed: " + ((LoginOutcome.Failure) outcome).message());
            }
        }));
        this.prelogin.addOption(new MenuOption("Register", () -> {
            RegisterOutcome outcome = this.registerRouter.doRegister();
            if (outcome instanceof RegisterOutcome.Success) {
                this.postlogin.interactWithMenu();
            } else {
                System.out.println("Registration failed: " + ((RegisterOutcome.Failure) outcome).message());
            }
        }));


        this.postlogin.addOption(new MenuOption("Create a new Chess Game", () -> {
            System.out.println("Create a new Chess Game");
        }));
        this.postlogin.addOption(new MenuOption("List all Chess Games available", () -> {
            System.out.println("List all Chess Games available");
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
