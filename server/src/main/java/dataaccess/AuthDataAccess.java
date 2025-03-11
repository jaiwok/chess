package dataaccess;

import model.AuthData;
import java.sql.SQLException;

public interface AuthDataAccess {
    /**
     * Clears all authData
     *
     */
    void clear() throws DataAccessException, SQLException;

    /**
     * Adds an authData object
     *
     * @param authData the object to add
     */
    void add(AuthData authData) throws DataAccessException;

    /**
     * finds and returns authData of a user
     *
     * @param username name of the user's authentication data
     * @return the authentication data
     */
    AuthData findAuthFromUsername(String username) throws DataAccessException;

    /**
     * retrieves a user's auth data with the given token
     *
     * @param token of the data to retrieve
     * @return the authentication data
     */
    AuthData findAuthDataByToken(String token) throws DataAccessException;

    /**
     * Removes an auth token when logging out
     *
     * @param authToken the auth token to remove, error if not found
     */
    void remove(String authToken) throws DataAccessException;
}