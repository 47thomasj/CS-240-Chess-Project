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
import models.results.RegisterResult;

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

    public void login(LoginRequest loginRequest) {}
    public void logout(LogoutRequest logoutRequest) {}
}
