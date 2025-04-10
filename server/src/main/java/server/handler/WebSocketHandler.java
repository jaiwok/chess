package server.handler;
import chess.*;
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
import java.util.Collection;

@WebSocket
public class WebSocketHandler {

    private final WebSocketSessionsHandler sessions = new WebSocketSessionsHandler();
    private final Gson gson = new Gson();
    GameDataAccess gameDao = new SQLGameData();
    AuthDataAccess authDao = new SQLAuthData();

    public WebSocketHandler() throws DataAccessException, SQLException, SQLException, DataAccessException {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws DataAccessException, IOException, InvalidMoveException {

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
            case RESIGN -> resignGame(message, session, game, username);
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
            message = username + " is playing as White";
        } else if(username.equals(game.blackUsername())) {
            message = username + " is playing as Black";
        } else {
            message = username + " is observing";
        }

        ServerNotification serverNotification = new ServerNotification(ServerMessageType.NOTIFICATION, message);
        sessions.broadcastMessage(gameId, gson.toJson(serverNotification), session);
    }

    private void makeMove(Session session, String message, GameData gameData, ChessMove move, String username)
            throws DataAccessException, IOException, InvalidMoveException {
        ChessGame.TeamColor team = gameData.game().getTeamTurn();
        ChessGame.TeamColor otherTeam = ChessGame.TeamColor.WHITE;
        String otherUserName = gameData.whiteUsername();
        if(team == ChessGame.TeamColor.WHITE ){
            otherTeam = ChessGame.TeamColor.BLACK;
            otherUserName = gameData.blackUsername();
        }
        if(gameData.game().getStatus() == ChessGame.GameStatus.GAME_OVER){
            ServerError load = new ServerError(ServerMessageType.ERROR, "Error: Game is over no moves to make");
            sessions.sendMessage(gson.toJson(load), session); //Send game to client
            return;
        }
        if(team == ChessGame.TeamColor.WHITE &&
           gameData.blackUsername() != null &&
           gameData.blackUsername().equals(username)) {
            ServerError load = new ServerError(ServerMessageType.ERROR, "Error: It's not your turn!");
            sessions.sendMessage(gson.toJson(load), session); //Send game to client
            return;
        } else if(team == ChessGame.TeamColor.BLACK &&
                  gameData.whiteUsername() != null &&
                  gameData.whiteUsername().equals(username)) {
            ServerError load = new ServerError(ServerMessageType.ERROR, "Error: It's not your turn!");
            sessions.sendMessage(gson.toJson(load), session); //Send game to client
            return;
        }else if (gameData.whiteUsername() != null &&
                !gameData.whiteUsername().equals(username) &&
                gameData.blackUsername() != null &&
                !gameData.blackUsername().equals(username)
        ){
            ServerError load = new ServerError(ServerMessageType.ERROR, "Error: You're just observing, you can't make moves");
            sessions.sendMessage(gson.toJson(load), session); //Send game to client
            return;
        }
        Collection<ChessMove> moves = null;
        try {
            moves = gameData.game().validMoves(move.getStartPosition());
        }catch (Exception e){
        }
        if( moves == null || !moves.contains(move) ) {
            ServerError load = new ServerError(ServerMessageType.ERROR, "Error: Your move is not possible");
            sessions.sendMessage(gson.toJson(load), session); //Send game to client
            return;
        }
        try {
            gameData.game().makeMove(move);
        }catch (Exception e){
            ServerError load = new ServerError(ServerMessageType.ERROR, "Error: Your move is not possible");
            sessions.sendMessage(gson.toJson(load), session); //Send game to client
            return;
        }
        Boolean gameOver = false;
        if(gameData.game().isInCheckmate(otherTeam) ){
            gameData.game().setStatus(ChessGame.GameStatus.GAME_OVER);
            gameOver = true;
        }
        ServerNotification notification= new ServerNotification(ServerMessageType.NOTIFICATION, username + " moved from " + getMoveString(move));
        sessions.broadcastMessage(gameData.gameID(), gson.toJson(notification), session);
        gameDao.updateGame(gameData);
        ServerLoadGame serverNotification = new ServerLoadGame(ServerMessageType.LOAD_GAME, gameData.game());
        sessions.broadcastMessage(gameData.gameID(), gson.toJson(serverNotification), null);
        if(gameOver){
            if(otherUserName ==null && otherTeam == ChessGame.TeamColor.WHITE){
                otherUserName = "White is in Checkmate and lost the game";
            }else if(otherUserName == null){
                otherUserName = "Black is in Checkmate and lost the game";
            }else{
                otherUserName = otherUserName + " is in Checkmate and lost the game";
            }
            notification = new ServerNotification(ServerMessage.ServerMessageType.NOTIFICATION, otherUserName);
            sessions.broadcastMessage(gameData.gameID(), gson.toJson(notification), null);
            return;
        }
        if(gameData.game().isInStalemate(otherTeam) ){
            gameData.game().setStatus(ChessGame.GameStatus.GAME_OVER);
            gameOver = true;
        }
        if(gameOver){
            otherUserName = " Stalemate no one wins";
            notification = new ServerNotification(ServerMessage.ServerMessageType.NOTIFICATION, otherUserName);
            sessions.broadcastMessage(gameData.gameID(), gson.toJson(notification), null);
            return;
        }
        //if game is still running check if the move put the other person in check
        if( gameData.game().isInCheck(otherTeam) ){
            if(otherUserName ==null && otherTeam == ChessGame.TeamColor.WHITE){
                otherUserName = "White is in Check";
            }else if(otherUserName == null){
                otherUserName = "Black is in Check";
            }else{
                otherUserName = otherUserName + " is in Check";
            }
            notification = new ServerNotification(ServerMessage.ServerMessageType.NOTIFICATION, otherUserName);
            sessions.broadcastMessage(gameData.gameID(), gson.toJson(notification), null);
        }
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

    private void resignGame(String message, Session session, GameData game, String username) throws DataAccessException, IOException {

        if(game.game().getStatus() == ChessGame.GameStatus.GAME_OVER){
            ServerError load = new ServerError(ServerMessageType.ERROR, "Error: You can't resign from a game that has already ended.");
            sessions.sendMessage(gson.toJson(load), session); //Send game to client
            return;
        }

        if(game.blackUsername() != null && game.blackUsername().equals(username)) {
            game = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
            message = username + " who was playing as Black has resigned";
        } else if(game.whiteUsername() != null && game.whiteUsername().equals(username)) {
            game = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
            message = username + " who was playing as White has resigned";
        }else if(game.whiteUsername() != null &&
                !game.whiteUsername().equals(username) &&
                game.blackUsername() != null &&
                !game.blackUsername().equals(username)) {
            ServerError load = new ServerError(ServerMessageType.ERROR, "Error: You can't resign a game you're not playing");
            sessions.sendMessage(gson.toJson(load), session); //Send game to client
            return;
        }

        game.game().setStatus(ChessGame.GameStatus.GAME_OVER);
        gameDao.updateGame(game);

        ServerNotification serverNotification = new ServerNotification(ServerMessage.ServerMessageType.NOTIFICATION, message);

        sessions.broadcastMessage(game.gameID(), gson.toJson(serverNotification), null);
    }


    // Helper function to convert ChessMove to a human-readable string
    private String getMoveString(ChessMove move) {
        // Get the starting and ending positions of the move
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        // Convert the column index to chess notation (1 -> 'a', 2 -> 'b', ..., 8 -> 'h')
        char startCol = (char) ('a' + start.getColumn() - 1);
        char endCol = (char) ('a' + end.getColumn() - 1);

        // Get the row numbers (1-8, which corresponds directly to the row value)
        int startRow = start.getRow();
        int endRow = end.getRow();

        // Construct the move string, e.g., "a2 to b3"
        String moveString = String.format("%c%d to %c%d", startCol, startRow, endCol, endRow);

        // If there's a promotion (e.g., pawn promotion), append it to the move string
        if (move.getPromotionPiece() != null) {
            // Promotion is typically represented by the promoted piece's initial (e.g., "a8=Q")
            char promotionPiece = move.getPromotionPiece().name().charAt(0); // First letter of the piece type
            moveString += "=" + promotionPiece;
        }

        return moveString;
    }
}
