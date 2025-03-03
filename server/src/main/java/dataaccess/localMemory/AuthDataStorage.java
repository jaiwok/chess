package dataaccess.localMemory;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import model.AuthData;

import java.util.HashMap;

public class AuthDataStorage implements AuthDataAccess {
    final private HashMap<String, AuthData> authMap = new HashMap<>();

    /**
     * Clears all authData
     */
    public void clear(){
        authMap.clear();
    }

    /**
     * Adds an authData object
     *
     * @param authData the object to add
     */
    public void add(AuthData authData){
        authMap.put(authData.authToken(), authData);
    }

    /**
     * finds and returns authData of a user
     *
     * @param username name of the user's authentication data
     * @return the authentication data
     */
    public AuthData findAuthFromUsername(String username) {
        return authMap.get(username);
    }

    /**
     * retrieves a user's authMap data with the given token
     *
     * @param token of the data to retrieve
     * @return the authentication data
     */
    public AuthData findAuthDataByToken(String token){
        return authMap.get(token);
    }

    /**
     * Removes an authMap token when logging out
     *
     * @param authToken the authMap token to remove, error if not found
     */
    public void remove(String authToken){
        authMap.remove(authToken);
    }

}