package service;

import chess.ChessGame;
import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import model.GameData;
import dataaccess.DataAccessException;
import service.exceptions.*;

import java.util.Objects;

public class GameService {
    private final AuthDataAccess authClass;
    private final GameDataAccess gameClass;

    public GameService(AuthDataAccess a, GameDataAccess g) {
        authClass = a;
        gameClass = g;
    }

    /**
     * Gets a list of all the games
     *
     * @param authToken token of signed-in user
     * @return the array of all games in the database
     */
    public GameData[] listGames(String authToken) throws DataAccessException, UnauthorizedUserException{
        if(authClass.findAuthDataByToken(authToken) !=  null){
            return gameClass.listGames();
        } else {
            throw new UnauthorizedUserException("Error: Unauthorized");
        }
    }

    /**
     * Creates a new game with a name.
     *
     * @param authToken token of signed-in user
     * @param name string of what the new chess game will be called
     * @return the integer ID of the new game
     */
    public int addGame(String authToken, String name) throws DataAccessException, UnauthorizedUserException, FaultyRequestException{
        if(authClass.findAuthDataByToken(authToken) !=  null) {
            if(name != null) {
                return gameClass.createGame(name);
            } else{
                throw new FaultyRequestException("Error: Invalid name");
            }
        } else {
            throw new UnauthorizedUserException("Error: Unauthorized");
        }
    }

    /**
     * Find game by its ID and join the game as param color if available
     *
     * @param authToken token of signed-in user
     * @param id the game you want to join
     * @param color team to join the game as
     */
    public void joinGame(String authToken, int id, chess.ChessGame.TeamColor color)
            throws DataAccessException, FaultyRequestException, NameAlreadyInUseException, UnauthorizedUserException, UserAlreadyInGameException {

        if(authClass.findAuthDataByToken(authToken) !=  null) {
            GameData game = gameClass.getGameData(id);
            
            if(game != null){
                
                if(colorIsFree(game,color)){
                    
                    String username = authClass.findAuthDataByToken(authToken).username();
                    if(Objects.equals(username, game.whiteUsername()) || Objects.equals(username, game.blackUsername())){
                        throw new UserAlreadyInGameException("Error: User already in game");
                    }
                    gameClass.joinGame(username, id, color);
                    
                }else{
                    throw new NameAlreadyInUseException("Error: Color already taken");
                }
            } else{
                throw new FaultyRequestException("Error: Invalid game ID");
            }
        } else{
            throw new UnauthorizedUserException("Error: Unauthorized");
        }
    }

    /**
     * Determines if the given color for a game is available
     *
     * @param game chess game to check on
     * @param color color user wants to be
     * @return if the color is free
     */
    private boolean colorIsFree(GameData game, ChessGame.TeamColor color) {

        return color == ChessGame.TeamColor.WHITE ? game.whiteUsername() == null :
                                                    game.blackUsername() == null;
    }
}
