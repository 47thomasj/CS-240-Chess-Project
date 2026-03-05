package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;

public class ClearApplicationService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;
    public ClearApplicationService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public void clear() throws DataAccessException {
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
    }
}
