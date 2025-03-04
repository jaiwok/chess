package server.handler;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import service.exceptions.*;

public class RegisterHandler implements Route {
    private final UserService userService;

    public RegisterHandler(UserService u) {
        userService = u;
    }

    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        UserData user;
        Gson json = new Gson();

        try {

            user = json.fromJson(req.body(), UserData.class);

            AuthData authToken = userService.register(user);

            res.type("application/json");
            res.status(200);
            return json.toJson(authToken);

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
