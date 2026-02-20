package dataaccess;

import model.UserData;

import java.util.*;

public class UserDAO {

    private HashMap<String, UserData> userTable;
    public UserDAO() {
        userTable = new HashMap<String, UserData>();
    }

    public void createUser(UserData data) throws DataAccessException {
        if (userTable.containsKey(data.username())) throw new DataAccessException("Error: already taken");
        userTable.put(data.username(), data);
    }

    public UserData readUser(String username) throws DataAccessException {
        if (!userTable.containsKey(username)) throw new DataAccessException("Error: unauthorized");
        return userTable.get(username);
    }

    public void clear() {
        userTable = new HashMap<String, UserData>();
    }
}
