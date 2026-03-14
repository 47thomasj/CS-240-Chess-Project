package routers;

import com.google.gson.Gson;
import java.util.Scanner;
import java.net.http.HttpClient;

import routers.ListGamesRouter.ListGamesOutcome;
import models.GameData;

import board.ChessPrinter;
import chess.ChessGame.TeamColor;

public class ObserveGameRouter {
    private final ListGamesRouter listGamesRouter;
    
    public ObserveGameRouter(String serverUrl, Gson gson, HttpClient client) {
        this.listGamesRouter = new ListGamesRouter(serverUrl, gson, client);
    }

    public void doObserveGame(String authToken) {
        ListGamesOutcome listGamesOutcome = listGamesRouter.doListGames(authToken);
        GameData[] games = null;

        if (listGamesOutcome instanceof ListGamesOutcome.Success) {
            games = ((ListGamesOutcome.Success) listGamesOutcome).games();
            listGamesRouter.printGames(games);
        } else {
            System.out.println("List Games failed: " + ((ListGamesOutcome.Failure) listGamesOutcome).message());
        }

        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the game to observe: ");
        String gameName = scanner.nextLine();
        GameData game = null;
        for (GameData g : games) {
            if (g.gameName().equals(gameName)) {
                game = g;
                break;
            }
        }
        if (game == null) {
            System.out.println("Game not found");
        }

        ChessPrinter.printBoard(game.game().getBoard(), TeamColor.WHITE);
        
    }
}
