package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import models.requests.CreateGameRequest;
import models.requests.ListGamesRequest;
import models.results.CreateGameResult;
import models.results.ListGamesResult;

import java.util.Arrays;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    ListGamesResult listGames(ListGamesRequest request) throws DataAccessException {
        authDAO.readAuth(request.authToken());

        GameData[] gamesList = gameDAO.listGames();
        return new ListGamesResult(gamesList);
    }

    CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        authDAO.readAuth(request.authToken());

        int gameId = gameDAO.getHighestGameID() + 1;
        GameData gameData = new GameData(gameId, "", "", request.gameName(), new ChessGame());
        gameDAO.createGame(gameData);
        return new CreateGameResult(gameId);
    }
}
