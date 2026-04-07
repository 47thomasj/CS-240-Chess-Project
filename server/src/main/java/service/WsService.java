package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import models.AuthData;
import models.GameData;
import chess.ChessGame;

import java.util.Objects;

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

    public String isPlayerInCheck(String authToken, int gameID) throws DataAccessException {
        AuthData authData = authDAO.readAuth(authToken);
        GameData game = gameDAO.readGame(gameID);
        if (Objects.equals(game.blackUsername(), authData.username()) && game.game().isInCheck(ChessGame.TeamColor.WHITE)) {
            return game.whiteUsername();
        } else if (Objects.equals(game.whiteUsername(), authData.username()) && game.game().isInCheck(ChessGame.TeamColor.BLACK)) {
            return game.blackUsername();
        }
        return null;
    }

    public String isPlayerInCheckmate(String authToken, int gameID) throws DataAccessException {
        AuthData authData = authDAO.readAuth(authToken);
        GameData game = gameDAO.readGame(gameID);
        if (Objects.equals(game.blackUsername(), authData.username()) && game.game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
            return game.whiteUsername();
        } else if (Objects.equals(game.whiteUsername(), authData.username()) && game.game().isInCheckmate(ChessGame.TeamColor.BLACK)) {
            return game.blackUsername();
        }
        return null;
    }

    public String isPlayerInStalemate(String authToken, int gameID) throws DataAccessException {
        AuthData authData = authDAO.readAuth(authToken);
        GameData game = gameDAO.readGame(gameID);
        if (Objects.equals(game.blackUsername(), authData.username()) && game.game().isInStalemate(ChessGame.TeamColor.WHITE)) {
            return game.whiteUsername();
        } else if (Objects.equals(game.whiteUsername(), authData.username()) && game.game().isInStalemate(ChessGame.TeamColor.BLACK)) {
            return game.blackUsername();
        }
        return null;
    }
}