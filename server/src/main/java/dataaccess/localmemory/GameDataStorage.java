package dataaccess.localmemory;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import model.GameData;
import java.util.HashMap;

public class GameDataStorage implements GameDataAccess{
    final private HashMap<Integer, GameData> gamesMap = new HashMap<>();

    /**
     * Clears the database of all games
     */
    public void clear() {
        gamesMap.clear();
    }

    /**
     * Adds gameData object
     *
     * @param game object to add
     */
    public void add(GameData game) {
        GameData newGame = new GameData(generateId(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        gamesMap.put(newGame.gameID(), newGame);
    }

    /**
     * finds a game by its id
     *
     * @param id the game to find and return
     */
    public GameData getGameData(int id) {
        return gamesMap.get(id);
    }

    /**
     * returns a list of all the games
     *
     */
    public GameData[] listGames() {
        return gamesMap.values().toArray(new GameData[0]);
    }

    /**
     * makes a new game and returns the id
     *
     * @param gameName name of the game to create
     */
    public int createGame(String gameName) {

        GameData newGame= new GameData(generateId(), null, null, gameName, new ChessGame());
        gamesMap.put(newGame.gameID(), newGame);
        return newGame.gameID();
    }

    /**
     * adds a player to the game
     *
     * @param username username of player to add to game
     * @param gameId which game for player to join
     * @param color which color user will be in game
     */
    public void joinGame( String username, int gameId, ChessGame.TeamColor color) {
        GameData game = gamesMap.get(gameId);
        GameData joinedGame;

        joinedGame = (color == ChessGame.TeamColor.WHITE) ?
                new GameData(gameId, username, game.blackUsername(), game.gameName(), game.game()) :
                new GameData(gameId, game.whiteUsername(), username, game.gameName(), game.game());

        gamesMap.put(gameId, joinedGame);
    }

    public void updateGame(GameData game) throws DataAccessException { return; }

    private int generateId() {
        int id = 1;
        while (gamesMap.get(id) != null) {
            id++;
        }
        return id;
    }
}
