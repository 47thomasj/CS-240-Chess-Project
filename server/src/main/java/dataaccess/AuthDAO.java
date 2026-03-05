package dataaccess;

import model.AuthData;
import java.sql.*;

public class AuthDAO {

    public AuthDAO() {
        try {
            DatabaseManager.configureDatabase(createStatements);
        } catch (DataAccessException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void createAuth(AuthData authData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.setString(2, authData.username());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: unable to create auth: %s", ex.getMessage()));
        }
    }

    public AuthData readAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    } else {
                        throw new DataAccessException("Error: unauthorized");
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: unable to read auth: %s", ex.getMessage()));
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auth WHERE authToken=?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                int deleted = preparedStatement.executeUpdate();
                if (deleted == 0)
                    throw new DataAccessException("Error: unauthorized");
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: unable to delete auth: %s", ex.getMessage()));
        }
    }

    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auth";
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: unable to clear auth: %s", ex.getMessage()));
        }
    }

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS auth (
        `authToken` VARCHAR(255) NOT NULL,
        `username` VARCHAR(255) NOT NULL,
        PRIMARY KEY (`authToken`)
        )
        """
    };

    private AuthData readAuth(ResultSet rs) throws DataAccessException {
        try {
            return new AuthData(rs.getString("authToken"), rs.getString("username"));
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: unable to read auth: %s", ex.getMessage()));
        }
    }
}
