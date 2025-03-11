package dataaccess;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;

import dataaccess.mysqlmemory.SQLUserData;
import model.UserData;
import service.exceptions.NameAlreadyInUseException;

public class UserODATest {

    private static UserDataAccess userDataObject;
    private final UserData user1 = new UserData("User_2", "poopy_pants", "fake_1@notreal.com");
    private final UserData user2 = new UserData("User_1", "clean_pants", "fake_2@notreal.com");

    @BeforeAll
    static void setUp() throws DataAccessException {
        userDataObject = new SQLUserData();
    }

    @BeforeEach
    void clearDatabase() throws SQLException, DataAccessException {
        userDataObject.clear();
    }

    @Test
    void clearValid() throws SQLException, DataAccessException, NameAlreadyInUseException {
        userDataObject.add(user1);
        userDataObject.clear();
        assertNull(userDataObject.getUser(user1.username()));
    }

    @Test
    void addValid() throws DataAccessException, SQLException, NameAlreadyInUseException {
        userDataObject.add(user1);
        assertNotNull(userDataObject.getUser(user1.username()));

        userDataObject.add(user2);
        assertNotNull(userDataObject.getUser(user2.username()));
    }

    @Test
    void addInvalid() throws SQLException, DataAccessException, NameAlreadyInUseException {
        userDataObject.add(user1);

        assertThrows(DataAccessException.class, () -> userDataObject.add(user1));
    }

    @Test
    void getUserValid() throws SQLException, DataAccessException, NameAlreadyInUseException {
        userDataObject.add(user1);
        userDataObject.add(user2);

        String user = userDataObject.getUser(user1.username()).username();
        String psw = userDataObject.getUser(user1.username()).password();

        assertEquals(user1.username(), user);
        assertTrue(BCrypt.checkpw(user1.password(), psw));

        user = userDataObject.getUser(user2.username()).username();
        psw = userDataObject.getUser(user2.username()).password();
        assertEquals(user2.username(), user);
        assertTrue(BCrypt.checkpw(user2.password(), psw));
    }

    @Test
    void getUserInvalid() throws DataAccessException, SQLException, NameAlreadyInUseException {
        userDataObject.add(user2);

        assertNull(userDataObject.getUser(user1.username()));
    }

    @Test
    void validateLoginValid() throws SQLException, DataAccessException, NameAlreadyInUseException {
        userDataObject.add(user1);
        userDataObject.add(user2);

        assertTrue(userDataObject.validateLogin(user1.username(), user1.password()));
        assertTrue(userDataObject.validateLogin(user2.username(), user2.password()));
    }

    @Test
    void validateLoginInvalid() throws SQLException, DataAccessException, NameAlreadyInUseException {
        userDataObject.add(user1);

        assertFalse(userDataObject.validateLogin(user2.username(), user2.password()));

        userDataObject.add(user2);

        assertFalse(userDataObject.validateLogin(user2.username(), user1.password()));
        assertFalse(userDataObject.validateLogin(user1.username(), user2.password()));
    }

    @Test
    void usernameInUseValid() throws SQLException, DataAccessException, NameAlreadyInUseException {
        userDataObject.add(user1);

        assertFalse(userDataObject.usernameInUse(user2.username()));
    }

    @Test
    void usernameInUseInvalid() throws SQLException, DataAccessException, NameAlreadyInUseException {
        userDataObject.add(user1);
        userDataObject.add(user2);

        assertTrue(userDataObject.usernameInUse(user1.username()));
        assertTrue(userDataObject.usernameInUse(user2.username()));
    }
}
