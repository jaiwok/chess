package server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.Request;
import spark.Response;
import spark.Route;

import dataaccess.DataAccessException;
import model.AuthData;
import service.UserService;
import service.exceptions.*;

public class LoginHandler implements Route {
    private final UserService userService;

    public LoginHandler(UserService u) {
        userService = u;
    }

    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        String username;
        String password;

        Gson json = new Gson();
        JsonObject jsonObject = JsonParser.parseString(req.body()).getAsJsonObject();

        try {
            username = jsonObject.get("username").getAsString();
            password = jsonObject.get("password").getAsString();

            AuthData authToken = userService.login(username, password);

            res.type("application/json");
            res.status(200);
            return json.toJson(authToken);

        } catch(UnauthorizedUserException e){

            res.status(401);
            return json.toJson(new ErrorMsg(e.getMessage()));
        } catch (Exception e) {

            res.status(500);
            return json.toJson(new ErrorMsg(e.getMessage()));
        }
    }
}
