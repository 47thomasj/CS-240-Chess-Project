package client;

import menu.Menu;
import menu.MenuOption;

public class Client {
    
    private final Menu prelogin;
    private final Menu postlogin;

    public Client() {
        String preLoginHelpString = "This is the landing page menu. You can select an option by entering the number of the option."
        + "\n\nOptions:"
        + "\n1. Login with your username and password"
        + "\n2. Register a new account"
        + "\n3. Quit";
        
        String postLoginHelpString = "This is the main menu. You can select an option by entering the number of the option."
        + "\n\nOptions:"
        + "\n1. Logout of your account"
        + "\n2. Create a new Chess Game"
        + "\n3. List all Chess Games available"
        + "\n4. Join and begin playing a pre-existing Chess Game"
        + "\n5. Observe a pre-existing Chess Game, but not participate in it"
        + "\n6. Quit";

        this.prelogin = new Menu("Quit", preLoginHelpString, "Welcome to the Chess Game!");
        this.postlogin = new Menu("Logout", postLoginHelpString, "You are now logged in. Welcome!");

        this.prelogin.addOption(new MenuOption("Login", () -> {
            return this.postlogin.interactWithMenu();
        }));
        this.prelogin.addOption(new MenuOption("Register", () -> {
            System.out.println("Register a new account");
            return null;
        }));


        this.postlogin.addOption(new MenuOption("Create a new Chess Game", () -> {
            System.out.println("Create a new Chess Game");
            return null;
        }));
        this.postlogin.addOption(new MenuOption("List all Chess Games available", () -> {
            System.out.println("List all Chess Games available");
            return null;
        }));
        this.postlogin.addOption(new MenuOption("Join and begin playing a pre-existing Chess Game", () -> {
            System.out.println("Join and begin playing a pre-existing Chess Game");
            return null;
        }));
        this.postlogin.addOption(new MenuOption("Observe a pre-existing Chess Game, but not participate in it", () -> {
            System.out.println("Observe a pre-existing Chess Game, but not participate in it");
            return null;
        }));
    }

    public void run() {
        String continueRunning = "";
        while (continueRunning != Menu.EXIT_OPTION) {
            try {
                continueRunning = (String) this.prelogin.interactWithMenu();
            } catch (Exception e) {
                System.out.println("An error occurred. Please try again.");
            }
        }
    }
}
