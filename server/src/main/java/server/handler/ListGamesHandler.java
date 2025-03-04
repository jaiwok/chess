package server.handler;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

import model.GameData;
import service.GameService;
import service.exceptions.*;

import java.util.List;

public class ListGamesHandler implements Route {
    private final GameService gameService;

    public ListGamesHandler(GameService g) {
        gameService = g;
    }

    @Override
    public Object handle(Request req, Response res) {
        Gson json = new Gson();

        String authToken;

        try{
            authToken = req.headers("authorization");

            if(authToken == null || authToken.isEmpty()){
                throw new UnauthorizedUserException("Error: Unauthorized");
            }

            List<GameData> allGames = List.of(gameService.listGames(authToken));

            res.type("application/json");
            res.status(200);
            return json.toJson(allGames);

        } catch (UnauthorizedUserException e) {

                res.status(401);
            return json.toJson(new ErrorMsg(e.getMessage()));
        } catch (Exception e) {

            res.status(500);
            return json.toJson(new ErrorMsg(e.getMessage()));
        }
    }
}
