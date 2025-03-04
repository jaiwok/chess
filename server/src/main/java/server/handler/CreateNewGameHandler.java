package server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.Request;
import spark.Response;
import spark.Route;

import service.GameService;
import service.exceptions.*;
import model.returnobjects.GameId;

public class CreateNewGameHandler implements Route {
    private final GameService gameService;

    public CreateNewGameHandler(GameService g) {
        gameService = g;
    }

    @Override
    public Object handle(Request req, Response res) {
        Gson json = new Gson();

        String authToken = req.headers("authorization");

        JsonObject jsonObj = JsonParser.parseString(req.body()).getAsJsonObject();
        String newGameName;

        try{
            newGameName = jsonObj.get("gameName").getAsString();

            if(authToken == null || authToken.isEmpty()){
                throw new UnauthorizedUserException("Error: Unauthorized");
            }
            if(newGameName == null || newGameName.isEmpty()){
                throw new FaultyRequestException("Error: Invalid game name");
            }

            int id = gameService.addGame(authToken, newGameName);

            res.type("application/json");
            res.status(200);
            return json.toJson(new GameId(id));

        } catch (UnauthorizedUserException e) {

            res.status(401);
            return json.toJson(new ErrorMsg(e.getMessage()));
        } catch(FaultyRequestException e){

            res.status(400);
            return json.toJson(new ErrorMsg(e.getMessage()));
        } catch (Exception e) {

            res.status(500);
            return json.toJson(new ErrorMsg(e.getMessage()));
        }
    }
}
