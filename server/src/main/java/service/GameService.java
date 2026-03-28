package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import models.AuthData;
import models.GameData;
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
        if (request.gameName() == null) {
            throw new DataAccessException("Error: bad request");
        }

        GameData gameData = new GameData(0, null, null, request.gameName(), new ChessGame());
        int gameId = gameDAO.createGame(gameData);
        if (gameId == -1) {
            throw new DataAccessException("Error: unable to create game");
        }
        return new CreateGameResult(gameId);
    }

    public JoinGameResult joinGame(JoinGameRequest request) throws DataAccessException {
        AuthData authData = authDAO.readAuth(request.authToken());

        boolean isBadColor = request.playerColor() == null || !(request.playerColor().equals("WHITE") || request.playerColor().equals("BLACK"));
        boolean isBadGameID = request.gameID() == null || request.gameID() <= 0;
        if (isBadColor || isBadGameID) {
            throw new DataAccessException("Error: bad request");
        }

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

    public void leaveGame(LeaveGameRequest request) throws DataAccessException {
        AuthData authData = authDAO.readAuth(request.authToken());

        boolean isBadGameID = request.gameID() == null || request.gameID() <= 0;
        if (isBadGameID) {
            throw new DataAccessException("Error: bad request");
        }
    }
}
