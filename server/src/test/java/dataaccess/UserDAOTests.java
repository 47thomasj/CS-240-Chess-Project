package dataaccess;

import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import models.UserData;

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
        DataAccessException exception = Assertions.assertThrows(DataAccessException.class, () -> freshDAO.readUser(testUser.username()));
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
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
    @DisplayName("Test that a user's password is stored as a hash")
    public void passwordIsHashed() throws DataAccessException {
        userDAO.createUser(testUser);
        UserData result = userDAO.readUser(testUser.username());
        Assertions.assertNotEquals(testUser.password(), result.password(), "Password should not be stored raw");
        String message = "Stored password should be a valid BCrypt hash of the original";
        Assertions.assertTrue(BCrypt.checkpw(testUser.password(), result.password()), message);
    }

    @Test
    @Order(6)
    @DisplayName("Test that the database can be cleared")
    public void clearDatabase() throws DataAccessException {
        userDAO.createUser(testUser);
        userDAO.clear();
        DataAccessException exception = Assertions.assertThrows(DataAccessException.class, () -> userDAO.readUser(testUser.username()));
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("Test that an exception is thrown when a user is created with a duplicate username")
    public void createDuplicateUser() throws DataAccessException {
        userDAO.createUser(testUser);
        Assertions.assertThrows(
            DataAccessException.class,
            () -> userDAO.createUser(testUser)
        );
    }

    @Test
    @Order(8)
    @DisplayName("Test that DAO throws an exception when a user is read that does not exist")
    public void readNonExistentUser() {
        DataAccessException exception = Assertions.assertThrows(DataAccessException.class, () -> userDAO.readUser("nonExistentUser"));
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }
}
