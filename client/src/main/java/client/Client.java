package client;

import menu.Menu;
import menu.MenuOption;

import board.ChessPrinter;

public class Client {

    private String serverUrl;
    private Menu prelogin;
    private Menu postlogin;
    private Menu gameplay;
    private Menu observe;

    private ServerFacade serverFacade;

    private String authToken;
    private GamesManager gamesManager;        

    public Client() {
        this.serverUrl = "http://localhost:8080";
        this.authToken = null;
        this.gamesManager = new GamesManager(null);

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

        String gameplayHelpString = "This is the gameplay menu. You can select an option by entering the number of the option."
        + "\n\nOptions:"
        + "\n1. Redraw the chess board"
        + "\n2. Leave the game"
        + "\n3. Make a move"
        + "\n4. Resign (you lose the game)"
        + "\n5. Highlight the moves a chosen piece can make"
        + "\n6. Display this help message again";

        this.prelogin = new Menu("Quit", () -> System.exit(0), preLoginHelpString, "Welcome to the Chess Game!");
        this.postlogin = new Menu("Logout", () -> {
            boolean outcome = serverFacade.logout(this.authToken, this.prelogin, this.postlogin);
            if (outcome) {
                this.authToken = null;
                this.prelogin.interactWithMenu();
            } else {
                this.postlogin.interactWithMenu();
            }
        }, postLoginHelpString, "You are now logged in. Welcome!");

        this.gameplay = new Menu("Leave", () -> {
            serverFacade.leaveGame(this.authToken, this.gamesManager.getCurrentGameID(), this.postlogin);
        }, gameplayHelpString, "Enjoy the match!");
        this.observe = new Menu("Leave", () -> {
            serverFacade.leaveObserveGame(this.authToken, this.gamesManager.getCurrentGameID(), this.postlogin);
        }, "Leave the game", "Enjoy the match (observing)!");

        this.prelogin.addOption(new MenuOption("Login", () -> {
            String outcome = serverFacade.login(this.postlogin);
            if (outcome != null) {
                this.authToken = outcome;
                this.postlogin.interactWithMenu();
            }
        }));
        this.prelogin.addOption(new MenuOption("Register", () -> {
            String outcome = serverFacade.register(this.prelogin, this.postlogin);
            if (outcome != null) {
                this.authToken = outcome;
                this.postlogin.interactWithMenu();
            }
        }));


        this.postlogin.addOption(new MenuOption("Create a new Chess Game", () -> serverFacade.createGame(this.authToken)));
        this.postlogin.addOption(new MenuOption("List all Chess Games available", () -> serverFacade.listGames(this.authToken)));
        this.postlogin.addOption(new MenuOption("Join and begin playing a pre-existing Chess Game", () -> serverFacade.joinGame(this.authToken)));
        this.postlogin.addOption(new MenuOption(
            "Observe a pre-existing Chess Game, but not participate in it", 
            () -> serverFacade.observeGame(this.authToken))
        );
        
        this.gameplay.addOption(new MenuOption(
            "Redraw the chess board", 
            () -> ChessPrinter.printBoard(
                this.gamesManager.getCurrentGame().getBoard(), 
                this.gamesManager.getCurrentTeamColor()
            )
        ));
        this.gameplay.addOption(new MenuOption(
            "Highlight the moves a chosen piece can make", 
            () -> ChessPrinter.printLegalMoves(
                this.gamesManager.getCurrentGame().getBoard(), 
                this.gamesManager.getCurrentTeamColor()
                )
            ));
        this.gameplay.addOption(new MenuOption(
            "Make a move", 
            () -> serverFacade.makeMove(this.authToken, this.gamesManager.getCurrentGameID()
        )));
        this.gameplay.addOption(new MenuOption(
            "Resign (you lose the game)", 
            () -> serverFacade.resignGame(this.authToken, this.gamesManager.getCurrentGameID()
        )));
    
        this.observe.addOption(new MenuOption(
            "Highlight the moves a chosen piece can make", 
            () -> ChessPrinter.printLegalMoves(
                this.gamesManager.getCurrentGame().getBoard(), 
                this.gamesManager.getCurrentTeamColor()
            )
        ));
    
        this.serverFacade = new ServerFacade(serverUrl, gamesManager, this.observe, this.gameplay);
    }

    public void run() {
        this.prelogin.interactWithMenu();
    }
}
