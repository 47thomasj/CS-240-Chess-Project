package dataaccess;

import model.UserData;

import java.util.*;

public class UserDAO {

    private HashMap<String, UserData> userTable;
    public UserDAO() {
        userTable = new HashMap<String, UserData>();
    }

    public void createUser(UserData data) throws DataAccessException {
        if (userTable.containsKey(data.username())) throw new DataAccessException("Error: username already taken");
        userTable.put(data.username(), data);
    }

    public UserData readUser(String username) throws DataAccessException {
        if (!userTable.containsKey(username)) throw new DataAccessException("Error: bad request");
        return userTable.get(username);
    }
    public void updateUser(UserData data) throws DataAccessException {
        if (!userTable.containsKey(data.username())) throw new DataAccessException("Error: bad request");
        userTable.replace(data.username(), data);
    }
    public void deleteUser(UserData data) throws DataAccessException {
        if (!userTable.containsKey(data.username())) throw new DataAccessException("Error: bad request");
        userTable.remove(data.username());
    }

    public void clear() {
        userTable = new HashMap<String, UserData>();
    }
}
