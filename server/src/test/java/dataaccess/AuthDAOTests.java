package dataaccess;

import org.junit.jupiter.api.*;

import models.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthDAOTests {

    private static AuthDAO authDAO;
    private static AuthData testAuth;

    @BeforeAll
    public static void init() {
        authDAO = new AuthDAO();
        testAuth = new AuthData("test-token", "testUser");
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        authDAO.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Test that an empty table is created on init where no database exists before")
    public void initCreatesEmptyTable() throws Exception {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("DROP TABLE IF EXISTS auth")) {
                ps.executeUpdate();
            }
        }
        AuthDAO freshDAO = new AuthDAO();
        DataAccessException exception = Assertions.assertThrows(
            DataAccessException.class,
            () -> freshDAO.readAuth(testAuth.authToken())
        );
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    @Order(2)
    @DisplayName("Test that no table is created on init where a database does exist before")
    public void initPreservesExistingTable() throws DataAccessException {
        authDAO.createAuth(testAuth);
        AuthDAO secondDAO = new AuthDAO();
        AuthData result = secondDAO.readAuth(testAuth.authToken());
        Assertions.assertEquals(testAuth.authToken(), result.authToken());
        Assertions.assertEquals(testAuth.username(), result.username());
    }

    @Test
    @Order(3)
    @DisplayName("Test that an auth can be created")
    public void createAuth() {
        Assertions.assertDoesNotThrow(() -> authDAO.createAuth(testAuth));
    }

    @Test
    @Order(4)
    @DisplayName("Test that an auth can be read (returns authData)")
    public void readAuth() throws DataAccessException {
        authDAO.createAuth(testAuth);
        AuthData result = authDAO.readAuth(testAuth.authToken());
        Assertions.assertNotNull(result, "readAuth should return a non-null AuthData");
        Assertions.assertEquals(testAuth.authToken(), result.authToken());
        Assertions.assertEquals(testAuth.username(), result.username());
    }

    @Test
    @Order(5)
    @DisplayName("Test that an auth can be deleted")
    public void deleteAuth() throws DataAccessException {
        authDAO.createAuth(testAuth);
        authDAO.deleteAuth(testAuth.authToken());
        Assertions.assertThrows(
            DataAccessException.class,
            () -> authDAO.readAuth(testAuth.authToken())
        );
    }

    @Test
    @Order(6)
    @DisplayName("Test that the database can be cleared")
    public void clearDatabase() throws DataAccessException {
        authDAO.createAuth(testAuth);
        authDAO.clear();
        Assertions.assertThrows(
            DataAccessException.class,
            () -> authDAO.readAuth(testAuth.authToken())
        );
    }

    @Test
    @Order(7)
    @DisplayName("Test that an exception is thrown when a auth is created with a duplicate authToken")
    public void createDuplicateAuth() throws DataAccessException {
        authDAO.createAuth(testAuth);
        Assertions.assertThrows(
            DataAccessException.class,
            () -> authDAO.createAuth(testAuth)
        );
    }

    @Test
    @Order(8)
    @DisplayName("Test that DAO throws an exception for an invalid auth when reading")
    public void readInvalidAuth() {
        DataAccessException exception = Assertions.assertThrows(
            DataAccessException.class,
            () -> authDAO.readAuth("nonExistentToken")
        );
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }
}
