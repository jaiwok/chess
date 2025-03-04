package dataaccess.localmemory;

import dataaccess.UserDataAccess;
import model.UserData;
import service.exceptions.NameAlreadyInUseException;

import java.util.HashMap;
import java.util.Objects;

public class UserDataStorage implements UserDataAccess {
    final private HashMap<String, UserData> usersMap = new HashMap<>();

    /**
     * Clears the database of all users
     */
    public void clear() {
        usersMap.clear();
    }

    /**
     * Add userData object
     *
     * @param user the new user
     */
    public void add(UserData user) throws NameAlreadyInUseException {
        String username = user.username();
        UserData newUser = new UserData(username, user.password(), user.email());

        if(getUser(username) == null){
            usersMap.put(newUser.username(), newUser);
        }
        else{
            throw new NameAlreadyInUseException("Error: Username is already in use");
        }
    }

    /**
     * retrieves userdata for the given username
     *
     * @param username the username of the userdata object to retrieve and return
     * @return userdata object
     */
    public UserData getUser(String username) {
        return usersMap.get(username);
    }


    /**
     * Checks if the username and password match an existing userdata object
     *
     * @param username the username entered when logging in
     * @param password the password entered when logging in
     * @return if there is a matching userdata object with the same username and password
     */
    public boolean validateLogin(String username, String password) {
        UserData foundUser =  getUser(username);
        return (foundUser != null) && Objects.equals(foundUser.password(), password);
    }


    /**
     * Checks if the username is already in use
     *
     * @param username the username entered when creating a new user
     * @return if the username is being used
     */
    public boolean usernameInUse(String username) {
        return usersMap.containsKey(username);
    }

}
