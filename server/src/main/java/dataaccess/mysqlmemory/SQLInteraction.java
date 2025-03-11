package dataaccess.mysqlmemory;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import com.google.gson.Gson;
import java.sql.*;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import chess.ChessGame;



public abstract class SQLInteraction {


    protected SQLInteraction() throws DataAccessException {
        configDatabase();
    }

    //to be overloaded by classes that extend from this abstract class
    protected abstract String[] getDescription();

    private void configDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (var database = DatabaseManager.getConnection()) {

            for (var description : getDescription()) {

                try (var statement = database.prepareStatement(description)) {
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {

            throw new DataAccessException(String.format("Failed to configure database: %s", e.getMessage()));
        }
    }

    int executeUpdate(String statement, Object... params) throws DataAccessException, SQLException {

        try (var database = DatabaseManager.getConnection()) {

            try (var newStatement = database.prepareStatement(statement, RETURN_GENERATED_KEYS)) {

                addParams(newStatement, params);
                newStatement.executeUpdate();
                ResultSet r = newStatement.getGeneratedKeys();

                if (r.next()) {
                    return r.getInt(1);
                }else {
                    return 0;
                }
            }
        } catch (SQLException e) {

            throw new DataAccessException(String.format("unable to update database: %s", e.getMessage()));
        }
    }

    private void addParams(PreparedStatement statement, Object[] params) throws SQLException, DataAccessException {

        for (int j = 0; j < params.length; j++) {
            Object param = params[j];

            //Java switch matching magic
            switch (param) {
                case ChessGame g -> statement.setString(j + 1, new Gson().toJson(g));
                case Integer i -> statement.setInt(j + 1, i);
                case String s -> statement.setString(j + 1, s);
                case null -> statement.setNull(j + 1, Types.NULL);
                default -> throw new DataAccessException("Unexpected type of: " + param.getClass());
            }
        }
    }

}
