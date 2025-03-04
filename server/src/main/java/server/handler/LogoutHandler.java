package server.handler;

import com.google.gson.Gson;
import spark.Response;
import spark.Route;

import dataaccess.DataAccessException;
import service.UserService;
import service.exceptions.*;
import spark.Request;

public class LogoutHandler implements Route {
    private final UserService userService;

    public LogoutHandler(UserService u) {
        userService = u;
    }

    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        Gson json = new Gson();

        String authToken = req.headers("authorization");

        try {

            if (authToken == null || authToken.isEmpty()) {
                throw new UnauthorizedUserException("Error: unauthorized");
            }

            userService.logout(authToken);

            res.type("application/json");
            res.status(200);
            return json.toJson(null);

        } catch(UnauthorizedUserException e ) {
            res.status(401);
            return json.toJson(new ErrorMsg(e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return json.toJson(new ErrorMsg(e.getMessage()));
        }
    }
}
