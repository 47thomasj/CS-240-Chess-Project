package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.*;
import java.util.*;

public class UserDAO {

    public UserDAO() throws DataAccessException {
        configureDatabase();
    }

    public void createUser(UserData data) throws DataAccessException {
        if (userTable.containsKey(data.username())) {
            throw new DataAccessException("Error: already taken");
        }
        userTable.put(data.username(), data);
    }

    public UserData readUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to get user by username: %s", ex.getMessage()));
        }
        return null;
    }

    public void clear() {
        userTable = new HashMap<String, UserData>();
    }

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS users (
        `username` VARCHAR(255) NOT NULL,
        `password` VARCHAR(255) NOT NULL,
        `email` VARCHAR(255) NOT NULL,
        PRIMARY KEY (`username`)
        )
        """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    private UserData readUser(ResultSet rs) throws DataAccessException {
        try {
            return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to read user: %s", ex.getMessage()));
        }
    }
}
