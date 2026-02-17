package service;


import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import models.Authtoken;
import models.requests.LoginRequest;
import models.requests.LogoutRequest;
import models.requests.RegisterRequest;
import models.results.LoginResult;
import models.results.LogoutResult;
import models.results.RegisterResult;

import java.util.Objects;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userDAO.createUser(user);

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
        if (!Objects.equals(userData.password(), loginRequest.password())) {
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
