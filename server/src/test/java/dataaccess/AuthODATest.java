package dataaccess;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;

import dataaccess.mysqlmemory.SQLAuthData;
import model.AuthData;

public class AuthODATest {

    private static AuthDataAccess authDataObject;
    private final AuthData authData1 = new AuthData("thisToken", "fakeUser1");
    private final AuthData authData2 = new AuthData("thatToken", "fakeUser2");

    @BeforeAll
    static void setUp() throws SQLException, DataAccessException {
        authDataObject = new SQLAuthData();
    }

    @BeforeEach
    void clearDatabase() throws SQLException, DataAccessException {
        authDataObject.clear();
    }

    @Test
    void clearValid() throws SQLException, DataAccessException {
        authDataObject.add(authData1);
        authDataObject.clear();
        assertNull(authDataObject.findAuthDataByToken(authData1.authToken()));
    }

    @Test
    void addValid() throws DataAccessException {
        authDataObject.add(authData1);
        authDataObject.add(authData2);

        assertEquals(authData1, authDataObject.findAuthDataByToken(authData1.authToken()));
        assertEquals(authData1, authDataObject.findAuthFromUsername(authData1.username()));
        assertEquals(authData2, authDataObject.findAuthDataByToken(authData2.authToken()));
        assertEquals(authData2, authDataObject.findAuthFromUsername(authData2.username()));
    }

    @Test
    void addInvalid() throws DataAccessException {
        authDataObject.add(authData1);
        assertThrows(DataAccessException.class, () -> authDataObject.add(authData1));
    }

    @Test
    void findAuthFromUsernameValid() throws DataAccessException {
        authDataObject.add(authData1);
        assertEquals(authData1, authDataObject.findAuthFromUsername(authData1.username()));
    }

    @Test
    void findAuthFromUsernameInvalid() throws DataAccessException {
        authDataObject.add(authData1);
        assertNull(authDataObject.findAuthFromUsername(authData2.username()));

        authDataObject.add(authData2);
        assertNotEquals(authData2, authDataObject.findAuthFromUsername(authData1.username()));
    }

    @Test
    void findAuthDataByTokenValid() throws DataAccessException {
        authDataObject.add(authData1);
        assertEquals(authData1, authDataObject.findAuthDataByToken(authData1.authToken()));
    }

    @Test
    void findAuthDataByTokenInvalid() throws DataAccessException {
        authDataObject.add(authData1);
        assertNull(authDataObject.findAuthDataByToken(authData2.authToken()));

        authDataObject.add(authData2);
        assertNotEquals(authData2, authDataObject.findAuthDataByToken(authData1.authToken()));
    }

    @Test
    void removeValid() throws DataAccessException {
        authDataObject.add(authData1);
        authDataObject.remove(authData1.authToken());

        assertNull(authDataObject.findAuthDataByToken(authData1.authToken()));
    }

    @Test
    void removeInvalid() throws DataAccessException {
        authDataObject.add(authData1);
        authDataObject.remove(authData2.authToken());
        assertNotNull(authDataObject.findAuthDataByToken(authData1.authToken()));
    }
}
