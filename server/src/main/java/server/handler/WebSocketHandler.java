package server.handler;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;
import dataaccess.mysqlmemory.*;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;
import websocket.messages.ServerMessage.ServerMessageType;
import websocket.messages.*;

import java.io.IOException;
import java.sql.SQLException;

@WebSocket
public class WebSocketHandler {

    private final WebSocketSessionsHandler sessions = new WebSocketSessionsHandler();
    private final Gson gson = new Gson();
    GameDataAccess gameDao = new SQLGameData();
    AuthDataAccess authDao = new SQLAuthData();

    public WebSocketHandler() throws DataAccessException, SQLException, SQLException, DataAccessException {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws DataAccessException, IOException {

        // determine message type
        UserGameCommand cmd = gson.fromJson(message, UserGameCommand.class);
        CommandType cmdType = cmd.getCommandType();

        // get things to pass to methods
        int id = cmd.getGameID();
        GameData game = gameDao.getGameData(id);

        if(game == null){
            ServerError load = new ServerError(ServerMessageType.ERROR, "Error: Game Not Found");
            sessions.sendMessage(gson.toJson(load), session); //Send game to client
            return;
        }

        String authToken = cmd.getAuthToken();
        AuthData data = authDao.findAuthDataByToken(authToken);

        if(data == null){
            ServerError load = new ServerError(ServerMessageType.ERROR, "Error: Authentication Failed");
            sessions.sendMessage(gson.toJson(load), session); //Send game to client
            return;
        }

        String username = authDao.findAuthDataByToken(authToken).username();

        ChessMove move = new ChessMove(null,null,null);
        if(cmdType == CommandType.MAKE_MOVE){
            move = cmd.getMove(); //somehow get the move from the command
        }

        switch (cmdType) {
            case LEAVE -> leaveGame(message, session, game, username);
            case RESIGN -> resignGame(message, game, username);
            case CONNECT -> connect(message, session, id, authToken);
            case MAKE_MOVE -> makeMove(session, message, game, move, username);
        }
    }

    // call service and send message to clients
    private void connect(String message, Session session, int gameId, String authToken)
            throws DataAccessException, IOException {

        sessions.addClientToSessionSet(gameId, session);
        GameData game = gameDao.getGameData(gameId); //get game from database

        ServerLoadGame load = new ServerLoadGame(ServerMessageType.LOAD_GAME, game.game());
        sessions.sendMessage(gson.toJson(load), session); //Send game to client

        String username = authDao.findAuthDataByToken(authToken).username();


        if(username.equals(game.whiteUsername())) {
            message = username + "is playing as White";
        } else if(username.equals(game.blackUsername())) {
            message = username + "is playing as Black";
        } else {
            message = username + "is observing";
        }

        ServerNotification serverNotification = new ServerNotification(ServerMessageType.NOTIFICATION, message);
        sessions.broadcastMessage(gameId, gson.toJson(serverNotification), session);
    }

    private void makeMove(Session session, String message, GameData gameData, ChessMove move, String username) {

    }

    private void leaveGame(String message, Session session, GameData game, String username) throws IOException, DataAccessException {

        if(game.blackUsername() != null && game.blackUsername().equals(username)) {
            game = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
            message = username + " who was playing as Black has left";
        } else if(game.whiteUsername() != null && game.whiteUsername().equals(username)) {
            game = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
            message = username + " who was playing as White has left";
        }else {
            message = username + " who was observing this game has left";
        }

        

        gameDao.updateGame(game);
        sessions.removeClientFromSessionSet(game.gameID(), session);

        ServerNotification serverNotification = new ServerNotification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        sessions.broadcastMessage(game.gameID(), gson.toJson(serverNotification), session);
    }

    private void resignGame(String message, GameData game, String username){

    }
}
