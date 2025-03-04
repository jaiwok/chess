package service;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import chess.ChessGame;
import dataaccess.*;
import dataaccess.localmemory.*;
import model.*;
import service.exceptions.NameAlreadyInUseException;

class ClearServiceTest {

    @Test
    void testClearService() throws DataAccessException, NameAlreadyInUseException {
        AuthDataAccess authDataAObject = new AuthDataStorage();
        GameDataAccess gameDataAObject = new GameDataStorage();
        UserDataAccess userDataAObject = new UserDataStorage();

        UserData user = new UserData("myUser", "poopypants", "fake@notreal.com");
        AuthData auth = new AuthData("token", user.username());
        GameData game = new GameData(1, user.username(), "otherUser", "goisnotchess", new ChessGame());

        authDataAObject.add(auth);
        gameDataAObject.add(game);
        userDataAObject.add(user);

        ClearService clearer =  new ClearService(userDataAObject, authDataAObject, gameDataAObject);
        clearer.clear();

        assertNull(authDataAObject.findAuthFromUsername(user.username()));
        assertNull(gameDataAObject.getGameData(game.gameID()));
        assertNull(userDataAObject.getUser(user.username()));
    }
}