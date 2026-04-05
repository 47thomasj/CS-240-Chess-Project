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

import routers.JoinGameRouter;

import routers.WebSocketRouter;

import websocket.messages.ServerMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import chess.ChessGame.TeamColor;
import chess.ChessPosition;
import java.util.Scanner;
import board.ChessPrinter;
import java.util.HashMap;
import websocket.commands.UserGameCommand;
import chess.ChessPiece;
import chess.ChessMove;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;

import menu.Menu;

public class ServerFacade {

    private final Gson gson;
    private final Menu observe;
    private final Menu gameplay;

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
        this(
            serverUrl, 
            gamesManager,
            new Menu("Leave", () -> {}, "Leave the game", "Enjoy the match (observing)!"),
            new Menu("Leave", () -> {}, "Leave the game", "Enjoy the match!")
        );
    }
    
    public ServerFacade(String serverUrl, GamesManager gamesManager, Menu observe, Menu gameplay) {
        this(serverUrl, gamesManager, HttpClient.newHttpClient(), ContainerProvider.getWebSocketContainer(), observe, gameplay);
    }

    public ServerFacade(String serverUrl, GamesManager gamesManager, HttpClient client, WebSocketContainer webSocketContainer, Menu observe, Menu gameplay) {
        this.gson = new Gson();
        this.gamesManager = gamesManager;
        this.observe = observe;
        this.gameplay = gameplay;
        this.logoutRouter = new LogoutRouter(serverUrl, client);
        this.loginRouter = new LoginRouter(serverUrl, gson, client);
        this.registerRouter = new RegisterRouter(serverUrl, gson, client);
        this.listGamesRouter = new ListGamesRouter(serverUrl, gson, client);
        this.createGameRouter = new CreateGameRouter(serverUrl, gson, client);
        this.observeGameRouter = new ObserveGameRouter(gamesManager);
        this.joinGameRouter = new JoinGameRouter(gamesManager, serverUrl, gson, client);
        this.webSocketRouter = new WebSocketRouter(gson, webSocketContainer);
        this.webSocketRouter.setMessageHandler(this::onMessage);
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
            gamesManager.printGames();
        } else {
            System.out.println("No games available");
        }
    }

    public void observeGame(String authToken) {
        ListGamesOutcome outcome = this.listGamesRouter.doListGames(authToken);
        if (outcome instanceof ListGamesOutcome.Success) {
            gamesManager.setGames(((ListGamesOutcome.Success) outcome).games());
            gamesManager.setPerspective(TeamColor.WHITE);
        } else {
            System.out.println("No games available");
            return;
        }
        int gameId = observeGameRouter.doObserveGame(TeamColor.WHITE);
        if (gameId != -1) {
            try {
                webSocketRouter.connect(authToken, gameId);
                gamesManager.setCurrentGameID(gameId);
            } catch (Exception e) {
                System.out.println("Could not connect to game. " + e.getMessage());
                return;
            }
            observe.interactWithMenu();
        } else {
            System.out.println("Could not observe game. Did you enter a valid game ID?");
            return;
        }
    }

    public void leaveObserveGame(String authToken, int gameID, Menu postlogin) {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            webSocketRouter.send(command);
            webSocketRouter.close();
            postlogin.interactWithMenu();
        } catch (Exception e) {
            System.out.println("Could not leave observe game. " + e.getMessage());
        }
    }

    public void createGame(String authToken) {
        CreateGameOutcome outcome = this.createGameRouter.doCreateGame(authToken);
        if (outcome instanceof CreateGameOutcome.Success) {
            System.out.println("Game created successfully!");
        } else {
            System.out.println("Could not create game. Did you enter a valid name?");
        }
    }

    public void joinGame(String authToken) {
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
                gamesManager.setCurrentGameID(gameID);
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

    public void leaveGame(String authToken, int gameID, Menu postlogin) {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            webSocketRouter.send(command);
            postlogin.interactWithMenu();
        } catch (Exception e) {
            System.out.println("Could not leave game. " + e.getMessage());
        }
    }

    public void resignGame(String authToken, int gameID) {
        try {
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);
            System.out.println("Are you sure you want to resign? (y/n)");
            String input = scanner.nextLine();
            if (!input.equals("y")) {
                return;
            }
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            webSocketRouter.send(command);
        } catch (Exception e) {
            System.out.println("Could not resign game. " + e.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID) {
        try {
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);

            System.out.println("\nEnter the position of the piece to move: ");
            ChessPosition startPosition = ChessPrinter.getPositionFromUser();
            System.out.println("\nEnter the position to move the piece to: ");
            ChessPosition endPosition = ChessPrinter.getPositionFromUser();

            HashMap<String, ChessPiece.PieceType> promotionTypes = new HashMap<>();
            promotionTypes.put("Q", ChessPiece.PieceType.QUEEN);
            promotionTypes.put("R", ChessPiece.PieceType.ROOK);
            promotionTypes.put("B", ChessPiece.PieceType.BISHOP);
            promotionTypes.put("N", ChessPiece.PieceType.KNIGHT);

            ChessPiece.PieceType promotionPiece = null;
            while (promotionPiece == null) {
                System.out.println("\nEnter the piece type to promote to (Q, R, B, N)");
                System.out.print("(Enter nothing for no promotion): ");
                String promotionType = scanner.nextLine();

                if (promotionType.isEmpty()) {
                    break;
                }

                if (!promotionTypes.containsKey(promotionType)) {
                    System.out.println("\nInvalid promotion type. Please enter a valid promotion type (Q, R, B, N).");
                    continue;
                }

                promotionPiece = promotionTypes.get(promotionType);
            }

            ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
            webSocketRouter.send(command);
        } catch (Exception e) {
            System.out.println("Could not make move. " + e.getMessage());
        }
    }

    private void onMessage(String message) {
        try {
            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
            if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                gamesManager.setCurrentGame(loadGameMessage.getGame());
                ChessPrinter.printBoard(gamesManager.getCurrentGame().getBoard(), gamesManager.getPerspective());
            }
            else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
                System.out.println(notificationMessage.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Could not parse message. " + e.getMessage());
        }
    }
}
