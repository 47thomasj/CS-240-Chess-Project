package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import models.AuthData;
import models.GameData;

public class WsService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    public WsService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public String getUsername(String authToken) throws DataAccessException {
        AuthData authData = authDAO.readAuth(authToken);
        return authData.username();
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return gameDAO.readGame(gameID);
    }
}