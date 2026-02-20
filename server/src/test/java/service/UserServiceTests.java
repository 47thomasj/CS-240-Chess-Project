package service;

import org.junit.jupiter.api.*;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;

import passoff.model.*;
import models.requests.*;
import models.results.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTests {

    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static UserService userService;

    private static TestUser testUser;

    @BeforeAll
    public static void init() {
        userDAO = new UserDAO();
        authDAO = new AuthDAO();
        userService = new UserService(userDAO, authDAO);

        testUser = new TestUser("testUser", "testPass", "test@mail.com");
    }

    @BeforeEach
    public void setup() {
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Normal User Registration")
    public void registerSuccess() throws DataAccessException {
        RegisterRequest request = new RegisterRequest(testUser.getUsername(), testUser.getPassword(), testUser.getEmail());
        RegisterResult result = userService.register(request);

        Assertions.assertEquals(testUser.getUsername(), result.username(), "Result did not return the registered username");
        Assertions.assertNotNull(result.authToken(), "Result did not return an auth token");
    }

    @Test
    @Order(2)
    @DisplayName("Re-Register User")
    public void registerDuplicateUsername() throws DataAccessException {
        RegisterRequest request = new RegisterRequest(testUser.getUsername(), testUser.getPassword(), testUser.getEmail());
        userService.register(request);

        DataAccessException exception = Assertions.assertThrows(
            DataAccessException.class,
            () -> userService.register(request)
        );
        Assertions.assertEquals("Error: already taken", exception.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Normal User Login")
    public void loginSuccess() throws DataAccessException {
        userService.register(new RegisterRequest(testUser.getUsername(), testUser.getPassword(), testUser.getEmail()));

        LoginRequest request = new LoginRequest(testUser.getUsername(), testUser.getPassword());
        LoginResult result = userService.login(request);

        Assertions.assertEquals(testUser.getUsername(), result.username(), "Result did not return the correct username");
        Assertions.assertNotNull(result.authToken(), "Result did not return an auth token");
    }

    @Test
    @Order(4)
    @DisplayName("Login Unregistered Username")
    public void loginNonRegisteredUsername() {
        LoginRequest request = new LoginRequest("nonRegisteredUser", "testPass");

        DataAccessException exception = Assertions.assertThrows(
            DataAccessException.class,
            () -> userService.login(request)
        );
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("Login Wrong Password")
    public void loginWrongPassword() throws DataAccessException {
        userService.register(new RegisterRequest(testUser.getUsername(), testUser.getPassword(), testUser.getEmail()));

        LoginRequest request = new LoginRequest(testUser.getUsername(), "wrongPass");

        DataAccessException exception = Assertions.assertThrows(
            DataAccessException.class,
            () -> userService.login(request)
        );
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("Normal User Logout")
    public void logoutSuccess() throws DataAccessException {
        RegisterResult registerResult = userService.register(
            new RegisterRequest(testUser.getUsername(), testUser.getPassword(), testUser.getEmail())
        );

        LogoutRequest request = new LogoutRequest(registerResult.authToken());
        LogoutResult result = userService.logout(request);

        Assertions.assertNotNull(result, "Logout did not return a result");
        Assertions.assertTrue(result.success(), "Logout result did not indicate success");
    }

    @Test
    @Order(7)
    @DisplayName("Logout Invalid Auth Token")
    public void logoutInvalidAuthToken() {
        LogoutRequest request = new LogoutRequest("not-a-real-token");

        DataAccessException exception = Assertions.assertThrows(
            DataAccessException.class,
            () -> userService.logout(request)
        );
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }
}
