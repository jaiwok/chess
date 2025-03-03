package dataaccess;

import chess.ChessGame;
import model.GameData;

public interface GameDataAccess {
    /**
     * Clears the database of all games
     */
    void clear() throws DataAccessException;

    /**
     * Creates a new game
     *
     * @param game the game to create
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
     * @param color
     * @param username
     * @param gameId
     */
    void joinGame(ChessGame.TeamColor color, String username, int gameId) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

}