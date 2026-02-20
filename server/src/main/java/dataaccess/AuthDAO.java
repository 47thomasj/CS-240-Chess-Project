package dataaccess;

import model.AuthData;
import java.util.*;

public class AuthDAO {
    private HashMap<String, AuthData> authTable;
    public AuthDAO() {
        authTable = new HashMap<String, AuthData>();
    }

    public void createAuth(AuthData authData) throws DataAccessException {
        authTable.put(authData.authToken(), authData);
    }

    public AuthData readAuth(String authToken) throws DataAccessException {
        if (!authTable.containsKey(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }
        return authTable.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authTable.containsKey(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }
        authTable.remove(authToken);
    }

    public void clear() {
        authTable = new HashMap<String, AuthData>();
    }
}
