package dataaccess.mysqlmemory;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;
import java.sql.ResultSet;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDataAccess;
import model.UserData;



public class SQLUserData extends SQLInteraction implements UserDataAccess {

    public SQLUserData() throws DataAccessException {
        super();
    }

    public void clear() throws SQLException, DataAccessException {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    public void add(UserData user) throws SQLException, DataAccessException {

        var statement = "INSERT INTO `user` (username, password, email) VALUES (?, ?, ?)";

        try (var database = DatabaseManager.getConnection();
             var newStatement = database.prepareStatement(statement)) {

            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            newStatement.setString(1, user.username());
            newStatement.setString(2, hashedPassword);
            newStatement.setString(3, user.email());
            newStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public UserData getUser(String username) throws DataAccessException {

        var statement = "SELECT username, password, email FROM user WHERE username=?";

        try (var database = DatabaseManager.getConnection();
             var newStatement = database.prepareStatement(statement)) {

                newStatement.setString(1, username);

                try (var r = newStatement.executeQuery()) {

                    if (r.next()) {
                        return formatUserObj(r);
                    }
                }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Database access failed while getting User: %s", e.getMessage()));
        }
        return null;
    }

    private UserData formatUserObj(ResultSet r) throws SQLException {
        String username = r.getString("username");
        String password = r.getString("password");
        String email = r.getString("email");
        return new UserData(username, password, email);
    }

    public boolean validateLogin(String username, String password) throws DataAccessException {

        var statement = "SELECT password FROM user WHERE username=?";

        try (var database = DatabaseManager.getConnection();
             var newStatement = database.prepareStatement(statement)) {

                newStatement.setString(1, username);

                try (var r = newStatement.executeQuery()) {

                    if (r.next()) {

                        String hashedPassword = r.getString("password");
                        return BCrypt.checkpw(password, hashedPassword);
                    }
                }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Database access failed while validating login: %s", e.getMessage()));
        }
        return false;
    }

    public boolean usernameInUse(String username) throws DataAccessException {

        var statement = "SELECT username FROM user WHERE username=?";

        try (var database = DatabaseManager.getConnection();
             var newStatement = database.prepareStatement(statement)) {

                newStatement.setString(1, username);

                try (var r = newStatement.executeQuery()) {

                    // if results, then username is in use
                    if (r.next()) {
                        return true;
                    }
                }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Database access failed while checking username availability: %s", e.getMessage()));
        }
        return false;
    }

    @Override
    protected String[] getDescription() {
        return new String[]{
                """
            CREATE TABLE IF NOT EXISTS `user` (
            `username` VARCHAR(64) NOT NULL PRIMARY KEY,
            `password` VARCHAR(64) NOT NULL,
            `email` VARCHAR(64) NOT NULL
            )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
        };
    }
}
