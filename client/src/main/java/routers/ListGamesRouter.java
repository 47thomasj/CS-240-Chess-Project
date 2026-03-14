package routers;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import models.GameData;
import models.results.ErrorResult;
import models.results.ListGamesResult;

public class ListGamesRouter {
    private final String serverUrl;
    private final Gson gson;
    private final HttpClient client;

    public ListGamesRouter(String serverUrl, Gson gson, HttpClient client) {
        this.serverUrl = serverUrl;
        this.gson = gson;
        this.client = client;
    }

    public ListGamesOutcome doListGames(String authToken) {
        ListGamesOutcome outcome = this.listGames(authToken);
        if (outcome instanceof ListGamesOutcome.Success) {
            return outcome;
        }
        System.out.println("List Games failed: " + ((ListGamesOutcome.Failure) outcome).message());
        return outcome;
    }

    private ListGamesOutcome listGames(String authToken) {
        String url = serverUrl + "/game";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("authorization", authToken)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            if (response.statusCode() == 200) {
                ListGamesResult r = gson.fromJson(responseBody, ListGamesResult.class);
                return ListGamesOutcome.success(r.games());
            }
            ErrorResult err = gson.fromJson(responseBody, ErrorResult.class);
            return ListGamesOutcome.failure(err != null ? err.message() : "List Games failed");
        } catch (Exception e) {
            return ListGamesOutcome.failure(e.getMessage());
        }
    }

    public sealed interface ListGamesOutcome {
        record Success(GameData[] games) implements ListGamesOutcome {}
        record Failure(String message) implements ListGamesOutcome {}

        static ListGamesOutcome success(GameData[] games) { return new Success(games); }
        static ListGamesOutcome failure(String message) { return new Failure(message); }
    }
}
