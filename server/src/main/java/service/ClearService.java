package service;

import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;
import dataaccess.DataAccessException;

public class ClearService {
    private final AuthDataAccess authClass;
    private final UserDataAccess userClass;
    private final GameDataAccess gameClass;

    public ClearService(UserDataAccess u, AuthDataAccess a, GameDataAccess g) {
        authClass = a;
        userClass = u;
        gameClass = g;
    }

    /**
     * clears all server data
     */
    public void clear() throws DataAccessException {
        authClass.clear();
        userClass.clear();
        gameClass.clear();
    }
}