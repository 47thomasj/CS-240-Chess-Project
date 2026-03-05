package dataaccess;

import model.GameData;
import chess.ChessGame;
import com.google.gson.Gson;

import java.util.*;
import java.sql.*;

public class GameDAO {
    private final Gson gson;

    public GameDAO() {
        gson = new Gson();
        try {
            DatabaseManager.configureDatabase(createStatements);
        } catch (DataAccessException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public int createGame(GameData data) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, data.whiteUsername());
                preparedStatement.setString(2, data.blackUsername());
                preparedStatement.setString(3, data.gameName());
                preparedStatement.setString(4, gson.toJson(data.game()));
                preparedStatement.executeUpdate();
                try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                throw new DataAccessException("Error: game id already taken");
            }
            throw new DataAccessException(String.format("Error: unable to create game: %s", ex.getMessage()));
        }
        return -1;

    }

    public GameData readGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    } else {
                        throw new DataAccessException("Error: bad request");
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: unable to get game by game id: %s", ex.getMessage()));
        }
    }

    public void updateGame(GameData data) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, data.whiteUsername());
                preparedStatement.setString(2, data.blackUsername());
                preparedStatement.setString(3, data.gameName());
                preparedStatement.setString(4, gson.toJson(data.game()));
                preparedStatement.setInt(5, data.gameID());
                int updated = preparedStatement.executeUpdate();
                if (updated == 0) {
                    throw new DataAccessException("Error: invalid game id");
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: unable to update game: %s", ex.getMessage()));
        }
    }

    public GameData[] listGames() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    List<GameData> games = new ArrayList<>();
                    while (rs.next()) {
                        games.add(readGame(rs));
                    }
                    return games.toArray(new GameData[0]);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: unable to list games: %s", ex.getMessage()));
        }
    }

    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM games";
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: unable to clear games: %s", ex.getMessage()));
        }
    }

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS games (
        `gameID` INT NOT NULL AUTO_INCREMENT,
        `whiteUsername` VARCHAR(255),
        `blackUsername` VARCHAR(255),
        `gameName` VARCHAR(255) NOT NULL,
        `game` BLOB NOT NULL,
        PRIMARY KEY (`gameID`)
        )
        """
    };

    private GameData readGame(ResultSet rs) throws DataAccessException {
        try {
            ChessGame game = gson.fromJson(rs.getString("game"), ChessGame.class);
            return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"), game);
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: unable to read user: %s", ex.getMessage()));
        }
    }
}
