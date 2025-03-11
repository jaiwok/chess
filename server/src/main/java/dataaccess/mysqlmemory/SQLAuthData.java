package dataaccess.mysqlmemory;

import java.sql.SQLException;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.AuthDataAccess;
import model.AuthData;


public class SQLAuthData extends SQLInteraction implements AuthDataAccess {

    public SQLAuthData() throws DataAccessException, SQLException {
        super();
    }

    public void clear() throws SQLException, DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    public void add(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO `auth` (authToken, username) VALUES (?, ?)";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {

            // fill in variables in the statement
            preparedStatement.setString(1, authData.authToken());
            preparedStatement.setString(2, authData.username());

            preparedStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public AuthData findAuthFromUsername(String username) throws DataAccessException {

        var statement = "SELECT authToken FROM `auth` WHERE username=?";

        try (var database = DatabaseManager.getConnection();
             var newStatement = database.prepareStatement(statement)) {

            newStatement.setString(1, username);

                try (var r = newStatement.executeQuery()) {

                    if (r.next()) {

                        return new AuthData(r.getString("authToken"), username);
                    }
                }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Database access failed while finding authData with user: %s", e.getMessage()));
        }
        return null;
    }

    public AuthData findAuthDataByToken(String token) throws DataAccessException {

        var statement = "SELECT username FROM `auth` WHERE authToken=?";

        try (var database = DatabaseManager.getConnection();
             var newStatement = database.prepareStatement(statement)) {

            newStatement.setString(1, token);

                try (var r = newStatement.executeQuery()) {

                    if (r.next()) {

                        return new AuthData(token, r.getString("username"));
                    }
                }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Database access failed while finding authData with token: %s", e.getMessage()));
        }
        return null;
    }

    public void remove(String authToken) throws DataAccessException {

        var statement = "DELETE FROM `auth` WHERE authToken=?";

        try (var database = DatabaseManager.getConnection();
             var newStatement = database.prepareStatement(statement)) {

            newStatement.setString(1, authToken);
            newStatement.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    protected String[] getDescription() {
        return new String[]{
                """
            CREATE TABLE IF NOT EXISTS `auth` (
            `authToken` VARCHAR(64) NOT NULL PRIMARY KEY,
            `username` VARCHAR(64) NOT NULL
            )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
        };
    }
}
