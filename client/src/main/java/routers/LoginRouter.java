package routers;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Scanner;

import models.AuthData;
import models.requests.LoginRequest;
import models.results.ErrorResult;
import models.results.LoginResult;

public class LoginRouter {
    private final String serverUrl;
    private final Gson gson;
    private final HttpClient client;

    public LoginRouter(String serverUrl, Gson gson, HttpClient client) {
        this.serverUrl = serverUrl;
        this.gson = gson;
        this.client = client;
    }

    public LoginOutcome doLogin() {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        LoginOutcome outcome = this.login(username, password);
        if (outcome instanceof LoginOutcome.Success) {
            return outcome;
        }
        System.out.println("Login failed: " + ((LoginOutcome.Failure) outcome).message());
        return outcome;
    }

    private LoginOutcome login(String username, String password) {
        String url = serverUrl + "/session";
        String body = gson.toJson(new LoginRequest(username, password));
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            if (response.statusCode() == 200) {
                LoginResult r = gson.fromJson(responseBody, LoginResult.class);
                return LoginOutcome.success(new AuthData(r.authToken(), r.username()));
            }
            ErrorResult err = gson.fromJson(responseBody, ErrorResult.class);
            return LoginOutcome.failure(err != null ? err.message() : "Login failed");
        } catch (Exception e) {
            return LoginOutcome.failure(e.getMessage());
        }
    }

    public sealed interface LoginOutcome {
        record Success(AuthData auth) implements LoginOutcome {}
        record Failure(String message) implements LoginOutcome {}

        static LoginOutcome success(AuthData auth) { return new Success(auth); }
        static LoginOutcome failure(String message) { return new Failure(message); }
    }
}
