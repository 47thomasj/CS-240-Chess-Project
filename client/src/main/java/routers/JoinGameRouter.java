package routers;

import java.util.Scanner;

import models.GameData;

import chess.ChessGame.TeamColor;

import client.GamesManager;

import com.google.gson.Gson;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;

import models.requests.JoinGameRequest;
import models.results.JoinGameResult;
import models.results.ErrorResult;

public class JoinGameRouter {
    private final GamesManager gamesManager;
    private final String serverUrl;
    private final Gson gson;
    private final HttpClient client;
    
    public JoinGameRouter(GamesManager gamesManager, String serverUrl, Gson gson, HttpClient client) {
        this.gamesManager = gamesManager;
        this.serverUrl = serverUrl;
        this.gson = gson;
        this.client = client;
    }

    public TeamColor doJoinGame(String authToken) {
        gamesManager.printGames();

        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter the ID of the game to join: ");
        int gameID = scanner.nextInt();
        scanner.nextLine();
        GameData game = gamesManager.getGameByNumber(gameID);

        if (game == null) {
            System.out.println("Game not found");
            return null;
        }

        TeamColor teamColor = null;
        if (game.blackUsername() == null && game.whiteUsername() == null) {
            System.out.print("Enter which color you want to play as (WHITE or BLACK): ");
            String color = scanner.nextLine().toUpperCase();
            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                System.out.println("Invalid color");
                return null;
            }
            teamColor = TeamColor.valueOf(color);
        } else if (game.blackUsername() == null) {
            teamColor = TeamColor.BLACK;
        } else if (game.whiteUsername() == null) {
            teamColor = TeamColor.WHITE;
        } else {
            System.out.println("Game is already full");
            return null;
        }
        
        JoinOutcome outcome = joinGame(game.gameID(), teamColor, authToken);
        if (outcome instanceof JoinOutcome.Success) {
            System.out.println("Joined game successfully");
            gamesManager.setCurrentGame(game.game());
            gamesManager.setCurrentTeamColor(teamColor);
            // Open websocket connection here?
            return teamColor;
        } else {
            System.out.println("Could not join game. Did you enter a valid color?");
            return null;
        }
    }

    private JoinOutcome joinGame(int gameID, TeamColor teamColor, String authToken) {
        String url = serverUrl + "/game";
        String body = gson.toJson(new JoinGameRequest(authToken, teamColor.toString(), gameID));
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", authToken)
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            if (response.statusCode() == 200) {
                JoinGameResult r = gson.fromJson(responseBody, JoinGameResult.class);
                return JoinOutcome.success(r.success());
            }
            ErrorResult err = gson.fromJson(responseBody, ErrorResult.class);
            return JoinOutcome.failure(err != null ? err.message() : "Join Game failed");
        } catch (Exception e) {
            return JoinOutcome.failure(e.getMessage());
        }
    }

    public sealed interface JoinOutcome {
        record Success(boolean success) implements JoinOutcome {}
        record Failure(String message) implements JoinOutcome {}

        static JoinOutcome success(boolean success) { return new Success(success); }
        static JoinOutcome failure(String message) { return new Failure(message); }
    }
}
