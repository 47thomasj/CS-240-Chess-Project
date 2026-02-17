package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import models.requests.CreateGameRequest;
import models.requests.JoinGameRequest;
import models.requests.ListGamesRequest;
import models.results.CreateGameResult;
import models.results.JoinGameResult;
import models.results.ListGamesResult;

import java.util.Objects;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException {
        authDAO.readAuth(request.authToken());

        GameData[] gamesList = gameDAO.listGames();
        return new ListGamesResult(gamesList);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        authDAO.readAuth(request.authToken());
        if (request.gameName() == null) throw new DataAccessException("Error: bad request");

        int gameId = gameDAO.getHighestGameID() + 1;
        GameData gameData = new GameData(gameId, null, null, request.gameName(), new ChessGame());
        gameDAO.createGame(gameData);
        return new CreateGameResult(gameId);
    }

    public JoinGameResult joinGame(JoinGameRequest request) throws DataAccessException {
        AuthData authData = authDAO.readAuth(request.authToken());

        boolean isBadColor = request.playerColor() == null || !(request.playerColor().equals("WHITE") || request.playerColor().equals("BLACK"));
        boolean isBadGameID = request.gameID() == null || request.gameID() <= 0;
        if (isBadColor || isBadGameID) throw new DataAccessException("Error: bad request");

        GameData game = gameDAO.readGame(request.gameID());

        String gamePlayerColor = Objects.equals(request.playerColor(), "WHITE") ? game.whiteUsername() : game.blackUsername();
        if (!Objects.equals(gamePlayerColor, null)) {
            throw new DataAccessException("Error: already taken");
        }

        String whiteUsername = Objects.equals(request.playerColor(), "WHITE") ? authData.username() : game.whiteUsername();
        String blackUsername = Objects.equals(request.playerColor(), "BLACK") ? authData.username() : game.blackUsername();
        GameData updatedGame = new GameData(request.gameID(), whiteUsername, blackUsername, game.gameName(), game.game());

        gameDAO.updateGame(updatedGame);

        return new JoinGameResult(true);
    }
}
