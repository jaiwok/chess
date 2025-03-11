package dataaccess.mysqlmemory;

import com.google.gson.Gson;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import service.exceptions.FaultyRequestException;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDataAccess;
import chess.ChessGame;
import model.GameData;

public class SQLGameData extends SQLInteraction implements GameDataAccess {

    public SQLGameData() throws DataAccessException {
        super();
    }

    public void clear() throws SQLException, DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    public void add(GameData game) throws DataAccessException {
        var statement = "INSERT INTO `game` (gameName, whiteUsername, blackUsername, game) VALUES (?, ?, ?, ?)";

        try (var database = DatabaseManager.getConnection();
             var newStatement = database.prepareStatement(statement)) {

            newStatement.setString(1, game.gameName());
            newStatement.setString(2, game.whiteUsername());
            newStatement.setString(3, game.blackUsername());
            Gson json = new Gson();
            newStatement.setString(4, json.toJson(game.game()));

            newStatement.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }


    public GameData getGameData(int id) throws DataAccessException {
        try (var database = DatabaseManager.getConnection()) {

            var statement = "SELECT gameId, gameName, whiteUsername, blackUsername, game FROM game WHERE gameId=?";
            try (var newStatement = database.prepareStatement(statement)) {

                newStatement.setInt(1, id);
                try (var r = newStatement.executeQuery()) {

                    if (r.next()) {
                        return formatGameObj(r);
                    }
                }
            }
        } catch (Exception e) {

            throw new DataAccessException(String.format("Database access failed while getting game Data: %s", e.getMessage()));
        }

        return null;
    }

    private GameData formatGameObj(ResultSet r) throws SQLException {
        Gson json = new Gson();

        int id = r.getInt("gameId");
        String wUsername = r.getString("whiteUsername");
        String bUsername = r.getString("blackUsername");
        String gameName = r.getString("gameName");
        var jsonGameData = r.getString("game");
        ChessGame game = json.fromJson(jsonGameData, ChessGame.class);

        return new GameData(id, wUsername, bUsername, gameName, game);
    }

    public GameData[] listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();

        try (var database = DatabaseManager.getConnection()) {

            var statement = "SELECT gameId, gameName, whiteUsername, blackUsername, game FROM `game`";

            try (var newStatement = database.prepareStatement(statement) ) {

                try (var r = newStatement.executeQuery()) {

                    while (r.next()) {
                        games.add(formatGameObj(r));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Database access failed while listing Games: %s", e.getMessage()));
        }

        return games.toArray(new GameData[0]);
    }

    public int createGame(String gameName) throws DataAccessException {

        var statement = "INSERT INTO `game` (gameName, whiteUsername, blackUsername, game) VALUES (?, ?, ?, ?)";

        try (var database = DatabaseManager.getConnection();
             var newStatement = database.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            Gson gson = new Gson();
            newStatement.setString(1, gameName);
            newStatement.setString(2, null);
            newStatement.setString(3, null);
            ChessGame game = new ChessGame();
            newStatement.setString(4, gson.toJson(game));
            newStatement.executeUpdate();

            try (ResultSet keys = newStatement.getGeneratedKeys()) {

                if (keys.next()) {

                    return keys.getInt(1);
                } else {

                    throw new DataAccessException("Database access failed while creating game.");
                }
            }
        } catch (SQLException | DataAccessException e) {

            throw new DataAccessException(e.getMessage());
        }
    }

    public void joinGame(String username, int id, ChessGame.TeamColor color) throws DataAccessException, FaultyRequestException {

        String statement;

        if(color == ChessGame.TeamColor.BLACK){
            statement = "UPDATE `game` SET blackUsername=? WHERE gameId=?";
        } else if (color == ChessGame.TeamColor.WHITE){
            statement = "UPDATE `game` SET whiteUsername=? WHERE gameId=?";
        } else{
            throw new FaultyRequestException("Must choose BLACK or WHITE as color");
        }

        try (var database = DatabaseManager.getConnection();
             var newStatement = database.prepareStatement(statement)) {

            newStatement.setString(1, username);
            newStatement.setInt(2, id);
            newStatement.executeUpdate();

        } catch (SQLException | DataAccessException e) {

            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    protected String[] getDescription() {
        return new String[]{
                """
            CREATE TABLE IF NOT EXISTS `game` (
            `gameId` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
            `gameName` VARCHAR(64) NOT NULL,
            `whiteUsername` VARCHAR(64),
            `blackUsername` VARCHAR(64),
            `game` LONGTEXT NOT NULL
            )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
        };
    }
}
