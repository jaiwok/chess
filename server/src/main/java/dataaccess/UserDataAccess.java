package dataaccess;

import model.UserData;
import service.exceptions.NameAlreadyInUseException;

public interface UserDataAccess {
    /**
     * Clears the database of all users
     */
    void clear() throws DataAccessException;

    /**
     * Add userData object
     *
     * @param user the new user
     */
    void add(UserData user) throws DataAccessException, NameAlreadyInUseException;

    /**
     * retrieves userdata for the given username
     *
     * @param username the username of the userdata object to retrieve and return
     * @return userdata object
     */
    UserData getUser(String username) throws DataAccessException;


    /**
     * Checks if the username and password match an existing userdata object
     *
     * @param username the username entered when logging in
     * @param password the password entered when logging in
     * @return if there is a matching userdata object with the same username and password
     */
    boolean validateLogin(String username, String password) throws DataAccessException;


    /**
     * Checks if the username is already in use
     *
     * @param username the username entered when creating a new user
     * @return if the username is being used
     */
    boolean usernameInUse(String username) throws DataAccessException;

}