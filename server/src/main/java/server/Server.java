package server;

import com.google.gson.Gson;
import dataaccess.*;
import dataaccess.localmemory.*;
import dataaccess.mysqlmemory.*;
import  service.*;
import service.exceptions.*;
import server.handler.*;
import spark.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;

import java.sql.SQLException;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        //sets up socket connection?
        Spark.webSocket("/ws", WebSocketHandler.class);

//        try {
//            WebSocketHandler ws = new WebSocketHandler();
//            Spark.webSocket("/ws", ws);
//        } catch (DataAccessException | SQLException e) {
//            throw new RuntimeException(e);
//        }

        Spark.staticFiles.location("web");

        UserDataAccess userClass;
        AuthDataAccess authClass;
        GameDataAccess gameClass;

        try {
            userClass = new SQLUserData();
            authClass = new SQLAuthData();
            gameClass = new SQLGameData();
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }

        ClearService clearService = new ClearService(userClass, authClass, gameClass);
        UserService userService = new UserService(userClass, authClass);
        GameService gameService = new GameService(authClass, gameClass);

        Gson json = new Gson();

        Spark.before("/session", (request, response) -> {
            if (request.requestMethod().equals("DELETE")) {
                authenticity(request, authClass, json);
            }
        });

        Spark.before("/game", (request, response) -> {
            authenticity(request, authClass, json);
        });

        Spark.delete("/db", new ClearHandler(clearService));
        Spark.post("/user", new RegisterHandler(userService));
        Spark.post("/session", new LoginHandler(userService));
        Spark.delete("/session", new LogoutHandler(userService));
        Spark.post("/game", new CreateNewGameHandler(gameService));
        Spark.get("/game", new ListGamesHandler(gameService));
        Spark.put("/game", new JoinGameHandler(gameService));

        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private static void authenticity(Request request, AuthDataAccess authClass, Gson json) throws DataAccessException {
        String authToken = request.headers("authorization");
        if (!authorized(authToken, authClass)) {
            Spark.halt(401,  json.toJson(new ErrorMsg("Error: Unauthorized")));
        }
    }

    private static boolean authorized(String authToken, AuthDataAccess authClass) throws DataAccessException {
        return authToken != null && !authToken.isEmpty() && authClass.findAuthDataByToken(authToken) != null;
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
