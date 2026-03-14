package routers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LogoutRouter {
    private final String serverUrl;
    private final HttpClient client;

    public LogoutRouter(String serverUrl, HttpClient client) {
        this.serverUrl = serverUrl;
        this.client = client;
    }

    public LogoutOutcome doLogout(String authToken) {
        LogoutOutcome outcome = this.logout(authToken);
        if (outcome instanceof LogoutOutcome.Success) {
            return outcome;
        }
        System.out.println("Logout failed: " + ((LogoutOutcome.Failure) outcome).message());
        return outcome;
    }

    private LogoutOutcome logout(String authToken) {
        String url = serverUrl + "/session";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("authorization", authToken)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return LogoutOutcome.success(true);
            }
            return LogoutOutcome.failure("Logout failed");
        } catch (Exception e) {
            return LogoutOutcome.failure(e.getMessage());
        }
    }

    public sealed interface LogoutOutcome {
        record Success(boolean success) implements LogoutOutcome {}
        record Failure(String message) implements LogoutOutcome {}

        static LogoutOutcome success(boolean success) { return new Success(success); }
        static LogoutOutcome failure(String message) { return new Failure(message); }
    }
}
