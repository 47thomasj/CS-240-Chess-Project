package routers;

import com.google.gson.Gson;

import java.util.Scanner;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import models.results.ErrorResult;
import models.results.CreateGameResult;
import models.requests.CreateGameRequest;

public class CreateGameRouter {
    private final String serverUrl;
    private final Gson gson;
    private final HttpClient client;

    public CreateGameRouter(String serverUrl, Gson gson, HttpClient client) {
        this.serverUrl = serverUrl;
        this.gson = gson;
        this.client = client;
    }

    public CreateGameOutcome doCreateGame(String authToken) {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        System.out.print("Game Name: ");
        String gameName = scanner.nextLine();
        CreateGameOutcome outcome = this.createGame(authToken, gameName);
        if (outcome instanceof CreateGameOutcome.Success) {
            return outcome;
        }
        System.out.println("Create Game failed: " + ((CreateGameOutcome.Failure) outcome).message());
        return outcome;
    }

    private CreateGameOutcome createGame(String authToken, String gameName) {
        String url = serverUrl + "/game";
        String body = gson.toJson(new CreateGameRequest(authToken, gameName));
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("authorization", authToken)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            if (response.statusCode() == 200) {
                CreateGameResult r = gson.fromJson(responseBody, CreateGameResult.class);
                return CreateGameOutcome.success(r.gameID());
            }
            ErrorResult err = gson.fromJson(responseBody, ErrorResult.class);
            return CreateGameOutcome.failure(err != null ? err.message() : "Create Game failed");
        } catch (Exception e) {
            return CreateGameOutcome.failure(e.getMessage());
        }
    }

    public sealed interface CreateGameOutcome {
        record Success(int gameID) implements CreateGameOutcome {}
        record Failure(String message) implements CreateGameOutcome {}

        static CreateGameOutcome success(int gameID) { return new Success(gameID); }
        static CreateGameOutcome failure(String message) { return new Failure(message); }
    }
}
