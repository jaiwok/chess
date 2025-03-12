package dataaccess;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;

import service.exceptions.FaultyRequestException;
import dataaccess.mysqlmemory.SQLGameData;
import chess.ChessGame;
import model.GameData;

public class GameODATest {

    private static GameDataAccess gameDataObject;
    private final GameData game1 = new GameData(1, "w_g1_user", "b_g1_user", "game1", new ChessGame());
    private final GameData game2 = new GameData(2, "w_g2_user", "b_g2_user", "game2", new ChessGame());
    private final GameData nullGame = new GameData(0, null, null, null, null);

    @BeforeAll
    static void setup() throws DataAccessException {
        gameDataObject = new SQLGameData();
    }

    @BeforeEach
    void clearDatabase() throws SQLException, DataAccessException {
        gameDataObject.clear();
    }

    @Test
    void clear() throws DataAccessException, SQLException {
        gameDataObject.add(game1);
        gameDataObject.add(game2);
        gameDataObject.clear();

        assertNull(gameDataObject.getGameData(game1.gameID()));
        assertNull(gameDataObject.getGameData(game2.gameID()));
    }

    @Test
    void addValid() throws DataAccessException {
        gameDataObject.add(game1);
        gameDataObject.add(game2);

        assertNotNull(gameDataObject.getGameData(game1.gameID()));
        assertNotNull(gameDataObject.getGameData(game2.gameID()));
    }

    @Test
    void addInvalid() throws DataAccessException {
        assertThrows(DataAccessException. class, () -> gameDataObject.add(nullGame));
    }

    @Test
    void getGameDataValid() throws DataAccessException {
        gameDataObject.add(game1);
        gameDataObject.add(game2);

        assertEquals(game1, gameDataObject.getGameData(game1.gameID()));
        assertEquals(game2, gameDataObject.getGameData(game2.gameID()));
    }

    @Test
    void getGameDataInvalid() throws DataAccessException {
        gameDataObject.add(game1);
        assertNull(gameDataObject.getGameData(game2.gameID()));

        gameDataObject.add(game2);
        assertNotEquals(game2, gameDataObject.getGameData(game1.gameID()));
    }

    @Test
    void listGamesValid() throws DataAccessException {
        gameDataObject.add(game1);
        assertEquals(1, gameDataObject.listGames().length);

        gameDataObject.add(game2);
        assertEquals(2, gameDataObject.listGames().length);

        var allGames = gameDataObject.listGames();
        assertEquals(game1, allGames[0]);
        assertEquals(game2, allGames[1]);
    }

    @Test
    void listGamesInvalid() throws DataAccessException {
        assertEquals(0, gameDataObject.listGames().length);
    }

    @Test
    void createGameValid() throws DataAccessException {
        gameDataObject.createGame(game1.gameName());
        assertNotNull(gameDataObject.getGameData(game1.gameID()));
        assertEquals(game1.gameName(), gameDataObject.getGameData(game1.gameID()).gameName());
    }

    @Test
    void createGameInvalid() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> gameDataObject.createGame(null));
    }

    @Test
    void joinGameValid() throws DataAccessException, FaultyRequestException {
        int id= gameDataObject.createGame(game1.gameName());
        gameDataObject.joinGame( game1.whiteUsername(), id, ChessGame.TeamColor.WHITE);

        GameData gameData = gameDataObject.getGameData(id);

        assertEquals(game1.gameName(), gameData.gameName());
        assertEquals(game1.whiteUsername(), gameData.whiteUsername());
        assertNull(gameData.blackUsername());
    }

    @Test
    void joinGameInvalid() throws DataAccessException {
        int id = gameDataObject.createGame(game1.gameName());
        assertThrows(FaultyRequestException.class, () -> gameDataObject.joinGame(null, id, null));
    }
}
