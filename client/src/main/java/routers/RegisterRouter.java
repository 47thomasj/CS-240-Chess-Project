package routers;

import java.util.Scanner;

import com.google.gson.Gson;

import models.AuthData;
import models.requests.RegisterRequest;
import models.results.ErrorResult;
import models.results.RegisterResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RegisterRouter {
    private final String serverUrl;
    private final Gson gson;
    private final HttpClient client;

    public RegisterRouter(String serverUrl, Gson gson, HttpClient client) {
        this.serverUrl = serverUrl;
        this.gson = gson;
        this.client = client;
    }

    public RegisterOutcome doRegister() {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        RegisterOutcome outcome = this.register(username, password, email);
        if (outcome instanceof RegisterOutcome.Success) {
            return outcome;
        }
        System.out.println("Registration failed: " + ((RegisterOutcome.Failure) outcome).message());
        return outcome;
    }

    /**
     * Calls POST /user. Returns AuthData on success, or a failure with message.
     */
    private RegisterOutcome register(String username, String password, String email) {
        String url = serverUrl + "/user";
        String body = gson.toJson(new RegisterRequest(username, password, email));
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            if (response.statusCode() == 200) {
                RegisterResult r = gson.fromJson(responseBody, RegisterResult.class);
                return RegisterOutcome.success(new AuthData(r.authToken(), r.username()));
            }
            ErrorResult err = gson.fromJson(responseBody, ErrorResult.class);
            return RegisterOutcome.failure(err != null ? err.message() : "Registration failed");
        } catch (Exception e) {
            return RegisterOutcome.failure(e.getMessage());
        }
    }

    public sealed interface RegisterOutcome {
        record Success(AuthData auth) implements RegisterOutcome {}
        record Failure(String message) implements RegisterOutcome {}

        static RegisterOutcome success(AuthData auth) { return new Success(auth); }
        static RegisterOutcome failure(String message) { return new Failure(message); }
    }
}
