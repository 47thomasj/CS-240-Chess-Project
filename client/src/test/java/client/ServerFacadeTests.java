package client;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import server.Server;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import models.GameData;

public class ServerFacadeTests {

    private static Server server;
    private static String serverUrl;

    private GamesManager gamesManager;
    private ServerFacade facade;

    @BeforeAll
    static void init() {
        server = new Server();
        int port = server.run(0);
        serverUrl = "http://localhost:" + port;
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void setup() throws Exception {
        HttpClient.newHttpClient().send(
            HttpRequest.newBuilder().uri(URI.create(serverUrl + "/db")).DELETE().build(),
            HttpResponse.BodyHandlers.ofString()
        );
        gamesManager = new GamesManager(new GameData[0]);
        facade = new ServerFacade(serverUrl, gamesManager);
    }

    private String registerUser(String username, String password, String email) {
        System.setIn(new ByteArrayInputStream((username + "\n" + password + "\n" + email + "\n").getBytes()));
        return facade.register(null, null);
    }

    private void createGame(String authToken, String gameName) {
        System.setIn(new ByteArrayInputStream((gameName + "\n").getBytes()));
        facade.createGame(authToken);
    }

    @Test
    void registerPositive() {
        String authToken = registerUser("user1", "password1", "email@test.com");
        assertNotNull(authToken);
    }

    @Test
    void registerNegative() {
        registerUser("user1", "password1", "email@test.com");
        String authToken = registerUser("user1", "password1", "email@test.com");
        assertNull(authToken);
    }

    @Test
    void loginPositive() {
        registerUser("user1", "password1", "email@test.com");
        System.setIn(new ByteArrayInputStream("user1\npassword1\n".getBytes()));
        String authToken = facade.login(null);
        assertNotNull(authToken);
    }

    @Test
    void loginNegative() {
        System.setIn(new ByteArrayInputStream("nonexistent\nwrongpassword\n".getBytes()));
        String authToken = facade.login(null);
        assertNull(authToken);
    }

    @Test
    void logoutPositive() {
        String authToken = registerUser("user1", "password1", "email@test.com");
        assertTrue(facade.logout(authToken, null, null));
    }

    @Test
    void logoutNegative() {
        assertFalse(facade.logout("badtoken", null, null));
    }

    @Test
    void listGamesPositive() {
        String authToken = registerUser("user1", "password1", "email@test.com");
        assertDoesNotThrow(() -> facade.listGames(authToken));
    }

    @Test
    void listGamesNegative() {
        assertDoesNotThrow(() -> facade.listGames("badtoken"));
    }

    @Test
    void createGamePositive() {
        String authToken = registerUser("user1", "password1", "email@test.com");
        System.setIn(new ByteArrayInputStream("TestGame\n".getBytes()));
        assertDoesNotThrow(() -> facade.createGame(authToken));
    }

    @Test
    void createGameNegative() {
        System.setIn(new ByteArrayInputStream("TestGame\n".getBytes()));
        assertDoesNotThrow(() -> facade.createGame("badtoken"));
    }

    @Test
    void observeGamePositive() {
        String authToken = registerUser("user1", "password1", "email@test.com");
        createGame(authToken, "TestGame");
        System.setIn(new ByteArrayInputStream("1\n".getBytes()));
        assertDoesNotThrow(() -> facade.observeGame(authToken));
    }

    @Test
    void observeGameNegative() {
        assertDoesNotThrow(() -> facade.observeGame("badtoken"));
    }

    @Test
    void joinGamePositive() {
        String authToken = registerUser("user1", "password1", "email@test.com");
        createGame(authToken, "TestGame");
        System.setIn(new ByteArrayInputStream("1\nWHITE\n".getBytes()));
        assertDoesNotThrow(() -> facade.joinGame(authToken));
    }

    @Test
    void joinGameNegative() {
        assertDoesNotThrow(() -> facade.joinGame("badtoken"));
    }
}
