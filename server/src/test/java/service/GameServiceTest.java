package service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import chess.ChessGame;
import dataaccess.*;
import dataaccess.DataAccessException;
import dataaccess.localmemory.*;
import model.*;
import service.exceptions.*;

class GameServiceTest {
    private AuthDataAccess authDataAObject;
    private GameDataAccess gameDataAObject;
    private UserDataAccess userDataAObject;
    private String authToken;
    private GameService gameService;
    private UserData user;


    @BeforeEach
    public void clearBeforeTests() throws DataAccessException, FaultyRequestException, NameAlreadyInUseException, SQLException {
        authDataAObject = new AuthDataStorage();
        gameDataAObject = new GameDataStorage();
        userDataAObject = new UserDataStorage();

        new ClearService(userDataAObject, authDataAObject, gameDataAObject).clear();


        user = new UserData("myUser", "poopypants", "fake@notreal.com");
        authToken = new UserService(userDataAObject, authDataAObject).register(user).authToken();
        gameService = new GameService(authDataAObject, gameDataAObject);
    }

    @Test
    public void testValidListGames() throws DataAccessException, FaultyRequestException, NameAlreadyInUseException, UnauthorizedUserException {
        List<GameData> expectedEmpty = new ArrayList<>();
        assertEquals(expectedEmpty, List.of(gameService.listGames(authToken)));
    }

    @Test
    public void testInvalidListGames(){
        assertThrows(UnauthorizedUserException.class, () -> {gameService.listGames("beep_bop_boop");});
    }

    @Test
    public void testValidAddGame() throws DataAccessException, UnauthorizedUserException, FaultyRequestException, NameAlreadyInUseException {
        int id = gameService.addGame(authToken, "correct game name");
        int expected = 1;

        assertEquals(expected, id);
        gameService.addGame(authToken, "other game name");
        assertEquals(2, List.of(gameService.listGames(authToken)).size());
    }

    @Test
    public void testInvalidAddGame(){
        assertThrows(FaultyRequestException.class, () -> {gameService.addGame(authToken, null);});
    }

    @Test
    public void testValidJoinGame() throws DataAccessException, UnauthorizedUserException, FaultyRequestException, NameAlreadyInUseException, UserAlreadyInGameException {
        int id = gameService.addGame(authToken, "correct game name");
        chess.ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;

        gameService.joinGame(authToken, id, color);

        GameData game = gameDataAObject.getGameData(id);
        assertEquals(user.username(), game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    public void testInvalidJoinGame() throws DataAccessException, UnauthorizedUserException, FaultyRequestException, NameAlreadyInUseException, UserAlreadyInGameException {
        int id = gameService.addGame(authToken, "correct game name");
        chess.ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;

        gameService.joinGame(authToken, id, color);
        assertThrows(NameAlreadyInUseException.class, () ->{gameService.joinGame(authToken, id, color);});
        assertThrows(FaultyRequestException.class, () -> {gameService.joinGame(authToken, 2, color);});
    }
}