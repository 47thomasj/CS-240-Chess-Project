package client;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import java.io.ByteArrayInputStream;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import models.GameData;
import models.results.ListGamesResult;
import chess.ChessGame;

public class ServerFacadeTests {

    private HttpClient mockHttpClient;
    private GamesManager gamesManager;
    private ServerFacade facade;
    private final Gson gson = new Gson();

    @BeforeEach
    void setup() {
        mockHttpClient = mock(HttpClient.class);
        gamesManager = new GamesManager(new GameData[0]);
        facade = new ServerFacade("http://localhost", gamesManager, mockHttpClient);
    }

    @SuppressWarnings("unchecked")
    private void stubResponse(int status, String body) throws Exception {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(status);
        when(mockResponse.body()).thenReturn(body);
        doReturn(mockResponse).when(mockHttpClient).send(any(), any());
    }

    @SuppressWarnings("unchecked")
    private void stubResponses(int s1, String b1, int s2, String b2) throws Exception {
        HttpResponse<String> r1 = mock(HttpResponse.class);
        when(r1.statusCode()).thenReturn(s1);
        when(r1.body()).thenReturn(b1);
        HttpResponse<String> r2 = mock(HttpResponse.class);
        when(r2.statusCode()).thenReturn(s2);
        when(r2.body()).thenReturn(b2);
        doReturn(r1).doReturn(r2).when(mockHttpClient).send(any(), any());
    }

    private String listGamesJsonWithOneGame() {
        GameData game = new GameData(1, null, null, "TestGame", new ChessGame());
        return gson.toJson(new ListGamesResult(new GameData[]{ game }));
    }

    @Test
    void registerPositive() throws Exception {
        stubResponse(200, "{\"username\":\"user1\",\"authToken\":\"tok1\"}");
        System.setIn(new ByteArrayInputStream("user1\npassword1\nemail@test.com\n".getBytes()));
        String authToken = facade.register(null, null);
        assertNotNull(authToken);
        assertEquals("tok1", authToken);
    }

    @Test
    void registerNegative() throws Exception {
        stubResponse(403, "{\"message\":\"Error: already taken\"}");
        System.setIn(new ByteArrayInputStream("user1\npassword1\nemail@test.com\n".getBytes()));
        String authToken = facade.register(null, null);
        assertNull(authToken);
    }

    @Test
    void loginPositive() throws Exception {
        stubResponse(200, "{\"username\":\"user1\",\"authToken\":\"tok1\"}");
        System.setIn(new ByteArrayInputStream("user1\npassword1\n".getBytes()));
        String authToken = facade.login(null);
        assertNotNull(authToken);
        assertEquals("tok1", authToken);
    }

    @Test
    void loginNegative() throws Exception {
        stubResponse(401, "{\"message\":\"Error: unauthorized\"}");
        System.setIn(new ByteArrayInputStream("user1\nwrongpassword\n".getBytes()));
        String authToken = facade.login(null);
        assertNull(authToken);
    }

    @Test
    void logoutPositive() throws Exception {
        stubResponse(200, "{}");
        boolean result = facade.logout("tok1", null, null);
        assertTrue(result);
    }

    @Test
    void logoutNegative() throws Exception {
        stubResponse(401, "{\"message\":\"Error: unauthorized\"}");
        boolean result = facade.logout("badtoken", null, null);
        assertFalse(result);
    }

    @Test
    void listGamesPositive() throws Exception {
        stubResponse(200, "{\"games\":[]}");
        assertDoesNotThrow(() -> facade.listGames("tok1"));
    }

    @Test
    void listGamesNegative() throws Exception {
        stubResponse(401, "{\"message\":\"Error: unauthorized\"}");
        assertDoesNotThrow(() -> facade.listGames("badtoken"));
    }

    @Test
    void createGamePositive() throws Exception {
        stubResponse(200, "{\"gameID\":1}");
        System.setIn(new ByteArrayInputStream("TestGame\n".getBytes()));
        assertDoesNotThrow(() -> facade.createGame("tok1"));
    }

    @Test
    void createGameNegative() throws Exception {
        stubResponse(401, "{\"message\":\"Error: unauthorized\"}");
        System.setIn(new ByteArrayInputStream("TestGame\n".getBytes()));
        assertDoesNotThrow(() -> facade.createGame("badtoken"));
    }

    @Test
    void observeGamePositive() throws Exception {
        stubResponse(200, listGamesJsonWithOneGame());
        System.setIn(new ByteArrayInputStream("1\n".getBytes()));
        assertDoesNotThrow(() -> facade.observeGame("tok1"));
    }

    @Test
    void observeGameNegative() throws Exception {
        stubResponse(401, "{\"message\":\"Error: unauthorized\"}");
        assertDoesNotThrow(() -> facade.observeGame("badtoken"));
    }

    @Test
    void joinGamePositive() throws Exception {
        stubResponses(200, listGamesJsonWithOneGame(), 200, "{\"success\":true}");
        System.setIn(new ByteArrayInputStream("1\nWHITE\n".getBytes()));
        assertDoesNotThrow(() -> facade.joinGame("tok1"));
    }

    @Test
    void joinGameNegative() throws Exception {
        stubResponse(401, "{\"message\":\"Error: unauthorized\"}");
        assertDoesNotThrow(() -> facade.joinGame("badtoken"));
    }
}
