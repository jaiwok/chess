package client;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import model.returnobjects.AuthTokenResponse;
import model.returnobjects.GameId;
import model.returnobjects.GameList;
import model.returnobjects.JoinGameRequest;
import org.junit.jupiter.api.*;
import server.Server;
import serverdata.Facade;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static Facade serverFacade;
    private static String username = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email.com";
    private String authToken;
    UserData user;

    @BeforeAll
    public static void init() throws Exception {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new Facade("http://localhost:" + port);
    }

    @BeforeEach
    public void registerUser() throws Exception {
        String newUser = username + new Random().nextInt();
        user = new UserData(newUser, PASSWORD, EMAIL);
        authToken = serverFacade.register(user).authToken();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerValidTest() throws Exception {
        String newUser = username + new Random().nextInt();;
        UserData newUserData = new UserData(newUser, PASSWORD, EMAIL);
        AuthTokenResponse authToken2 = serverFacade.register(newUserData);
        assertNotNull(authToken2);
    }

    @Test
    public void registerInvalidTest() {
        assertThrows(Exception.class, () -> serverFacade.register(user));
    }

    @Test
    public void loginValidTest() throws Exception {
        AuthTokenResponse auth = serverFacade.login(user);
        assertNotNull(auth);
        assertNotEquals(authToken, auth.authToken());
    }

    @Test
    public void loginInvalidTest() {
        UserData fake = new UserData("FakeUser", PASSWORD, EMAIL);
        assertThrows(Exception.class, () -> serverFacade.login(fake));
    }

    @Test
    public void logoutValidTest() throws Exception {
        serverFacade.logout();
        assertThrows(Exception.class, () -> serverFacade.listGames());
        assertThrows(Exception.class, () -> serverFacade.createGame(new GameData(1, null, null, "nullGame", new ChessGame())));
    }

    @Test
    public void logoutInvalidTest() throws Exception {
        serverFacade.logout();
        assertThrows(Exception.class, () -> serverFacade.logout());
    }

    @Test
    public void createGameValidTest() throws Exception {
        GameData gameData = new GameData(69, null, null, "newGame!", new ChessGame());
        GameId gameId = serverFacade.createGame(gameData);
        assertNotNull(gameId);
    }

    @Test
    public void createGameInvalidTest() {
        GameData gameData = new GameData(49, null, null, null, null);
        assertThrows(Exception.class, () -> serverFacade.createGame(gameData));
    }

    @Test
    public void listGamesValidTest() throws Exception {
        GameData gameData = new GameData(69, null, null, "newGame!", new ChessGame());
        serverFacade.createGame(gameData);
        GameList games = serverFacade.listGames();
        assertNotNull(games);
        assertNotEquals(0, games.games().length);
    }

    @Test
    public void listGamesInvalidTest() throws Exception {
        serverFacade.logout();
        assertThrows(Exception.class, () -> serverFacade.listGames());
    }

    @Test
    public void joinGameValidTest() throws Exception {
        GameData gameData = new GameData(69, null, null, "newGame!", new ChessGame());
        int id = serverFacade.createGame(gameData).gameID();

        JoinGameRequest req = new JoinGameRequest(ChessGame.TeamColor.WHITE, id);
        assertDoesNotThrow(() -> serverFacade.joinGame(req));
    }

    @Test
    public void joinGameInvalidTest() throws Exception {
        GameData gameData = new GameData(69, null, null, "newGame!", new ChessGame());
        int id = serverFacade.createGame(gameData).gameID();

        JoinGameRequest req = new JoinGameRequest(ChessGame.TeamColor.WHITE, id);
        serverFacade.joinGame(req);

        assertThrows(Exception.class, () -> serverFacade.joinGame(req));
    }

}
