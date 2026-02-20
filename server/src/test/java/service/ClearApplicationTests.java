package service;

import dataaccess.UserDAO;

import org.junit.jupiter.api.*;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClearApplicationTests {
    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;

    private static ClearApplicationService clearApplicationService;

    @BeforeAll
    public static void init() {
        userDAO = new UserDAO();
        authDAO = new AuthDAO();
        gameDAO = new GameDAO();
        clearApplicationService = new ClearApplicationService(authDAO, gameDAO, userDAO);
    }

    @Test
    @Order(1)
    @DisplayName("Clear Application")
    public void clearApplication() {
        Assertions.assertDoesNotThrow(() -> clearApplicationService.clear(), "Clear application threw an exception");
    }
}
