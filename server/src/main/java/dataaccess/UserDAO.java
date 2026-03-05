package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserDAO {

    public UserDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void createUser(UserData data) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(data.password(), BCrypt.gensalt());
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, data.username());
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, data.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to create user: %s", ex.getMessage()));
        }
    }

    public UserData readUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    } else {
                        throw new DataAccessException("Error: unauthorized");
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to get user by username: %s", ex.getMessage()));
        }
    }

    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM users";
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to clear users: %s", ex.getMessage()));
        }
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
