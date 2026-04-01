package routers;

import java.util.Scanner;

import models.GameData;

import board.ChessPrinter;
import chess.ChessGame.TeamColor;

import client.GamesManager;

public class ObserveGameRouter {
    private final GamesManager gamesManager;
    
    public ObserveGameRouter(GamesManager gamesManager) {
        this.gamesManager = gamesManager;
    }

    public int doObserveGame(TeamColor teamColor) {
        gamesManager.printGames();

        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the ID of the game to observe: ");
        int gameID = scanner.nextInt();
        GameData game = gamesManager.getGameByNumber(gameID);
        if (game == null) {
            System.out.println("Game not found");
            return -1;
        }

        ChessPrinter.printBoard(game.game().getBoard(), teamColor);
        return game.gameID();
    }
}
