package model;

import java.util.UUID;

public record AuthData(String authToken, String username){

    /**
     * generates auth tokens for users given a username
     *
     * @param username the name of a user for which needs an authToken
     * @return the authToken and username
     */
    public static AuthData generateToken(String username){
        return new AuthData(UUID.randomUUID().toString(), username);
    }
}
