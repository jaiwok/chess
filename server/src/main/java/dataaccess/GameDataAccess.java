package dataaccess;

import chess.ChessGame;
import model.GameData;

public interface GameDataAccess {
    /**
     * Clears the database of all games
     */
    void clear() throws DataAccessException;

    /**
     * Adds gameData object
     *
     * @param game object to add
     */
    void add(GameData game) throws DataAccessException;

    /**
     * finds a game by its id
     *
     * @param id the game to find and return
     */
    GameData getGameData(int id) throws DataAccessException;

    /**
     * returns a list of all the games
     *
     */
    GameData[] listGames() throws DataAccessException;

    /**
     * makes a new game and returns the id
     *
     * @param gameName name of the game to create
     */
    int createGame(String gameName) throws DataAccessException;

    /**
     * adds a player to the game
     *
     * @param username username of player to add to game
     * @param gameId which game for player to join
     * @param color which color user will be in game
     */
    void joinGame( String username, int gameId, ChessGame.TeamColor color) throws DataAccessException;

    void updatedGame(GameData game) throws  DataAccessException;

}