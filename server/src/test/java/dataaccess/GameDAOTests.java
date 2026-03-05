package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.GameData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameDAOTests {

    private static GameDAO gameDAO;
    private static GameData testGameData;

    @BeforeAll
    public static void init() {
        gameDAO = new GameDAO();
        testGameData = new GameData(0, null, null, "testGame", new ChessGame());
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        gameDAO.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Test that an empty table is created on init where no database exists before")
    public void initCreatesEmptyTable() throws Exception {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("DROP TABLE IF EXISTS games")) {
                ps.executeUpdate();
            }
        }
        GameDAO freshDAO = new GameDAO();
        DataAccessException exception = Assertions.assertThrows(
            DataAccessException.class,
            () -> freshDAO.readGame(-1)
        );
        Assertions.assertEquals("Error: bad request", exception.getMessage());
    }

    @Test
    @Order(2)
    @DisplayName("Test that no table is created on init where a database does exist before")
    public void initPreservesExistingTable() throws DataAccessException {
        int gameID = gameDAO.createGame(testGameData);
        GameDAO secondDAO = new GameDAO();
        GameData result = secondDAO.readGame(gameID);
        Assertions.assertNotNull(result, "Game should still exist after re-initialization");
    }

    @Test
    @Order(3)
    @DisplayName("Test that a game can be created")
    public void createGame() {
        Assertions.assertDoesNotThrow(() -> gameDAO.createGame(testGameData));
    }

    @Test
    @Order(4)
    @DisplayName("Test that a game can be read (Create a chess game, then add it, then read it back, and assert that the resulting game obj is eq to the original one)")
    public void readGame() throws DataAccessException {
        ChessGame game = new ChessGame();
        int gameID = gameDAO.createGame(new GameData(0, "white", "black", "testGame", game));
        GameData result = gameDAO.readGame(gameID);
        Assertions.assertEquals(game, result.game(), "Deserialized game should equal the original game");
    }

    @Test
    @Order(5)
    @DisplayName("Test that requesting to read a game that isn't in the database throws \"Error: bad request\"")
    public void readNonExistentGame() {
        DataAccessException exception = Assertions.assertThrows(
            DataAccessException.class,
            () -> gameDAO.readGame(-1)
        );
        Assertions.assertEquals("Error: bad request", exception.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("Test that a game can be updated (Create a chess game, then add it, then change it's state by making a move, then update it, then read it back, and assert that the resulting game obj is eq to the updated one)")
    public void updateGame() throws Exception {
        ChessGame game = new ChessGame();
        int gameID = gameDAO.createGame(new GameData(0, "white", "black", "testGame", game));
        game.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));
        gameDAO.updateGame(new GameData(gameID, "white", "black", "testGame", game));
        GameData result = gameDAO.readGame(gameID);
        Assertions.assertEquals(game, result.game(), "Deserialized game should equal the updated game");
    }

    @Test
    @Order(7)
    @DisplayName("Test that requesting to update a game that isn't in the database throws \"Error: invalid game id\"")
    public void updateNonExistentGame() {
        DataAccessException exception = Assertions.assertThrows(
            DataAccessException.class,
            () -> gameDAO.updateGame(new GameData(-1, "white", "black", "testGame", new ChessGame()))
        );
        Assertions.assertEquals("Error: invalid game id", exception.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Test that the database can be cleared")
    public void clearDatabase() throws DataAccessException {
        int gameID = gameDAO.createGame(testGameData);
        gameDAO.clear();
        Assertions.assertThrows(
            DataAccessException.class,
            () -> gameDAO.readGame(gameID)
        );
    }

    @Test
    @Order(9)
    @DisplayName("Test that a database with 2+ games in it can have those games listed")
    public void listGames() throws DataAccessException {
        gameDAO.createGame(testGameData);
        gameDAO.createGame(testGameData);
        GameData[] games = gameDAO.listGames();
        Assertions.assertEquals(2, games.length, "listGames should return all inserted games");
    }
}
