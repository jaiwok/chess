package service;

import dataaccess.*;
import model.*;
import service.exceptions.*;

public class UserService {
    private final UserDataAccess userClass;
    private final AuthDataAccess authClass;

    public UserService(UserDataAccess u, AuthDataAccess a) {
        userClass = u;
        authClass = a;
    }

    /**
     * Adds a new user with username, password, and email
     *
     * @param user the user to add to the users database
     * @return an auth token associated with the new user
     * @throws DataAccessException Error to throw if bad user, or username in use
     */
    public AuthData register(UserData user) throws DataAccessException, NameAlreadyInUseException, FaultyRequestException{
        AuthData authData;

        if(user != null && user.username() != null && user.password() != null) {
            if (!userClass.usernameInUse(user.username())) {
                userClass.add(user);

                authData = AuthData.generateToken(user.username());
                authClass.add(authData);

                return authData;
            } else {
                throw new NameAlreadyInUseException("Error: Username already taken");
            }
        } else {
            throw new FaultyRequestException("Error: Bad register user request");
        }
    }

    /**
     * logs in a user if username and password are correct
     *
     * @param username username to log in with
     * @param password password to log in with
     * @return a new auth token associated with this user
     */
    public AuthData login(String username, String password) throws DataAccessException, UnauthorizedUserException, FaultyRequestException{
        AuthData authData;

        if (username != null && password != null) {
            if (userClass.validateLogin(username, password)) {

                authData = AuthData.generateToken(username);
                authClass.add(authData);

                return authData;
            } else{
                throw new UnauthorizedUserException("Error: Unauthorized");
            }
        } else{
            throw new FaultyRequestException("Error: Bad login user request");
        }
    }

    /**
     * removes the authToken associated with the user
     *
     * @param authToken the auth token belonging to the user
     */
    public void logout(String authToken) throws DataAccessException, UnauthorizedUserException {

        if (authClass.findAuthDataByToken(authToken) != null) {
            authClass.remove(authToken);
        } else {
            throw new UnauthorizedUserException("Error: Unauthorized");
        }
    }
}
