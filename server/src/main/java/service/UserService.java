package service;


import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import models.AuthData;
import models.Authtoken;
import models.UserData;
import models.requests.LoginRequest;
import models.requests.LogoutRequest;
import models.requests.RegisterRequest;
import models.results.LoginResult;
import models.results.LogoutResult;
import models.results.RegisterResult;

import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        boolean usernameNull = registerRequest.username() == null;
        boolean passwordNull = registerRequest.password() == null;
        boolean emailNull = registerRequest.email() == null;
        if (usernameNull || passwordNull || emailNull) {
            throw new DataAccessException("Error: bad request");
        }
        
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        try {
            userDAO.createUser(user);
        } catch (DataAccessException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Error: already taken");
            } else {
                throw new DataAccessException(ex.getMessage());
            }
        }

        Authtoken token = new Authtoken();
        AuthData authData = new AuthData(token.getAuthToken(), user.username());
        authDAO.createAuth(authData);

        return new RegisterResult(user.username(), token.getAuthToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        boolean usernameNull = loginRequest.username() == null;
        boolean passwordNull = loginRequest.password() == null;
        if (usernameNull || passwordNull) {
            throw new DataAccessException("Error: bad request");
        } 
        
        UserData userData = userDAO.readUser(loginRequest.username());
        if (!BCrypt.checkpw(loginRequest.password(), userData.password())) {
            throw new DataAccessException("Error: unauthorized");
        }

        Authtoken token = new Authtoken();
        AuthData authData = new AuthData(token.getAuthToken(), loginRequest.username());
        authDAO.createAuth(authData);

        return new LoginResult(loginRequest.username(), token.getAuthToken());
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {
        authDAO.deleteAuth(logoutRequest.authToken());

        return new LogoutResult(true);
    }
}
