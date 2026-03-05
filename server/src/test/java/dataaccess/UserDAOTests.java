package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOTests {

    private static UserDAO userDAO;
    private static UserData testUser;

    @BeforeAll
    public static void init() throws DataAccessException {
        userDAO = new UserDAO();
        testUser = new UserData("testUser", "testPass", "test@mail.com");
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Test that an empty table is created on init where no database exists before")
    public void initCreatesEmptyTable() throws Exception {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("DROP TABLE IF EXISTS users")) {
                ps.executeUpdate();
            }
        }
        UserDAO freshDAO = new UserDAO();
        UserData result = freshDAO.readUser(testUser.username());
        Assertions.assertNull(result, "Table should be empty after initialization from scratch");
    }

    @Test
    @Order(2)
    @DisplayName("Test that no table is created on init where a database does exist before")
    public void initPreservesExistingTable() throws DataAccessException {
        userDAO.createUser(testUser);
        UserDAO secondDAO = new UserDAO();
        UserData result = secondDAO.readUser(testUser.username());
        Assertions.assertNotNull(result, "User should still exist after re-initialization");
        Assertions.assertEquals(testUser.username(), result.username());
    }

    @Test
    @Order(3)
    @DisplayName("Test that a user can be created")
    public void createUser() {
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(testUser));
    }

    @Test
    @Order(4)
    @DisplayName("Test that a user can be read")
    public void readUser() throws DataAccessException {
        userDAO.createUser(testUser);
        UserData result = userDAO.readUser(testUser.username());
        Assertions.assertNotNull(result, "readUser should return a non-null user");
        Assertions.assertEquals(testUser.username(), result.username());
        Assertions.assertEquals(testUser.email(), result.email());
    }

    @Test
    @Order(5)
    @DisplayName("Test that the database can be cleared")
    public void clearDatabase() throws DataAccessException {
        userDAO.createUser(testUser);
        userDAO.clear();
        UserData result = userDAO.readUser(testUser.username());
        Assertions.assertNull(result, "User should not exist after clear");
    }

    @Test
    @Order(6)
    @DisplayName("Test that an exception is thrown when a user is created with a duplicate username")
    public void createDuplicateUser() throws DataAccessException {
        userDAO.createUser(testUser);
        Assertions.assertThrows(
            DataAccessException.class,
            () -> userDAO.createUser(testUser)
        );
    }

    @Test
    @Order(7)
    @DisplayName("Test that DAO returns null when a user is read that does not exist")
    public void readNonExistentUser() {
        try {
            UserData result = userDAO.readUser("nonExistentUser");
            Assertions.assertNull(result, "readUser should return null when a user does not exist");
        } catch (DataAccessException ex) {
            Assertions.fail("readUser should not throw an exception when a user does not exist");
        }
    }
}
