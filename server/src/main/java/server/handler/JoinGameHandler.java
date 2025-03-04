package server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.Request;
import spark.Response;
import spark.Route;

import chess.ChessGame;
import service.GameService;
import service.exceptions.*;




public class JoinGameHandler implements Route {
    private final GameService gameService;

    public JoinGameHandler(GameService g) {
        gameService = g;
    }

    @Override
    public Object handle(Request req, Response res) throws FaultyRequestException {
        int id;
        Gson json = new Gson();
        chess.ChessGame.TeamColor color;

        String authToken = req.headers("authorization");
        JsonObject jsonObject = JsonParser.parseString(req.body()).getAsJsonObject();

        try{
            id = jsonObject.get("gameID").getAsInt();
            color = ChessGame.TeamColor.valueOf(jsonObject.get("playerColor").getAsString());

            if(authToken == null || authToken.isEmpty()){
                throw new UnauthorizedUserException("Error: Unauthorized");
            }

            gameService.joinGame(authToken, id, color);

            res.type("application/json");
            res.status(200);
            return json.toJson(null);

        } catch (UnauthorizedUserException e) {
            res.status(401);
            return json.toJson(new ErrorMsg(e.getMessage()));
        } catch(FaultyRequestException e){
            res.status(400);
            return json.toJson(new ErrorMsg(e.getMessage()));
        } catch(NameAlreadyInUseException e){
            res.status(403);
            return json.toJson(new ErrorMsg(e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return json.toJson(new ErrorMsg(e.getMessage()));
        }
    }
}
