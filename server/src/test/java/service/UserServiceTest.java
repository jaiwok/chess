package service;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import dataaccess.*;
import dataaccess.localmemory.*;
import model.*;
import service.exceptions.*;

class UserServiceTest {
    private AuthDataAccess authDataAObject;
    private GameDataAccess gameDataAObject;
    private UserDataAccess userDataAObject;
    private final UserData user = new UserData("myUser", "poopypants", "fake@notreal.com");

    @BeforeEach
    public void clearBeforeTests() throws DataAccessException {
        authDataAObject = new AuthDataStorage();
        gameDataAObject = new GameDataStorage();
        userDataAObject = new UserDataStorage();

        new ClearService(userDataAObject, authDataAObject, gameDataAObject).clear();
    }

    @Test
    void testValidRegister() throws DataAccessException, FaultyRequestException, NameAlreadyInUseException {

        AuthData newRegUser = new UserService(userDataAObject, authDataAObject).register(user);

        assertNotNull(newRegUser.authToken());
        assertEquals(user.username(), newRegUser.username());

        assertTrue(userDataAObject.usernameInUse(user.username()));
        assertEquals(authDataAObject.findAuthDataByToken(newRegUser.authToken()).username(), user.username());
    }

    @Test
    void testInvalidRegister() throws DataAccessException, FaultyRequestException, NameAlreadyInUseException {
        new UserService(userDataAObject, authDataAObject).register(user);

        assertThrows(NameAlreadyInUseException.class, () -> new UserService(userDataAObject, authDataAObject).register(user));
    }

    @Test
    void testValidLogin() throws DataAccessException, UnauthorizedUserException, FaultyRequestException, NameAlreadyInUseException {
        new UserService(userDataAObject, authDataAObject).register(user);
        AuthData authData = new UserService(userDataAObject, authDataAObject).login(user.username(), user.password());

        assertNotNull(authData.authToken());
        assertEquals(authData.username(), user.username());
    }

    @Test
    void testInvalidLogin() throws DataAccessException, UnauthorizedUserException, FaultyRequestException, NameAlreadyInUseException {
         assertThrows(UnauthorizedUserException.class, () ->
                 new UserService(userDataAObject, authDataAObject).login(user.username(), user.password()));
    }

    @Test
    void testValidLogout() throws DataAccessException, FaultyRequestException, NameAlreadyInUseException, UnauthorizedUserException {
        AuthData authData = new UserService(userDataAObject, authDataAObject).register(user);
        new UserService(userDataAObject, authDataAObject).logout(authData.authToken());

        assertNull(authDataAObject.findAuthDataByToken(authData.authToken()));
    }

    @Test
    void testInvalidLogout() throws DataAccessException, FaultyRequestException, NameAlreadyInUseException, UnauthorizedUserException {
        assertThrows(UnauthorizedUserException.class, () -> new UserService(userDataAObject, authDataAObject).logout("authToken"));
    }
}