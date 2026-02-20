package service;

import org.junit.jupiter.api.*;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

import passoff.model.*;
import models.requests.*;
import models.results.*;

import dataaccess.DataAccessException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameServiceTests {
    
    private static GameDAO gameDAO;
    private static UserDAO userDAO;
    private static AuthDAO authDAO;

    private static TestUser testUser;
    private static TestUser badUser;

    private static GameService gameService;
    private static UserService userService;

    @BeforeAll
    public static void init() {

        gameDAO = new GameDAO();
        userDAO = new UserDAO();
        authDAO = new AuthDAO();

        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);

        testUser = new TestUser("testUser", "testPass", "test@mail.com");
        badUser = new TestUser("badUser", "badPass", "bad@mail.com");

        try {
            userService.register(new RegisterRequest(testUser.getUsername(), testUser.getPassword(), testUser.getEmail()));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void setup() {
        gameDAO.clear();
    }

    @Test
    @Order(1)
    @DisplayName("List Games Valid User")
    public void listGamesSuccess() throws DataAccessException {
        String authToken = userService.login(new LoginRequest(testUser.getUsername(), testUser.getPassword())).authToken();
        ListGamesResult result = gameService.listGames(new ListGamesRequest(authToken));

        Assertions.assertNotNull(result, "List games did not return a result");
        Assertions.assertNotNull(result.games(), "List games result did not contain a games array");
    }

    @Test
    @Order(2)
    @DisplayName("List Games Invalid User")
    public void listGamesUnauthorized() {
        DataAccessException exception = Assertions.assertThrows(
            DataAccessException.class,
            () -> gameService.listGames(new ListGamesRequest("not-a-real-token"))
        );
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Create Game Valid User")
    public void createGameSuccess() throws DataAccessException {
        String authToken = userService.login(new LoginRequest(testUser.getUsername(), testUser.getPassword())).authToken();
        CreateGameResult result = gameService.createGame(new CreateGameRequest(authToken, "testGame"));

        Assertions.assertNotNull(result, "Create game did not return a result");
        Assertions.assertTrue(result.gameID() > 0, "Create game did not return a valid game ID");
    }

    @Test
    @Order(4)
    @DisplayName("Create Game Invalid User")
    public void createGameUnauthorized() {
        DataAccessException exception = Assertions.assertThrows(
            DataAccessException.class,
            () -> gameService.createGame(new CreateGameRequest("not-a-real-token", "testGame"))
        );
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("Join Game Valid User")
    public void joinGameSuccess() throws DataAccessException {
        String authToken = userService.login(new LoginRequest(testUser.getUsername(), testUser.getPassword())).authToken();
        int gameID = gameService.createGame(new CreateGameRequest(authToken, "testGame")).gameID();
        JoinGameResult result = gameService.joinGame(new JoinGameRequest(authToken, "WHITE", gameID));

        Assertions.assertNotNull(result, "Join game did not return a result");
        Assertions.assertTrue(result.success(), "Join game result did not indicate success");
    }

    @Test
    @Order(6)
    @DisplayName("Join Game Invalid User")
    public void joinGameUnauthorized() throws DataAccessException {
        String authToken = userService.login(new LoginRequest(testUser.getUsername(), testUser.getPassword())).authToken();
        int gameID = gameService.createGame(new CreateGameRequest(authToken, "testGame")).gameID();

        DataAccessException exception = Assertions.assertThrows(
            DataAccessException.class,
            () -> gameService.joinGame(new JoinGameRequest("not-a-real-token", "WHITE", gameID))
        );
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("Join Game Invalid Game ID")
    public void joinGameBadGameId() throws DataAccessException {
        String authToken = userService.login(new LoginRequest(testUser.getUsername(), testUser.getPassword())).authToken();

        DataAccessException exception = Assertions.assertThrows(
            DataAccessException.class,
            () -> gameService.joinGame(new JoinGameRequest(authToken, "WHITE", 99999))
        );
        Assertions.assertEquals("Error: bad request", exception.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Join Game Color Already Taken")
    public void joinGameColorTaken() throws DataAccessException {
        String authToken = userService.login(new LoginRequest(testUser.getUsername(), testUser.getPassword())).authToken();
        int gameID = gameService.createGame(new CreateGameRequest(authToken, "testGame")).gameID();
        gameService.joinGame(new JoinGameRequest(authToken, "WHITE", gameID));

        DataAccessException exception = Assertions.assertThrows(
            DataAccessException.class,
            () -> gameService.joinGame(new JoinGameRequest(authToken, "WHITE", gameID))
        );
        Assertions.assertEquals("Error: already taken", exception.getMessage());
    }

}
